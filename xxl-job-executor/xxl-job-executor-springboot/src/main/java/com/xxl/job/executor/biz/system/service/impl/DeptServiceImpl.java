package com.xxl.job.executor.biz.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.executor.biz.system.entity.DeptDO;
import com.xxl.job.executor.biz.system.mapper.DeptMapper;
import com.xxl.job.executor.biz.system.service.DeptService;
import com.xxl.job.executor.mybatis.core.query.LambdaQueryWrapperX;

import jakarta.annotation.Resource;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author Li Wei
 * @since 2026-02-04
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, DeptDO> implements DeptService {
    @Resource
    private DeptMapper deptMapper;

    public DeptDO getDept(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public boolean insertOrUpdateDept(DeptDO deptDO) {
        return deptMapper.insertOrUpdate(deptDO);
    }

    @Override
    public List<DeptDO> getAllDept() {
        List<DeptDO> list = deptMapper.selectList(
                new LambdaQueryWrapperX<DeptDO>()
                        .eq(DeptDO::getLocked, 0)
        );
        return list;
    }
}
