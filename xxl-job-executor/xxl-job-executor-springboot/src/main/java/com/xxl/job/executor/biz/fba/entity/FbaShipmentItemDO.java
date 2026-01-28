package com.xxl.job.executor.biz.fba.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 货件明细
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_shipment_item")
public class FbaShipmentItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属货件ID
     */
    @TableField("shipment_id")
    private Long shipmentId;

    /**
     * 站点ID
     */
    @TableField("marketplace_id")
    private String marketplaceId;

    /**
     * 计划ID
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 计划编号
     */
    @TableField("plan_sn")
    private String planSn;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * ASIN
     */
    @TableField("asin")
    private String asin;

    /**
     * MSKU
     */
    @TableField("msku")
    private String msku;

    /**
     * FNSKU
     */
    @TableField("fn_sku")
    private String fnSku;

    /**
     * 商品ID
     */
    @TableField("commodity_id")
    private Long commodityId;

    /**
     * SKU
     */
    @TableField("commodity_sku")
    private String commoditySku;

    /**
     * 商品名称
     */
    @TableField("commodity_name")
    private String commodityName;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 主图URL
     */
    @TableField("main_image")
    private String mainImage;

    /**
     * 组合品
     */
    @TableField("is_group")
    private Byte isGroup;

    /**
     * 子SKU
     */
    @TableField("child_sku")
    private String childSku;

    /**
     * 装箱规格ID
     */
    @TableField("commodity_size_id")
    private Long commoditySizeId;

    /**
     * 装箱规格
     */
    @TableField("commodity_size_template_name")
    private String commoditySizeTemplateName;

    /**
     * 贴标方
     */
    @TableField("label_owner")
    private String labelOwner;

    /**
     * 责任方
     */
    @TableField("prep_owner")
    private String prepOwner;

    /**
     * 预处理分类
     */
    @TableField("prep_category")
    private String prepCategory;

    /**
     * 预处理类型
     */
    @TableField("prep_type")
    private String prepType;

    /**
     * 整箱
     */
    @TableField("are_cases_required")
    private Byte areCasesRequired;

    /**
     * 外箱长
     */
    @TableField("outer_box_length")
    private BigDecimal outerBoxLength;

    /**
     * 外箱宽
     */
    @TableField("outer_box_width")
    private BigDecimal outerBoxWidth;

    /**
     * 外箱高
     */
    @TableField("outer_box_height")
    private BigDecimal outerBoxHeight;

    /**
     * 外箱重
     */
    @TableField("outer_box_weight")
    private BigDecimal outerBoxWeight;

    /**
     * 有效期
     */
    @TableField("expiration_date")
    private LocalDate expirationDate;

    /**
     * 计划发货量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 装箱数量
     */
    @TableField("box_quantity")
    private Integer boxQuantity;

    /**
     * 已接收量
     */
    @TableField("quantity_received")
    private Integer quantityReceived;

    /**
     * 出库数
     */
    @TableField("ship_count")
    private Integer shipCount;

    /**
     * 箱数
     */
    @TableField("case_num")
    private Integer caseNum;

    /**
     * 每箱数量
     */
    @TableField("quantity_in_case")
    private Integer quantityInCase;

    /**
     * 申报数量
     */
    @TableField("declare_quantity")
    private Integer declareQuantity;

    /**
     * Transparency打印次数
     */
    @TableField("transparency_print_count")
    private Integer transparencyPrintCount;

    /**
     * Transparency商品
     */
    @TableField("transparency_product_flag")
    private Byte transparencyProductFlag;

    /**
     * 装箱任务号
     */
    @TableField("packing_task_sn")
    private String packingTaskSn;

    /**
     * 任务总数
     */
    @TableField("total_task_num")
    private Integer totalTaskNum;

    /**
     * 重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 材重
     */
    @TableField("material_weight")
    private BigDecimal materialWeight;

    /**
     * 计费重
     */
    @TableField("billing_weight")
    private BigDecimal billingWeight;

    /**
     * 体积
     */
    @TableField("volume")
    private BigDecimal volume;

    /**
     * 围长
     */
    @TableField("circ_length")
    private Integer circLength;

    /**
     * 在线状态
     */
    @TableField("online_status")
    private String onlineStatus;

    /**
     * 预处理指令数组
     */
    @TableField("prep_instruction_list")
    private String prepInstructionList;

    /**
     * 运单号列表
     */
    @TableField("ship_sn_list")
    private String shipSnList;

    /**
     * 箱子列表
     */
    @TableField("carton_list")
    private String cartonList;

    /**
     * 原始JSON
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
}
