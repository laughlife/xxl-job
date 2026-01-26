<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
	<#import "../common/common.macro.ftl" as netCommon>

	<!-- 1-style start -->
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.css">
	<link href="https://cdn.jsdelivr.net/npm/tom-select@2.4.3/dist/css/tom-select.bootstrap4.min.css" rel="stylesheet">
	<!-- 1-style end -->

</head>
<body class="hold-transition" style="background-color: #ecf0f5;">
<div class="wrapper">
	<section class="content">

		<!-- 2-content start -->

		<#-- 查询区域 -->
		<div class="box" style="margin-bottom:9px;">
			<div class="box-body">
				<div class="row" id="data_filter" >

					<div class="col-3">
						<div class="input-group">
							<span class="input-group-addon">执行器</span>
							<select class="form-control" id="jobGroup" >
								<#list JobGroupList as group>
									<option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
								</#list>
							</select>
						</div>
					</div>
					<div class="col-1">
						<div class="input-group">
							<select class="form-control" id="triggerStatus" >
								<option value="-1" >全部</option>
								<option value="0" >停止</option>
								<option value="1" >启动</option>
							</select>
						</div>
					</div>
					<div class="col-2">
						<div class="input-group">
							<input type="text" class="form-control" id="jobDesc" placeholder="请输入任务描述" >
						</div>
					</div>
					<div class="col-2">
						<div class="input-group">
							<input type="text" class="form-control" id="executorHandler" placeholder="请输入JobHandler" >
						</div>
					</div>
					<div class="col-2">
						<div class="input-group">
							<input type="text" class="form-control" id="author" placeholder="请输入负责人" >
						</div>
					</div>

					<div class="col-1">
						<button class="btn btn-block btn-primary searchBtn" >搜索</button>
					</div>
					<div class="col-1">
						<button class="btn btn-block btn-secondary resetBtn" >重置</button>
					</div>
				</div>
			</div>
		</div>

		<#-- 数据表格区域 -->
		<div class="row">
			<div class="col-12">
				<div class="box">
					<div class="box-header pull-left" id="data_operation" >
						<button class="btn btn-sm btn-info add" type="button"><i class="fa fa-plus" ></i>新增</button>                        <#-- add -->
						<button class="btn btn-sm btn-warning selectOnlyOne update" type="button"><i class="fa fa-edit"></i>编辑</button>    <#-- update -->
                        <button class="btn btn-sm btn-warning selectOnlyOne glue_ide" type="button">GLUE IDE</button>									        <#-- GLUE IDE：'BEAN' != row.glueType -->
						<button class="btn btn-sm btn-danger selectOnlyOne delete" type="button"><i class="fa fa-remove "></i>删除</button>   <#-- delete -->
						｜
						<button class="btn btn-sm btn-secondary selectOnlyOne job_copy" type="button">复制</button>
						<button class="btn btn-sm btn-warning selectOnlyOne job_resume" type="button">启动</button>				<#-- 启动 -->
						<button class="btn btn-sm btn-warning selectOnlyOne job_pause" type="button">停止</button>					<#-- 停止 -->
						｜
						<button class="btn btn-sm btn-primary selectOnlyOne job_trigger" type="button">执行一次</button>					<#-- 执行一次 -->
						<button class="btn btn-sm btn-primary selectOnlyOne job_log" type="button">查询日志</button>						<#-- 执行日志：base_url +'/joblog?jobId='+ row.id -->
						<button class="btn btn-sm btn-secondary selectOnlyOne job_registryinfo" type="button">注册节点</button>	<#-- 注册节点 -->
						<button class="btn btn-sm btn-secondary selectOnlyOne job_next_time" type="button">下次执行时间</button>			<#-- 下次执行时间：row.scheduleType == 'CRON' || row.scheduleType == 'FIX_RATE' -->
					</div>
					<div class="box-body" >
						<table id="data_list" class="table table-bordered table-striped" width="100%" >
							<thead></thead>
							<tbody></tbody>
							<tfoot></tfoot>
						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- job新增.模态框 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >新增</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >

							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">基础配置</p>    <#-- 基础信息 -->
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">执行器<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="jobGroup" >
										<#list JobGroupList as group>
											<option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
										</#list>
									</select>
								</div>

								<label for="lastname" class="col-sm-2 col-form-label">任务描述<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="请输入任务描述" maxlength="50" ></div>
							</div>
							<div class="row mb-3">
								<label for="lastname" class="col-sm-2 col-form-label">负责人<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="请输入负责人" maxlength="50" ></div>
								<label for="lastname" class="col-sm-2 col-form-label">报警邮件<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="请输入报警邮件，多个邮件地址则逗号分隔" maxlength="100" ></div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">调度配置</p>    <#-- 调度 -->
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">调度类型<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control scheduleType" name="scheduleType" >
										<#list ScheduleTypeEnum as item>
											<option value="${item}" <#if 'CRON' == item >selected</#if> >${item.title}</option>
										</#list>
									</select>
								</div>

								<input type="hidden" name="scheduleConf" />
								<div class="schedule_conf schedule_conf_NONE" style="display: none" >
								</div>
								<div class="schedule_conf schedule_conf_CRON" style="display: contents" >
									<label for="lastname" class="col-sm-2 col-form-label">Cron<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_CRON" placeholder="请输入Cron" maxlength="128" ></div>
								</div>
								<div class="schedule_conf schedule_conf_FIX_RATE" style="display: none" >
									<label for="lastname" class="col-sm-2 col-form-label">固定速度<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_FIX_RATE" placeholder="请输入 （ Second ）" maxlength="10" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								</div>
								<div class="schedule_conf schedule_conf_FIX_DELAY" style="display: none" >
									<label for="lastname" class="col-sm-2 col-form-label">固定延迟<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_FIX_DELAY" placeholder="请输入 （ Second ）" maxlength="10" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								</div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">任务配置</p>    <#-- 任务配置 -->

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">运行模式<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control glueType" name="glueType" >
										<#list GlueTypeEnum as item>
											<option value="${item}" >${item.desc}</option>
										</#list>
									</select>
								</div>
								<label for="firstname" class="col-sm-2 col-form-label">JobHandler<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="请输入JobHandler" maxlength="100" ></div>
							</div>

							<div class="row mb-3 python-version-row" style="display:none">
								<label for="firstname" class="col-sm-2 col-form-label">Python版本<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="pythonId">
										<option value="">请选择Python版本</option>
										<#list pythonList![] as py>
											<option value="${py.id}">${py.version} (${py.name})</option>
										</#list>
									</select>
								</div>
								<label for="firstname" class="col-sm-2 col-form-label">&nbsp;</label>
								<div class="col-sm-4"></div>
							</div>

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">任务参数<font color="black">*</font></label>
								<div class="col-sm-10">
									<textarea class="textarea form-control" name="executorParam" placeholder="请输入任务参数" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
								</div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">高级配置</p>    <#-- 高级配置 -->

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">路由策略<font color="black">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="executorRouteStrategy" >
										<#list ExecutorRouteStrategyEnum as item>
											<option value="${item}" >${item.title}</option>
										</#list>
									</select>
								</div>

								<label for="lastname" class="col-sm-2 col-form-label">子任务ID<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="childJobId" placeholder="请输入子任务的任务ID,如存在多个则逗号分隔" maxlength="100" ></div>
							</div>

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">调度过期策略<font color="black">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="misfireStrategy" >
										<#list MisfireStrategyEnum as item>
											<option value="${item}" <#if 'DO_NOTHING' == item >selected</#if> >${item.title}</option>
										</#list>
									</select>
								</div>

								<label for="firstname" class="col-sm-2 col-form-label">阻塞处理策略<font color="black">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="executorBlockStrategy" >
										<#list ExecutorBlockStrategyEnum as item>
											<option value="${item}" >${item.title}</option>
										</#list>
									</select>
								</div>
							</div>

							<div class="row mb-3">
								<label for="lastname" class="col-sm-2 col-form-label">任务超时时间<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorTimeout" placeholder="任务超时时间，单位秒，大于零时生效" maxlength="6" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								<label for="lastname" class="col-sm-2 col-form-label">失败重试次数<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorFailRetryCount" placeholder="失败重试次数，大于零时生效" maxlength="4" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
							</div>

							<hr>
							<div class="row mb-3">
								<div class="offset-sm-3 col-sm-6">
									<button type="submit" class="btn btn-primary"  >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
								</div>
							</div>

<input type="hidden" name="glueRemark" value="GLUE代码初始化" >
<textarea name="glueSource" style="display:none;" ></textarea>
<textarea class="glueSource_java" style="display:none;" >
package com.xxl.job.service.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;

public class DemoGlueJobHandler extends IJobHandler {

	@Override
	public void execute() throws Exception {
		XxlJobHelper.log("XXL-JOB, Hello World.");
	}

}
</textarea>
<textarea class="glueSource_shell" style="display:none;" >
#!/bin/bash
echo "xxl-job: hello shell"

echo "脚本位置：$0"
echo "任务参数：$1"
echo "分片序号 = $2"
echo "分片总数 = $3"
<#--echo "参数数量：$#"
for param in $*
do
    echo "参数 : $param"
    sleep 1s
done-->

echo "Good bye!"
exit 0
</textarea>
<textarea class="glueSource_python" style="display:none;" >
#!/usr/bin/python
# -*- coding: UTF-8 -*-
import time
import sys

print("xxl-job: hello python")

print("脚本位置：", sys.argv[0])
print("任务参数：", sys.argv[1])
print("分片序号：", sys.argv[2])
print("分片总数：", sys.argv[3])

print("Good bye!")
exit(0)
</textarea>
<textarea class="glueSource_python2" style="display:none;" >
#!/usr/bin/python
# -*- coding: UTF-8 -*-
import time
import sys

print "xxl-job: hello python"

print "脚本位置：", sys.argv[0]
print "任务参数：", sys.argv[1]
print "分片序号：", sys.argv[2]
print "分片总数：", sys.argv[3]
<#--for i in range(1, len(sys.argv)):
	time.sleep(1)
	print "参数", i, sys.argv[i]-->

print "Good bye!"
exit(0)
<#--
import logging
logging.basicConfig(level=logging.DEBUG)
logging.info("脚本文件：" + sys.argv[0])
-->
</textarea>
<textarea class="glueSource_php" style="display:none;" >
<?php

    echo "xxl-job: hello php  \n";

    echo "脚本位置：$argv[0]  \n";
    echo "任务参数：$argv[1]  \n";
    echo "分片序号 = $argv[2]  \n";
    echo "分片总数 = $argv[3]  \n";

    echo "Good bye!  \n";
    exit(0);

?>
</textarea>
<textarea class="glueSource_nodejs" style="display:none;" >
#!/usr/bin/env node
console.log("xxl-job: hello nodejs")

var arguments = process.argv

console.log("脚本位置: " + arguments[1])
console.log("任务参数: " + arguments[2])
console.log("分片序号: " + arguments[3])
console.log("分片总数: " + arguments[4])
<#--for (var i = 2; i < arguments.length; i++){
	console.log("参数 %s = %s", (i-1), arguments[i]);
}-->

console.log("Good bye!")
process.exit(0)
</textarea>
<textarea class="glueSource_powershell" style="display:none;" >
Write-Host "xxl-job: hello powershell"

Write-Host "脚本位置: " $MyInvocation.MyCommand.Definition
Write-Host "任务参数: "
	if ($args.Count -gt 2) { $args[0..($args.Count-3)] }
Write-Host "分片序号: " $args[$args.Count-2]
Write-Host "分片总数: " $args[$args.Count-1]

Write-Host "Good bye!"
exit 0
</textarea>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 更新.模态框 -->
		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >更新任务</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >

							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">基础配置</p>    <#-- 基础信息 -->
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">执行器<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="jobGroup" >
										<#list JobGroupList as group>
											<option value="${group.id}" >${group.title}</option>
										</#list>
									</select>
								</div>

								<label for="lastname" class="col-sm-2 col-form-label">任务描述<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="请输入任务描述" maxlength="50" ></div>
							</div>
							<div class="row mb-3">
								<label for="lastname" class="col-sm-2 col-form-label">负责人<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="请输入负责人" maxlength="50" ></div>
								<label for="lastname" class="col-sm-2 col-form-label">报警邮件<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="请输入报警邮件，多个邮件地址则逗号分隔" maxlength="100" ></div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">调度配置</p>    <#-- 调度配置 -->
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">调度类型<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control scheduleType" name="scheduleType" >
										<#list ScheduleTypeEnum as item>
											<option value="${item}" >${item.title}</option>
										</#list>
									</select>
								</div>

								<input type="hidden" name="scheduleConf" />
								<div class="schedule_conf schedule_conf_NONE" style="display: none" >
								</div>
								<div class="schedule_conf schedule_conf_CRON" style="display: contents" >
									<label for="lastname" class="col-sm-2 col-form-label">Cron<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_CRON" placeholder="请输入Cron" maxlength="128" ></div>
								</div>
								<div class="schedule_conf schedule_conf_FIX_RATE" style="display: none" >
									<label for="lastname" class="col-sm-2 col-form-label">固定速度<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_FIX_RATE" placeholder="请输入 （ Second ）" maxlength="10" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								</div>
								<div class="schedule_conf schedule_conf_FIX_DELAY" style="display: none" >
									<label for="lastname" class="col-sm-2 col-form-label">固定延迟<font color="red">*</font></label>
									<div class="col-sm-4"><input type="text" class="form-control" name="schedule_conf_FIX_DELAY" placeholder="请输入 （ Second ）" maxlength="10" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								</div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">任务配置</p>    <#-- 任务配置 -->

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">运行模式<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control glueType" name="glueType" disabled >
										<#list GlueTypeEnum as item>
											<option value="${item}" >${item.desc}</option>
										</#list>
									</select>
								</div>
								<label for="firstname" class="col-sm-2 col-form-label">JobHandler<font color="red">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="请输入JobHandler" maxlength="100" ></div>
							</div>

							<div class="row mb-3 python-version-row" style="display:none">
								<label for="firstname" class="col-sm-2 col-form-label">Python版本<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="pythonId">
										<option value="">请选择Python版本</option>
										<#list pythonList![] as py>
											<option value="${py.id}">${py.version} (${py.name})</option>
										</#list>
									</select>
								</div>
								<label for="firstname" class="col-sm-2 col-form-label">&nbsp;</label>
								<div class="col-sm-4"></div>
							</div>

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">任务参数<font color="black">*</font></label>
								<div class="col-sm-10">
									<textarea class="textarea form-control" name="executorParam" placeholder="请输入任务参数" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
								</div>
							</div>

							<br>
							<p style="margin: 0 0 10px;text-align: left;border-bottom: 1px solid #e5e5e5;color: gray;">高级配置</p>    <#-- 高级配置 -->

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">路由策略<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="executorRouteStrategy" >
										<#list ExecutorRouteStrategyEnum as item>
											<option value="${item}" >${item.title}</option>
										</#list>
									</select>
								</div>

								<label for="lastname" class="col-sm-2 col-form-label">子任务ID<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="childJobId" placeholder="请输入子任务的任务ID,如存在多个则逗号分隔" maxlength="100" ></div>
							</div>

							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">调度过期策略<font color="black">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="misfireStrategy" >
										<#list MisfireStrategyEnum as item>
											<option value="${item}" <#if 'DO_NOTHING' == item >selected</#if> >${item.title}</option>
										</#list>
									</select>
								</div>

								<label for="firstname" class="col-sm-2 col-form-label">阻塞处理策略<font color="red">*</font></label>
								<div class="col-sm-4">
									<select class="form-control" name="executorBlockStrategy" >
										<#list ExecutorBlockStrategyEnum as item>
											<option value="${item}" >${item.title}</option>
										</#list>
									</select>
								</div>
							</div>

							<div class="row mb-3">
								<label for="lastname" class="col-sm-2 col-form-label">任务超时时间<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorTimeout" placeholder="任务超时时间，单位秒，大于零时生效" maxlength="6" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
								<label for="lastname" class="col-sm-2 col-form-label">失败重试次数<font color="black">*</font></label>
								<div class="col-sm-4"><input type="text" class="form-control" name="executorFailRetryCount" placeholder="失败重试次数，大于零时生效" maxlength="4" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')" ></div>
							</div>

							<hr>
							<div class="row mb-3">
								<div class="offset-sm-3 col-sm-6">
									<button type="submit" class="btn btn-primary"  >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
									<input type="hidden" name="id" >
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>

		<#-- trigger -->
		<div class="modal fade" id="jobTriggerModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog ">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" >执行一次</h4>
					</div>
					<div class="modal-body">
						<form class="form-horizontal form" role="form" >
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">任务参数<font color="black">*</font></label>
								<div class="col-sm-10">
									<textarea class="textarea form-control" name="executorParam" placeholder="请输入任务参数" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
								</div>
							</div>
							<div class="row mb-3">
								<label for="firstname" class="col-sm-2 col-form-label">机器地址<font color="black">*</font></label>
								<div class="col-sm-10">
									<textarea class="textarea form-control" name="addressList" placeholder="请输入本次执行的机器地址，为空则从执行器获取" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
								</div>
							</div>
							<hr>
							<div class="row mb-3">
								<div class="offset-sm-3 col-sm-6">
									<button type="button" class="btn btn-primary ok" >保存</button>
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
									<input type="hidden" name="id" >
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- 2-content end -->

	</section>
</div>

<!-- 3-script start -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/bootstrap-table/bootstrap-table.js"></script>
<script src="${request.contextPath}/static/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
<#-- admin table -->
<script src="${request.contextPath}/static/biz/common/admin.table.js"></script>
<#-- admin util -->
<script src="${request.contextPath}/static/biz/common/admin.util.js"></script>
<#-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<#-- cronGen -->
<script src="${request.contextPath}/static/plugins/cronGen/cronGen.js"></script>
<script src="https://cdn.jsdelivr.net/npm/tom-select@2.4.3/dist/js/tom-select.complete.min.js"></script>
<script>
	$(function() {

		// init Tom Select
		var config = {
			create: false,
			sortField: { field: "text", direction: "asc" },
			placeholder: "请选择执行器",
			plugins: ['dropdown_input']
		};

		new TomSelect("#jobGroup", config);
		new TomSelect("#addModal .form select[name=jobGroup]", config);
		new TomSelect("#updateModal .form select[name=jobGroup]", config);

		// ---------------------- filter ----------------------

		/**
		 * jobGroup change
		 */
		$('#jobGroup').on('change', function(){
			//reload
			var jobGroup = $('#jobGroup').val();
			window.location.href = base_url + "/jobinfo?jobGroup=" + jobGroup;
		});

		// reset filter
		var jobGroup = '${jobGroup}';
		function resetFilter(){
			if (jobGroup > 0) {
				$("#jobGroup").val( jobGroup );
			}
		}
		resetFilter();

		// ---------------------- table ----------------------

		/**
		 * init table
		 */
		$.adminTable.initTable({
			table: '#data_list',
			url: base_url + "/jobinfo/pageList",
			queryParams: function (params) {
				var obj = {};
				obj.jobGroup = $('#jobGroup').val();
				obj.triggerStatus = $('#triggerStatus').val();
				obj.jobDesc = $('#jobDesc').val();
				obj.executorHandler = $('#executorHandler').val();
				obj.author = $('#author').val();
				obj.offset = params.offset;
				obj.pagesize = params.limit;
				return obj;
			},resetHandler : function() {
				// default
				$('#data_filter input[type="text"]').val('');
				$('#data_filter select').each(function() {
					$(this).prop('selectedIndex', 0);
				});

				// reset filter
				resetFilter();
			},
			columns:[
				{
					checkbox: true,
					field: 'state',
					width: '5',
					widthUnit: '%',
					align: 'center',
					valign: 'middle'
				},{
					title: '任务ID',
					field: 'id',
					width: '5',
					widthUnit: '%',
					align: 'left'
				}
				,{
					title: '任务描述',
					field: 'jobDesc',
					width: '25',
					widthUnit: '%',
					align: 'left',
					formatter: function(value, row, index) {
						if (value.length > 15) {
							return '<span title="' + value + '">' + value.substr(0, 15) + '...</span>';
						} else {
							return value;
						}
					}
				},{
					title: '调度类型',
					field: 'scheduleType',
					width: '15',
					widthUnit: '%',
					formatter: function(value, row, index) {
						if (row.scheduleConf) {
							return row.scheduleType + '：'+ row.scheduleConf;
						} else {
							return row.scheduleType;
						}
					}
				},{
					title: '运行模式',
					field: 'glueType',
					width: '25',
					widthUnit: '%',
					formatter: function(value, row, index) {
						// find glueType title
						let glueTypeTitle = '';
						$("#addModal .form select[name=glueType] option").each(function () {
							if (row.glueType == $(this).val()) {
								glueTypeTitle = $(this).text();
							}
						});

						// append handler
						if (row.executorHandler) {
							return glueTypeTitle +"：" + row.executorHandler;
						} else {
							return glueTypeTitle;
						}
					}
				},{
					title: '状态',
					field: 'triggerStatus',
					width: '10',
					widthUnit: '%',
					formatter: function(value, row, index) {
						// 调度状态：0-停止，1-运行
						if (1 == value) {
							return '<small class="label label-success" >RUNNING</small>';
						} else {
							return '<small class="label label-default" >STOP</small>';
						}
						return value;
					}
				},{
					title: '负责人',
					field: 'author',
					width: '10',
					widthUnit: '%'
				}
			]
		});

		// ---------------------- delete ----------------------

		/**
		 * delete
		 */
		/*$.adminTable.initDelete({
			url: base_url + "/jobinfo/delete"
		});*/
		$("#data_operation").on('click', '.delete',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select ids
			const selectIds = (rows && rows.length > 0) ? rows.map(row => row.id) : [];
			if (selectIds.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}

			// do delete
			layer.confirm( '确定' + '删除' + '?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/jobinfo/delete",
					data : {
						"ids" : selectIds
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( '删除' + '成功' );
							// refresh table
							$('#data_filter .searchBtn').click();
						} else {
							layer.msg( data.msg || '删除' + '失败' );
						}
					},
					error: function(xhr, status, error) {
						// Handle error
						console.log("Error: " + error);
						layer.open({
							icon: '2',
							content: ('删除' + '失败')
						});
					}
				});
			});
		});

		// ---------------------- start  ----------------------

		/**
		 * start
		 */
		$("#data_operation").on('click', '.job_resume',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select ids
			const selectIds = (rows && rows.length > 0) ? rows.map(row => row.id) : [];
			if (selectIds.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}

			// invoke
			layer.confirm( '确定' + '启动' + '?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/jobinfo/start",
					data : {
						"ids" : selectIds
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( '启动' + '成功' );
							// refresh table
							$('#data_filter .searchBtn').click();
						} else {
							layer.msg( data.msg || '启动' + '失败' );
						}
					},
					error: function(xhr, status, error) {
						// Handle error
						console.log("Error: " + error);
						layer.open({
							icon: '2',
							content: ('启动' + '失败')
						});
					}
				});
			});
		});

		// ---------------------- stop ----------------------

		/**
		 * stop
		 */
		$("#data_operation").on('click', '.job_pause',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select ids
			const selectIds = (rows && rows.length > 0) ? rows.map(row => row.id) : [];
			if (selectIds.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}

			// invoke
			layer.confirm( '确定' + '停止' + '?', {
				icon: 3,
				title: '系统提示' ,
				btn: [ '确定', '取消' ]
			}, function(index){
				layer.close(index);

				$.ajax({
					type : 'POST',
					url : base_url + "/jobinfo/stop",
					data : {
						"ids" : selectIds
					},
					dataType : "json",
					success : function(data){
						if (data.code === 200) {
							layer.msg( '停止' + '成功' );
							// refresh table
							$('#data_filter .searchBtn').click();
						} else {
							layer.msg( data.msg || '停止' + '失败' );
						}
					},
					error: function(xhr, status, error) {
						// Handle error
						console.log("Error: " + error);
						layer.open({
							icon: '2',
							content: ('停止' + '失败')
						});
					}
				});
			});
		});

		// ---------------------- trigger ----------------------

		/**
		 * job trigger
		 */
		$("#data_operation").on('click', '.job_trigger',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// fill modal
			$("#jobTriggerModal .form input[name='id']").val( row.id );
			$("#jobTriggerModal .form textarea[name='executorParam']").val( row.executorParam );

			$('#jobTriggerModal').modal({backdrop: false, keyboard: false}).modal('show');
		});
		$("#jobTriggerModal .ok").on('click',function() {
			$.ajax({
				type : 'POST',
				url : base_url + "/jobinfo/trigger",
				data : {
					"id" : $("#jobTriggerModal .form input[name='id']").val(),
					"executorParam" : $("#jobTriggerModal .textarea[name='executorParam']").val(),
					"addressList" : $("#jobTriggerModal .textarea[name='addressList']").val()
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						$('#jobTriggerModal').modal('hide');

						layer.msg( '执行一次' + '成功' );
					} else {
						layer.msg( data.msg || '执行一次' + '失败' );
					}
				}
			});
		});
		$("#jobTriggerModal").on('hide.bs.modal', function () {
			$("#jobTriggerModal .form")[0].reset();
		});

		// ---------------------- registryinfo ----------------------

		/**
		 * job registryinfo
		 */
		$("#data_operation").on('click', '.job_registryinfo',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// invoke
			$.ajax({
				type : 'POST',
				url : base_url + "/jobgroup/loadById",
				data : {
					"id" : row.jobGroup
				},
				dataType : "json",
				success : function(data){

					var html = '<div>';
					if (data.code == 200 && data.data.registryList) {
						for (var index in data.data.registryList) {
							html += (parseInt(index)+1) + '. <span class="badge bg-green" >' + data.data.registryList[index] + '</span><br>';
						}
					}
					html += '</div>';

					layer.open({
						title: '注册节点' ,
						btn: [ '确定' ],
						content: html
					});

				}
			});

		});

		// ---------------------- job_log ----------------------

		/**
		 * job_log
		 */
		$("#data_operation").on('click', '.job_log',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// open tab
			let url = base_url +'/joblog?jobId='+ row.id;
			openTab(url, '调度日志', false);
		});

		// ---------------------- glue_ide ----------------------

		/**
		 * glue_ide
		 */
		$("#data_operation").on('click', '.glue_ide',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// valid
			if ('BEAN' === row.glueType) {
				layer.msg('该任务非GLUE模式');
				return;
			}

			// open tab
			let url = base_url +'/jobcode?jobId='+ row.id;
			window.open(url);
			//openTab(url, 'GLUE IDE', false);
		});

		// ---------------------- job_next_time ----------------------

		/**
		 * job registryinfo
		 */
		$("#data_operation").on('click', '.job_next_time',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// invoke
			$.ajax({
				type : 'POST',
				url : base_url + "/jobinfo/nextTriggerTime",
				data : {
					"scheduleType" : row.scheduleType,
					"scheduleConf" : row.scheduleConf
				},
				dataType : "json",
				success : function(data){

					if (data.code != 200) {
						layer.open({
							title: '下次执行时间' ,
							btn: [ '确定' ],
							content: data.msg
						});
					} else {
						var html = '<center>';
						if (data.code == 200 && data.data) {
							for (var index in data.data) {
								html += '<span>' + data.data[index] + '</span><br>';
							}
						}
						html += '</center>';

						layer.open({
							title: '下次执行时间' ,
							btn: [ '确定' ],
							content: html
						});
					}

				}
			});

		});

		// ---------------------- add ----------------------

		/**
		 * add
		 */
		$.adminTable.initAdd( {
			url: base_url + "/jobinfo/insert",
			rules : {
				jobDesc : {
					required : true,
					maxlength: 50
				},
				author : {
					required : true
				}
			},
			messages : {
				jobDesc : {
					required : '请输入' + '任务描述'
				},
				author : {
					required : '请输入' + '负责人'
				}
			},
			writeFormData: function() {
				// init-cronGen
				$("#addModal .form input[name='schedule_conf_CRON']").show().siblings().remove();
				$("#addModal .form input[name='schedule_conf_CRON']").cronGen({});

				// 》init scheduleType
				$("#addModal .form select[name=scheduleType]").change();

				// 》init glueType
				$("#addModal .form select[name=glueType]").change();
			},
			readFormData: function() {

				// process executorTimeout+executorFailRetryCount
				var executorTimeout = $("#addModal .form input[name='executorTimeout']").val();
				if(!/^\d+$/.test(executorTimeout)) {
					executorTimeout = 0;
				}
				$("#addModal .form input[name='executorTimeout']").val(executorTimeout);
				var executorFailRetryCount = $("#addModal .form input[name='executorFailRetryCount']").val();
				if(!/^\d+$/.test(executorFailRetryCount)) {
					executorFailRetryCount = 0;
				}
				$("#addModal .form input[name='executorFailRetryCount']").val(executorFailRetryCount);

				// process schedule_conf
				var scheduleType = $("#addModal .form select[name='scheduleType']").val();
				var scheduleConf;
				if (scheduleType == 'CRON') {
					scheduleConf = $("#addModal .form input[name='cronGen_display']").val();
				} else if (scheduleType == 'FIX_RATE') {
					scheduleConf = $("#addModal .form input[name='schedule_conf_FIX_RATE']").val();
				} else if (scheduleType == 'FIX_DELAY') {
					scheduleConf = $("#addModal .form input[name='schedule_conf_FIX_DELAY']").val();
				}
				$("#addModal .form input[name='scheduleConf']").val( scheduleConf );

				return $("#addModal .form").serialize();
			}
		});

		// scheduleType change
		$(".scheduleType").change(function(){
			var scheduleType = $(this).val();
			$(this).parents("form").find(".schedule_conf").hide();
			$(this).parents("form").find(".schedule_conf_" + scheduleType).css("display", "contents");

		});

		// glueType change
		$(".glueType").change(function(){
			var $form = $(this).parents("form");
			// executorHandler
			var $executorHandler = $form.find("input[name='executorHandler']");
			var glueType = $(this).val();
			if ('BEAN' != glueType) {
				$executorHandler.val("");
				$executorHandler.attr("readonly","readonly");
			} else {
				$executorHandler.removeAttr("readonly");
			}
			if ('GLUE_PYTHON' === glueType) {
				$form.find(".python-version-row").show();
			} else {
				$form.find(".python-version-row").hide();
				$form.find("select[name='pythonId']").val('');
			}
		});

		// glueType init source
		$("#addModal .glueType").change(function(){
			// glueSource
			var glueType = $(this).val();
			if ('GLUE_GROOVY'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_java").val() );
			} else if ('GLUE_SHELL'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_shell").val() );
			} else if ('GLUE_PYTHON'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_python").val() );
			} else if ('GLUE_PYTHON2'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_python2").val() );
			} else if ('GLUE_PHP'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_php").val() );
			} else if ('GLUE_NODEJS'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_nodejs").val() );
			} else if ('GLUE_POWERSHELL'==glueType){
				$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_powershell").val() );
			} else {
				$("#addModal .form textarea[name='glueSource']").val("");
			}
		});

		// ---------------------- update ----------------------

		/**
		 * init update
		 */
		$.adminTable.initUpdate( {
			url: base_url + "/jobinfo/update",
			rules : {
				jobDesc : {
					required : true,
					maxlength: 50
				},
				author : {
					required : true
				}
			},
			messages : {
				jobDesc : {
					required : '请输入' + '任务描述'
				},
				author : {
					required : '请输入' + '负责人'
				}
			},
			writeFormData: function(row) {

				// fill base
				$("#updateModal .form input[name='id']").val( row.id );
				$('#updateModal .form select[name=jobGroup] option[value='+ row.jobGroup +']').prop('selected', true);
				$("#updateModal .form input[name='jobDesc']").val( row.jobDesc );
				$("#updateModal .form input[name='author']").val( row.author );
				$("#updateModal .form input[name='alarmEmail']").val( row.alarmEmail );

				// fill trigger
				$('#updateModal .form select[name=scheduleType] option[value='+ row.scheduleType +']').prop('selected', true);
				$("#updateModal .form input[name='scheduleConf']").val( row.scheduleConf );
				if (row.scheduleType == 'CRON') {
					$("#updateModal .form input[name='schedule_conf_CRON']").val( row.scheduleConf );
				} else if (row.scheduleType == 'FIX_RATE') {
					$("#updateModal .form input[name='schedule_conf_FIX_RATE']").val( row.scheduleConf );
				} else if (row.scheduleType == 'FIX_DELAY') {
					$("#updateModal .form input[name='schedule_conf_FIX_DELAY']").val( row.scheduleConf );
				}

				// 》init scheduleType
				$("#updateModal .form select[name=scheduleType]").change();

				// fill job
				$('#updateModal .form select[name=glueType] option[value='+ row.glueType +']').prop('selected', true);
				if (row.pythonId) {
					$("#updateModal .form select[name='pythonId']").val(row.pythonId);
				} else {
					$("#updateModal .form select[name='pythonId']").val('');
				}
				$("#updateModal .form input[name='executorHandler']").val( row.executorHandler );
				$("#updateModal .form textarea[name='executorParam']").val( row.executorParam );

				// 》init glueType
				$("#updateModal .form select[name=glueType]").change();

				// 》init-cronGen
				$("#updateModal .form input[name='schedule_conf_CRON']").show().siblings().remove();
				$("#updateModal .form input[name='schedule_conf_CRON']").cronGen({});

				// fill advanced
				$('#updateModal .form select[name=executorRouteStrategy] option[value='+ row.executorRouteStrategy +']').prop('selected', true);
				$("#updateModal .form input[name='childJobId']").val( row.childJobId );
				$('#updateModal .form select[name=misfireStrategy] option[value='+ row.misfireStrategy +']').prop('selected', true);
				$('#updateModal .form select[name=executorBlockStrategy] option[value='+ row.executorBlockStrategy +']').prop('selected', true);
				$("#updateModal .form input[name='executorTimeout']").val( row.executorTimeout );
				$("#updateModal .form input[name='executorFailRetryCount']").val( row.executorFailRetryCount );

			},
			readFormData: function() {

				// process executorTimeout + executorFailRetryCount
				var executorTimeout = $("#updateModal .form input[name='executorTimeout']").val();
				if(!/^\d+$/.test(executorTimeout)) {
					executorTimeout = 0;
				}
				$("#updateModal .form input[name='executorTimeout']").val(executorTimeout);
				var executorFailRetryCount = $("#updateModal .form input[name='executorFailRetryCount']").val();
				if(!/^\d+$/.test(executorFailRetryCount)) {
					executorFailRetryCount = 0;
				}
				$("#updateModal .form input[name='executorFailRetryCount']").val(executorFailRetryCount);


				// process schedule_conf
				var scheduleType = $("#updateModal .form select[name='scheduleType']").val();
				var scheduleConf;
				if (scheduleType == 'CRON') {
					scheduleConf = $("#updateModal .form input[name='cronGen_display']").val();
				} else if (scheduleType == 'FIX_RATE') {
					scheduleConf = $("#updateModal .form input[name='schedule_conf_FIX_RATE']").val();
				} else if (scheduleType == 'FIX_DELAY') {
					scheduleConf = $("#updateModal .form input[name='schedule_conf_FIX_DELAY']").val();
				}
				$("#updateModal .form input[name='scheduleConf']").val( scheduleConf );

				return $("#updateModal .form").serialize();
			}
		});

		// ---------------------- job_copy ----------------------

		/**
		 * job_copy
		 */
		$("#data_operation").on('click', '.job_copy',function() {
			// get select rows
			var rows = $.adminTable.table.bootstrapTable('getSelections');

			// find select row
			if (rows.length !== 1) {
				layer.msg('请选择' + '一条' + '数据');
				return;
			}
			var row = rows[0];

			// open addModel
			$("#data_operation .add").click();

			// fill base
			$('#addModal .form select[name=jobGroup] option[value='+ row.jobGroup +']').prop('selected', true);
			$("#addModal .form input[name='jobDesc']").val( row.jobDesc );
			$("#addModal .form input[name='author']").val( row.author );
			$("#addModal .form input[name='alarmEmail']").val( row.alarmEmail );

			// fill trigger
			$('#addModal .form select[name=scheduleType] option[value='+ row.scheduleType +']').prop('selected', true);
			$("#addModal .form input[name='scheduleConf']").val( row.scheduleConf );
			if (row.scheduleType == 'CRON') {
				$("#addModal .form input[name='schedule_conf_CRON']").val( row.scheduleConf );
			} else if (row.scheduleType == 'FIX_RATE') {
				$("#addModal .form input[name='schedule_conf_FIX_RATE']").val( row.scheduleConf );
			} else if (row.scheduleType == 'FIX_DELAY') {
				$("#addModal .form input[name='schedule_conf_FIX_DELAY']").val( row.scheduleConf );
			}

			// 》init scheduleType
			$("#addModal .form select[name=scheduleType]").change();

			// fill job
			$('#addModal .form select[name=glueType] option[value='+ row.glueType +']').prop('selected', true);
			if (row.pythonId) {
				$("#addModal .form select[name='pythonId']").val(row.pythonId);
			} else {
				$("#addModal .form select[name='pythonId']").val('');
			}
			$("#addModal .form input[name='executorHandler']").val( row.executorHandler );
			$("#addModal .form textarea[name='executorParam']").val( row.executorParam );

			// 》init glueType
			$("#addModal .form select[name=glueType]").change();

			// 》init-cronGen
			$("#addModal .form input[name='schedule_conf_CRON']").show().siblings().remove();
			$("#addModal .form input[name='schedule_conf_CRON']").cronGen({});

			// fill advanced
			$('#addModal .form select[name=executorRouteStrategy] option[value='+ row.executorRouteStrategy +']').prop('selected', true);
			$("#addModal .form input[name='childJobId']").val( row.childJobId );
			$('#addModal .form select[name=misfireStrategy] option[value='+ row.misfireStrategy +']').prop('selected', true);
			$('#addModal .form select[name=executorBlockStrategy] option[value='+ row.executorBlockStrategy +']').prop('selected', true);
			$("#addModal .form input[name='executorTimeout']").val( row.executorTimeout );
			$("#addModal .form input[name='executorFailRetryCount']").val( row.executorFailRetryCount );
		});

	});

</script>
<!-- 3-script end -->

</body>
</html>
