# 批量优化快速参考指南

## 核心改进点

### 1. 查询优化（从N次到1次）
**优化前：**
```java
// 每条订单都查询一次
for (订单 in 订单列表) {
    OrderDO existing = orderMapper.selectByOrderId(订单号);  // N次查询
}
```

**优化后：**
```java
// 一次查询所有订单
List<String> orderIds = 收集所有订单号();
List<OrderDO> existingOrders = orderMapper.selectBatchIds(orderIds);  // 1次查询
Map<String, OrderDO> orderMap = 转换为Map();  // O(1)查找
```

### 2. 插入/更新优化（从N次到2次）
**优化前：**
```java
for (订单 in 订单列表) {
    if (存在) {
        orderMapper.updateById(订单);  // N次更新
    } else {
        orderMapper.insert(订单);  // N次插入
    }
}
```

**优化后：**
```java
List<OrderDO> toInsert = 收集需要插入的();
List<OrderDO> toUpdate = 收集需要更新的();
orderMapper.saveBatch(toInsert);      // 1次批量插入
orderMapper.updateBatchById(toUpdate); // 1次批量更新
```

## 数据库操作对比

| 操作类型 | 优化前 | 优化后 | 减少比例 |
|---------|--------|--------|----------|
| 订单查询 | 200次 | 1次 | 99.5% |
| 买家查询 | 200次 | 1次 | 99.5% |
| 扩展查询 | 600次 | 3次 | 99.5% |
| 商品查询 | 800次 | 2次 | 99.75% |
| 插入更新 | 2000次 | 10次 | 99.5% |
| **总计** | **3800次** | **17次** | **99.55%** |

## 关键代码片段

### 批量查询模式
```java
// 1. 收集ID
List<String> amazonOrderIds = new ArrayList<>();
for (int i = 0; i < list.size(); i++) {
    amazonOrderIds.add(list.getJSONObject(i).getString("amazonOrderId"));
}

// 2. 批量查询
List<OrderDO> existingOrders = list(new LambdaQueryWrapper<OrderDO>()
        .in(OrderDO::getAmazonOrderId, amazonOrderIds));

// 3. 转换为Map便于查找
Map<String, OrderDO> existingOrderMap = existingOrders.stream()
        .collect(Collectors.toMap(OrderDO::getAmazonOrderId, o -> o));
```

### 批量保存模式
```java
// 1. 准备数据列表
List<OrderDO> ordersToInsert = new ArrayList<>();
List<OrderDO> ordersToUpdate = new ArrayList<>();

// 2. 分类数据
for (订单 in 订单列表) {
    if (existingOrderMap.containsKey(订单号)) {
        ordersToUpdate.add(订单);
    } else {
        ordersToInsert.add(订单);
    }
}

// 3. 批量操作
if (!ordersToInsert.isEmpty()) {
    saveBatch(ordersToInsert);
}
if (!ordersToUpdate.isEmpty()) {
    updateBatchById(ordersToUpdate);
}
```

## 性能测试命令

### 测试200条订单
```bash
# 记录开始时间
echo "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"

# 执行任务（通过XXL-JOB触发）
# 或直接调用API测试

# 记录结束时间
echo "结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
```

### 查看日志
```bash
# 查看处理日志
tail -f logs/xxl-job-executor.log | grep "批量处理完成"
```

## 监控指标

### 关键日志输出
```
批量处理完成，共处理 200 条订单
```

### 数据库监控
```sql
-- 查看慢查询
SHOW PROCESSLIST;

-- 查看表大小
SELECT 
    table_name,
    table_rows,
    ROUND(data_length/1024/1024, 2) AS 'Data Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'your_database'
AND table_name LIKE 'amazon_%'
ORDER BY data_length DESC;
```

## 故障排查

### 问题1：批量插入失败
**现象：** 部分数据未保存
**排查：**
1. 检查事务是否回滚
2. 查看错误日志
3. 验证数据完整性

### 问题2：性能未提升
**排查：**
1. 确认索引是否创建（参考 `doc/db/optimize_indexes.sql`）
2. 检查数据库连接池配置
3. 查看网络延迟

### 问题3：内存占用过高
**解决：**
1. 考虑分批处理（每批500条）
2. 及时清理临时Map对象
3. 调整JVM堆内存

## 最佳实践

1. **批量大小控制**：建议每批200-1000条
2. **事务边界**：整个批量操作在一个事务中
3. **错误处理**：使用`@Transactional(rollbackFor = Exception.class)`
4. **日志记录**：只记录关键节点，避免过多日志
5. **索引优化**：确保查询字段都有索引

## 相关文档
- 详细优化报告：`doc/BATCH_OPTIMIZATION_SUMMARY.md`
- 索引优化：`doc/db/INDEX_OPTIMIZATION_README.md`
- 数据库表结构：`doc/Amazon订单数据库表结构文档.md`
