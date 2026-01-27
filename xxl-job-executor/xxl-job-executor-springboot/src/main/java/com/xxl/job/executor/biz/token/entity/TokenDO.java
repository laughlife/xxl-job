package com.xxl.job.executor.biz.token.entity;

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
 * token表
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-27
 */
@Getter
@Setter
@ToString
@TableName("ruiyi_token")
public class TokenDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口名
     */
    @TableField("name")
    private String name;

    /**
     * 请求token
     */
    @TableField("access_token")
    private String accessToken;

    @TableField("app_key")
    private String appKey;

    @TableField("app_secret")
    private String appSecret;

    /**
     * 部分接口可能需要
     */
    @TableField("agent_id")
    private String agentId;

    /**
     * 刷新token
     */
    @TableField("refresh_token")
    private String refreshToken;

    /**
     * 保存时间
     */
    @TableField("save_time")
    private Long saveTime;

    /**
     * 过期时间
     */
    @TableField("expires_time")
    private Long expiresTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
