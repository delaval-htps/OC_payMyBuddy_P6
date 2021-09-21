CREATE DATABASE IF NOT EXISTS paymybuddy DEFAULT CHARACTER SET utf8mb4 ^;

-- use of database 
USE paymybuddy ^;

-- creation of Tables if not exists
CREATE TABLE IF NOT EXISTS application_identifier (
                application_identifier_id INT AUTO_INCREMENT NOT NULL,
                email VARCHAR(30) NOT NULL,
                password VARCHAR(30) NOT NULL,
                PRIMARY KEY (application_identifier_id)
)ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4^;


CREATE TABLE IF NOT EXISTS social_network_identifier (
                social_network_identifier_id INT AUTO_INCREMENT NOT NULL,
                social_nnetwork_id INT NOT NULL,
                username_login VARCHAR(30) NOT NULL,
                user_password VARCHAR(30) NOT NULL,
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
                phone INT NOT NULL,
                number_application_account INT NOT NULL,
                number_bank_account INT NOT NULL,
                social_network_identifier_id INT NOT NULL,
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
-- use of procedure because of sql don't support if not exists --

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

CREATE PROCEDURE social_network_identifer_user_fk() 
BEGIN
	IF NOT EXISTS(SELECT null 
				FROM information_schema.TABLE_CONSTRAINTS
				WHERE TABLE_SCHEMA = 'paymybuddy' 
				AND CONSTRAINT_NAME= 'social_network_identifer_user_fk'
				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
	THEN
		ALTER TABLE user ADD CONSTRAINT social_network_identifer_user_fk
		FOREIGN KEY (social_network_identifier_id)
		REFERENCES social_network_identifier (social_network_identifier_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION;
	END IF;
END ^;

CALL application_identifier_user_fk()^;
CALL social_network_identifer_user_fk()^;

DROP PROCEDURE IF EXISTS social_network_identifer_user_fk^;
DROP PROCEDURE IF EXISTS application_identifier_user_fk^;

-- 
-- ALTER TABLE bank_account ADD CONSTRAINT card_bank_bank_account_fk
-- FOREIGN KEY (numberCard)
-- REFERENCES card_bank (numberCard)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE user ADD CONSTRAINT application_account_user_fk
-- FOREIGN KEY (number_application_account)
-- REFERENCES application_account (number_account)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE user ADD CONSTRAINT bank_account_user_fk
-- FOREIGN KEY (number_bank_account)
-- REFERENCES bank_account (number_account)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- 
-- ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk
-- FOREIGN KEY (user_id)
-- REFERENCES user (user_id)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk1
-- FOREIGN KEY (user_connection_id)
-- REFERENCES user (user_id)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE invoice ADD CONSTRAINT user_invoice_fk
-- FOREIGN KEY (user_id)
-- REFERENCES user (user_id)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE transaction ADD CONSTRAINT connection_transaction_fk
-- FOREIGN KEY (user_id, user_connection_id)
-- REFERENCES connection_user (user_id, user_connection_id)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 
-- ALTER TABLE invoice ADD CONSTRAINT transaction_daily_invoice_fk
-- FOREIGN KEY (transaction_id)
-- REFERENCES transaction (transaction_id)
-- ON DELETE NO ACTION
-- ON UPDATE NO ACTION^;
-- 
-- 	
-- DROP PROCEDURE If EXISTs rajout_fk ^;
-- 
-- CREATE PROCEDURE rajout_fk( IN p_alter_table_name VARCHAR(50),
-- 							IN p_contraint_name VARCHAR(50),
-- 							IN p_fk_name VARCHAR(50),
-- 							IN p_fk_name_in_fk_table VARCHAR(50),
-- 							IN p_fk_table VARCHAR(50)) 
-- BEGINcreation of foreign feys
-- 	IF NOT EXISTS(SELECT null 
-- 				FROM information_schema.TABLE_CONSTRAINTS
-- 				WHERE TABLE_SCHEMA = 'paymybuddy' 
-- 				AND CONSTRAINT_NAME= p_contraint_name  
-- 				AND CONSTRAINT_TYPE= 'FOREIGN KEY')
-- 	THEN
-- 	DECLARE @p_sql_query varchar(100)
-- 		SET @p_sql_query = 
-- 		'ALTER TABLE'+ p_alter_table_name+  
-- 		'ADD CONSTRAINT' + p_contraint_name+
-- 		'FOREIGN KEY ('+p_fk_name+')'+
-- 		'REFERENCES'+ p_fk_table+'('+ p_fk_name_in_fk_table+')'+
-- 		'ON DELETE NO ACTION ON UPDATE NO ACTION^;'
-- 		EXEC(@p_sql_query)
-- 	END IF^;
-- END ^;
-- 
-- SET @p_alter_table_name = 'user' ^;
-- SET @p_contraint_name = 'application_identifier_user_fk' ^;
-- SET @p_fk_name = 'application_identifier_id' ^;
-- SET @p_fk_table = 'application_identifier' ^;
-- 
-- CALL  rajout_fk(@p_alter_table_name,@p_contraint_name,@p_fk_name,@p_fk_name,@p_fk_table) ^;


