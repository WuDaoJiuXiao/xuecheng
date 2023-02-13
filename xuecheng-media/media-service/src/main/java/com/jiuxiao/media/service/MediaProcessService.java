package com.jiuxiao.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiuxiao.media.module.po.MediaProcess;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
public interface MediaProcessService extends IService<MediaProcess> {

    /**
     * @param shardTotal 分片任务总数
     * @param shardIndex 分片任务索引
     * @param count      总线程数
     * @return: java.util.List<com.jiuxiao.media.module.po.MediaProcess>
     * @decription 根据分片任务信息查询待处理任务列表
     * @date 2023/2/13 14:33
     */
    List<MediaProcess> selectListByShardInfo(int shardTotal, int shardIndex, int count);

    /**
     * @param taskId   任务ID
     * @param status   任务状态（待处理、已处理、处理失败）
     * @param fieldId  处理的文件ID
     * @param url      文件MINIO存储路径
     * @param errorMsg 错误信息
     * @return: void
     * @decription 存储文集爱你处理结果信息
     * @date 2023/2/13 14:42
     */
    void saveProcessFinishStatus(Long taskId, String status, String fieldId, String url, String errorMsg);
}
