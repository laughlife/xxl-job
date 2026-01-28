package com.xxl.job.executor.jobhandler;

import org.springframework.stereotype.Component;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.HubuService;
import com.xxl.job.executor.biz.token.service.SellfoxToken;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.xxl.job.executor.service.YourBusinessService;

/**
 * 数据库交互任务示例
 */
@Component
@Slf4j
public class TokenXxlJob {

    // 1. 注入你的业务Service或Mapper
    // @Autowired
    // private YourBusinessService yourBusinessService;

    @Resource
    HubuService hubuService;

    @Resource
    SellfoxToken sellfoxToken;

    @Resource
    private TokenMapper tokenMapper;
    /**
     * 示例：处理过期订单
     */
    @XxlJob("refreshHubuSellfoxToken")
    public void refreshTokenJobHandler() throws Exception {
        XxlJobHelper.log("开始刷新Token信息...");

        // 2. 获取调度中心传递的参数（例如处理日期）
        // String param = XxlJobHelper.getJobParam();
        // XxlJobHelper.log("任务参数: " + param);

        try {
            boolean hubuStatus = hubuService.getOrRefreshToken();
            boolean sellfoxStatus = sellfoxToken.getOrRefreshToken();
            XxlJobHelper.log("数据库任务执行完成，虎佈状态: " + hubuStatus + ", 赛狐状态: " + sellfoxStatus);
        } catch (Exception e) {
            log.error("任务执行异常", e);
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }


}
