package com.xxl.job.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xxl.job.admin.model.XxlJobTaskGroup;

/**
 * 任务组Mapper
 * 
 * @author liwei
 */
@Mapper
public interface XxlJobTaskGroupMapper {

    /**
     * 查询执行器下的所有任务组
     */
    List<XxlJobTaskGroup> findByJobGroupId(@Param("jobGroupId") int jobGroupId);

    /**
     * 根据ID查询任务组
     */
    XxlJobTaskGroup findById(@Param("id") int id);

    /**
     * 新增任务组
     */
    int insert(XxlJobTaskGroup taskGroup);

    /**
     * 更新任务组
     */
    int update(XxlJobTaskGroup taskGroup);

    /**
     * 删除任务组
     */
    int delete(@Param("id") int id);

    /**
     * 查询任务组下的任务数量
     */
    int countJobsByTaskGroupId(@Param("taskGroupId") int taskGroupId);
}
