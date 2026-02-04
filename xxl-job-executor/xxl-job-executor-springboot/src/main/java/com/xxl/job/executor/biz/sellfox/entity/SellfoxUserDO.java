package com.xxl.job.executor.biz.sellfox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 赛狐用户表
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Getter
@Setter
@ToString
@TableName("erp_sellfox_user")
public class SellfoxUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId("id")
    private String id;

    /**
     * 账号
     */
    @TableField("account")
    private String account;

    /**
     * 电话
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 角色ID
     */
    @TableField("role_ids")
    private String roleIds;

    /**
     * 角色名称
     */
    @TableField("role_names")
    private String roleNames;

    /**
     * 登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录数
     */
    @TableField("login_num")
    private String loginNum;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
