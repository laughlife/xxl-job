package com.xxl.job.executor.biz.sellfox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.sellfox.entity.SellfoxUserDO;

/**
 * <p>
 * 赛狐用户表 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
public interface SellfoxUserService extends IService<SellfoxUserDO> {
    String syncSellfoxUser();
}
