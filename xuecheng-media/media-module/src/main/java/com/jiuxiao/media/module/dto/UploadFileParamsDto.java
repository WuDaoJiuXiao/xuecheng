package com.jiuxiao.media.module.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 上传普通文件请求参数DTO
 * @Author: 悟道九霄
 * @Date: 2023年02月01日 16:14
 * @Version: 1.0.0
 */
@Data
@ToString
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件content-type
     */
    private String contentType;

    /**
     * 文件类型（文档，音频，视频）
     */
    private String fileType;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private String username;

    /**
     * 备注
     */
    private String remark;
}