INSERT INTO platform_users
    (id,
    name,
    email,
    created_at,
    updated_at,
    updated_by
) VALUES
    (
    gen_random_uuid(),
    'Alice Kim',
    'alice@example.com',
    NOW(),
    NOW(),
    NULL
    ),
    (
    gen_random_uuid(),
    'Brian Lee',
    'brian@example.com',
    NOW(),
    NOW(),
    NULL
    ),
    (
    gen_random_uuid(),
    'Chris Park',
    'chris@example.com',
    NOW(),
    NOW(),
    NULL
    );