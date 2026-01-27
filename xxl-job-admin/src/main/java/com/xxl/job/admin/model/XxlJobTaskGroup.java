package com.xxl.job.admin.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务组实体
 * 
 * @author liwei
 */
@Data
public class XxlJobTaskGroup {
    
    private int id;                 // 主键ID
    private int jobGroupId;         // 执行器ID
    private String groupName;       // 任务组名称
    private String groupDesc;       // 任务组描述
    private int groupOrder;         // 任务组排序
    private Date addTime;           // 创建时间
    private Date updateTime;        // 更新时间
    
}
