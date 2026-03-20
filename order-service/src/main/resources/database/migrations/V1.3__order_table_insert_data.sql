INSERT INTO platform_order
    (order_id
    , product_id
    , quantity)
VALUES (
           gen_random_uuid()
       ,(select product_id from platform_product where product_name = 'Wireless Mouse')
       ,1
       );