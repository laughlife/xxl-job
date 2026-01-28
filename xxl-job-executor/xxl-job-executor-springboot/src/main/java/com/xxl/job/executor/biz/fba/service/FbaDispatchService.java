package com.xxl.job.executor.biz.fba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.executor.biz.fba.entity.FbaDispatchDO;

/**
 * <p>
 * FBA发件/子单表 服务类
 * </p>
 *
 * @author Li Wei
 * @since 2026-01-28
 */
public interface FbaDispatchService extends IService<FbaDispatchDO> {

    /**
     * 处理lmt下载的fba发货数据
     */
    void analyzeLmtDownloadData();

}
