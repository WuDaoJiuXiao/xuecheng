package com.jiuxiao.media.service.job;

import com.jiuxiao.base.utils.Mp4VideoUtil;
import com.jiuxiao.media.module.po.MediaProcess;
import com.jiuxiao.media.service.MediaFilesService;
import com.jiuxiao.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频转码任务
 * @Author: 悟道九霄
 * @Date: 2023年02月10日 16:14
 * @Version: 1.0.0
 */
@Slf4j
@Component
public class VideoTranscodeTask {

    @Resource
    private MediaProcessService mediaProcessService;

    @Resource
    private MediaFilesService mediaFilesService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegPath;

    /**
     * @return: void
     * @decription 视频转码任务
     * @date 2023/2/13 15:25
     */
    @XxlJob("VideoCodeHandler")
    public void videoCodeHandler() throws Exception {
        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();

        //查询待处理的任务列表，任务核心数与CPU核心数一致
        List<MediaProcess> processList = mediaProcessService.selectListByShardInfo(shardTotal, shardIndex, 8);
        if (null == processList) {
            log.info("查询到的待处理任务数量为零");
            return;
        }

        //启动多线程去处理任务
        int size = processList.size();  //要进行处理的总任务数量
        CountDownLatch countDownLatch = new CountDownLatch(size);   //任务计数器
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        //将每个任务放入线程池
        processList.forEach(mediaProcess -> {
            threadPool.execute(() -> { //每个任务的执行逻辑
                //若该视频状态为 “已处理”，则不需要再次处理（幂等性）
                String status = mediaProcess.getStatus();
                if (status.equals("2")) {
                    log.info("该视频任务已经处理，无需再次处理，视频信息 : {}", mediaProcess.toString());
                    countDownLatch.countDown();
                    return;
                }

                //获取待处理视频的信息，然后从MINIO下载到本地
                String bucket = mediaProcess.getBucket();
                String filePath = mediaProcess.getFilePath();
                String fileId = mediaProcess.getFileId();
                File originFile = null;
                File mp4File = null;
                try {
                    originFile = File.createTempFile("origin", null);
                    mp4File = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("处理视频前春创建临时文件失败");
                    countDownLatch.countDown();
                    return;
                }
                try {
                    mediaFilesService.downloadFileFromMinIO(originFile, bucket, filePath);
                } catch (Exception e) {
                    log.error("下载视频文件 {} 失败 : {}", mediaProcess.getFilename(), e.getMessage());
                    countDownLatch.countDown();
                    return;
                }

                //调用视频转码工具类转换视频格式
                String mp4Name = fileId + ".mp4";
                String mp4Path = mp4File.getAbsolutePath();
                String originFilePath = originFile.getAbsolutePath();
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, originFilePath, mp4Name, mp4Path);
                //成功会返回 success，失败会返回失败信息
                String result = videoUtil.generateMp4();
                String newStatus = "3"; //最终的任务状态
                String newUrl = null;   //最终的视频访问路径
                if (result.equals("success")) { //转换成功，上传到MINIO
                    String objectName = getFilePathByMd5(fileId, ".mp4");
                    try {
                        mediaFilesService.uploadFileToMinIO(mp4Path, bucket, objectName);
                    } catch (Exception e) {
                        log.error("上传文件 {} 到MINIO失败 : {}", mp4File.getName(), e.getMessage());
                        countDownLatch.countDown();
                        return;
                    }
                    newStatus = "2";    //处理成功的状态
                    newUrl = "/" + bucket + "/" + objectName;
                }
                //不管任务成功与否，都要记录任务处理结果
                try {
                    mediaProcessService.saveProcessFinishStatus(
                            mediaProcess.getId(), newStatus, fileId, newUrl, result
                    );
                }catch (Exception e){
                    log.error("保存视频 {} 处理结果失败 : {}",mediaProcess.getFilename(), e.getMessage());
                    countDownLatch.countDown();
                    return;
                }
                //每处理完一个任务，计数器减一。可是有个问题：假设上面的某个环节出错了，抛出了异常，那么：
                // 下面的计数器就不会减一、计数器也就永远不会归零、线程也就会一致阻塞下去
                // 所以在每次出错并且 return 之前，就要让计时器及时减一
                countDownLatch.countDown();
            });
        });

        //阻塞线程直到任务完成，即当 countDownLatch 计数器归零，线程阻塞才会解除
        //虽然上面进行了计数器有可能不会归零的处理，但是以防万一，这里应该再设置一个超时时间，防止出现某些意外导致线程无限期阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    /**
     * @param fileMd5       文件MD5值
     * @param extensionName 文件扩展名
     * @return: java.lang.String
     * @decription 根据文件md5值及扩展名拿到文件在MINIO的路径
     * @date 2023/2/13 16:01
     */
    public String getFilePathByMd5(String fileMd5, String extensionName) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + extensionName;
    }

}