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
 * FBA发货详情
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
@Getter
@Setter
@ToString
@TableName("erp_fba_shipment_details")
public class FbaShipmentDetailsDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 货件号
     */
    @TableField("job_no")
    private String jobNo;

    /**
     * 原始JSON
     */
    @TableField("raw_json")
    private String rawJson;

    /**
     * 是否分析
     */
    @TableField("analyzed")
    private Integer analyzed;

    /**
     * 是否同步
     */
    @TableField("synced")
    private Integer synced;

    /**
     * 件数
     */
    @TableField("pcs")
    private Integer pcs;

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
     * 收件人电话
     */
    @TableField("re_tel")
    private String reTel;

    /**
     * 收件人邮编
     */
    @TableField("re_zip")
    private Integer reZip;

    /**
     * 参考号/追踪号
     */
    @TableField("refno")
    private String refno;

    /**
     * 实际重量
     */
    @TableField("actual")
    private BigDecimal actual;

    /**
     * 收件人姓名
     */
    @TableField("re_name")
    private String reName;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 发货日期时间戳（毫秒）
     */
    @TableField("sd_date")
    private Long sdDate;

    /**
     * 分区大小
     */
    @TableField("div_size")
    private Integer divSize;

    /**
     * 枢纽类型
     */
    @TableField("hub_type")
    private String hubType;

    /**
     * 签收件数
     */
    @TableField("sign_pcs")
    private Integer signPcs;

    /**
     * 客户名称
     */
    @TableField("cust_name")
    private String custName;

    /**
     * 申报价值
     */
    @TableField("dec_value")
    private BigDecimal decValue;

    /**
     * 目的地名称
     */
    @TableField("dest_name")
    private String destName;

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
     * 入库代码
     */
    @TableField("hub_in_code")
    private String hubInCode;

    /**
     * 入库名称
     */
    @TableField("hub_in_name")
    private String hubInName;

    /**
     * 是否偏远地区（0否1是）
     */
    @TableField("isfaraway")
    private Byte isfaraway;

    /**
     * 收件公司
     */
    @TableField("re_company")
    private String reCompany;

    /**
     * 地址类型编码
     */
    @TableField("address_type")
    private String addressType;

    /**
     * 申报货币
     */
    @TableField("dec_value_cur")
    private String decValueCur;

    /**
     * 枢纽类型名称
     */
    @TableField("hub_type_name")
    private String hubTypeName;

    /**
     * 问题类型
     */
    @TableField("problem_type")
    private Integer problemType;

    /**
     * 参考号2
     */
    @TableField("referenceno")
    private String referenceno;

    /**
     * 解锁状态
     */
    @TableField("unlock_state")
    private Byte unlockState;

    /**
     * 类别类型
     */
    @TableField("category_type")
    private String categoryType;

    /**
     * 客户类型
     */
    @TableField("cocustom_type")
    private String cocustomType;

    /**
     * 货物类型名称
     */
    @TableField("goods_type_name")
    private String goodsTypeName;

    /**
     * 收费标准
     */
    @TableField("chargestandard")
    private BigDecimal chargestandard;

    /**
     * 最后扫描备注
     */
    @TableField("last_scan_remark")
    private String lastScanRemark;

    /**
     * 地址类型名称
     */
    @TableField("address_type_name")
    private String addressTypeName;

    /**
     * 最后扫描站点
     */
    @TableField("last_scan_station")
    private String lastScanStation;

    /**
     * 类别类型名称
     */
    @TableField("category_type_name")
    private String categoryTypeName;

    /**
     * 客户类型名称
     */
    @TableField("cocustom_type_name")
    private String cocustomTypeName;

    /**
     * 最后扫描状态名称
     */
    @TableField("last_scan_status_name")
    private String lastScanStatusName;

    /**
     * 清单状态名称
     */
    @TableField("manifest_status_name")
    private String manifestStatusName;

    /**
     * 创建者（这个是运营，谁的订单，这里就是谁）
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

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
}
