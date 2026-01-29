package com.xxl.job.admin.controller.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobTaskGroup;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.service.XxlJobTaskGroupService;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 任务组Controller
 * 
 * @author liwei
 */
@Controller
@RequestMapping("/taskgroup")
public class JobTaskGroupController {

    @Autowired
    private XxlJobTaskGroupService taskGroupService;

    @Autowired
    private XxlJobGroupMapper xxlJobGroupMapper;

    /**
     * 任务组管理页面
     */
    @RequestMapping
    public String index(HttpServletRequest request, Model model) {
        // 执行器列表
        List<XxlJobGroup> jobGroupListTotal = xxlJobGroupMapper.findAll();

        // filter group
        List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
        if (CollectionTool.isEmpty(jobGroupList)) {
            throw new XxlJobException("不存在有效执行器,请联系管理员");
        }

        model.addAttribute("JobGroupList", jobGroupList);

        return "biz/taskgroup.list";
    }

    /**
     * 分页查询任务组列表
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public Response<PageModel<XxlJobTaskGroup>> pageList(@RequestParam("jobGroupId") int jobGroupId,
                                                          @RequestParam(required = false) String groupName,
                                                          @RequestParam(required = false, defaultValue = "0") int offset,
                                                          @RequestParam(required = false, defaultValue = "10") int pagesize) {
        PageModel<XxlJobTaskGroup> pageModel = taskGroupService.pageList(jobGroupId, groupName, offset, pagesize);
        return Response.ofSuccess(pageModel);
    }

    /**
     * 查询任务组列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Response<List<XxlJobTaskGroup>> list(@RequestParam("jobGroupId") int jobGroupId) {
        List<XxlJobTaskGroup> list = taskGroupService.findByJobGroupId(jobGroupId);
        return Response.ofSuccess(list);
    }

    /**
     * 根据ID查询任务组
     */
    @RequestMapping("/get")
    @ResponseBody
    public Response<XxlJobTaskGroup> get(@RequestParam("id") int id) {
        XxlJobTaskGroup taskGroup = taskGroupService.findById(id);
        return taskGroup != null ? Response.ofSuccess(taskGroup) : Response.ofFail("任务组不存在");
    }

    /**
     * 新增任务组
     */
    @RequestMapping("/add")
    @ResponseBody
    public Response<String> add(XxlJobTaskGroup taskGroup) {
        return taskGroupService.add(taskGroup);
    }

    /**
     * 更新任务组
     */
    @RequestMapping("/update")
    @ResponseBody
    public Response<String> update(XxlJobTaskGroup taskGroup) {
        return taskGroupService.update(taskGroup);
    }

    /**
     * 删除任务组
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Response<String> delete(@RequestParam("id") int id) {
        return taskGroupService.delete(id);
    }

    /**
     * 执行任务组
     */
    @RequestMapping("/execute")
    @ResponseBody
    public Response<String> execute(@RequestParam("id") int id) {
        return taskGroupService.executeTaskGroup(id);
    }
}
