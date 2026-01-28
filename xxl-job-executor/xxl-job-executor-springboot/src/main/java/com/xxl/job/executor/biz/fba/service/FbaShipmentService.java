package com.xxl.job.executor.biz.fba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.fba.entity.FbaShipmentDO;

/**
 * <p>
 * 货件信息 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
public interface FbaShipmentService extends IService<FbaShipmentDO> {

    /**
     * 同步FBA货件信息
     * @return 同步结果描述
     */
    String syncFbaShipment();

    /**
     * 解析sellfox下载的货件信息
     * @return
     */
    public String analyzeFbaShipment();
}
