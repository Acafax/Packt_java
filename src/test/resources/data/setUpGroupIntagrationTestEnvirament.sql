INSERT INTO users (id, uid, name, email, country, date_of_birth, profile_photo_id, profile_photo)
VALUES
    (1, 'test-uid-001', 'Jan Kowalski', 'jan.kowalski@example.com', 'Poland', '1990-05-15', NULL, NULL),
    (2, 'test-uid-002', 'Anna Nowak', 'anna.nowak@example.com', 'Poland', '1992-08-22', NULL, NULL),
    (3, 'test-uid-003', 'Piotr Wiśniewski', 'piotr.wisniewski@example.com', 'Poland', '1988-11-30', NULL, NULL),
    (4, 'test-uid-004', 'Maria Zielińska', 'maria.zielinska@example.com', 'Germany', '1995-03-10', NULL, NULL),
    (5, 'test-uid-005', 'Tomasz Lewandowski', 'tomasz.lewandowski@example.com', 'Czech Republic', '1987-12-05', NULL, NULL);

INSERT INTO groups (id, name, description, currency, max_budget, start_date, end_date, profile_photo_id, profile_id)
VALUES
    (1, 'Wakacje 2025', 'Wspólny wyjazd na wakacje do Grecji - zwiedzanie wysp, plaże i kultura', 'PLN', 5000.00, '2025-07-01 00:00:00', '2025-07-15 23:59:59', NULL, NULL),
    (2, 'Projekt osobisty', 'Mój projekt startupowy - aplikacja mobilna', 'PLN', 2000.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', NULL, NULL),
    (3, 'Wycieczka górska', 'Wspinaczka w Tatrach - sprzęt, noclegi, przewodnicy', 'EUR', 3500.00, '2025-06-01 00:00:00', '2025-06-10 23:59:59', NULL, NULL),
    (4, 'Weekend w SPA', 'Relaksujący weekend dla przyjaciół - masaże, basen, sauna', 'PLN', 1500.00, '2025-09-15 00:00:00', '2025-09-17 23:59:59', NULL, NULL),
    (5, 'Konferencja Tech 2025', 'Udział w konferencji technologicznej - bilety, hotel, transport', 'USD', 4000.00, '2025-10-20 00:00:00', '2025-10-25 23:59:59', NULL, NULL);

INSERT INTO user_group (user_id, group_id, role)
VALUES
    (1, 1, 'ADMIN'),
    (2, 1, 'USER'),
    (3, 1, 'USER'),
    (4, 1, 'USER'),

    (3, 2, 'ADMIN'),

    (1, 3, 'USER'),
    (2, 3, 'ADMIN'),
    (5, 3, 'USER'),

    (1, 4, 'USER'),
    (2, 4, 'USER'),
    (3, 4, 'ADMIN'),
    (4, 4, 'USER'),
    (5, 4, 'USER'),

    (3, 5, 'USER'),
    (4, 5, 'ADMIN'),
    (5, 5, 'USER');
