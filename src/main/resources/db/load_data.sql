INSERT INTO taxiservice.car (idCar, model, manufacturer, plateNumber, seatNumber, carType) VALUES (1,'model', 'manufacturer', 'plateNum', 2, 2);

INSERT INTO taxiservice.user (idUser,name, email, idCar, userType) VALUES (1,'name', 'email@gmail.com', null, 0);
INSERT INTO taxiservice.user (idUser,name, email, idCar, userType) VALUES (2,'name2', 'email2@gmail.com', 1, 1);

INSERT INTO taxiservice.users (username, password, salt, enabled) VALUES ('email2@gmail.com', '1234', '2124', 1);
INSERT INTO taxiservice.users (username, password, salt, enabled) VALUES ('email@gmail.com', '1111', '123', 1);
INSERT INTO taxiservice.authorities (authority, username) VALUES ('TAXI_DRIVER', 'email2@gmail.com');
INSERT INTO taxiservice.authorities (authority, username) VALUES ('CUSTOMER', 'email@gmail.com');

INSERT INTO taxiservice.mobilenumber (mobileNumber, idUser) VALUES ('+380975106619', 1);
INSERT INTO taxiservice.mobilenumber (mobileNumber, idUser) VALUES ('+380935705681', 1);
INSERT INTO taxiservice.mobilenumber (mobileNumber, idUser) VALUES ('+380935705681(1)', 2);
INSERT INTO taxiservice.mobilenumber (mobileNumber, idUser) VALUES ('+380975106619(1)', 2);

INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (1,'2016-12-15 23:00:05.222000', 'start_isChanged', 'end_isChanded', 1, 100, 1, 2);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (2,'2016-12-10 23:00:05.222000', 'start', 'end', 0, 500, 1, null);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (3,'2016-12-10 23:00:05.222000', 'start', 'end', 0, 500, 1, null);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (4,'2016-12-10 23:00:05.222000', 'start', 'end', 0, 2000, 1, null);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (5,'2016-12-11 00:19:39.187000', 'start1', 'end1', 0, 2000, 1, null);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (6,'2016-12-14 23:00:05.222000', 'start', 'end', 0, 1000, 1, null);
INSERT INTO taxiservice.account (idAccount, startTime, startPoint, endPoint, statusId, price, idCustomer, idTaxiDriver) VALUES (7,'2016-12-17 23:00:05.222000', 'start', 'end', 0, 1000, 1, null);

INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (1, 3,1);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (1, 4,1);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (1, 1,1);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (2, 5,1);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (1, 5,2);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (2, 6,1);
INSERT INTO taxiservice.additionalrequirement (idAdditionalRequirementValue, idAccount,idAdditionalRequirement) VALUES (1, 6,2);