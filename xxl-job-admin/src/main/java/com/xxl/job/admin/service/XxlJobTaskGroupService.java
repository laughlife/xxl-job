package com.xxl.job.admin.service;

import java.util.List;

import com.xxl.job.admin.model.XxlJobTaskGroup;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

/**
 * 任务组Service
 * 
 * @author liwei
 */
public interface XxlJobTaskGroupService {

    /**
     * 分页查询任务组列表
     */
    PageModel<XxlJobTaskGroup> pageList(int jobGroupId, String groupName, int offset, int pagesize);

    /**
     * 查询执行器下的所有任务组
     */
    List<XxlJobTaskGroup> findByJobGroupId(int jobGroupId);

    /**
     * 根据ID查询任务组
     */
    XxlJobTaskGroup findById(int id);

    /**
     * 新增任务组
     */
    Response<String> add(XxlJobTaskGroup taskGroup);

    /**
     * 更新任务组
     */
    Response<String> update(XxlJobTaskGroup taskGroup);

    /**
     * 删除任务组
     */
    Response<String> delete(int id);

    /**
     * 批量执行任务组内的任务（按顺序）
     */
    Response<String> executeTaskGroup(int taskGroupId);
}
