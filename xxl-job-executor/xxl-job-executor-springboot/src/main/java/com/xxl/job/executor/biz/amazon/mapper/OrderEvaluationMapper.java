package com.xxl.job.executor.biz.amazon.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.executor.biz.amazon.entity.OrderEvaluationDO;

/**
 * 亚马逊订单评价 Mapper 接口
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Mapper
public interface OrderEvaluationMapper extends BaseMapper<OrderEvaluationDO> {
}
