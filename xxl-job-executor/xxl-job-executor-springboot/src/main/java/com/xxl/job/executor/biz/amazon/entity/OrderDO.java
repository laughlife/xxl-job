package com.xxl.job.executor.biz.amazon.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 亚马逊订单(最新快照，高频查询字段)
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_order")
public class OrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Sellfox系统订单ID
     */
    @TableField("source_id")
    private Long sourceId;

    /**
     * Amazon订单号
     */
    @TableField("amazon_order_id")
    private String amazonOrderId;

    /**
     * 买家ID，关联amazon_buyer表
     */
    @TableField("buyer_id")
    private Long buyerId;

    /**
     * 业务PUID
     */
    @TableField("puid")
    private Long puid;

    /**
     * 店铺ID
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 店铺名称
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * 站点MarketplaceId
     */
    @TableField("marketplace_id")
    private String marketplaceId;

    /**
     * 站点中文
     */
    @TableField("marketplace_cn")
    private String marketplaceCn;

    /**
     * 下单时间
     */
    @TableField("purchase_date")
    private LocalDateTime purchaseDate;

    /**
     * 最后更新时间
     */
    @TableField("last_update_date")
    private LocalDateTime lastUpdateDate;

    /**
     * 回款/结算时间
     */
    @TableField("payments_date")
    private LocalDateTime paymentsDate;

    /**
     * 订单状态
     */
    @TableField("order_status")
    private String orderStatus;

    /**
     * 订单类型
     */
    @TableField("order_type")
    private String orderType;

    /**
     * 配送渠道
     */
    @TableField("fulfillment_channel")
    private String fulfillmentChannel;

    /**
     * 订单总金额币种
     */
    @TableField("order_total_currency")
    private String orderTotalCurrency;

    /**
     * 订单总金额
     */
    @TableField("order_total_amount")
    private BigDecimal orderTotalAmount;

    /**
     * 订单利润
     */
    @TableField("order_profit")
    private BigDecimal orderProfit;

    /**
     * 利润
     */
    @TableField("profit")
    private BigDecimal profit;

    /**
     * 利润率，百分比
     */
    @TableField("profit_rate")
    private BigDecimal profitRate;

    /**
     * 是否退货订单
     */
    @TableField("is_return_order")
    private Boolean isReturnOrder;

    /**
     * 是否企业订单
     */
    @TableField("is_business_order")
    private Boolean isBusinessOrder;

    /**
     * 是否Prime
     */
    @TableField("is_prime")
    private Boolean isPrime;

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
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;
}
