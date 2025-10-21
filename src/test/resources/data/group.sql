INSERT INTO groups (id, name, description, currency, max_budget, start_date, end_date, profile_photo_id, profile_id)
VALUES
    (1, 'Wakacje 2025', 'Wspólny wyjazd na wakacje', 'PLN', 5000.00, '2025-07-01 00:00:00', '2025-07-15 23:59:59', NULL, NULL),
    (2, 'Projekt osobisty', 'Mój projekt', 'PLN', 2000.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', NULL, NULL);


INSERT INTO user_group (user_id, group_id, role)
VALUES
    (1, 1, 'ADMIN'),
    (2, 1, 'USER'),
    (3, 2, 'ADMIN');

