-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema taxiservice
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `taxiservice` ;

-- -----------------------------------------------------
-- Schema taxiservice
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `taxiservice` DEFAULT CHARACTER SET utf8 ;
USE `taxiservice` ;

-- -----------------------------------------------------
-- Table `taxiservice`.`car`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`car` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`car` (
  `car_id` INT(11) NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(20) NOT NULL,
  `manufacturer` VARCHAR(20) NOT NULL,
  `plate_number` VARCHAR(20) NOT NULL,
  `seat_number` INT(11) NULL DEFAULT NULL,
  `car_type` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`car_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`driver_license`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`driver_license` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`driver_license` (
  `driver_license_id` INT(11) NOT NULL,
  `driver_license` CHAR(20) NOT NULL,
  `expiration_time` DATETIME NOT NULL,
  `front_side_scan` BLOB NOT NULL,
  `back_side_scan` BLOB NOT NULL,
  PRIMARY KEY (`driver_license_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`user_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`user_type` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`user_type` (
  `user_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_type` ENUM('CUSTOMER', 'TAXI_DRIVER') NOT NULL DEFAULT 'CUSTOMER',
  PRIMARY KEY (`user_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`user` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`user` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `car_id` INT(11) NULL DEFAULT NULL,
  `user_type` INT(11) NOT NULL,
  `driver_license_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  INDEX `fk_User_Car1_idx` (`car_id` ASC),
  INDEX `fk_user_driverlicense1_idx` (`driver_license_id` ASC),
  INDEX `user_user_type_user_type_id_fk` (`user_type` ASC),
  CONSTRAINT `fk_User_Car1`
    FOREIGN KEY (`car_id`)
    REFERENCES `taxiservice`.`car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_driverlicense1`
    FOREIGN KEY (`driver_license_id`)
    REFERENCES `taxiservice`.`driver_license` (`driver_license_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_user_type_user_type_id_fk`
    FOREIGN KEY (`user_type`)
    REFERENCES `taxiservice`.`user_type` (`user_type_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`taxi_order`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`taxi_order` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`taxi_order` (
  `taxi_order_id` INT(11) NOT NULL AUTO_INCREMENT,
  `start_time` DATETIME(6) NOT NULL,
  `start_point` VARCHAR(45) NOT NULL,
  `end_point` VARCHAR(45) NOT NULL,
  `order_status_id` INT(11) NOT NULL,
  `price` DOUBLE NOT NULL,
  `customer_id` INT(11) NOT NULL,
  `taxi_driver_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`taxi_order_id`),
  INDEX `fk_Account_User1_idx` (`customer_id` ASC),
  INDEX `fk_Account_User2_idx` (`taxi_driver_id` ASC),
  CONSTRAINT `fk_Account_User1`
    FOREIGN KEY (`customer_id`)
    REFERENCES `taxiservice`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_User2`
    FOREIGN KEY (`taxi_driver_id`)
    REFERENCES `taxiservice`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`additional_requirement`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`additional_requirement` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`additional_requirement` (
  `add_req_id` INT(11) NOT NULL,
  `add_req_value_id` INT(11) NOT NULL,
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `taxi_order_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_additionalrequirement_account1_idx` (`taxi_order_id` ASC),
  CONSTRAINT `fk_additionalrequirement_account1`
    FOREIGN KEY (`taxi_order_id`)
    REFERENCES `taxiservice`.`taxi_order` (`taxi_order_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`users` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`users` (
  `username` VARCHAR(40) NOT NULL,
  `password` VARCHAR(360) NOT NULL,
  `salt` VARCHAR(45) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`authorities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`authorities` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`authorities` (
  `authority` VARCHAR(25) NOT NULL,
  `username` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`username`),
  INDEX `fk_roles_userdetails1_idx` (`username` ASC),
  CONSTRAINT `fk_roles_userdetails1`
    FOREIGN KEY (`username`)
    REFERENCES `taxiservice`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`mobile_number`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`mobile_number` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`mobile_number` (
  `mobile_number_id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NULL DEFAULT NULL,
  `mobile_number` CHAR(13) NOT NULL,
  PRIMARY KEY (`mobile_number_id`),
  INDEX `fk_MobileNumber_User_idx` (`user_id` ASC),
  CONSTRAINT `fk_MobileNumber_User`
    FOREIGN KEY (`user_id`)
    REFERENCES `taxiservice`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
