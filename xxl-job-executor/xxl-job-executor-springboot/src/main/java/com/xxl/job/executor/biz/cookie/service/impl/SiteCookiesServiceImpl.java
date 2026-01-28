package com.xxl.job.executor.biz.cookie.service.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.cookie.entity.SiteCookiesDO;
import com.xxl.job.executor.biz.cookie.mapper.SiteCookiesMapper;
import com.xxl.job.executor.biz.cookie.service.SiteCookiesService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * <p>
 * cookie 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Service
@Valid
public class SiteCookiesServiceImpl extends ServiceImpl<SiteCookiesMapper, SiteCookiesDO> implements SiteCookiesService {

    @Resource
    private SiteCookiesMapper siteCookiesMapper;

    @Override
    public SiteCookiesDO getSiteCookiesByName(String siteName) {
        return siteCookiesMapper.selectOne("site_name", siteName);
    }

    @Override
    public String getSiteCookieStrByName(String siteName) {
        SiteCookiesDO siteCookiesDO = getSiteCookiesByName(siteName);
        // 添加空值检查
        if (siteCookiesDO == null || siteCookiesDO.getCookies() == null) {
            return "";
        }

        return JSONUtil.parseArray(siteCookiesDO.getCookies())
                .stream()
                .map(ac -> (JSONObject) ac)
                .map(acJson -> acJson.getStr("name") + "=" + acJson.getStr("value"))
                .collect(Collectors.joining(";"));
    }
}
