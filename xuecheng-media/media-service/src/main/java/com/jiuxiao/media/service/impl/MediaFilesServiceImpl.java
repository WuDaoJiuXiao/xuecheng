package com.jiuxiao.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.jiuxiao.base.exception.XueChengException;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.base.utils.AssertUtils;
import com.jiuxiao.media.mapper.MediaFilesMapper;
import com.jiuxiao.media.module.dto.QueryMediaParamsDto;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.module.dto.UploadFileResultDto;
import com.jiuxiao.media.module.po.MediaFiles;
import com.jiuxiao.media.service.MediaFilesService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
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
     * @param bytes      文件的字节数组
     * @param bucketName 上传后存储的桶名称
     * @param objectName 文件对象名称
     * @return: void
     * @decription 上传文件到MINIO
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
     * @param id     要移除的文件ID
     * @param params 分页参数
     * @param dto    媒资查询DTO
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
}
