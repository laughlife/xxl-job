package com.xxl.job.executor.biz.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.system.entity.SystemUsersDO;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */

public interface SystemUsersService extends IService<SystemUsersDO> {


    /**
     * 同步钉钉部门信息
     * @return
     */
    String syncDingTalkDept();

    /**
     * 同步钉钉用户信息
     * @return
     */
    String syncDingTalkUsers();

    /**
     * 检查用户赛狐ID
     * @return
     */
    public String checkUserSellfoxId();

}