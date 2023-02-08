package com.jiuxiao.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.jiuxiao.base.exception.XueChengException;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.base.module.RestResponse;
import com.jiuxiao.base.utils.AssertUtils;
import com.jiuxiao.media.mapper.MediaFilesMapper;
import com.jiuxiao.media.module.dto.QueryMediaParamsDto;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.module.dto.UploadFileResultDto;
import com.jiuxiao.media.module.po.MediaFiles;
import com.jiuxiao.media.service.MediaFilesService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    @Resource
    private MinioClient minioClient;

    @Lazy
    @Resource //直接注可能会产生 Service 循环依赖的问题，使用懒加载即可解决
    private MediaFilesService currentProxy;

    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String videoBucket;

    /**
     * @param companyId           机构ID
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.media.module.po.MediaFiles>
     * @decription 媒资文件查询方法
     * @date 2023/2/1 16:25
     */
    @Override
    public PageResult<MediaFiles> queryMediaFile(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //文件名称
        queryWrapper.like(
                StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()),
                MediaFiles::getFilename, queryMediaParamsDto.getFilename()
        );
        //文件类型
        queryWrapper.eq(
                StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()),
                MediaFiles::getFileType, queryMediaParamsDto.getFileType()
        );
        //是否显示：1显示，0不显示
        queryWrapper.eq(MediaFiles::getStatus, "1");
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        List<MediaFiles> list = pageResult.getRecords();
        return new PageResult<>(list, pageResult.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * @param companyId           机构ID
     * @param uploadFileParamsDto 上传的文件信息
     * @param bytes               文件的总字节数组
     * @param folder              桶下的子目录
     * @param objectName          对象名称
     * @return: com.jiuxiao.media.module.dto.UploadFileResultDto
     * @decription 上传文件的通用接口
     * @date 2023/2/1 16:26
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
        //如果 folder 为空，则根据年月日生成子文件夹
        if (StringUtils.isEmpty(folder)) {
            folder = getFileFolder(new Date(), true, true, true);
        } else if (!folder.contains("/")) {
            folder = folder + "/";
        }

        //如果objectName为空，使用文件的md5值为objectName
        String filename = uploadFileParamsDto.getFilename();
        String fileMd5 = DigestUtils.md5Hex(bytes);
        if (StringUtils.isEmpty(objectName)) {
            //对象名为 Md5 + 文件扩展名
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }
        //存储进minio的对象名
        objectName = folder + objectName;

        try {
            uploadFileToMinIO(bytes, bucketFiles, objectName);
            // 要进行事务控制，必须满足两个条件：
            // 1. 要进行事务控制的方法，必须被代理对象调用
            // 2. 必须对进行事务控制的方法加上 @Transactional 注解
            // 此处虽然对 insertFileInfoToDB() 加上了注解，但是只是满足了第二个条件，该方法的调用方为 this，他不是代理对象，因此不会进行事务控制
            // MediaFiles mediaFiles = this.insertFileInfoToDB(companyId, fileMd5, uploadFileParamsDto, bucketFiles, objectName);

            // 那么，第二个条件已经满足，想要满足第一个条件，就需要使用代理对象来调用 insertFileInfoToDB() 方法，怎么实现？
            // 将该方法抽象为接口，然后将其注入，这样就会变成代理对象进行调用 -> currentProxy
            // 【注意】：非事务方法想要调用一个事务方法，必须使用代理对象去调用
            MediaFiles mediaFiles = currentProxy.insertFileInfoToDB(companyId, fileMd5, uploadFileParamsDto, bucketFiles, objectName);

            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.debug("上传文件失败：{}", e.getMessage());
            // 这里需要进行异常捕获，如果只在 insertFileInfoToDB() 方法上进行事务控制，它出异常后会抛给它的调用方（即uploadFile()方法）
            // 想要进行事务回滚，就必须在捕获异常后进行抛出，告诉 Sprig 框架我这里出异常了，此时 Spring 框架才会进行事务回滚
            // 如果不抛出异常给 Spring 框架，即使加了 @Transactional 注解，也不会进行回滚
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param filePath   文件在服务器本地的存储路径
     * @param bucketName 上传后存储的桶名称
     * @param objectName 文件对象名称
     * @return: void
     * @decription 上传文件到MINIO（大文件）
     * @date 2023/2/8 15:25
     */
    private void uploadFileToMinIO(String filePath, String bucketName, String objectName) {
        try {
            UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            minioClient.uploadObject(objectArgs);
            log.info("合并后的文件上传到minio成功 : {}", filePath);
        } catch (Exception e) {
            log.error("合并后的文件上传到minio失败 : {}", e.getMessage());
            XueChengException.cast("合并后的文件上传到minio失败");
        }
    }

    /**
     * @param bytes      文件的字节数组
     * @param bucketName 上传后存储的桶名称
     * @param objectName 文件对象名称
     * @return: void
     * @decription 上传文件到MINIO（小文件）
     * @date 2023/2/4 11:08
     */
    private void uploadFileToMinIO(byte[] bytes, String bucketName, String objectName) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        //传入的对象是否有扩展名，没有则默认设置为未知的二进制流类型
        if (objectName.contains(".")) {
            String extensionName = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extensionName);
            //扩展名存在则匹配正确，否则默认为未知二进制类型文件
            if (null != extensionMatch) {
                contentType = extensionMatch.getMimeType();
            }
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minio
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error("上传文件失败:{}", e.getMessage());
            XueChengException.cast("上传文件失败");
        }
    }

    /**
     * @param companyId  机构ID
     * @param fileId     上传文件的ID
     * @param dto        上传文件参数类
     * @param bucket     上传的桶名称
     * @param objectName 上传文件的对象名称
     * @return: com.jiuxiao.media.module.po.MediaFiles
     * @decription 将上传的文件信息插入数据库
     * @date 2023/2/4 11:22
     */
    @Transactional
    public MediaFiles insertFileInfoToDB(Long companyId, String fileId, UploadFileParamsDto dto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);

        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(dto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            mediaFilesMapper.insert(mediaFiles);
        }
        return mediaFiles;
    }

    /**
     * @param date
     * @param year
     * @param month
     * @param day
     * @return: java.lang.String
     * @decription 根据日期拼接目录
     * @date 2023/2/1 16:31
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuilder folderString = new StringBuilder();
        if (year) {
            folderString.append(dateStringArray[0]).append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]).append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]).append("/");
        }
        return folderString.toString();
    }

    /**
     * @param id 要移除的文件ID
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.media.module.po.MediaFiles>
     * @decription 根据ID移除文件
     * @date 2023/2/7 11:25
     */
    @Override
    public void deleteMedia(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        AssertUtils.isTrue(mediaFiles != null, "查询的媒资文件不存在");
        mediaFiles.setStatus("0");
        int update = mediaFilesMapper.updateById(mediaFiles);
        AssertUtils.isTrue(update > 0, "删除媒资文件失败");
    }

    /**
     * @param fileMd5 文件的MD5值
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 检查待上传的文件
     * @date 2023/2/8 10:33
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //只有在数据库存在、文件系统也存在，才认为文件存在，否则重新上传进行覆盖
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (null == mediaFiles) {
            return RestResponse.success(false);
        }
        GetObjectArgs objectArgs = GetObjectArgs
                .builder()
                .bucket(mediaFiles.getBucket())
                .object(mediaFiles.getFilePath())
                .build();
        try {
            InputStream inputStream = minioClient.getObject(objectArgs);
            if (null == inputStream) {
                log.info("源文件不存在，需要上传");
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            log.warn("源文件校验失败 : {}", e.getMessage());
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    /**
     * @param fileMd5 文件的MD5值
     * @param chunk   分块的序号
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 检查待上传的分块文件
     * @date 2023/2/8 10:34
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunk) {
        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFolderByMd5(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        //查询文件系统分块文件是否存在
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs
                .builder()
                .bucket(videoBucket)
                .object(chunkFilePath)
                .build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream == null) {
                log.info("分片文件不存在，需要上传");
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            log.warn("分片文件校验失败 : {}", e.getMessage());
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    /**
     * @param fileMd5 文件的MD5值
     * @return: java.lang.String
     * @decription 根据文件的MD5值，拿到分块文件的MINIO存储路径
     * @date 2023/2/8 10:50
     */
    private String getChunkFolderByMd5(String fileMd5) {
        // 在MINIO中大文件存储规律如下（该存储方式是为了加快IO索引速度）：
        // 1. 根据文件的MD5值，取前两位创建目录，然后在该目录下存储文件源文件和分块文件
        // 2. 假设，某视频的MD5值为 5fnjkasnjd.......asdds，并且存储在MINIO的 video 桶之下，则：
        //      2.1 该视频文件的路径为：/video/5/f/5fnjkasnjd.......asdds.mp4
        //      2.2 该视频文件的分块文件存储路径为：/video/5/f/chunk/
        //      2.3 分块文件存储依然按照 0，1，2.... 的顺序在 /chunk/ 文件夹下存储
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * @param fileMd5 文件的MD5值
     * @param fileExt 文件的后缀名
     * @return: java.lang.String
     * @decription 根据文件的MD5值，拿到合并后文件的MINIO存储路径
     * @date 2023/2/8 15:32
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        //合并后的文件存储在该文件分块文件路劲的同级目录下
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5
                + "/" + fileMd5 + fileExt;
    }

    /**
     * @param fileMd5 文件MD5值
     * @param chunk   分块序号
     * @param bytes   文件的字节数组
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 上传分块文件
     * @date 2023/2/8 11:09
     */
    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        String chunkFileFolder = getChunkFolderByMd5(fileMd5);  //分块文件所在目录
        String chunkFilePath = chunkFileFolder + chunk; //分块文件的路径
        try {
            uploadFileToMinIO(bytes, videoBucket, chunkFilePath);
            log.info("分片文件上传成功 : {}", chunkFilePath);
            return RestResponse.success(true);
        } catch (Exception e) {
            log.error("分块文件上传失败 : {}", e.getMessage());
            return RestResponse.validFail(false, "分块文件上传失败");
        }
    }

    /**
     * @param companyId  机构ID
     * @param fileMd5    文件MD5值
     * @param chunkTotal 分块总数量
     * @param dto        文件上传参数DTO
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 合并分块文件
     * @date 2023/2/8 11:10
     */
    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto dto) {
        //下载分块文件，下载的文件已经有序，因此这里不需要对分块文件排序
        File[] files = downloadChunkFiles(fileMd5, chunkTotal);
        //获取合并后文件的扩展名
        String filename = dto.getFilename();
        String extensionName = filename.substring(filename.lastIndexOf("."));

        //创建最终的合并文件
        File mergeFile = null;
        try {
            try {
                mergeFile = File.createTempFile("merge", extensionName);
            } catch (Exception e) {
                log.error("创建临时合并文件失败 : {}", e.getMessage());
                XueChengException.cast("创建临时合并文件失败");
            }

            //合并分块文件
            RandomAccessFile fileWrite = null;
            try {
                fileWrite = new RandomAccessFile(mergeFile, "rw");
                byte[] bytes = new byte[1024];
                for (File file : files) {
                    RandomAccessFile fileRead = new RandomAccessFile(file, "r");
                    int len = -1;
                    while ((len = fileRead.read(bytes)) != -1) {
                        fileWrite.write(bytes, 0, len);
                    }
                }
            } catch (IOException e) {
                log.error("合并分块文件失败 : {}", e.getMessage());
                XueChengException.cast("合并分块文件失败");
            }

            //校验合并后的文件与源文件是否一致
            try {
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    log.warn("合并文件校验不通过，文件路径 : {},源文件MD5 : {}", mergeFile.getAbsolutePath(), fileMd5);
                    XueChengException.cast("合并文件校验不通过");
                }
            } catch (IOException e) {
                log.error("合并文件校验失败 : {}\n文件路径 : {},源文件MD5 : {}", e.getMessage(), mergeFile.getAbsolutePath(), fileMd5);
                XueChengException.cast("合并文件校验失败");
            }

            //将合并成功的文件上传到MINIO
            String mergeFilePathInMinIO = getFilePathByMd5(fileMd5, extensionName);
            uploadFileToMinIO(mergeFile.getAbsolutePath(), videoBucket, mergeFilePathInMinIO);

            //将合并成功的文件信息加入数据库
            dto.setFileSize(mergeFile.length());
            insertFileInfoToDB(companyId, fileMd5, dto, videoBucket, mergeFilePathInMinIO);

            return RestResponse.success(true);
        } finally {//在最后要关闭流、删除临时文件
            //删除所有的临时分块文件
            for (File file : files) {
                if (null != file) {
                    file.delete();
                }
            }
            //删除临时合并文件
            if (null != mergeFile) {
                mergeFile.delete();
            }

        }
    }

    /**
     * @param fileMd5    文件的MD5值
     * @param chunkTotal 分块总数量
     * @return: java.io.File[]
     * @decription 下载分块文件
     * @date 2023/2/8 14:15
     */
    private File[] downloadChunkFiles(String fileMd5, int chunkTotal) {
        String chunkFolder = getChunkFolderByMd5(fileMd5);
        File[] resultFiles = new File[chunkTotal]; //最终的分块文件数组
        //先查询该分块是否存在，存在则下载，否则抛异常
        for (int i = 0; i < chunkTotal; i++) {
            //所请求的分块文件在minio的路径
            String chunkFilePath = chunkFolder + i;
            //创建临时的分块文件
            File tempChunk = null;
            try {
                tempChunk = File.createTempFile("chunk", null);
            } catch (Exception e) {
                log.error("创建临时分块出错 : {}", e.getMessage());
                XueChengException.cast("创建临时分块出错");
            }
            //去minio查询是否存在所要下载的分块文件
            tempChunk = downloadFileFromMinIO(tempChunk, videoBucket, chunkFilePath);
            resultFiles[i] = tempChunk;
        }
        return resultFiles;
    }

    /**
     * @param file       下载的文件
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return: java.io.File
     * @decription 根据桶名和对象名从minio下载一个文件
     * @date 2023/2/8 14:46
     */
    private File downloadFileFromMinIO(File file, String bucketName, String objectName) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
        try (
                InputStream inputStream = minioClient.getObject(objectArgs);
                FileOutputStream fileOutputStream = new FileOutputStream(file)
        ) {
            IOUtils.copy(inputStream, fileOutputStream);
            return file;
        } catch (Exception e) {
            log.warn("查询分块文件{}出错 : {}", file.getName(), e.getMessage());
            XueChengException.cast("查询分块文件出错");
        }
        return null;
    }
}

