package com.jiuxiao.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.base.module.RestResponse;
import com.jiuxiao.media.module.dto.QueryMediaParamsDto;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.module.dto.UploadFileResultDto;
import com.jiuxiao.media.module.po.MediaFiles;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
public interface MediaFilesService extends IService<MediaFiles> {
    /**
     * @param companyId           机构ID
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.media.module.po.MediaFiles>
     * @decription 媒资文件查询方法
     * @date 2023/2/1 16:25
     */
    PageResult<MediaFiles> queryMediaFile(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

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
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

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
    MediaFiles insertFileInfoToDB(Long companyId, String fileId, UploadFileParamsDto dto, String bucket, String objectName);

    /**
     * @param id 要移除的文件ID
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.media.module.po.MediaFiles>
     * @decription 根据ID移除文件
     * @date 2023/2/7 11:25
     */
    void deleteMedia(String id);

    /**
     * @param fileMd5 文件的MD5值
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 检查待上传的文件
     * @date 2023/2/8 10:33
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @param fileMd5 文件的MD5值
     * @param chunk   分块的序号
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 检查待上传的分块文件
     * @date 2023/2/8 10:34
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    /**
     * @param fileMd5 文件MD5值
     * @param chunk   分块序号
     * @param bytes   文件的字节数组
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 上传分块文件
     * @date 2023/2/8 11:09
     */
    RestResponse<Boolean> uploadChunk(String fileMd5, int chunk, byte[] bytes);

    /**
     * @param companyId  机构ID
     * @param fileMd5    文件MD5值
     * @param chunkTotal 分块总数量
     * @param dto        文件上传参数DTO
     * @return: com.jiuxiao.base.module.RestResponse<java.lang.Boolean>
     * @decription 合并分块文件
     * @date 2023/2/8 11:10
     */
    RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto dto) throws IOException;

    /**
     * @param id
     * @return: com.jiuxiao.media.module.po.MediaFiles
     * @decription 根据文件ID获得文件
     * @date 2023/2/10 15:08
     */
    MediaFiles getFileById(String id);

    /**
     * @param file       下载的文件
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return: java.io.File
     * @decription 根据桶名和对象名从minio下载一个文件
     * @date 2023/2/8 14:46
     */
    File downloadFileFromMinIO(File file, String bucketName, String objectName);

    /**
     * @param filePath   文件在服务器本地的存储路径
     * @param bucketName 上传后存储的桶名称
     * @param objectName 文件对象名称
     * @return: void
     * @decription 上传文件到MINIO（大文件）
     * @date 2023/2/8 15:25
     */
    void uploadFileToMinIO(String filePath, String bucketName, String objectName);
}
