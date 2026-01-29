package com.xxl.job.executor.biz.amazon.entity;

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
 * 亚马逊订单扩展(历史快照，只增不改，含完整JSON)
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-29
 */
@Getter
@Setter
@ToString
@TableName("amazon_order_ext")
public class OrderExtDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId("id")
    private Long id;

    /**
     * Amazon订单号
     */
    @TableField("amazon_order_id")
    private String amazonOrderId;

    /**
     * 最后更新时间
     */
    @TableField("last_update_date")
    private LocalDateTime lastUpdateDate;

    /**
     * 快照入库时间
     */
    @TableField("captured_at")
    private LocalDateTime capturedAt;

    /**
     * 订单备注
     */
    @TableField("comment")
    private String comment;

    /**
     * 备注颜色
     */
    @TableField("comment_color")
    private String commentColor;

    /**
     * 最早发货时间
     */
    @TableField("earliest_ship_date")
    private LocalDateTime earliestShipDate;

    /**
     * 最晚发货时间
     */
    @TableField("latest_ship_date")
    private LocalDateTime latestShipDate;

    /**
     * 最早送达时间
     */
    @TableField("earliest_delivery_date")
    private LocalDateTime earliestDeliveryDate;

    /**
     * 最晚送达时间
     */
    @TableField("latest_delivery_date")
    private LocalDateTime latestDeliveryDate;

    /**
     * 退款状态
     */
    @TableField("refund_status")
    private Byte refundStatus;

    /**
     * 退款时间
     */
    @TableField("refund_date")
    private LocalDateTime refundDate;

    /**
     * 买家是否请求取消
     */
    @TableField("is_buyer_requested_cancel")
    private Boolean isBuyerRequestedCancel;

    /**
     * 是否补发单
     */
    @TableField("is_replacement_order")
    private Boolean isReplacementOrder;

    /**
     * 被替换订单号
     */
    @TableField("replaced_order_id")
    private String replacedOrderId;

    /**
     * 是否Vine订单
     */
    @TableField("is_vine_order")
    private Boolean isVineOrder;

    /**
     * 是否自定义订单
     */
    @TableField("custom_order")
    private Boolean customOrder;

    /**
     * 是否有促销
     */
    @TableField("promotion_flag")
    private Boolean promotionFlag;

    /**
     * 促销ID集合
     */
    @TableField("promotion_ids")
    private String promotionIds;

    /**
     * 订单标记
     */
    @TableField("order_flag")
    private Integer orderFlag;

    /**
     * 索评状态
     */
    @TableField("order_review_status")
    private String orderReviewStatus;

    /**
     * Feed上传状态
     */
    @TableField("upload_feed_status")
    private Integer uploadFeedStatus;

    /**
     * 发票状态描述
     */
    @TableField("invoice_status_description")
    private String invoiceStatusDescription;

    /**
     * 支付方式数组
     */
    @TableField("payment_method_details")
    private String paymentMethodDetails;

    /**
     * 完整原始订单JSON
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
