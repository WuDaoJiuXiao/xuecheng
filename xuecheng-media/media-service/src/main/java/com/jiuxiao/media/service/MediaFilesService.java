package com.jiuxiao.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.media.module.dto.QueryMediaParamsDto;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.module.dto.UploadFileResultDto;
import com.jiuxiao.media.module.po.MediaFiles;

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
}
