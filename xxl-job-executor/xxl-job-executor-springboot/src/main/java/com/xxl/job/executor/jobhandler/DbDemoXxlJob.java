package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.xxl.job.executor.service.YourBusinessService;

/**
 * 数据库交互任务示例
 */
@Component
public class DbDemoXxlJob {
    private static final Logger logger = LoggerFactory.getLogger(DbDemoXxlJob.class);

    // 1. 注入你的业务Service或Mapper
    // @Autowired
    // private YourBusinessService yourBusinessService;

    /**
     * 示例：处理过期订单
     */
    @XxlJob("dbJobHandler")
    public void dbJobHandler() throws Exception {
        XxlJobHelper.log("开始执行数据库相关任务...");

        // 2. 获取调度中心传递的参数（例如处理日期）
        String param = XxlJobHelper.getJobParam();
        XxlJobHelper.log("任务参数: " + param);

        try {
            // 3. 调用Service方法操作数据库
            // int count = yourBusinessService.closeOverdueOrders(param);
            // XxlJobHelper.log("成功关闭过期订单数: " + count);
            
            // 模拟业务执行
            logger.info("正在查询数据库...");
            Thread.sleep(1000);
            logger.info("正在更新数据状态...");
            
            XxlJobHelper.log("数据库任务执行完成");
        } catch (Exception e) {
            logger.error("任务执行异常", e);
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }
}
