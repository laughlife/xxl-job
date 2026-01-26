package com.xxl.job.admin.python;

import java.util.Date;

import lombok.Data;

@Data
public class XxlJobPython {
    private int id;
    private String name;
    private String version;
    private String execPath;
    private String remark;
    private Date addTime;
    private Date updateTime;
}
