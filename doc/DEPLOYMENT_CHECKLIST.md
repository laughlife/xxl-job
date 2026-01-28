# XXL-JOB 任务分组改造 - 部署检查清单

## 部署前准备

### 1. 备份 ✅
- [ ] 备份数据库
- [ ] 备份现有代码
- [ ] 记录当前版本号

### 2. 环境检查 ✅
- [ ] 确认MySQL版本 >= 5.7
- [ ] 确认Java版本 >= 8
- [ ] 确认有数据库操作权限
- [ ] 确认有代码部署权限

## 数据库迁移

### 1. 执行迁移脚本 ✅
```bash
# 连接到数据库
mysql -u用户名 -p密码

# 执行迁移脚本
source doc/db/migration_task_group.sql;

# 或者使用命令行
mysql -u用户名 -p密码 < doc/db/migration_task_group.sql
```

### 2. 验证数据库改造 ✅
```sql
-- 检查任务组表是否创建成功
SHOW TABLES LIKE 'xxl_job_task_group';

-- 检查任务组表结构
DESC xxl_job_task_group;

-- 检查任务表是否添加了新字段
DESC xxl_job_info;

-- 查看是否有默认任务组
SELECT * FROM xxl_job_task_group;

-- 检查索引是否创建成功
SHOW INDEX FROM xxl_job_info WHERE Key_name = 'idx_task_group_id';
SHOW INDEX FROM xxl_job_task_group WHERE Key_name = 'idx_job_group_id';
```

### 3. 数据迁移验证 ✅
```sql
-- 检查现有任务是否都关联到了默认任务组
SELECT 
    COUNT(*) as total_jobs,
    SUM(CASE WHEN task_group_id IS NOT NULL THEN 1 ELSE 0 END) as jobs_with_group,
    SUM(CASE WHEN task_group_id IS NULL THEN 1 ELSE 0 END) as jobs_without_group
FROM xxl_job_info;
```

## 代码部署

### 1. 编译项目 ✅
```bash
# 进入项目根目录
cd /path/to/xxl-job

# 清理并编译
mvn clean package -DskipTests

# 或者包含测试
mvn clean package
```

### 2. 检查编译结果 ✅
- [ ] 编译成功，无错误
- [ ] 生成了 xxl-job-admin.jar
- [ ] 检查jar包大小是否正常

### 3. 部署新版本 ✅
```bash
# 停止旧服务
./stop.sh

# 备份旧jar包
cp xxl-job-admin.jar xxl-job-admin.jar.backup

# 复制新jar包
cp target/xxl-job-admin.jar ./

# 启动新服务
./start.sh

# 查看启动日志
tail -f logs/xxl-job-admin.log
```

## 功能测试

### 1. 基础功能测试 ✅

#### 1.1 任务组管理
- [ ] 访问 http://localhost:8080/xxl-job-admin
- [ ] 登录系统（admin/123456）
- [ ] 测试查询任务组列表API
- [ ] 测试新增任务组
- [ ] 测试更新任务组
- [ ] 测试删除任务组（需要先确保没有关联任务）

#### 1.2 任务管理
- [ ] 创建新任务时可以选择任务组
- [ ] 创建新任务时可以设置组内排序
- [ ] 任务列表可以按任务组筛选
- [ ] 编辑任务时可以修改任务组和排序

#### 1.3 顺序执行
- [ ] 创建多个任务并关联到同一个任务组
- [ ] 设置不同的task_order值
- [ ] 调用执行任务组API
- [ ] 手动触发第一个任务
- [ ] 验证任务是否按顺序执行

### 2. API测试 ✅

使用提供的测试脚本：

**Linux/Mac:**
```bash
chmod +x doc/test_task_group_api.sh
./doc/test_task_group_api.sh
```

**Windows:**
```cmd
doc\test_task_group_api.bat
```

### 3. 兼容性测试 ✅
- [ ] 现有任务是否正常运行
- [ ] 现有任务的调度是否正常
- [ ] 现有任务的日志是否正常
- [ ] 不使用任务组功能时，系统是否正常

### 4. 性能测试 ✅
- [ ] 查询任务列表的响应时间
- [ ] 按任务组筛选的响应时间
- [ ] 大量任务时的查询性能
- [ ] 数据库索引是否生效

## 回滚方案

### 如果出现问题，按以下步骤回滚：

#### 1. 代码回滚 ✅
```bash
# 停止新服务
./stop.sh

# 恢复旧jar包
cp xxl-job-admin.jar.backup xxl-job-admin.jar

# 启动旧服务
./start.sh
```

#### 2. 数据库回滚 ✅
```sql
-- 删除任务组表
DROP TABLE IF EXISTS xxl_job_task_group;

-- 删除任务表的新字段
ALTER TABLE xxl_job_info DROP COLUMN task_group_id;
ALTER TABLE xxl_job_info DROP COLUMN task_order;
ALTER TABLE xxl_job_info DROP INDEX idx_task_group_id;
```

**注意**: 数据库回滚会丢失所有任务组数据，请谨慎操作！

## 监控和日志

### 1. 日志检查 ✅
- [ ] 检查应用启动日志
- [ ] 检查是否有错误日志
- [ ] 检查SQL执行日志
- [ ] 检查API调用日志

### 2. 监控指标 ✅
- [ ] CPU使用率
- [ ] 内存使用率
- [ ] 数据库连接数
- [ ] API响应时间
- [ ] 任务执行成功率

## 文档更新

### 1. 更新文档 ✅
- [ ] 更新用户手册
- [ ] 更新API文档
- [ ] 更新部署文档
- [ ] 更新版本说明

### 2. 培训和通知 ✅
- [ ] 通知相关人员新功能上线
- [ ] 提供使用培训
- [ ] 准备FAQ文档
- [ ] 建立问题反馈渠道

## 常见问题

### Q1: 数据库迁移脚本执行失败？
**A**: 检查数据库权限，确保有CREATE TABLE和ALTER TABLE权限。脚本包含了存在性检查，可以重复执行。

### Q2: 编译失败？
**A**: 检查Maven配置，确保依赖都能正常下载。可以尝试 `mvn clean install -U` 强制更新依赖。

### Q3: 启动后访问API返回404？
**A**: 检查Controller是否被Spring扫描到，确认包路径是否正确。

### Q4: 任务组列表查询为空？
**A**: 检查数据库迁移脚本是否执行成功，确认是否创建了默认任务组。

### Q5: 现有任务无法执行？
**A**: 检查任务表的新字段是否设置为可空，确认兼容性。如有问题，立即回滚。

## 联系方式

如有问题，请联系：
- 开发负责人：李伟
- 邮箱：[your-email@example.com]
- 电话：[your-phone]

## 版本信息

- 改造版本：v1.0
- 改造日期：2026-01-27
- 基础版本：XXL-JOB 2.x
