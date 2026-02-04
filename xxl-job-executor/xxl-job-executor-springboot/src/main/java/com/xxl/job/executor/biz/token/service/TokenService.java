package com.xxl.job.executor.biz.token.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.token.entity.TokenDO;

/**
 * <p>
 * token表 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
public interface TokenService extends IService<TokenDO> {
    TokenDO getTokenByName(String name);
}
