CREATE DATABASE IF NOT EXISTS paymybuddy DEFAULT CHARACTER SET utf8mb4 ^;

-- use of database 
USE paymybuddy ^;

-- drop procedures to be able to recreate them when restart app
DROP PROCEDURE IF EXISTS application_identifier_user_fk^;
DROP PROCEDURE IF EXISTS user_social_network_identifier_fk^;
DROP PROCEDURE IF EXISTS social_network_identifer_user_fk^;
DROP PROCEDURE IF EXISTS card_bank_bank_account_fk^;
DROP PROCEDURE IF EXISTS application_account_user_fk^;
DROP PROCEDURE IF EXISTS bank_account_user_fk^;
DROP PROCEDURE IF EXISTS user_connection_fk^;
DROP PROCEDURE IF EXISTS user_connection_fk1^;
DROP PROCEDURE IF EXISTS user_invoice_fk^;
DROP PROCEDURE IF EXISTS connection_transaction_fk^;
DROP PROCEDURE IF EXISTS transaction_daily_invoice_fk^;

-- creation of Tables if not exists -- 

CREATE TABLE IF NOT EXISTS application_identifier (
                application_identifier_id INT AUTO_INCREMENT NOT NULL,
                email VARCHAR(30) NOT NULL,
                password CHAR(68) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
                enabled TINYINT NOT NULL DEFAULT 1,
                PRIMARY KEY (application_identifier_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4^;


CREATE TABLE IF NOT EXISTS social_network_identifier (
                social_network_identifier_id INT AUTO_INCREMENT NOT NULL,
               	network_provider_name VARCHAR(50) NOT NULL,
                provider_user_id INT NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (social_network_identifier_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS card_bank (
                numberCard INT NOT NULL,
                card_code INT NOT NULL,
                date_expiration_card DATE NOT NULL,
                PRIMARY KEY (numberCard)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS application_account (
                number_account INT AUTO_INCREMENT NOT NULL,
                balance DECIMAL(8,2) NOT NULL,
                PRIMARY KEY (number_account)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS bank_account (
                number_account INT NOT NULL,
                iban VARCHAR(34) NOT NULL,
                balance DECIMAL(8,2) NOT NULL,
                numberCard INT NOT NULL,
                PRIMARY KEY (number_account)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS user (
                user_id INT AUTO_INCREMENT NOT NULL,
                lastName VARCHAR(30) NOT NULL,
                firstName VARCHAR(30) NOT NULL,
                address VARCHAR(100) NOT NULL,
                zip INT NOT NULL,
                city VARCHAR(30) NOT NULL,
                phone VARCHAR(10) NOT NULL,
                -- number_application_account,number_bank_account,social_network_identifier can be null if it' a new user 
                number_application_account INT , 
                number_bank_account INT ,
                application_identifier_id INT NOT NULL,
                PRIMARY KEY (user_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS connection_user (
                user_id INT NOT NULL,
                user_connection_id INT NOT NULL,
                PRIMARY KEY (user_id, user_connection_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS transaction (
                transaction_id INT AUTO_INCREMENT NOT NULL,
                date_transaction DATETIME NOT NULL,
                description VARCHAR(100) NOT NULL,
                amount DECIMAL(8,2) NOT NULL,
                commision_percent DECIMAL(3,2) NOT NULL,
                type_transaction VARCHAR(20) NOT NULL,
                user_id INT NOT NULL,
                user_connection_id INT NOT NULL,
                PRIMARY KEY (transaction_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


CREATE TABLE IF NOT EXISTS invoice (
                daily_invoice_id INT AUTO_INCREMENT NOT NULL,
                date_invoice DATETIME NOT NULL,
                price_ht DECIMAL(8,2) NOT NULL,
                price_ttc DECIMAL(8,2) NOT NULL,
                taxe_percent DECIMAL(3,2) NOT NULL,
                transaction_id INT NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (daily_invoice_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4 ^;


-- creation of foreign keys 
-- use of procedure because sql don't support if not exists --

CREATE PROCEDURE application_identifier_user_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'application_identifier_user_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE user ADD CONSTRAINT application_identifier_user_fk
		FOREIGN KEY (application_identifier_id)	
		REFERENCES application_identifier (application_identifier_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE user_social_network_identifier_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'user_social_network_identifier_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE social_network_identifier ADD CONSTRAINT user_social_network_identifier_fk
		FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE card_bank_bank_account_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'card_bank_bank_account_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE bank_account ADD CONSTRAINT card_bank_bank_account_fk
		FOREIGN KEY (numberCard)
		REFERENCES card_bank (numberCard)
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
		FOREIGN KEY (number_application_account)
		REFERENCES application_account (number_account)
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
		FOREIGN KEY (number_bank_account)
		REFERENCES bank_account (number_account)
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
		REFERENCES user (user_id)
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
		REFERENCES user (user_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;

	END IF;
END ^;

CREATE PROCEDURE user_invoice_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'user_invoice_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE invoice ADD CONSTRAINT user_invoice_fk
		FOREIGN KEY (user_id)
		REFERENCES user (user_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE connection_transaction_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'connection_transaction_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE transaction ADD CONSTRAINT connection_transaction_fk
		FOREIGN KEY (user_id, user_connection_id)
		REFERENCES connection_user (user_id, user_connection_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CREATE PROCEDURE transaction_daily_invoice_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'transaction_daily_invoice_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE invoice ADD CONSTRAINT transaction_daily_invoice_fk
		FOREIGN KEY (transaction_id)
		REFERENCES transaction (transaction_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

-- call of procedures
CALL application_identifier_user_fk()^;
CALL user_social_network_identifier_fk()^;
CALL card_bank_bank_account_fk()^;
CALL application_account_user_fk()^;
CALL bank_account_user_fk()^;
CALL user_connection_fk()^;
CALL user_connection_fk1()^;
CALL user_invoice_fk()^;
CALL connection_transaction_fk()^;
CALL transaction_daily_invoice_fk()^;





