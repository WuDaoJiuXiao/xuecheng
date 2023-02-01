package com.jiuxiao.media.module.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 查询媒资文件请求DTO
 * @Author: 悟道九霄
 * @Date: 2023年02月01日 16:11
 * @Version: 1.0.0
 */
@Data
@ToString
public class QueryMediaParamsDto {

    @ApiModelProperty("媒资文件名称")
    private String filename;

    @ApiModelProperty("媒资类型")
    private String fileType;

    @ApiModelProperty("审核状态")
    private String auditStatus;

}