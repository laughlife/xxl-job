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
 * 亚马逊订单商品扩展(低频字段+完整JSON，按快照存档)
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_order_product_ext")
public class OrderProductExtDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Amazon订单号
     */
    @TableField("amazon_order_id")
    private String amazonOrderId;

    /**
     * Sellfox系统商品明细ID
     */
    @TableField("source_item_id")
    private Long sourceItemId;

    /**
     * FNSKU
     */
    @TableField("fnsku")
    private String fnsku;

    /**
     * IOSS号
     */
    @TableField("ioss_number")
    private String iossNumber;

    /**
     * 促销ID字符串
     */
    @TableField("promotion_ids")
    private String promotionIds;

    /**
     * 预扣税金额
     */
    @TableField("withheld_tax_amount")
    private BigDecimal withheldTaxAmount;

    /**
     * 运费
     */
    @TableField("shipping_charge_amount")
    private BigDecimal shippingChargeAmount;

    /**
     * 运费税
     */
    @TableField("shipping_tax_amount")
    private BigDecimal shippingTaxAmount;

    /**
     * 礼品包装费
     */
    @TableField("gift_wrap_amount")
    private BigDecimal giftWrapAmount;

    /**
     * 礼品包装税
     */
    @TableField("gift_wrap_tax_amount")
    private BigDecimal giftWrapTaxAmount;

    /**
     * 折扣币种
     */
    @TableField("promotion_discount_currency")
    private String promotionDiscountCurrency;

    /**
     * 折扣金额
     */
    @TableField("promotion_discount_amount")
    private BigDecimal promotionDiscountAmount;

    /**
     * 其他金额
     */
    @TableField("other_amount")
    private BigDecimal otherAmount;

    /**
     * FBA单件履约费
     */
    @TableField("fba_per_unit_fulfillment_fee")
    private BigDecimal fbaPerUnitFulfillmentFee;

    /**
     * 佣金
     */
    @TableField("commission")
    private BigDecimal commission;

    /**
     * 促销折扣
     */
    @TableField("promotion_discount")
    private BigDecimal promotionDiscount;

    /**
     * 亚马逊退款
     */
    @TableField("amazon_back_to_article")
    private BigDecimal amazonBackToArticle;

    /**
     * 合并采购成本
     */
    @TableField("merge_purchase_cost")
    private BigDecimal mergePurchaseCost;

    /**
     * 采购成本
     */
    @TableField("purchase_cost")
    private BigDecimal purchaseCost;

    /**
     * 头程费用
     */
    @TableField("head_trip_cost")
    private BigDecimal headTripCost;

    /**
     * 头程分摊
     */
    @TableField("head_trip_share")
    private Boolean headTripShare;

    /**
     * FBM运费
     */
    @TableField("fbm_ship_cost")
    private BigDecimal fbmShipCost;

    /**
     * 资金币种
     */
    @TableField("capital_currency")
    private String capitalCurrency;

    /**
     * 资金日期
     */
    @TableField("capital_date")
    private LocalDateTime capitalDate;

    /**
     * 佣金币种
     */
    @TableField("commission_currency")
    private String commissionCurrency;

    /**
     * 佣金日期
     */
    @TableField("commission_date")
    private LocalDateTime commissionDate;

    /**
     * 评价数
     */
    @TableField("evaluation")
    private Integer evaluation;

    /**
     * 承运商
     */
    @TableField("carrier")
    private String carrier;

    /**
     * 运单号
     */
    @TableField("track_no")
    private String trackNo;

    /**
     * 发货时间
     */
    @TableField("shipment_date")
    private LocalDateTime shipmentDate;

    /**
     * 预计到达时间
     */
    @TableField("estimated_arrival_date")
    private LocalDateTime estimatedArrivalDate;

    /**
     * 完整原始商品行JSON
     */
    @TableField("raw_json")
    private String rawJson;

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
