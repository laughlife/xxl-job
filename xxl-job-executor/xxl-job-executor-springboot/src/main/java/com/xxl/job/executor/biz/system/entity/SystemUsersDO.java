package com.xxl.job.executor.biz.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 用户信息表
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Getter
@Setter
@ToString
@TableName("system_users")
public class SystemUsersDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 钉钉用户ID
     */
    @TableField("dingtalk_id")
    private String dingtalkId;

    /**
     * 赛狐ID
     */
    @TableField("sellfox_id")
    private String sellfoxId;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 岗位编号数组
     */
    @TableField("post_ids")
    private String postIds;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 用户性别
     */
    @TableField("sex")
    private Byte sex;

    /**
     * 供应商
     */
    @TableField("suppliers")
    private String suppliers;

    /**
     * 物流商
     */
    @TableField("logistics")
    private String logistics;

    /**
     * 头像地址
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    @TableField("status")
    private Byte status;

    /**
     * 最后登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField("login_date")
    private LocalDateTime loginDate;

    /**
     * 是否锁定
     */
    @TableField("locked")
    private Integer locked;

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

    /**
     * 租户编号
     */
    @TableField("tenant_id")
    private Long tenantId;
}
