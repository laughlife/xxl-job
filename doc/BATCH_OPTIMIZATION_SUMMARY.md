# Amazon订单批量处理优化总结

## 优化目标
将原有的单条记录处理逻辑改为批量处理，减少数据库查询次数，提升订单同步性能。

## 优化前的问题
1. **N+1查询问题**：每条订单都单独查询数据库，导致大量重复查询
2. **单条插入/更新**：每条记录都单独执行INSERT/UPDATE操作
3. **性能瓶颈**：处理200条订单需要执行数千次数据库操作

## 优化方案

### 1. 批量查询优化
- 一次性查询所有订单号对应的现有数据
- 使用Map结构缓存查询结果，避免重复查询
- 批量查询涉及的表：
  - `amazon_order` - 订单主表
  - `amazon_buyer` - 买家信息
  - `amazon_order_ext` - 订单扩展
  - `amazon_order_evaluation` - 订单评价
  - `amazon_order_product` - 订单商品
  - `amazon_order_product_ext` - 商品扩展

### 2. 批量插入/更新优化
- 将需要插入和更新的数据分别收集到List中
- 使用MyBatis-Plus的`saveBatch()`和`updateBatchById()`方法
- 一次性提交所有数据，减少数据库交互次数

### 3. 代码结构优化
创建了专门的构建方法，提高代码可读性：
- `buildOrder()` - 构建订单对象
- `buildOrderExt()` - 构建订单扩展对象
- `buildOrderEvaluation()` - 构建订单评价对象
- `buildOrderProduct()` - 构建订单商品对象
- `buildOrderProductExt()` - 构建商品扩展对象
- `batchSaveOrderProducts()` - 批量处理订单商品

## 核心实现逻辑

### saveQuerySellfoxOrder() 方法流程

```
1. 批量收集所有订单号
   ↓
2. 批量查询现有订单（一次查询）
   ↓
3. 批量查询买家信息（一次查询）
   ↓
4. 批量查询订单扩展（一次查询）
   ↓
5. 批量查询订单评价（一次查询）
   ↓
6. 遍历处理每条订单
   - 检查lastUpdateDate判断是否需要更新
   - 将数据分类到insert/update列表
   ↓
7. 批量保存买家（一次操作）
   ↓
8. 批量插入/更新订单（两次操作）
   ↓
9. 批量插入/更新扩展表（六次操作）
   ↓
10. 批量处理订单商品（调用batchSaveOrderProducts）
```

### batchSaveOrderProducts() 方法流程

```
1. 收集所有商品的订单号和商品ID
   ↓
2. 批量查询现有商品（一次查询）
   ↓
3. 批量查询现有商品扩展（一次查询）
   ↓
4. 遍历处理每个商品
   - 将数据分类到insert/update列表
   ↓
5. 批量插入/更新商品（两次操作）
   ↓
6. 批量插入/更新商品扩展（两次操作）
```

## 性能提升预估

### 优化前（单条处理）
处理200条订单，每条订单2个商品：
- 订单查询：200次
- 买家查询：200次
- 扩展查询：200次 × 3 = 600次
- 商品查询：400次 × 2 = 800次
- 插入/更新：约2000次数据库操作
- **总计：约3800次数据库操作**

### 优化后（批量处理）
处理200条订单，每条订单2个商品：
- 批量查询：5次（订单、买家、扩展×3）
- 批量商品查询：2次
- 批量插入/更新：约10次（分insert/update）
- **总计：约17次数据库操作**

### 性能提升
- **数据库操作次数减少：约99.5%**
- **预计处理速度提升：10-50倍**（取决于网络延迟和数据库负载）

## 关键技术点

### 1. Map缓存策略
```java
Map<String, OrderDO> existingOrderMap = existingOrders.stream()
    .collect(java.util.stream.Collectors.toMap(OrderDO::getAmazonOrderId, o -> o));
```

### 2. 批量操作
```java
if (!ordersToInsert.isEmpty()) {
    saveBatch(ordersToInsert);
}
if (!ordersToUpdate.isEmpty()) {
    updateBatchById(ordersToUpdate);
}
```

### 3. 事务控制
```java
@Transactional(rollbackFor = Exception.class)
public void saveQuerySellfoxOrder(JSONArray list) {
    // 所有操作在一个事务中完成
}
```

## 已删除的旧方法
以下单条处理方法已被移除：
- `saveBuyer()` - 单条保存买家
- `saveOrUpdateOrder()` - 单条保存/更新订单
- `saveOrUpdateOrderExt()` - 单条保存/更新订单扩展
- `saveOrUpdateOrderEvaluation()` - 单条保存/更新订单评价
- `saveOrUpdateOrderProduct()` - 单条保存/更新订单商品
- `saveOrUpdateOrderProductExt()` - 单条保存/更新商品扩展

## 注意事项

1. **买家ID处理**：买家需要先批量保存获取ID后，才能更新到订单中
2. **lastUpdateDate判断**：保持原有逻辑，只有lastUpdateDate变化才更新
3. **事务一致性**：所有操作在同一事务中，保证数据一致性
4. **空值处理**：保持原有的空值处理逻辑（空字符串转NULL）

## 测试建议

1. **功能测试**：验证数据是否正确保存到各个表
2. **性能测试**：对比优化前后的处理时间
3. **压力测试**：测试大批量数据（1000+订单）的处理能力
4. **异常测试**：验证事务回滚是否正常工作

## 后续优化方向

1. **分批处理**：如果单次数据量过大（如5000+订单），可以考虑分批处理
2. **异步处理**：对于非关键路径的数据，可以考虑异步保存
3. **缓存优化**：对于频繁查询的买家信息，可以考虑使用Redis缓存
4. **监控告警**：添加性能监控，及时发现性能问题

## 文件变更
- 修改文件：`xxl-job-executor/xxl-job-executor-springboot/src/main/java/com/xxl/job/executor/biz/amazon/service/impl/OrderServiceImpl.java`
- 变更行数：约300行代码重构

## 完成时间
2026-01-29
