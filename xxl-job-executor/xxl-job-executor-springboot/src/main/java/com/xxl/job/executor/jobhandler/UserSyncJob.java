package com.xxl.job.executor.jobhandler;

import org.springframework.stereotype.Component;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.biz.sellfox.service.SellfoxUserService;
import com.xxl.job.executor.biz.system.service.SystemUsersService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.xxl.job.executor.service.YourBusinessService;

/**
 * 数据库交互任务示例
 */
@Component
@Slf4j
public class UserSyncJob {

    // 1. 注入你的业务Service或Mapper
    // @Autowired
    // private YourBusinessService yourBusinessService;

    @Resource
    SystemUsersService systemUsersService;

    @Resource
    SellfoxUserService sellfoxUserService;
    /**
     * 示例：处理过期订单
     */
    @XxlJob("refreshDingTalkUser")
    public void refreshDingTalkUserJobHandler() throws Exception {
        XxlJobHelper.log("开始同步钉钉部门和用户信息...");

        // 2. 获取调度中心传递的参数（例如处理日期）
        // String param = XxlJobHelper.getJobParam();
        // XxlJobHelper.log("任务参数: " + param);

        try {
            String result = systemUsersService.syncDingTalkDept();
            XxlJobHelper.log("钉钉部门同步状态: " + result);

            String result2 = systemUsersService.syncDingTalkUsers();
            XxlJobHelper.log("钉钉用户同步状态: " + result2);

            String sellfoxResult = sellfoxUserService.syncSellfoxUser();
            XxlJobHelper.log("赛狐用户同步状态: " + sellfoxResult);

            String checkUserSellfoxIdStatus = systemUsersService.checkUserSellfoxId();
            XxlJobHelper.log("用户赛狐ID检查状态: " + checkUserSellfoxIdStatus);
            XxlJobHelper.handleSuccess("数据同步完成，钉钉部门同步状态: " + result + ", 钉钉用户同步状态: " + result2 + ", 赛狐用户同步状态: " + sellfoxResult);
        } catch (Exception e) {
            log.error("任务执行异常", e);
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }


}
