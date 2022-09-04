CREATE DATABASE IF NOT EXISTS paymybuddy DEFAULT CHARACTER SET utf8mb4 ^;

-- use of database 
USE paymybuddy ^;

-- drop procedures to be able to recreate them when restart app
DROP PROCEDURE IF EXISTS user_oauth2_provider_fk^;
DROP PROCEDURE IF EXISTS bank_card_bank_account_fk^;
DROP PROCEDURE IF EXISTS application_account_user_fk^;
DROP PROCEDURE IF EXISTS bank_account_user_fk^;
DROP PROCEDURE IF EXISTS user_connection_fk^;
DROP PROCEDURE IF EXISTS user_connection_fk1^;
DROP PROCEDURE IF EXISTS sender_transaction_fk^;
DROP PROCEDURE IF EXISTS receiver_transaction_fk^;
DROP PROCEDURE IF EXISTS transaction_invoice_fk^;
DROP PROCEDURE IF EXISTS user_role_fk^;
DROP PROCEDURE IF EXISTS user_role_fk1^;

-- creation of Tables if not exists -- 

CREATE TABLE IF NOT EXISTS oauth2_provider (
	id INT AUTO_INCREMENT NOT NULL,
	registration_client VARCHAR(50) NOT NULL,
	provider_user_id VARCHAR(50) NOT NULL,
	user_id INT NOT NULL,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;

CREATE TABLE IF NOT EXISTS role(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(20),
	PRIMARY KEY (id)
)ENGINE= InnoDB, DEFAULT CHARSET= utf8mb4 ^;

CREATE TABLE IF NOT EXISTS user_role(
	user_id INT NOT NULL,
	role_id INT NOT NULL,
	PRIMARY KEY (user_id,role_id)
)ENGINE= InnoDB, DEFAULT CHARSET=utf8mb4 ^;

CREATE TABLE IF NOT EXISTS application_account (
	id INT NOT NULL AUTO_INCREMENT,
	account_number VARCHAR(36),
	balance DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;

CREATE TABLE IF NOT EXISTS bank_account (
	id INT NOT NULL AUTO_INCREMENT,
	iban VARCHAR(38) NOT NULL,
	bic VARCHAR(10) NOT NULL,
	balance DECIMAL(8,2) NOT NULL,
	bank_card_id INT,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;

CREATE TABLE IF NOT EXISTS bank_card (
	id INT NOT NULL AUTO_INCREMENT,
	card_number VARCHAR(19) NOT NULL,
	card_code INT NOT NULL,
	expiration_date DATE NOT NULL,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS user (
	id INT AUTO_INCREMENT NOT NULL,
	email VARCHAR(30) NOT NULL,
	password CHAR(68) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
	enabled TINYINT NOT NULL DEFAULT 1,
	last_name VARCHAR(30) NOT NULL,
	first_name VARCHAR(30) NOT NULL,
	address VARCHAR(100) NOT NULL,
	zip INT NOT NULL,
	city VARCHAR(30) NOT NULL,
	phone VARCHAR(10) NOT NULL,
	application_account_id INT NOT NULL,
	bank_account_id INT,
	PRIMARY KEY (id),
	UNIQUE KEY(email)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS connection_user (
	user_id INT NOT NULL ,
	user_connection_id INT NOT NULL ,
	PRIMARY KEY (user_id, user_connection_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS transaction (
	id INT AUTO_INCREMENT NOT NULL,
	transaction_date DATETIME NOT NULL,
	description VARCHAR(100) NOT NULL,
	type VARCHAR(8)NOT NULL,
	amount DECIMAL(8,2) NOT NULL,
	amount_commission DECIMAL(8,2) NOT NULL,
	sender_id INT NOT NULL,
	receiver_id INT NOT NULL,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS invoice (
	id INT AUTO_INCREMENT NOT NULL,
	date_invoice DATETIME NOT NULL,
	price DECIMAL(8,2) NOT NULL,
	transaction_id INT NOT NULL,
	PRIMARY KEY (id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


-- creation of foreign keys 
-- use of procedure because sql don't support if not exists --

CREATE PROCEDURE user_oauth2_provider_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'user_oauth2_provider_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE oauth2_provider ADD CONSTRAINT user_oauth2_provider_fk
		FOREIGN KEY (user_id)
		REFERENCES user(id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE user_role_fk()
BEGIN
	IF NOT EXISTS(SELECT NULL
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA ='paymybuddy'
				AND CONSTRAINT_NAME = 'user_role_fk'
				AND CONSTRAINT_TYPE = 'FOREIGN KEY')
	THEN
		ALTER TABLE user_role ADD CONSTRAINT user_role_fk
		FOREIGN KEY (user_id)
		REFERENCES user(id)
		ON DELETE NO ACTION 
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE user_role_fk1()
BEGIN
	IF NOT EXISTS(SELECT NULL
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA ='paymybuddy'
				AND CONSTRAINT_NAME = 'user_role_fk1'
				AND CONSTRAINT_TYPE = 'FOREIGN KEY')
	THEN
		ALTER TABLE user_role ADD CONSTRAINT user_role_fk1
		FOREIGN KEY (role_id)
		REFERENCES role (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE application_account_user_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'application_account_user_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE user ADD CONSTRAINT application_account_user_fk
		FOREIGN KEY (application_account_id)
		REFERENCES application_account (id)
		ON DELETE NO ACTION 
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE bank_account_user_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'bank_account_user_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE user ADD CONSTRAINT bank_account_user_fk
		FOREIGN KEY (bank_account_id)
		REFERENCES bank_account (id)
		ON DELETE SET NULL 
		ON UPDATE CASCADE;
	END IF;
END ^;

CREATE PROCEDURE bank_card_bank_account_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'bank_card_bank_account_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE bank_account ADD CONSTRAINT bank_card_bank_account_fk
		FOREIGN KEY (bank_card_id)
		REFERENCES bank_card (id)
		ON DELETE NO ACTION 
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE user_connection_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'user_connection_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk
		FOREIGN KEY (user_id)
		REFERENCES user (id)
		ON DELETE NO ACTION 
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE user_connection_fk1() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'user_connection_fk1'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk1
		FOREIGN KEY (user_connection_id)
		REFERENCES user (id)
		ON DELETE NO ACTION  
		ON UPDATE NO ACTION;

	END IF;
END ^;


CREATE PROCEDURE sender_transaction_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'sender_transaction_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE transaction ADD CONSTRAINT sender_transaction_fk
		FOREIGN KEY (sender_id) REFERENCES user (id)
		ON DELETE NO ACTION  
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE receiver_transaction_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'receiver_transaction_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE transaction ADD CONSTRAINT receiver_transaction_fk
		FOREIGN KEY (receiver_id) REFERENCES user (id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE transaction_invoice_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'transaction_daily_invoice_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE invoice ADD CONSTRAINT transaction_daily_invoice_fk
		FOREIGN KEY (transaction_id)
		REFERENCES transaction (id)
		ON DELETE NO ACTION  
		ON UPDATE NO ACTION;
	END IF;
END ^;

-- call of procedures
CALL user_oauth2_provider_fk()^;
CALL application_account_user_fk()^;
CALL bank_account_user_fk()^;
CALL bank_card_bank_account_fk()^;
CALL user_connection_fk()^;
CALL user_connection_fk1()^;
CALL sender_transaction_fk()^;
CALL receiver_transaction_fk()^;
CALL transaction_invoice_fk()^;
CALL user_role_fk()^;
CALL user_role_fk1()^;





