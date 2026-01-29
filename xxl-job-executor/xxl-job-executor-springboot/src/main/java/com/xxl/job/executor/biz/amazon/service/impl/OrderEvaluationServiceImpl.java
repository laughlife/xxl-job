package com.xxl.job.executor.biz.amazon.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.amazon.entity.OrderEvaluationDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderEvaluationMapper;
import com.xxl.job.executor.biz.amazon.service.OrderEvaluationService;

/**
 * 亚马逊订单评价 服务实现类
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderEvaluationServiceImpl extends ServiceImpl<OrderEvaluationMapper, OrderEvaluationDO> 
        implements OrderEvaluationService {

    @Override
    public OrderEvaluationDO getByAmazonOrderId(String amazonOrderId) {
        return getOne(new LambdaQueryWrapper<OrderEvaluationDO>()
                .eq(OrderEvaluationDO::getAmazonOrderId, amazonOrderId));
    }

    @Override
    public void saveOrUpdateByOrderId(OrderEvaluationDO evaluation) {
        OrderEvaluationDO existing = getByAmazonOrderId(evaluation.getAmazonOrderId());
        if (existing != null) {
            evaluation.setId(existing.getId());
            updateById(evaluation);
        } else {
            save(evaluation);
        }
    }
}
