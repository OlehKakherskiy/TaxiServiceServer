SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @@auto_increment_increment=1;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `heroku_c6f857e7f6daba9`;
CREATE SCHEMA IF NOT EXISTS `heroku_c6f857e7f6daba9` DEFAULT CHARACTER SET utf8 ;
USE `heroku_c6f857e7f6daba9`;

-- -----------------------------------------------------
-- Table `administration_area`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `administration_area` (
  `admin_area_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_area_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`admin_area_id`),
  INDEX `admin_area_name_idx` (`admin_area_name` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `city`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `city` (
  `city_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `city_name` VARCHAR(45) NOT NULL,
  `admin_area_id` INT(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`city_id`),
  INDEX `fk_city_administration_area1_idx` (`admin_area_id` ASC),
  INDEX `city_name_idx` (`city_name` ASC),
  CONSTRAINT `fk_city_administration_area1`
  FOREIGN KEY (`admin_area_id`)
  REFERENCES `administration_area` (`admin_area_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `district`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `district` (
  `district_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `district_name` VARCHAR(45) NOT NULL,
  `city_id` INT(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`district_id`),
  INDEX `fk_district_city1_idx` (`city_id` ASC),
  INDEX `district_name_idx` (`district_name` ASC),
  CONSTRAINT `fk_district_city1`
  FOREIGN KEY (`city_id`)
  REFERENCES `city` (`city_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `street`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `street` (
  `street_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `street_name` VARCHAR(45) NOT NULL,
  `district_id` INT(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`street_id`),
  INDEX `fk_street_district1_idx` (`district_id` ASC),
  INDEX `street_name_idx` (`street_name` ASC),
  CONSTRAINT `fk_street_district1`
  FOREIGN KEY (`district_id`)
  REFERENCES `district` (`district_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address` (
  `address_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `house_number` VARCHAR(10) NOT NULL,
  `street_id` INT(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`address_id`),
  INDEX `fk_address_street_idx` (`street_id` ASC),
  CONSTRAINT `fk_address_street`
  FOREIGN KEY (`street_id`)
  REFERENCES `street` (`street_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_credential`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_credential` (
  `username` VARCHAR(40) NOT NULL,
  `password` VARCHAR(360) NOT NULL,
  `salt` VARCHAR(45) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `authority` (
  `authority` VARCHAR(25) NOT NULL,
  `username` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`username`),
  INDEX `fk_roles_userdetails1_idx` (`username` ASC),
  CONSTRAINT `fk_roles_userdetails1`
  FOREIGN KEY (`username`)
  REFERENCES `user_credential` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `car_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `car_type` (
  `car_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `type` CHAR(30) NOT NULL,
  PRIMARY KEY (`car_type_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `car`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `car` (
  `car_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(20) NOT NULL,
  `brand` VARCHAR(20) NOT NULL,
  `plate_number` VARCHAR(20) NOT NULL,
  `seat_number` INT(11) NULL DEFAULT NULL,
  `car_type_id` INT(11) NOT NULL,
  PRIMARY KEY (`car_id`),
  INDEX `fk_car_car_type2_idx` (`car_type_id` ASC),
  CONSTRAINT `fk_car_car_type2`
  FOREIGN KEY (`car_type_id`)
  REFERENCES `car_type` (`car_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `driver_license`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `driver_license` (
  `driver_license_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `driver_license` CHAR(20) NOT NULL,
  `expiration_time` DATETIME NOT NULL,
  `front_side_scan` BLOB NULL DEFAULT NULL,
  `back_side_scan` BLOB NULL DEFAULT NULL,
  PRIMARY KEY (`driver_license_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_type` (
  `user_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_type` ENUM('CUSTOMER','TAXI_DRIVER') NOT NULL DEFAULT 'CUSTOMER',
  PRIMARY KEY (`user_type_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `user_type` INT(11) NOT NULL,
  `driver_license_id` INT(11) UNSIGNED NULL DEFAULT NULL,
  `car_id` INT(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  INDEX `user_user_type_user_type_id_fk` (`user_type` ASC),
  INDEX `fk_user_driver_license1_idx` (`driver_license_id` ASC),
  INDEX `fk_user_car1_idx` (`car_id` ASC),
  CONSTRAINT `fk_user_car1`
  FOREIGN KEY (`car_id`)
  REFERENCES `car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_driver_license1`
  FOREIGN KEY (`driver_license_id`)
  REFERENCES `driver_license` (`driver_license_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_user_type_user_type_id_fk`
  FOREIGN KEY (`user_type`)
  REFERENCES `user_type` (`user_type_id`)
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `mobile_number`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mobile_number` (
  `mobile_number_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `mobile_number` CHAR(13) NOT NULL,
  `user_id` INT(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`mobile_number_id`),
  INDEX `fk_mobile_number_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_mobile_number_user1`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `order_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `order_status` (
  `order_status_id` INT(11) NOT NULL,
  `order_status` CHAR(30) NOT NULL,
  PRIMARY KEY (`order_status_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `payment_method`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment_method` (
  `payment_method_id` INT(11) NOT NULL AUTO_INCREMENT,
  `payment_method` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`payment_method_id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxi_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `taxi_order` (
  `taxi_order_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `start_time` DATETIME NULL DEFAULT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `customer_id` INT(11) UNSIGNED NOT NULL,
  `taxi_driver_id` INT(11) UNSIGNED NULL DEFAULT NULL,
  `pet` TINYINT(1) NULL DEFAULT '0',
  `luggage` TINYINT(1) NULL DEFAULT '0',
  `extra_price` DECIMAL(10,2) NULL DEFAULT '0.00',
  `drive_my_car` TINYINT(1) NULL DEFAULT '0',
  `passenger_count` TINYINT(4) NULL DEFAULT '1',
  `comment` VARCHAR(300) NULL DEFAULT '',
  `payment_method_id` INT(11) NOT NULL DEFAULT '0',
  `order_status_id` INT(11) NOT NULL,
  `car_type_id` INT(11) NOT NULL,
  `add_time` DATETIME NOT NULL,
  `distance` DOUBLE NOT NULL,
  `new_column` INT(11) NULL DEFAULT NULL,
  `duration` TIME NOT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`taxi_order_id`),
  INDEX `fk_Account_User1_idx` (`customer_id` ASC),
  INDEX `fk_Account_User2_idx` (`taxi_driver_id` ASC),
  INDEX `fk_taxi_order_payment_method2_idx` (`payment_method_id` ASC),
  INDEX `fk_taxi_order_order_status2_idx` (`order_status_id` ASC),
  INDEX `fk_taxi_order_car_type2_idx` (`car_type_id` ASC),
  CONSTRAINT `fk_Account_User1`
  FOREIGN KEY (`customer_id`)
  REFERENCES `user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_User2`
  FOREIGN KEY (`taxi_driver_id`)
  REFERENCES `user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_car_type2`
  FOREIGN KEY (`car_type_id`)
  REFERENCES `car_type` (`car_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_order_status2`
  FOREIGN KEY (`order_status_id`)
  REFERENCES `order_status` (`order_status_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_payment_method2`
  FOREIGN KEY (`payment_method_id`)
  REFERENCES `payment_method` (`payment_method_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `route_point`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `route_point` (
  `taxi_order_id` INT(11) UNSIGNED NOT NULL,
  `address_id` INT(11) UNSIGNED NOT NULL,
  `point_index` INT(11) NOT NULL,
  `longtitude` DOUBLE NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `route_point_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`route_point_id`),
  INDEX `fk_taxi_order_has_address_address1_idx` (`address_id` ASC),
  INDEX `fk_taxi_order_has_address_taxi_order1_idx` (`taxi_order_id` ASC),
  CONSTRAINT `fk_taxi_order_has_address_address1`
  FOREIGN KEY (`address_id`)
  REFERENCES `address` (`address_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_has_address_taxi_order1`
  FOREIGN KEY (`taxi_order_id`)
  REFERENCES `taxi_order` (`taxi_order_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

ALTER TABLE `user_type` AUTO_INCREMENT=0;
ALTER TABLE `car_type` AUTO_INCREMENT=0;
ALTER TABLE `payment_method` AUTO_INCREMENT=0;
ALTER TABLE `order_status` AUTO_INCREMENT=0;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;