package com.xxl.job.executor.biz.fba.entity;

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
 * 货件信息
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_shipment")
public class FbaShipmentDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    @TableId("id")
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 卖方身份
     */
    @TableField("selling_partner_id")
    private String sellingPartnerId;

    /**
     * 注册地
     */
    @TableField("region")
    private String region;

    /**
     * 店铺ID
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 店铺
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * 市场ID
     */
    @TableField("marketplace_id")
    private String marketplaceId;

    /**
     * 市场名
     */
    @TableField("marketplace_name")
    private String marketplaceName;

    /**
     * 入站计划ID
     */
    @TableField("inbound_plan_id")
    private String inboundPlanId;

    /**
     * STA货运ID
     */
    @TableField("sta_shipment_id")
    private String staShipmentId;

    /**
     * 运单号
     */
    @TableField("amazon_shipment_id")
    private String amazonShipmentId;

    /**
     * 主转单号
     */
    @TableField("transfer_order_no")
    private String transferOrderNo;

    /**
     * 货运状态
     */
    @TableField("shipment_status")
    private String shipmentStatus;

    /**
     * 仓库编码
     */
    @TableField("fulfillment_center_id")
    private String fulfillmentCenterId;

    /**
     * ReferenceId
     */
    @TableField("reference_id")
    private String referenceId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 电话
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 接收数量
     */
    @TableField("quantity_received")
    private Integer quantityReceived;

    /**
     * 箱数
     */
    @TableField("box_quantity")
    private Integer boxQuantity;

    /**
     * 申报箱数
     */
    @TableField("carton_num")
    private Integer cartonNum;

    /**
     * 发货单数量
     */
    @TableField("quantity_from_ship_order")
    private Integer quantityFromShipOrder;

    /**
     * 发货数量差异
     */
    @TableField("quantity_difference")
    private Integer quantityDifference;

    /**
     * 接收数量差异
     */
    @TableField("received_difference")
    private Integer receivedDifference;

    /**
     * 收发数量差
     */
    @TableField("qua_rec_difference")
    private Integer quaRecDifference;

    /**
     * 批次号列表
     */
    @TableField("batch_sn")
    private String batchSn;

    /**
     * 承运商名称
     */
    @TableField("carrier_name")
    private String carrierName;

    /**
     * 运输方案ID
     */
    @TableField("transportation_option_id")
    private String transportationOptionId;

    /**
     * 预约送仓时间窗口ID
     */
    @TableField("delivery_window_option_id")
    private String deliveryWindowOptionId;

    /**
     * 实际发货日期
     */
    @TableField("shipped_date")
    private LocalDateTime shippedDate;

    /**
     * 预计到仓开始时间
     */
    @TableField("expect_arrival_start")
    private LocalDateTime expectArrivalStart;

    /**
     * 预计到仓截止时间
     */
    @TableField("expect_arrival_end")
    private LocalDateTime expectArrivalEnd;

    /**
     * 是否超期
     */
    @TableField("has_overdue")
    private Byte hasOverdue;

    /**
     * SPD/LTL等
     */
    @TableField("shipping_mode")
    private String shippingMode;

    /**
     * 运输方案
     */
    @TableField("shipping_solution")
    private String shippingSolution;

    /**
     * 货件类型
     */
    @TableField("shipment_type")
    private Byte shipmentType;

    /**
     * 生成发货单
     */
    @TableField("shipping_order_flag")
    private Byte shippingOrderFlag;

    /**
     * 工厂直送
     */
    @TableField("factory_direct_delivery")
    private Byte factoryDirectDelivery;

    /**
     * 在途状态
     */
    @TableField("transit_status")
    private Integer transitStatus;

    /**
     * 发货单在途状态
     */
    @TableField("transit_status_for_ship_order")
    private Integer transitStatusForShipOrder;

    /**
     * 接收时间
     */
    @TableField("receiving_time")
    private LocalDateTime receivingTime;

    /**
     * 提单号
     */
    @TableField("ltl_bill_of_lading_number")
    private String ltlBillOfLadingNumber;

    /**
     * 账单号
     */
    @TableField("ltl_freight_bill_number")
    private String ltlFreightBillNumber;

    /**
     * 发货地址ID
     */
    @TableField("source_addr_id")
    private Long sourceAddrId;

    /**
     * 目的仓ID
     */
    @TableField("dest_addr_id")
    private Long destAddrId;

    /**
     * 目的仓邮编
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 目的仓邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 是否过期
     */
    @TableField("is_outdated_ful_center")
    private Byte isOutdatedFulCenter;

    /**
     * 创建者
     */
    @TableField("create_name")
    private String createName;

    /**
     * 是否已同步
     */
    @TableField("synced")
    private Integer synced;

    /**
     * 原始报文(可选，便于排查)
     */
    @TableField("raw_json")
    private String rawJson;

    /**
     * 创建者（这个是运营，谁的订单，这里就是谁）
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
     * 部门编号，用于数据权限控制
     */
    @TableField("dept_id")
    private Long deptId;
}
