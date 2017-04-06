INSERT INTO car_type (car_type_id, type) VALUES (0, 'TRUCK');
INSERT INTO car_type (car_type_id, type) VALUES (1, 'PASSENGER_CAR');
INSERT INTO car_type (car_type_id, type) VALUES (2, 'MINIBUS');

INSERT INTO payment_method (payment_method_id, payment_method) VALUES (0, 'CASH');
INSERT INTO payment_method (payment_method_id, payment_method) VALUES (1, 'CREDIT_CARD');

INSERT INTO user_type (user_type_id, user_type) VALUES (1, 'CUSTOMER');
INSERT INTO user_type (user_type_id, user_type) VALUES (2, 'TAXI_DRIVER');

INSERT INTO order_status (order_status_id, order_status) VALUES (0, 'NEW');
INSERT INTO order_status (order_status_id, order_status) VALUES (1, 'ACCEPTED');
INSERT INTO order_status (order_status_id, order_status) VALUES (2, 'CANCELLED');
INSERT INTO order_status (order_status_id, order_status) VALUES (3, 'DONE');

INSERT INTO car (car_id,model, brand, plate_number, seat_number, car_type_id) VALUES (1,'307', 'Pejo', '5411', 4, 1);
INSERT INTO car (car_id,model, brand, plate_number, seat_number, car_type_id) VALUES (2,'X5', 'BMW', '5143', 5, 1);
INSERT INTO car (car_id,model, brand, plate_number, seat_number, car_type_id) VALUES (3,'2141', 'Москвич', '64137', 3, 1);

INSERT INTO driver_license (driver_license_id, driver_license, expiration_time, front_side_scan, back_side_scan) VALUES (1, 'BXX 990640', '2020-04-03 03:20:20', NULL, NULL);
INSERT INTO driver_license (driver_license_id, driver_license, expiration_time, front_side_scan, back_side_scan) VALUES (2, 'BXA 990641', '2023-07-03 03:21:56', NULL, NULL);
INSERT INTO driver_license (driver_license_id, driver_license, expiration_time, front_side_scan, back_side_scan) VALUES (3, 'ПОП 990642', '2023-05-03 03:22:28', NULL, NULL);


INSERT INTO user (user_id, name, email, car_id, user_type, driver_license_id) VALUES (1, 'Daniel Morales', 'daniel@gmail.com', 1, 2, 1);
INSERT INTO user (user_id, name, email, car_id, user_type, driver_license_id) VALUES (2, 'Jason Statham', 'jason@gmail.com', 2, 2, 2);
INSERT INTO user (user_id, name, email, car_id, user_type, driver_license_id) VALUES (3, 'Лёлик', 'lelik@gmail.com', 3, 2, 3);
INSERT INTO user (user_id, name, email, car_id, user_type, driver_license_id) VALUES (4, 'Emilien Kerbalec', 'emilien@gmail.com', NULL, 1, NULL);
INSERT INTO user (user_id, name, email, car_id, user_type, driver_license_id) VALUES (5, 'Семён Горбунков', 'gorbunkov@gmail.com', NULL, 1, NULL);

INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (112, 0958428809, 1);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (122, 0976651562, 1);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (132, 0976651562, 2);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (142, 0958428809, 2);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (152, 0976651562, 3);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (162, 0958428809, 3);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (172, 0958428809, 4);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (182, 0976651562, 4);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (192, 0958428809, 5);
INSERT INTO mobile_number (mobile_number_id, mobile_number, user_id) VALUES (202, 0976651562, 5);

INSERT INTO user_credential (username, password, salt, enabled) VALUES ('emilien@gmail.com', '$argon2i$v=19$m=65536,t=2,p=1$gaN6IbmiiQ6f6JbvX34hTA$Ygp4tjMZZ5+kVTs+Ktk5haH18FXnFL7wZLm9eFqYxRs', '', 1);
INSERT INTO user_credential (username, password, salt, enabled) VALUES ('daniel@gmail.com', '$argon2i$v=19$m=65536,t=2,p=1$A7DCcyRlE5kHgLgFwRyuzg$DPu36x3CZvrMLiifsyj0ybWT7gJ0LjHmYuLCOifF2tQ', '', 1);
INSERT INTO user_credential (username, password, salt, enabled) VALUES ('gorbunkov@gmail.com', '$argon2i$v=19$m=65536,t=2,p=1$bKeEfgHQH+HObbkm4bMTeg$hDKXNUNHL1EGRnvKS/8xvj3DBSp+EL5ZBoYynhWicro', '', 1);
INSERT INTO user_credential (username, password, salt, enabled) VALUES ('jason@gmail.com', '$argon2i$v=19$m=65536,t=2,p=1$aFlgx20e9ZwrOnqkNwHunw$UTdSbX6i8IjGywQlFoQUg2VYsv8vxDxeEB4+tGvHVao', '', 1);
INSERT INTO user_credential (username, password, salt, enabled) VALUES ('lelik@gmail.com', '$argon2i$v=19$m=65536,t=2,p=1$duN2j9xocAzrwikrL0J9zQ$Ud5l5u+gUDt7LUNc1s+jJZps7GykaJc2BTneWV10Hts', '', 1);

