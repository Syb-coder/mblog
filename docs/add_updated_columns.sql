-- ============================================
-- 为各业务表添加 updated（最后修改时间）字段
-- 适用数据库：MySQL
-- 说明：项目使用 ddl-auto: update，Hibernate 会自动添加该列，
--       此脚本仅作手动执行参考
-- ============================================

ALTER TABLE mto_post ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';

ALTER TABLE mto_comment ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';

ALTER TABLE mto_user ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';

ALTER TABLE mto_channel ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';

ALTER TABLE mto_tag ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';

ALTER TABLE mto_options ADD COLUMN updated DATETIME NULL COMMENT '最后修改时间';