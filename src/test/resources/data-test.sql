INSERT INTO
	application_account (id, account_number, balance)
VALUES
	(1, '000001', 1000),
	(2, '000002', 500.50);

--	insert datas in table use
INSERT INTO
	user (
		id,
		email,
		password,
		enabled,
		last_name,
		first_name,
		address,
		zip,
		city,
		phone,
		application_account_id
	)
VALUES
	(
		1,
		'delaval.htps@gmail.com',
		'$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',
		1,
		'Delaval',
		'Dorian',
		'26 av Maréchal Foch',
		13260,
		'Cassis',
		'0618460160',
		(
			SELECT
				id
			FROM
				application_account
			WHERE
				account_number = '000001'
		)
	),
	(
		2,
		'emilie.baudouin@gmail.com',
		'$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',
		1,
		'Baudouin',
		'Emilie',
		'26 av Maréchal Foch',
		13260,
		'Cassis',
		'0622296638',
		(
			SELECT
				id
			FROM
				application_account
			WHERE
				account_number = '000002'
		)
	);

INSERT INTO
	transaction (
		id,
		transaction_date,
		description,
		type,
		amount,
		amount_commission,
		sender_id,
		receiver_id
	)
VALUES
	(
		1,
		'2022-05-29',
		'transaction1 between sender delaval and receiver emilie',
		'WITHDRAW',
		100,
		5,
		1,
		2
	),
	(
		2,
		'2022-05-30',
		'transaction2 between sender delaval and receiver emilie',
		'CREDIT',
		200,
		10,
		1,
		2
	);

INSERT INTO
	role (id, name)
VALUES
	(1, 'ROLE_USER'),
	(2, 'ROLE_ADMIN');

INSERT INTO
	user_role (user_id, role_id)
VALUES
	(2, 1),
	(1, 1)
;