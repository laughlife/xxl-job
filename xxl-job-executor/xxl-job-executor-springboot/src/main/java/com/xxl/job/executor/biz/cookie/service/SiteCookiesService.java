package com.xxl.job.executor.biz.cookie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.cookie.entity.SiteCookiesDO;

/**
 * <p>
 * cookie 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
public interface SiteCookiesService extends IService<SiteCookiesDO> {
    /**
     * 
     * 根据站点获取cookie信息
     * @param siteName
     * @return
     */
    SiteCookiesDO getSiteCookiesByName(String siteName);

    /**
     * 
     * 根据站点获取cookie字符串信息
     * @param siteName
     * @return
     */
    String getSiteCookieStrByName(String siteName);
}
