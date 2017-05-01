CREATE DATABASE  IF NOT EXISTS `taxiservice` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `taxiservice`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: taxiservice
-- ------------------------------------------------------
-- Server version	5.6.35-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `address_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `house_number` varchar(10) DEFAULT NULL,
  `street_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`address_id`),
  KEY `fk_address_street_idx` (`street_id`),
  CONSTRAINT `fk_address_street` FOREIGN KEY (`street_id`) REFERENCES `street` (`street_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `administration_area`
--

DROP TABLE IF EXISTS `administration_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `administration_area` (
  `admin_area_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `admin_area_name` varchar(45) DEFAULT NULL,
  `country_id` int(11) NOT NULL,
  PRIMARY KEY (`admin_area_id`),
  KEY `admin_area_name_idx` (`admin_area_name`),
  KEY `fk_administration_area_country1_idx` (`country_id`),
  CONSTRAINT `fk_administration_area_country1` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administration_area`
--

LOCK TABLES `administration_area` WRITE;
/*!40000 ALTER TABLE `administration_area` DISABLE KEYS */;
/*!40000 ALTER TABLE `administration_area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authority` (
  `authority` varchar(25) NOT NULL,
  `username` varchar(40) NOT NULL,
  PRIMARY KEY (`username`),
  KEY `fk_roles_userdetails1_idx` (`username`),
  CONSTRAINT `fk_roles_userdetails1` FOREIGN KEY (`username`) REFERENCES `user_credential` (`username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authority`
--

LOCK TABLES `authority` WRITE;
/*!40000 ALTER TABLE `authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car`
--

DROP TABLE IF EXISTS `car`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `car` (
  `car_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `model` varchar(20) NOT NULL,
  `brand` varchar(20) NOT NULL,
  `plate_number` varchar(20) NOT NULL,
  `seat_number` int(11) DEFAULT NULL,
  `car_type_id` int(11) NOT NULL,
  PRIMARY KEY (`car_id`),
  KEY `fk_car_car_type2_idx` (`car_type_id`),
  CONSTRAINT `fk_car_car_type2` FOREIGN KEY (`car_type_id`) REFERENCES `car_type` (`car_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car`
--

LOCK TABLES `car` WRITE;
/*!40000 ALTER TABLE `car` DISABLE KEYS */;
INSERT INTO `car` VALUES (1,'307','Pejo','5411',4,1),(2,'X5','BMW','5143',5,1),(3,'2141','Москвич','64137',3,1);
/*!40000 ALTER TABLE `car` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_type`
--

DROP TABLE IF EXISTS `car_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `car_type` (
  `car_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `type` char(30) NOT NULL,
  PRIMARY KEY (`car_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_type`
--

LOCK TABLES `car_type` WRITE;
/*!40000 ALTER TABLE `car_type` DISABLE KEYS */;
INSERT INTO `car_type` VALUES (0,'TRUCK'),(1,'PASSENGER_CAR'),(2,'MINIBUS');
/*!40000 ALTER TABLE `car_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `city_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `city_name` varchar(45) NOT NULL,
  `admin_area_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`city_id`),
  KEY `fk_city_administration_area1_idx` (`admin_area_id`),
  KEY `city_name_idx` (`city_name`),
  CONSTRAINT `fk_city_administration_area1` FOREIGN KEY (`admin_area_id`) REFERENCES `administration_area` (`admin_area_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `country_id` int(11) NOT NULL AUTO_INCREMENT,
  `country_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district`
--

DROP TABLE IF EXISTS `district`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district` (
  `district_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `district_name` varchar(45) DEFAULT NULL,
  `city_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`district_id`),
  KEY `fk_district_city1_idx` (`city_id`),
  KEY `district_name_idx` (`district_name`),
  CONSTRAINT `fk_district_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`city_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district`
--

LOCK TABLES `district` WRITE;
/*!40000 ALTER TABLE `district` DISABLE KEYS */;
/*!40000 ALTER TABLE `district` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver_license`
--

DROP TABLE IF EXISTS `driver_license`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `driver_license` (
  `driver_license_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `driver_license` char(20) NOT NULL,
  `expiration_time` datetime NOT NULL,
  `front_side_scan` blob,
  `back_side_scan` blob,
  PRIMARY KEY (`driver_license_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driver_license`
--

LOCK TABLES `driver_license` WRITE;
/*!40000 ALTER TABLE `driver_license` DISABLE KEYS */;
INSERT INTO `driver_license` VALUES (1,'BXX 990640','2020-04-03 03:20:20',NULL,NULL),(2,'BXA 990641','2023-07-03 03:21:56',NULL,NULL),(3,'ПОП 990642','2023-05-03 03:22:28',NULL,NULL);
/*!40000 ALTER TABLE `driver_license` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mobile_number`
--

DROP TABLE IF EXISTS `mobile_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mobile_number` (
  `mobile_number_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mobile_number` char(13) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`mobile_number_id`),
  KEY `fk_mobile_number_user1_idx` (`user_id`),
  CONSTRAINT `fk_mobile_number_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mobile_number`
--

LOCK TABLES `mobile_number` WRITE;
/*!40000 ALTER TABLE `mobile_number` DISABLE KEYS */;
INSERT INTO `mobile_number` VALUES (1,'958428809',1),(2,'976651562',1),(3,'976651562',2),(4,'958428809',2),(5,'976651562',3),(6,'958428809',3),(7,'958428809',4),(8,'976651562',4),(9,'958428809',5),(10,'976651562',5);
/*!40000 ALTER TABLE `mobile_number` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status`
--

DROP TABLE IF EXISTS `order_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_status` (
  `order_status_id` int(11) NOT NULL,
  `order_status` char(30) NOT NULL,
  PRIMARY KEY (`order_status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status`
--

LOCK TABLES `order_status` WRITE;
/*!40000 ALTER TABLE `order_status` DISABLE KEYS */;
INSERT INTO `order_status` VALUES (0,'NEW'),(1,'ACCEPTED'),(2,'CANCELLED'),(3,'DONE'),(4,'WAITING'),(5,'PROCESSING');
/*!40000 ALTER TABLE `order_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_method` (
  `payment_method_id` int(11) NOT NULL AUTO_INCREMENT,
  `payment_method` varchar(45) NOT NULL,
  PRIMARY KEY (`payment_method_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES (0,'CASH'),(1,'CREDIT_CARD');
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route_point`
--

DROP TABLE IF EXISTS `route_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route_point` (
  `taxi_order_id` int(11) unsigned DEFAULT NULL,
  `address_id` int(11) unsigned NOT NULL,
  `point_index` int(11) NOT NULL,
  `longtitude` double NOT NULL,
  `latitude` double NOT NULL,
  `route_point_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`route_point_id`),
  KEY `fk_taxi_order_has_address_address1_idx` (`address_id`),
  KEY `fk_taxi_order_has_address_taxi_order1_idx` (`taxi_order_id`),
  CONSTRAINT `fk_taxi_order_has_address_address1` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_has_address_taxi_order1` FOREIGN KEY (`taxi_order_id`) REFERENCES `taxi_order` (`taxi_order_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_point`
--

LOCK TABLES `route_point` WRITE;
/*!40000 ALTER TABLE `route_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `route_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street`
--

DROP TABLE IF EXISTS `street`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street` (
  `street_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `street_name` varchar(45) DEFAULT NULL,
  `district_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`street_id`),
  KEY `fk_street_district1_idx` (`district_id`),
  KEY `street_name_idx` (`street_name`),
  CONSTRAINT `fk_street_district1` FOREIGN KEY (`district_id`) REFERENCES `district` (`district_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street`
--

LOCK TABLES `street` WRITE;
/*!40000 ALTER TABLE `street` DISABLE KEYS */;
/*!40000 ALTER TABLE `street` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `taxi_order`
--

DROP TABLE IF EXISTS `taxi_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `taxi_order` (
  `taxi_order_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `start_time` datetime DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `customer_id` int(11) unsigned NOT NULL,
  `taxi_driver_id` int(11) unsigned DEFAULT NULL,
  `pet` tinyint(1) DEFAULT '0',
  `luggage` tinyint(1) DEFAULT '0',
  `extra_price` decimal(10,2) DEFAULT '0.00',
  `drive_my_car` tinyint(1) DEFAULT '0',
  `passenger_count` tinyint(4) DEFAULT '1',
  `comment` varchar(300) DEFAULT '',
  `payment_method_id` int(11) NOT NULL DEFAULT '0',
  `order_status_id` int(11) NOT NULL,
  `car_type_id` int(11) NOT NULL,
  `add_time` datetime NOT NULL,
  `distance` double NOT NULL,
  `removed` TINYINT(1) NOT NULL DEFAULT 0,
  `duration` time NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`taxi_order_id`),
  KEY `fk_Account_User1_idx` (`customer_id`),
  KEY `fk_Account_User2_idx` (`taxi_driver_id`),
  KEY `fk_taxi_order_payment_method2_idx` (`payment_method_id`),
  KEY `fk_taxi_order_order_status2_idx` (`order_status_id`),
  KEY `fk_taxi_order_car_type2_idx` (`car_type_id`),
  CONSTRAINT `fk_Account_User1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_User2` FOREIGN KEY (`taxi_driver_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_car_type2` FOREIGN KEY (`car_type_id`) REFERENCES `car_type` (`car_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_order_status2` FOREIGN KEY (`order_status_id`) REFERENCES `order_status` (`order_status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_payment_method2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`payment_method_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `taxi_order`
--

LOCK TABLES `taxi_order` WRITE;
/*!40000 ALTER TABLE `taxi_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `taxi_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `user_type` int(11) NOT NULL,
  `driver_license_id` int(11) unsigned DEFAULT NULL,
  `car_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `user_user_type_user_type_id_fk` (`user_type`),
  KEY `fk_user_driver_license1_idx` (`driver_license_id`),
  KEY `fk_user_car1_idx` (`car_id`),
  CONSTRAINT `fk_user_car1` FOREIGN KEY (`car_id`) REFERENCES `car` (`car_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_driver_license1` FOREIGN KEY (`driver_license_id`) REFERENCES `driver_license` (`driver_license_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_user_type_user_type_id_fk` FOREIGN KEY (`user_type`) REFERENCES `user_type` (`user_type_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Daniel Morales','daniel@gmail.com',1,1,1),(2,'Jason Statham','jason@gmail.com',1,2,2),(3,'Лёлик','lelik@gmail.com',1,3,3),(4,'Emilien Kerbalec','emilien@gmail.com',0,NULL,NULL),(5,'Семён Горбунков','gorbunkov@gmail.com',0,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_credential`
--

DROP TABLE IF EXISTS `user_credential`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_credential` (
  `username` varchar(40) NOT NULL,
  `password` varchar(360) NOT NULL,
  `salt` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_credential`
--

LOCK TABLES `user_credential` WRITE;
/*!40000 ALTER TABLE `user_credential` DISABLE KEYS */;
INSERT INTO `user_credential` VALUES ('daniel@gmail.com','$argon2i$v=19$m=65536,t=2,p=1$A7DCcyRlE5kHgLgFwRyuzg$DPu36x3CZvrMLiifsyj0ybWT7gJ0LjHmYuLCOifF2tQ','',1),('emilien@gmail.com','$argon2i$v=19$m=65536,t=2,p=1$gaN6IbmiiQ6f6JbvX34hTA$Ygp4tjMZZ5+kVTs+Ktk5haH18FXnFL7wZLm9eFqYxRs','',1),('gorbunkov@gmail.com','$argon2i$v=19$m=65536,t=2,p=1$bKeEfgHQH+HObbkm4bMTeg$hDKXNUNHL1EGRnvKS/8xvj3DBSp+EL5ZBoYynhWicro','',1),('jason@gmail.com','$argon2i$v=19$m=65536,t=2,p=1$aFlgx20e9ZwrOnqkNwHunw$UTdSbX6i8IjGywQlFoQUg2VYsv8vxDxeEB4+tGvHVao','',1),('lelik@gmail.com','$argon2i$v=19$m=65536,t=2,p=1$duN2j9xocAzrwikrL0J9zQ$Ud5l5u+gUDt7LUNc1s+jJZps7GykaJc2BTneWV10Hts','',1);
/*!40000 ALTER TABLE `user_credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_type`
--

DROP TABLE IF EXISTS `user_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_type` (
  `user_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_type` enum('CUSTOMER','TAXI_DRIVER') NOT NULL DEFAULT 'CUSTOMER',
  PRIMARY KEY (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_type`
--

LOCK TABLES `user_type` WRITE;
/*!40000 ALTER TABLE `user_type` DISABLE KEYS */;
INSERT INTO `user_type` VALUES (0,'CUSTOMER'),(1,'TAXI_DRIVER');
/*!40000 ALTER TABLE `user_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-28 23:05:11
