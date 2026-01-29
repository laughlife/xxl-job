package com.xxl.job.executor.biz.amazon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 亚马逊买家信息(维表)
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_buyer")
public class BuyerDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 买家ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 买家邮箱，用于去重
     */
    @TableField("buyer_email")
    private String buyerEmail;

    /**
     * 买家名称
     */
    @TableField("buyer_name")
    private String buyerName;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
