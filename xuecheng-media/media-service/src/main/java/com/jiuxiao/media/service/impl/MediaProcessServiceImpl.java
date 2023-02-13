package com.jiuxiao.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiuxiao.media.mapper.MediaFilesMapper;
import com.jiuxiao.media.mapper.MediaProcessHistoryMapper;
import com.jiuxiao.media.mapper.MediaProcessMapper;
import com.jiuxiao.media.module.po.MediaFiles;
import com.jiuxiao.media.module.po.MediaProcess;
import com.jiuxiao.media.module.po.MediaProcessHistory;
import com.jiuxiao.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
@Slf4j
@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    @Resource
    private MediaProcessMapper mediaProcessMapper;

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    @Resource
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    /**
     * @param shardTotal 分片任务总数
     * @param shardIndex 分片任务索引
     * @param count      总线程数
     * @return: java.util.List<com.jiuxiao.media.module.po.MediaProcess>
     * @decription 根据分片任务信息查询待处理任务列表
     * @date 2023/2/13 14:33
     */
    @Override
    public List<MediaProcess> selectListByShardInfo(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selectListByShardInfo(shardTotal, shardIndex, count);
    }

    /**
     * @param taskId   任务ID
     * @param status   任务状态（待处理、已处理、处理失败）
     * @param fieldId  处理的文件ID
     * @param url      文件MINIO存储路径
     * @param errorMsg 错误信息
     * @return: void
     * @decription 存储任务处理结果信息
     * @date 2023/2/13 14:42
     */
    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fieldId, String url, String errorMsg) {
        //查询该任务是否存在
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (null == mediaProcess) {
            log.info("所查询的任务不存在,taskId = {}", taskId);
            return;
        }
        //若任务存在但是任务失败，则更新该任务的失败信息
        LambdaQueryWrapper<MediaProcess> wrapper = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if (status.equals("3")) {
            MediaProcess process = new MediaProcess();
            process.setStatus("3");
            process.setErrormsg(errorMsg.length() > 1024 ? errorMsg.substring(0, 1024) : errorMsg);
            mediaProcessMapper.update(process, wrapper);
            return;
        }
        // 若任务处理成功，则需要：
        //      1. 更新待处理任务表 media_process 中的任务状态以及url等信息
        //      2. 将处理完成的任务信息插入到 media_process_history 历史任务表中
        //      3. 将任务信息从待处理任务表 media_process 中删除
        if (status.equals("2")) {
            //更新任务表 media_process
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);
            //更新文件表中的url
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fieldId);
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        mediaProcessMapper.deleteById(taskId);
    }
}
