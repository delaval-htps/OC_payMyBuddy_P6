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
  
INSERT INTO user (id,email,password,enabled,last_name, first_name,address,zip,city,phone,number_application_account)
VALUES
	(1,'delaval.htps@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Delaval','Dorian','26 av Maréchal Foch',13260,'Cassis','0618460160',
		(SELECT number_account FROM application_account WHERE balance = 1000)),
	(2,'emilie.baudouin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Baudouin','Emilie','26 av Maréchal Foch',13260,'Cassis','0622296638',
		(SELECT number_account FROM application_account WHERE balance = 500.50)),
	(3,'monique.baudouin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Baudouin','Monique','8 rue de la Tour',68300,'Mulbach Sur Munter','0718460250',
		(SELECT number_account FROM application_account WHERE balance = 100.78)),
	(4,'caroline.verrier@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Verrier','Caroline','310 rue Jean Jaures',59860,'Bruay sur Escaut','0743421689',
		(SELECT number_account FROM application_account WHERE balance = 0)),
	(5,'beatrice.hubald@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Hubald','Beatrice','8 rue Chabaud la Tour',59860,'Bruay Sur Escaut','0612131415',
		(SELECT number_account FROM application_account WHERE balance = 50)),
	(6,'manon.rollin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Rollin','Manon','10 place de la Piscine',68350,'Colmar','0645467033', 
		(SELECT number_account FROM application_account WHERE balance = 100))
	as new(uid,m,pa,e,l,f,a,z,c,ph,n)
ON DUPLICATE KEY UPDATE id= uid ^;

