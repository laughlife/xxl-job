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
import java.time.LocalDateTime;

/**
 * <p>
 * FBA发件/子单表
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_dispatch")
public class FbaDispatchDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始发件报文
     */
    @TableField("raw_json")
    private String rawJson;

    /**
     * 是否同步
     */
    @TableField("synced")
    private Integer synced;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 是否分析
     */
    @TableField("analyzed")
    private Integer analyzed;

    /**
     * FBA货件ID
     */
    @TableField("fba_shipment_id")
    private Long fbaShipmentId;

    /**
     * 发件任务号
     */
    @TableField("job_no")
    private String jobNo;

    /**
     * 主转单号
     */
    @TableField("transfer_order_no")
    private String transferOrderNo;

    /**
     * 件数
     */
    @TableField("pcs")
    private Integer pcs;

    /**
     * 数量
     */
    @TableField("qty")
    private Integer qty;

    /**
     * 体积
     */
    @TableField("vol")
    private BigDecimal vol;

    /**
     * 重量
     */
    @TableField("weig")
    private BigDecimal weig;

    /**
     * 参考重量
     */
    @TableField("rweig")
    private BigDecimal rweig;

    /**
     * 实际重量
     */
    @TableField("actual")
    private BigDecimal actual;

    /**
     * 付费重量
     */
    @TableField("pay_weig")
    private BigDecimal payWeig;

    /**
     * 是否偏远地区（0否1是）
     */
    @TableField("isfaraway")
    private Byte isfaraway;

    /**
     * 收件人电话（加密）
     */
    @TableField("re_tel")
    private String reTel;

    /**
     * 收件人邮编（加密）
     */
    @TableField("re_zip")
    private String reZip;

    /**
     * 收件人姓名（加密）
     */
    @TableField("re_name")
    private String reName;

    /**
     * 收件公司（加密）
     */
    @TableField("re_company")
    private String reCompany;

    /**
     * 收件地址1（加密）
     */
    @TableField("re_addr")
    private String reAddr;

    /**
     * 收件城市（加密）
     */
    @TableField("re_city")
    private String reCity;

    /**
     * 收件地址2（加密）
     */
    @TableField("re_addr2")
    private String reAddr2;

    /**
     * 收件地址3（加密）
     */
    @TableField("re_addr3")
    private String reAddr3;

    /**
     * 收件州/省（加密）
     */
    @TableField("re_state")
    private String reState;

    /**
     * 收件国家（加密）
     */
    @TableField("re_country")
    private String reCountry;

    /**
     * 参考号/追踪号
     */
    @TableField("refno")
    private String refno;

    /**
     * 客户编号
     */
    @TableField("cust_no")
    private String custNo;

    /**
     * 主运单ID
     */
    @TableField("oawb_id")
    private Integer oawbId;

    /**
     * 收件人代码
     */
    @TableField("re_code")
    private String reCode;

    /**
     * 参考号2
     */
    @TableField("referenceno")
    private String referenceno;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 客户名称
     */
    @TableField("cust_name")
    private String custName;

    /**
     * 目的地代码
     */
    @TableField("dest_code")
    private String destCode;

    /**
     * 目的地名称
     */
    @TableField("dest_name")
    private String destName;

    /**
     * CC费用
     */
    @TableField("cc_charge")
    private BigDecimal ccCharge;

    /**
     * 申报价值（加密）
     */
    @TableField("dec_value")
    private String decValue;

    /**
     * 费用总计（加密）
     */
    @TableField("fee_count")
    private String feeCount;

    /**
     * COD费用
     */
    @TableField("cod_charge")
    private BigDecimal codCharge;

    /**
     * 收费标准
     */
    @TableField("chargestandard")
    private BigDecimal chargestandard;

    /**
     * 包装类型
     */
    @TableField("pack_type")
    private String packType;

    /**
     * 货物类型
     */
    @TableField("goods_type")
    private String goodsType;

    /**
     * 货物类型名称
     */
    @TableField("goods_type_name")
    private String goodsTypeName;

    /**
     * 货物描述
     */
    @TableField("descr_name")
    private String descrName;

    /**
     * 货物描述（英文）
     */
    @TableField("descr_name_en")
    private String descrNameEn;

    /**
     * 入库代码
     */
    @TableField("hub_in_code")
    private String hubInCode;

    /**
     * 入库名称（加密）
     */
    @TableField("hub_in_name")
    private String hubInName;

    /**
     * 出库代码
     */
    @TableField("hub_out_code")
    private String hubOutCode;

    /**
     * 出库名称
     */
    @TableField("hub_out_name")
    private String hubOutName;

    /**
     * 发货日期时间戳（毫秒）
     */
    @TableField("sd_date")
    private Long sdDate;

    /**
     * 发货站点
     */
    @TableField("sd_station")
    private String sdStation;

    /**
     * CC支付方式
     */
    @TableField("cc_payment")
    private String ccPayment;

    /**
     * CC费用货币
     */
    @TableField("cc_charge_cur")
    private String ccChargeCur;

    /**
     * 申报货币（加密）
     */
    @TableField("dec_value_cur")
    private String decValueCur;

    /**
     * COD费用货币
     */
    @TableField("cod_charge_cur")
    private String codChargeCur;

    /**
     * CCTAX支付方式
     */
    @TableField("cctax_payment")
    private String cctaxPayment;

    /**
     * 收费标准货币
     */
    @TableField("chargestandard_cur")
    private String chargestandardCur;

    /**
     * 客户类型
     */
    @TableField("cocustom_type")
    private String cocustomType;

    /**
     * 客户类型名称
     */
    @TableField("cocustom_type_name")
    private String cocustomTypeName;

    /**
     * 计费规则名称
     */
    @TableField("flip_rule_name")
    private String flipRuleName;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 文件列表
     */
    @TableField("file_list")
    private String fileList;

    /**
     * 清单件数
     */
    @TableField("manifest_pcs")
    private String manifestPcs;

    /**
     * 普通发票
     */
    @TableField("common_invoice")
    private String commonInvoice;

    /**
     * 海关文件列表
     */
    @TableField("customs_file_list")
    private String customsFileList;

    /**
     * 子清单列表
     */
    @TableField("manifest_child_list")
    private String manifestChildList;

    /**
     * 问题信息DTO列表
     */
    @TableField("problem_info_dto_list")
    private String problemInfoDtoList;

    /**
     * 清单结算DTO列表
     */
    @TableField("manifest_settle_dto_list")
    private String manifestSettleDtoList;

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
     * 创建者（这个是运营，谁的订单，这里就是谁）
     */
    @TableField("creator")
    private String creator;
}
