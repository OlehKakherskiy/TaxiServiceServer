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
  `idCar` INT(11) NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(20) NOT NULL,
  `manufacturer` VARCHAR(20) NOT NULL,
  `plateNumber` VARCHAR(20) NOT NULL,
  `seatNumber` INT(11) NULL DEFAULT NULL,
  `carType` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`idCar`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`user` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`user` (
  `idUser` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `idCar` INT(11) NULL DEFAULT NULL,
  `userType` INT(11) NOT NULL,
  PRIMARY KEY (`idUser`),
  INDEX `fk_User_Car1_idx` (`idCar` ASC),
  CONSTRAINT `fk_User_Car1`
    FOREIGN KEY (`idCar`)
    REFERENCES `taxiservice`.`car` (`idCar`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`account` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`account` (
  `idAccount` INT(11) NOT NULL AUTO_INCREMENT,
  `startTime` DATETIME(6) NOT NULL,
  `startPoint` VARCHAR(45) NOT NULL,
  `endPoint` VARCHAR(45) NOT NULL,
  `statusId` INT(11) NOT NULL,
  `price` DOUBLE NOT NULL,
  `idCustomer` INT(11) NOT NULL,
  `idTaxiDriver` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`idAccount`),
  INDEX `fk_Account_User1_idx` (`idCustomer` ASC),
  INDEX `fk_Account_User2_idx` (`idTaxiDriver` ASC),
  CONSTRAINT `fk_Account_User1`
    FOREIGN KEY (`idCustomer`)
    REFERENCES `taxiservice`.`user` (`idUser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Account_User2`
    FOREIGN KEY (`idTaxiDriver`)
    REFERENCES `taxiservice`.`user` (`idUser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`additionalrequirement`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`additionalrequirement` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`additionalrequirement` (
  `idAdditionalRequirement` INT(11) NOT NULL,
  `idAdditionalRequirementValue` INT(11) NOT NULL,
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `idAccount` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_additionalrequirement_account1_idx` (`idAccount` ASC),
  CONSTRAINT `fk_additionalrequirement_account1`
    FOREIGN KEY (`idAccount`)
    REFERENCES `taxiservice`.`account` (`idAccount`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `taxiservice`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`users` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`users` (
  `username` VARCHAR(40) NOT NULL,
  `password` VARCHAR(60) NOT NULL,
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
-- Table `taxiservice`.`mobilenumber`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxiservice`.`mobilenumber` ;

CREATE TABLE IF NOT EXISTS `taxiservice`.`mobilenumber` (
  `idMobileNumber` INT(11) NOT NULL AUTO_INCREMENT,
  `mobileNumber` VARCHAR(45) NOT NULL,
  `idUser` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`idMobileNumber`),
  INDEX `fk_MobileNumber_User_idx` (`idUser` ASC),
  CONSTRAINT `fk_MobileNumber_User`
    FOREIGN KEY (`idUser`)
    REFERENCES `taxiservice`.`user` (`idUser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
