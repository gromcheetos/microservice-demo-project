INSERT INTO platform_product
(product_id
, product_name
, price
, stock
, available
)
VALUES (
           gen_random_uuid(), 'Wireless Mouse', '29.99', 50, true
       ),
       (
           gen_random_uuid(), 'Mechanical Keyboard', '45.49', 0, false
       ),
       (
            gen_random_uuid(), 'USB-C Hub', '38.00', 25, true
       );