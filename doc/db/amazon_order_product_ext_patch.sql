SET @schema := DATABASE();

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'source_item_id'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN source_item_id BIGINT COMMENT ''Sellfox系统商品明细ID'' AFTER amazon_order_id',
  'SELECT ''amazon_order_product_ext.source_item_id already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'promotion_discount'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN promotion_discount DECIMAL(12,4) DEFAULT 0 COMMENT ''促销折扣''',
  'SELECT ''amazon_order_product_ext.promotion_discount already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'amazon_back_to_article'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN amazon_back_to_article DECIMAL(12,4) DEFAULT 0 COMMENT ''亚马逊退款''',
  'SELECT ''amazon_order_product_ext.amazon_back_to_article already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'merge_purchase_cost'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN merge_purchase_cost DECIMAL(12,4) DEFAULT 0 COMMENT ''合并采购成本''',
  'SELECT ''amazon_order_product_ext.merge_purchase_cost already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'purchase_cost'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN purchase_cost DECIMAL(12,4) DEFAULT 0 COMMENT ''采购成本''',
  'SELECT ''amazon_order_product_ext.purchase_cost already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'head_trip_cost'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN head_trip_cost DECIMAL(12,4) DEFAULT 0 COMMENT ''头程费用''',
  'SELECT ''amazon_order_product_ext.head_trip_cost already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'head_trip_share'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN head_trip_share TINYINT DEFAULT 0 COMMENT ''头程分摊''',
  'SELECT ''amazon_order_product_ext.head_trip_share already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'fbm_ship_cost'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN fbm_ship_cost DECIMAL(12,4) DEFAULT 0 COMMENT ''FBM运费''',
  'SELECT ''amazon_order_product_ext.fbm_ship_cost already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'capital_currency'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN capital_currency VARCHAR(8) DEFAULT '''' COMMENT ''资金币种''',
  'SELECT ''amazon_order_product_ext.capital_currency already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'capital_date'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN capital_date DATETIME DEFAULT NULL COMMENT ''资金日期''',
  'SELECT ''amazon_order_product_ext.capital_date already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'commission_currency'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN commission_currency VARCHAR(8) DEFAULT '''' COMMENT ''佣金币种''',
  'SELECT ''amazon_order_product_ext.commission_currency already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'commission_date'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN commission_date DATETIME DEFAULT NULL COMMENT ''佣金日期''',
  'SELECT ''amazon_order_product_ext.commission_date already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND COLUMN_NAME = 'evaluation'
);

SET @ddl := IF(
  @col_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD COLUMN evaluation INT DEFAULT 0 COMMENT ''评价数''',
  'SELECT ''amazon_order_product_ext.evaluation already exists'''
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_exists := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = @schema
    AND TABLE_NAME = 'amazon_order_product_ext'
    AND INDEX_NAME = 'uk_order_item'
);

SET @ddl2 := IF(
  @idx_exists = 0,
  'ALTER TABLE amazon_order_product_ext ADD UNIQUE INDEX uk_order_item (amazon_order_id, source_item_id)',
  'SELECT ''amazon_order_product_ext.uk_order_item already exists'''
);

PREPARE stmt2 FROM @ddl2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;
