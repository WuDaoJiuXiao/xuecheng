package com.jiuxiao.media.controller;


import com.jiuxiao.base.module.RestResponse;
import com.jiuxiao.media.module.dto.UploadFileParamsDto;
import com.jiuxiao.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 大文件上传 前端控制器
 * </p>
 * @author 悟道九霄
 * @since 2023-02-08
 */
@RestController
@Api(value = "大文件上传接口", tags = "大文件上传接口")
public class BigFilesController {

    @Resource
    private MediaFilesService mediaFilesService;

    @ApiOperation("检查待上传的文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        return mediaFilesService.checkFile(fileMd5);
    }

    @ApiOperation("检查待上传的分块文件")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFilesService.checkChunk(fileMd5, chunk);
    }

    @ApiOperation("上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        return mediaFilesService.uploadChunk(fileMd5, chunk, file.getBytes());
    }

    @ApiOperation("合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1L;
        UploadFileParamsDto dto = new UploadFileParamsDto();
        dto.setFilename(fileName);
        dto.setFileType("001002");
        dto.setTags("测试视频");
        return mediaFilesService.mergeChunks(companyId, fileMd5, chunkTotal, dto);
    }
}
