
CREATE TABLE daily_invoice (
                daily_invoice_id INT AUTO_INCREMENT NOT NULL,
                date_invoice DATETIME NOT NULL,
                price_ht DECIMAL(8,2) NOT NULL,
                price_ttc DECIMAL(8,2) NOT NULL,
                taxe_percent DECIMAL(3,2) NOT NULL,
                PRIMARY KEY (daily_invoice_id)
);


CREATE TABLE application_account (
                number_account INT AUTO_INCREMENT NOT NULL,
                balance DECIMAL(8,2) NOT NULL,
                PRIMARY KEY (number_account)
);


CREATE TABLE user (
                user_id INT NOT NULL,
                lastName VARCHAR(30) NOT NULL,
                firstName VARCHAR(30) NOT NULL,
                email VARCHAR(30) NOT NULL,
                password VARCHAR(30) NOT NULL,
                number_account INT NOT NULL,
                PRIMARY KEY (user_id)
);


CREATE TABLE application_identifier (
                application_identifier_id INT AUTO_INCREMENT NOT NULL,
                user_password VARCHAR(30) NOT NULL,
                username_login VARCHAR(30) NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (application_identifier_id, user_password, username_login)
);


CREATE TABLE social_network_identifer (
                social_network_id INT AUTO_INCREMENT NOT NULL,
                username_login VARCHAR(30) NOT NULL,
                user_password VARCHAR(30) NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (social_network_id)
);


CREATE TABLE bank_account (
                number_account INT NOT NULL,
                iban VARCHAR(34) NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (number_account)
);


CREATE TABLE chekbook (
                number_checkbook INT NOT NULL,
                number_account INT NOT NULL,
                PRIMARY KEY (number_checkbook, number_account)
);


CREATE TABLE card_bank (
                numberCard INT NOT NULL,
                number_account INT NOT NULL,
                date_expiration_card DATE NOT NULL,
                PRIMARY KEY (numberCard, number_account)
);


CREATE TABLE connection_user (
                user_id INT NOT NULL,
                user_connection_id INT AUTO_INCREMENT NOT NULL,
                PRIMARY KEY (user_id, user_connection_id)
);


CREATE TABLE transaction (
                transaction_id INT AUTO_INCREMENT NOT NULL,
                date_transaction DATETIME NOT NULL,
                description VARCHAR(100) NOT NULL,
                amount DECIMAL(8,2) NOT NULL,
                commision_percent DECIMAL(3,2) NOT NULL,
                type_transaction VARCHAR(20) NOT NULL,
                user_id INT NOT NULL,
                application_number_account INT NOT NULL,
                bank_number_account INT NOT NULL,
                user_connection_id INT NOT NULL,
                PRIMARY KEY (transaction_id)
);


CREATE TABLE line_transaction (
                transaction_id INT NOT NULL,
                daily_invoice_id INT NOT NULL,
                time_transaction TIME NOT NULL,
                PRIMARY KEY (transaction_id, daily_invoice_id)
);


ALTER TABLE line_transaction ADD CONSTRAINT daily_invoice_line_transaction_fk
FOREIGN KEY (daily_invoice_id)
REFERENCES daily_invoice (daily_invoice_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE user ADD CONSTRAINT application_account_user_fk
FOREIGN KEY (number_account)
REFERENCES application_account (number_account)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT application_account_transaction_fk
FOREIGN KEY (application_number_account)
REFERENCES application_account (number_account)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk
FOREIGN KEY (user_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE connection_user ADD CONSTRAINT user_connection_fk1
FOREIGN KEY (user_connection_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT user_transaction_fk
FOREIGN KEY (user_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE bank_account ADD CONSTRAINT user_bank_account_fk
FOREIGN KEY (user_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE social_network_identifer ADD CONSTRAINT user_social_network_identifer_fk
FOREIGN KEY (user_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE application_identifier ADD CONSTRAINT user_application_identifier_fk
FOREIGN KEY (user_id)
REFERENCES user (user_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE card_bank ADD CONSTRAINT bank_account_card_bank_fk
FOREIGN KEY (number_account)
REFERENCES bank_account (number_account)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE chekbook ADD CONSTRAINT bank_account_chekbook_fk
FOREIGN KEY (number_account)
REFERENCES bank_account (number_account)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT bank_account_transaction_fk
FOREIGN KEY (bank_number_account)
REFERENCES bank_account (number_account)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT connection_transaction_fk
FOREIGN KEY (user_id, user_connection_id)
REFERENCES connection_user (user_id, user_connection_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE line_transaction ADD CONSTRAINT transaction_line_transaction_fk
FOREIGN KEY (transaction_id)
REFERENCES transaction (transaction_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;
