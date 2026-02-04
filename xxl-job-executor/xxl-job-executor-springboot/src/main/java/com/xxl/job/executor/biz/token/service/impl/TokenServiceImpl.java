package com.xxl.job.executor.biz.token.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.token.entity.TokenDO;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.TokenService;

import jakarta.annotation.Resource;

/**
 * <p>
 * token表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, TokenDO> implements TokenService {

    @Resource
    private TokenMapper tokenMapper;

    @Override
    public TokenDO getTokenByName(String name) {
        return tokenMapper.selectFirstOne(TokenDO::getName, name);
    }
}
