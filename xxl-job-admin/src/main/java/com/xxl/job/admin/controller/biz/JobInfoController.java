package com.xxl.job.admin.controller.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobPythonMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.python.XxlJobPython;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupMapper xxlJobGroupMapper;
	@Resource
	private XxlJobService xxlJobService;
	@Resource
	private XxlJobPythonMapper xxlJobPythonMapper;
	
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(value = "jobGroup", required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// 调度类型
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略

		// 执行器列表
		List<XxlJobGroup> jobGroupListTotal =  xxlJobGroupMapper.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
		if (CollectionTool.isEmpty(jobGroupList)) {
			throw new XxlJobException("不存在有效执行器,请联系管理员");
		}

		// parse jobGroup
		if (!(CollectionTool.isNotEmpty(jobGroupList)
				&& jobGroupList.stream().map(XxlJobGroup::getId).toList().contains(jobGroup))) {
			jobGroup = -1;
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		List<XxlJobPython> pythonList = xxlJobPythonMapper.findAll();
		model.addAttribute("pythonList", pythonList);

		return "biz/job.list";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Response<PageModel<XxlJobInfo>> pageList(HttpServletRequest request,
													@RequestParam(required = false, defaultValue = "0") int offset,
													@RequestParam(required = false, defaultValue = "10") int pagesize,
													@RequestParam int jobGroup,
													@RequestParam int triggerStatus,
													@RequestParam String jobDesc,
													@RequestParam String executorHandler,
													@RequestParam String author,
													@RequestParam(required = false) Integer taskGroupId) {

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// page
		return xxlJobService.pageList(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author, taskGroupId);
	}
	
	@RequestMapping("/insert")
	@ResponseBody
	public Response<String> add(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

		// opt
		return xxlJobService.add(jobInfo, loginInfo);
	}

	@RequestMapping("/update")
	@ResponseBody
	public Response<String> update(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

		// opt
		return xxlJobService.update(jobInfo, loginInfo);
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Response<String> delete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {

		// valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			return Response.ofFail("请选择" + "一条" + "数据");
		}

		// invoke
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.remove(ids.get(0), loginInfoResponse.getData());
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	public Response<String> pause(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {

		// valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			return Response.ofFail("请选择" + "一条" + "数据");
		}

		// invoke
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.stop(ids.get(0), loginInfoResponse.getData());
	}
	
	@RequestMapping("/start")
	@ResponseBody
	public Response<String> start(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {

		// valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			return Response.ofFail("请选择" + "一条" + "数据");
		}

		// invoke
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.start(ids.get(0), loginInfoResponse.getData());
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public Response<String> triggerJob(HttpServletRequest request,
									  @RequestParam("id") int id,
									  @RequestParam("executorParam") String executorParam,
									  @RequestParam("addressList") String addressList) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.trigger(loginInfoResponse.getData(), id, executorParam, addressList);
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public Response<List<String>> nextTriggerTime(@RequestParam("scheduleType") String scheduleType,
												 @RequestParam("scheduleConf") String scheduleConf) {

		// valid
		if (StringTool.isBlank(scheduleType) || StringTool.isBlank(scheduleConf)) {
			return Response.ofSuccess(new ArrayList<>());
		}

		// param
		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		// generate
		List<String> result = new ArrayList<>();
		try {
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {

				// generate next trigger time
				ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(paramXxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
				lastTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(paramXxlJobInfo, lastTime);

				// collect data
				if (lastTime != null) {
					result.add(DateTool.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(">>>>>>>>>>> nextTriggerTime error. scheduleType = {}, scheduleConf= {}, error:{} ", scheduleType, scheduleConf, e.getMessage());
			return Response.ofFail(("调度类型"+"非法") + e.getMessage());
		}
		return Response.ofSuccess(result);

	}

}
