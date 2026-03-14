ALTER TABLE user_service.platform_users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE user_service.platform_users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE user_service.platform_users ADD COLUMN IF NOT EXISTS updated_by uuid;