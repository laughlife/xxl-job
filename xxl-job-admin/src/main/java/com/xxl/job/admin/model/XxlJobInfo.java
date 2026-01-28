package com.xxl.job.admin.model;

import java.util.Date;

import lombok.Data;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */

@Data
public class XxlJobInfo {
	
	private int id;				// 主键ID
	
	private int jobGroup;		// 执行器主键ID
	private Integer taskGroupId;	// 任务组ID
	private int taskOrder;		// 任务组内排序
	private String taskGroupName;	// 任务组名称（非数据库字段，用于展示）
	private String jobDesc;
	private Integer pythonId;
	
	private Date addTime;
	private Date updateTime;
	
	private String author;		// 负责人
	private String alarmEmail;	// 报警邮件

	private String scheduleType;			// 调度类型：ScheduleTypeEnum
	private String scheduleConf;			// 调度配置，值含义取决于调度类型
	private String misfireStrategy;			// 调度过期策略：MisfireStrategyEnum

	private String executorRouteStrategy;	// 执行器路由策略：ExecutorRouteStrategyEnum
	private String executorHandler;		    // 执行器，任务Handler名称
	private String executorParam;		    // 执行器，任务参数
	private String executorBlockStrategy;	// 阻塞处理策略：ExecutorBlockStrategyEnum
	private int executorTimeout;     		// 任务执行超时时间，单位秒
	private int executorFailRetryCount;		// 失败重试次数
	
	private String glueType;		// GLUE类型：GlueTypeEnum
	private String glueSource;		// GLUE源代码
	private String glueRemark;		// GLUE备注
	private Date glueUpdatetime;	// GLUE更新时间

	private String childJobId;		// 子任务ID，多个逗号分隔

	private int triggerStatus;		// 调度状态：TriggerStatus
	private long triggerLastTime;	// 上次调度时间
	private long triggerNextTime;	// 下次调度时间

}
