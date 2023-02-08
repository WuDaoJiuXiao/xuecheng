package com.jiuxiao.media.controller;


import com.jiuxiao.base.exception.XueChengException;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.media.module.dto.QueryMediaParamsDto;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.module.dto.UploadFileResultDto;
import com.jiuxiao.media.module.po.MediaFiles;
import com.jiuxiao.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 媒资信息 前端控制器
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
@RestController
@Api(value = "媒资管理接口", tags = "媒资管理接口")
public class MediaFilesController {

    @Resource
    private MediaFilesService mediaFilesService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFilesService.queryMediaFile(companyId, pageParams, queryMediaParamsDto);
    }

    @ApiOperation("上传课程文件")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
                                      @RequestParam(value = "folder", required = false) String folder,
                                      @RequestParam(value = "objectName", required = false) String objectName) {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();

        String contentType = filedata.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        uploadFileParamsDto.setFileSize(filedata.getSize());//文件大小
        assert contentType != null;
        if (contentType.contains("image")) {
            //是个图片
            uploadFileParamsDto.setFileType("001001");
        } else {
            uploadFileParamsDto.setFileType("001003");
        }
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());

        UploadFileResultDto uploadFileResultDto = null;
        try {
            uploadFileResultDto = mediaFilesService.uploadFile(companyId, uploadFileParamsDto, filedata.getBytes(), folder, objectName);
        } catch (Exception e) {
            XueChengException.cast("上传文件过程中出错");
        }

        return uploadFileResultDto;
    }

    @ApiOperation("根据ID移除文件")
    @DeleteMapping("/{id}")
    public void deleteMedia(@PathVariable String id){
        mediaFilesService.deleteMedia(id);
    }

}
