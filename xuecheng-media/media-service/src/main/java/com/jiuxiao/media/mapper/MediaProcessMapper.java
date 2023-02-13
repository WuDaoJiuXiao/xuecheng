package com.jiuxiao.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuxiao.media.module.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 * @author 悟道九霄
 * @since 2023-02-01
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * @param shardTotal 分片任务总数
     * @param shardIndex 分片任务索引
     * @param count      总线程数
     * @return: java.util.List<com.jiuxiao.media.module.po.MediaProcess>
     * @decription 根据分片任务信息查询待处理任务列表
     * @date 2023/2/13 14:33
     */
    @Select("select * from xuecheng_media.media_process where id % #{shardTotal} = #{shardIndex} limit #{count}")
    List<MediaProcess> selectListByShardInfo(
            @Param("shardTotal") int shardTotal,
            @Param("shardIndex") int shardIndex,
            @Param("count") int count
    );
}
