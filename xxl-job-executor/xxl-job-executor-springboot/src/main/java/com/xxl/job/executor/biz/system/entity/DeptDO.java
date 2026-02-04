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
 * 部门表
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Getter
@Setter
@ToString
@TableName("system_dept")
public class DeptDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 父部门id
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 负责人
     */
    @TableField("leader_user_id")
    private Long leaderUserId;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @TableField("status")
    private Byte status;

    /**
     * 锁定不删除
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

    /**
     * 特殊标记，开发人员使用。运营：operator
     */
    @TableField("dept")
    private String dept;
}
