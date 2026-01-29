package com.xxl.job.executor.biz.amazon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.amazon.entity.BuyerDO;

/**
 * 亚马逊买家信息(维表) 服务类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
public interface BuyerService extends IService<BuyerDO> {

    /**
     * 根据邮箱查询买家
     */
    BuyerDO getByEmail(String email);
}
