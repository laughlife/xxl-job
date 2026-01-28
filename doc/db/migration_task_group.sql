-- ========================================
-- XXL-JOB 任务分组改造 - 数据库迁移脚本
-- 版本: v1.0
-- 日期: 2026-01-27
-- 说明: 添加任务组功能，支持任务分组管理和顺序执行
-- ========================================

USE `xxl_job`;

-- ========================================
-- 1. 创建任务组表
-- ========================================

-- 检查表是否存在，不存在则创建
CREATE TABLE IF NOT EXISTS `xxl_job_task_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group_id` int(11) NOT NULL COMMENT '执行器ID',
  `group_name` varchar(100) NOT NULL COMMENT '任务组名称',
  `group_desc` varchar(255) DEFAULT NULL COMMENT '任务组描述',
  `group_order` int(11) NOT NULL DEFAULT '0' COMMENT '任务组排序',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_job_group_id` (`job_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务组表';

-- ========================================
-- 2. 修改任务表 - 添加任务组字段
-- ========================================

-- 检查字段是否存在，不存在则添加
SET @dbname = DATABASE();
SET @tablename = 'xxl_job_info';
SET @columnname = 'task_group_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' int(11) DEFAULT NULL COMMENT ''任务组ID'' AFTER job_group')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查字段是否存在，不存在则添加
SET @columnname = 'task_order';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' int(11) NOT NULL DEFAULT ''0'' COMMENT ''任务组内排序'' AFTER task_group_id')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查索引是否存在，不存在则添加
SET @indexname = 'idx_task_group_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (INDEX_NAME = @indexname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD INDEX ', @indexname, ' (task_group_id)')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- ========================================
-- 3. 初始化数据（可选）
-- ========================================

-- 为现有执行器创建默认任务组（仅在任务组表为空时执行）
INSERT INTO xxl_job_task_group (job_group_id, group_name, group_desc, group_order, add_time, update_time)
SELECT 
    jg.id, 
    CONCAT(jg.title, '-默认组'), 
    '系统自动创建的默认任务组', 
    0, 
    NOW(), 
    NOW()
FROM xxl_job_group jg
WHERE NOT EXISTS (
    SELECT 1 FROM xxl_job_task_group tg WHERE tg.job_group_id = jg.id
);

-- 将现有任务关联到默认任务组（仅更新未关联任务组的任务）
UPDATE xxl_job_info ji
INNER JOIN xxl_job_task_group tg ON ji.job_group = tg.job_group_id AND tg.group_name LIKE '%-默认组'
SET ji.task_group_id = tg.id
WHERE ji.task_group_id IS NULL;

-- ========================================
-- 4. 验证脚本
-- ========================================

-- 查看任务组表
SELECT '任务组表记录数:' AS info, COUNT(*) AS count FROM xxl_job_task_group;

-- 查看已关联任务组的任务数
SELECT '已关联任务组的任务数:' AS info, COUNT(*) AS count FROM xxl_job_info WHERE task_group_id IS NOT NULL;

-- 查看未关联任务组的任务数
SELECT '未关联任务组的任务数:' AS info, COUNT(*) AS count FROM xxl_job_info WHERE task_group_id IS NULL;

COMMIT;
