--insert into application_identifier 

INSERT INTO application_identifier ( application_identifier_id,email,password)
VALUES
	(1,'delaval.htps@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2'),
	(2,'emilie.baudouin@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2'),
	(3,'monique.baudouin@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2'),
	(4,'caroline.verrier@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2'),
	(5,'beatrice.hubald@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2'),
	(6,'manon.rollin@gmail.com','{bcrypt}$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2')
	as new(aid,m,p)
ON DUPLICATE KEY UPDATE  application_identifier_id= aid ^;
	
INSERT INTO application_account (number_account,balance)
VALUES
	(1,1000),
	(2,500.50),
	(3,100.78),
	(4,0),
	(5,50),
	(6,100)
	as new (nid,b)
ON DUPLICATE KEY UPDATE number_account = nid ^;
	
--	insert datas in table use
  
INSERT INTO user (user_id,lastName, firstName,address,zip,city,phone,number_application_account,application_identifier_id)
VALUES
	(1,'Delaval','Dorian','26 av Maréchal Foch',13260,'Cassis','0618460160',
		(SELECT number_account FROM application_account WHERE balance = 1000),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'delaval.htps@gmail.com')),
	(2,'Baudouin','Emilie','26 av Maréchal Foch',13260,'Cassis','0622296638',
		(SELECT number_account FROM application_account WHERE balance = 500.50),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'emilie.baudouin@gmail.com')),
	(3,'Baudouin','Monique','8 rue de la Tour',68300,'Mulbach Sur Munter','0718460250',
		(SELECT number_account FROM application_account WHERE balance = 100.78),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'monique.baudouin@gmail.com')),
	(4,'Verrier','Caroline','310 rue Jean Jaures',59860,'Bruay sur Escaut','0743421689',
		(SELECT number_account FROM application_account WHERE balance = 0),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'caroline.verrier@gmail.com')),
	(5,'Hubald','Beatrice','8 rue Chabaud la Tour',59860,'Bruay Sur Escaut','0612131415',
		(SELECT number_account FROM application_account WHERE balance = 50),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'beatrice.hubald@gmail.com')),
	(6,'Rollin','Manon','10 place de la Piscine',68350,'Colmar','0645467033', 
		(SELECT number_account FROM application_account WHERE balance = 100),
		(SELECT application_identifier_id FROM application_identifier WHERE email = 'manon.rollin@gmail.com'))
	as new(uid,l,f,a,z,c,p,n,id)
ON DUPLICATE KEY UPDATE user_id= uid ^;

