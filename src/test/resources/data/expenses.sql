INSERT INTO expense (id, name, description, category, price, date_of_expense, date_of_adding, creator, group_id)
VALUES
    ( 1,'Zakupy spożywcze', 'Zakupy w supermarkecie', 'Żywność', 250.50, '2025-01-10 10:00:00', '2025-01-10 11:00:00', 'test-uid-001', 1),
    ( 2,'Bilety lotnicze', 'Loty do Grecji', 'Transport', 1500.00, '2025-06-15 08:00:00', '2025-06-15 09:00:00', 'test-uid-001', 1),
    ( 3,'Hotel', 'Rezerwacja hotelu w Atenach', 'Zakwaterowanie', 2000.00, '2025-07-01 14:00:00', '2025-07-01 15:00:00', 'test-uid-002', 1);

INSERT INTO expenses_user (expense_id, user_id, role)
VALUES
    (1, 1, 'PAYER'),
    (1, 2, 'PARTICIPANT'),
    (2, 1, 'PAYER'),
    (2, 2, 'PARTICIPANT'),
    (3, 2, 'PAYER'),
    (3, 1, 'PARTICIPANT');

SELECT setval('expense_id_seq', (SELECT MAX(id) FROM expense), true);