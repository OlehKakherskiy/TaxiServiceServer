SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `taxiservice` ;
CREATE SCHEMA IF NOT EXISTS `taxiservice` DEFAULT CHARACTER SET utf8 ;
SHOW WARNINGS;
USE `taxiservice` ;

-- -----------------------------------------------------
-- Table `user_credential`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `user_credential` (
  `username` VARCHAR(40) NOT NULL,
  `password` VARCHAR(360) NOT NULL,
  `salt` VARCHAR(45) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `authority`
-- -----------------------------------------------------
SHOW WARNINGS;
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

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `car_type`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `car_type` (
  `car_type_id` INT(11) NOT NULL,
  `type` CHAR(30) NOT NULL,
  PRIMARY KEY (`car_type_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `car`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `car` (
  `car_id` INT(11) NOT NULL AUTO_INCREMENT,
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
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `driver_license`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `driver_license` (
  `driver_license_id` INT(11) NOT NULL,
  `driver_license` CHAR(20) NOT NULL,
  `expiration_time` DATETIME NOT NULL,
  `front_side_scan` BLOB NULL,
  `back_side_scan` BLOB NULL,
  PRIMARY KEY (`driver_license_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `user_type`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `user_type` (
  `user_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_type` ENUM('CUSTOMER', 'TAXI_DRIVER') NOT NULL DEFAULT 'CUSTOMER',
  PRIMARY KEY (`user_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `user_type` INT(11) NOT NULL,
  `driver_license_id` INT(11) NULL,
  `car_id` INT(11) NULL,
  PRIMARY KEY (`user_id`),
  INDEX `user_user_type_user_type_id_fk` (`user_type` ASC),
  INDEX `fk_user_driver_license1_idx` (`driver_license_id` ASC),
  INDEX `fk_user_car1_idx` (`car_id` ASC),
  CONSTRAINT `user_user_type_user_type_id_fk`
    FOREIGN KEY (`user_type`)
    REFERENCES `user_type` (`user_type_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_driver_license1`
    FOREIGN KEY (`driver_license_id`)
    REFERENCES `driver_license` (`driver_license_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_car1`
    FOREIGN KEY (`car_id`)
    REFERENCES `car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 47
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `mobile_number`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `mobile_number` (
  `mobile_number_id` INT(11) NOT NULL AUTO_INCREMENT,
  `mobile_number` CHAR(13) NOT NULL,
  `user_id` INT(11) NULL,
  PRIMARY KEY (`mobile_number_id`),
  INDEX `fk_mobile_number_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_mobile_number_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 99
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `payment_method`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `payment_method` (
  `payment_method_id` INT(11) NOT NULL,
  `payment_method` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`payment_method_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `order_status`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `order_status` (
  `order_status_id` INT(11) NOT NULL,
  `order_status` CHAR(30) NOT NULL,
  PRIMARY KEY (`order_status_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `taxi_order`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `taxi_order` (
  `taxi_order_id` INT(11) NOT NULL AUTO_INCREMENT,
  `start_time` DATETIME NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `customer_id` INT(11) NOT NULL,
  `taxi_driver_id` INT(11) NULL DEFAULT NULL,
  `pet` TINYINT(1) NULL DEFAULT 0,
  `luggage` TINYINT(1) NULL DEFAULT 0,
  `extra_price` DECIMAL(10,2) NULL DEFAULT 0,
  `drive_my_car` TINYINT(1) NULL DEFAULT 0,
  `passenger_count` TINYINT NULL DEFAULT 1,
  `comment` VARCHAR(300) NULL DEFAULT '',
  `payment_method_id` INT(11) NOT NULL DEFAULT 0,
  `order_status_id` INT(11) NOT NULL,
  `car_type_id` INT(11) NOT NULL,
  `add_time` DATETIME NOT NULL,
  `distance` DOUBLE NOT NULL,
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
  CONSTRAINT `fk_taxi_order_payment_method2`
    FOREIGN KEY (`payment_method_id`)
    REFERENCES `payment_method` (`payment_method_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_order_status2`
    FOREIGN KEY (`order_status_id`)
    REFERENCES `order_status` (`order_status_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_car_type2`
    FOREIGN KEY (`car_type_id`)
    REFERENCES `car_type` (`car_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `administration_area`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `administration_area` (
  `admin_area_id` INT(11) NOT NULL AUTO_INCREMENT,
  `admin_area_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`admin_area_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `city`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `city` (
  `city_id` INT(11) NOT NULL AUTO_INCREMENT,
  `city_name` VARCHAR(45) NOT NULL,
  `admin_area_id` INT(11) NOT NULL,
  PRIMARY KEY (`city_id`),
  INDEX `fk_city_administration_area1_idx` (`admin_area_id` ASC),
  CONSTRAINT `fk_city_administration_area1`
    FOREIGN KEY (`admin_area_id`)
    REFERENCES `administration_area` (`admin_area_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `district`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `district` (
  `district_id` INT(11) NOT NULL AUTO_INCREMENT,
  `district_name` VARCHAR(45) NOT NULL,
  `city_id` INT(11) NOT NULL,
  PRIMARY KEY (`district_id`),
  INDEX `fk_district_city1_idx` (`city_id` ASC),
  CONSTRAINT `fk_district_city1`
    FOREIGN KEY (`city_id`)
    REFERENCES `city` (`city_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `street`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `street` (
  `street_id` INT(11) NOT NULL AUTO_INCREMENT,
  `street_name` VARCHAR(45) NOT NULL,
  `district_id` INT(11) NOT NULL,
  PRIMARY KEY (`street_id`),
  INDEX `fk_street_district1_idx` (`district_id` ASC),
  CONSTRAINT `fk_street_district1`
    FOREIGN KEY (`district_id`)
    REFERENCES `district` (`district_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `address`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `address` (
  `address_id` INT(11) NOT NULL AUTO_INCREMENT,
  `house_number` VARCHAR(10) NOT NULL,
  `street_id` INT(11) NOT NULL,
  PRIMARY KEY (`address_id`),
  INDEX `fk_address_street_idx` (`street_id` ASC),
  CONSTRAINT `fk_address_street`
    FOREIGN KEY (`street_id`)
    REFERENCES `street` (`street_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `taxi_order_has_address`
-- -----------------------------------------------------
SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `taxi_order_has_address` (
  `taxi_order_id` INT(11) NOT NULL,
  `address_id` INT(11) NOT NULL,
  `point_index` INT(11) NOT NULL,
  `longtitude` DOUBLE NOT NULL,
  `latitude` DOUBLE NOT NULL,
  PRIMARY KEY (`taxi_order_id`, `address_id`),
  INDEX `fk_taxi_order_has_address_address1_idx` (`address_id` ASC),
  INDEX `fk_taxi_order_has_address_taxi_order1_idx` (`taxi_order_id` ASC),
  CONSTRAINT `fk_taxi_order_has_address_taxi_order1`
    FOREIGN KEY (`taxi_order_id`)
    REFERENCES `taxi_order` (`taxi_order_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_taxi_order_has_address_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `address` (`address_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
