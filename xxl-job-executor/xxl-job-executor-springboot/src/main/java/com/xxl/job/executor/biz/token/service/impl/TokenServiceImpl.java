package com.xxl.job.executor.biz.token.service.impl;

import com.xxl.job.executor.biz.token.entity.Token;
import com.xxl.job.executor.biz.token.mapper.TokenMapper;
import com.xxl.job.executor.biz.token.service.TokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * token表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-27
 */
@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, Token> implements TokenService {

}
