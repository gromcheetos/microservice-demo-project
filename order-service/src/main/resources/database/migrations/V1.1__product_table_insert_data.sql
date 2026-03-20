ALTER TABLE order_service.platform_product ADD COLUMN IF NOT EXISTS price varchar(100);

INSERT INTO platform_product
    (product_id
    , product_name
    , price
    , stock
    , available)
VALUES
    (gen_random_uuid(), 'Product 1', '$20', 100, true);