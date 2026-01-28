package com.xxl.job.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobTaskGroupMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobTaskGroup;
import com.xxl.job.admin.service.XxlJobTaskGroupService;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

/**
 * 任务组Service实现
 * 
 * @author liwei
 */
@Service
public class XxlJobTaskGroupServiceImpl implements XxlJobTaskGroupService {

    @Autowired
    private XxlJobTaskGroupMapper taskGroupMapper;

    @Autowired
    private XxlJobInfoMapper jobInfoMapper;

    @Override
    public PageModel<XxlJobTaskGroup> pageList(int jobGroupId, String groupName, int offset, int pagesize) {
        List<XxlJobTaskGroup> list = taskGroupMapper.pageList(jobGroupId, groupName, offset, pagesize);
        int total = taskGroupMapper.pageListCount(jobGroupId, groupName);
        
        PageModel<XxlJobTaskGroup> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(total);
        return pageModel;
    }

    @Override
    public List<XxlJobTaskGroup> findByJobGroupId(int jobGroupId) {
        return taskGroupMapper.findByJobGroupId(jobGroupId);
    }

    @Override
    public XxlJobTaskGroup findById(int id) {
        return taskGroupMapper.findById(id);
    }

    @Override
    public Response<String> add(XxlJobTaskGroup taskGroup) {
        // 参数校验
        if (taskGroup.getJobGroupId() <= 0) {
            return Response.ofFail("执行器ID不能为空");
        }
        if (StringTool.isBlank(taskGroup.getGroupName())) {
            return Response.ofFail("任务组名称不能为空");
        }

        // 保存
        int ret = taskGroupMapper.insert(taskGroup);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail("新增任务组失败");
    }

    @Override
    public Response<String> update(XxlJobTaskGroup taskGroup) {
        // 参数校验
        if (taskGroup.getId() <= 0) {
            return Response.ofFail("任务组ID不能为空");
        }
        if (StringTool.isBlank(taskGroup.getGroupName())) {
            return Response.ofFail("任务组名称不能为空");
        }

        // 更新
        int ret = taskGroupMapper.update(taskGroup);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail("更新任务组失败");
    }

    @Override
    public Response<String> delete(int id) {
        // 检查是否有关联任务
        int count = taskGroupMapper.countJobsByTaskGroupId(id);
        if (count > 0) {
            return Response.ofFail("该任务组下还有" + count + "个任务，无法删除");
        }

        // 删除
        int ret = taskGroupMapper.delete(id);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail("删除任务组失败");
    }

    @Override
    public Response<String> executeTaskGroup(int taskGroupId) {
        // 查询任务组下的所有任务，按 task_order 排序
        List<XxlJobInfo> jobs = jobInfoMapper.pageList(0, 1000, 0, -1, null, null, null, taskGroupId);

        if (jobs == null || jobs.isEmpty()) {
            return Response.ofFail("任务组下没有任务");
        }

        // 过滤出属于该任务组的任务并按task_order排序
        jobs.sort((a, b) -> Integer.compare(a.getTaskOrder(), b.getTaskOrder()));

        // 使用子任务链式执行
        // 将任务按顺序设置为子任务关系：A.childJobId=B, B.childJobId=C, ...
        for (int i = 0; i < jobs.size() - 1; i++) {
            XxlJobInfo currentJob = jobs.get(i);
            XxlJobInfo nextJob = jobs.get(i + 1);
            currentJob.setChildJobId(String.valueOf(nextJob.getId()));
            jobInfoMapper.update(currentJob);
        }

        // 清空最后一个任务的子任务ID
        XxlJobInfo lastJob = jobs.get(jobs.size() - 1);
        lastJob.setChildJobId("");
        jobInfoMapper.update(lastJob);

        return Response.ofSuccess("任务组已配置为顺序执行，请手动触发第一个任务：" + jobs.get(0).getJobDesc());
    }
}
