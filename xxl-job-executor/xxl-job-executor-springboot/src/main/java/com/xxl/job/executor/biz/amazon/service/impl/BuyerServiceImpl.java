package com.xxl.job.executor.biz.amazon.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.BuyerDO;
import com.xxl.job.executor.biz.amazon.mapper.BuyerMapper;
import com.xxl.job.executor.biz.amazon.service.BuyerService;

/**
 * 亚马逊买家信息(维表) 服务实现类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class BuyerServiceImpl extends ServiceImpl<BuyerMapper, BuyerDO> implements BuyerService {

    @Override
    public BuyerDO getByEmail(String email) {
        return getOne(new LambdaQueryWrapper<BuyerDO>()
                .eq(BuyerDO::getBuyerEmail, email));
    }
}
