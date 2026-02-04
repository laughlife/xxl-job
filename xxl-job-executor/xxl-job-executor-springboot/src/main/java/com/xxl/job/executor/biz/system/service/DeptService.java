package com.xxl.job.executor.biz.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.system.entity.DeptDO;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
public interface DeptService extends IService<DeptDO> {
    DeptDO getDept(Long id);

    boolean insertOrUpdateDept(DeptDO deptDO);

    public List<DeptDO> getAllDept();
}
