package com.xxl.job.executor.jobhandler;

import org.springframework.stereotype.Component;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.biz.fba.service.FbaDispatchService;
import com.xxl.job.executor.biz.fba.service.FbaShipmentService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * FBA货件同步任务
 */
@Component
@Slf4j
public class FbaShipmentJob {

    @Resource
    private FbaShipmentService fbaShipmentService;

    @Resource
    private FbaDispatchService fbaDispatchService;

    /**
     * 同步赛狐货件信息
     */
    @XxlJob("syncFbaShipment")
    public void syncFbaShipmentJobHandler() {
        XxlJobHelper.log("开始同步FBA货件信息...");

        String result = fbaShipmentService.syncFbaShipment();

        XxlJobHelper.log(result);
        if (result.startsWith("同步完成")) {
            XxlJobHelper.handleSuccess(result);
        } else {
            XxlJobHelper.handleFail(result);
        }
    }

    @XxlJob("analyzeFbaShipment")
    public void analyzeFbaShipmentJobHandler() {
        XxlJobHelper.log("开始分析FBA货件信息...");

        String result = fbaShipmentService.analyzeFbaShipment();

        XxlJobHelper.log(result);
        if (result.startsWith("分析完成")) {
            XxlJobHelper.handleSuccess(result);
        } else {
            XxlJobHelper.handleFail(result);
        }
    }

    @XxlJob("analyzeLmtDownload")
    public void analyzeLmtDownloadJobHandler() throws Exception {
        XxlJobHelper.log("开始分析LMT下载数据...");

        try {
            // 2. 执行业务逻辑
            fbaDispatchService.analyzeLmtDownloadData();

            // 3. 记录日志
            XxlJobHelper.log("分析LMT下载数据任务执行完成");
        } catch (Exception e) {
            log.error("任务执行异常", e);
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }

}
