package com.xxl.job.executor.biz.amazon.service.impl;

import com.xxl.job.executor.biz.amazon.entity.OrderExtDO;
import com.xxl.job.executor.biz.amazon.mapper.OrderExtMapper;
import com.xxl.job.executor.biz.amazon.service.OrderExtService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 亚马逊订单扩展(历史快照，只增不改，含完整JSON) 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Service
public class OrderExtServiceImpl extends ServiceImpl<OrderExtMapper, OrderExtDO> implements OrderExtService {

}
