package com.jiuxiao.media.module.dto;

import com.jiuxiao.media.module.po.MediaFiles;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传普通文件返回结构DTO
 * @Author: 悟道九霄
 * @Date: 2023年02月01日 16:15
 * @Version: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UploadFileResultDto extends MediaFiles {

}