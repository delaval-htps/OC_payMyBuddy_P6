INSERT INTO application_account (id,account_number,balance)
VALUES
	(1,000001,1000),
	(2,000002,500.50),
	(3,000003,100.78),
	(4,000004,0),
	(5,000005,50),
	(6,000006,100)
	as new (nid,an,b)
ON DUPLICATE KEY UPDATE id = nid ^;
	
--	insert datas in table use
  
INSERT INTO user (id,email,password,enabled,last_name, first_name,address,zip,city,phone,application_account_id)
VALUES
	 (1,'delaval.htps@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Delaval','Dorian','26 av Maréchal Foch',13260,'Cassis','0618460160',
		(SELECT id FROM application_account WHERE balance = 1000)),
	(2,'emilie.baudouin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Baudouin','Emilie','26 av Maréchal Foch',13260,'Cassis','0622296638',
		(SELECT id FROM application_account WHERE balance = 500.50)),
	(3,'monique.baudouin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Baudouin','Monique','8 rue de la Tour',68300,'Mulbach Sur Munter','0718460250',
		(SELECT id FROM application_account WHERE balance = 100.78)),
	(4,'caroline.verrier@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Verrier','Caroline','310 rue Jean Jaures',59860,'Bruay sur Escaut','0743421689',
		(SELECT id FROM application_account WHERE balance = 0)),
	(5,'beatrice.hubald@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Hubald','Beatrice','8 rue Chabaud la Tour',59860,'Bruay Sur Escaut','0612131415',
		(SELECT id FROM application_account WHERE balance = 50)),
	(6,'manon.rollin@gmail.com','$2a$10$a7gjURC2zg97BnIJw61WOOdG.Fz7sDbU6tSmUuN.bqtvxrh89BOF2',1,'Rollin','Manon','10 place de la Piscine',68350,'Colmar','0645467033', 
		(SELECT id FROM application_account WHERE balance = 100))
	as new(uid,m,pa,e,l,f,a,z,c,ph,aaid)
ON DUPLICATE KEY UPDATE id= uid ^;

INSERT INTO role (id,name)
VALUES
	(1,"ROLE_USER"),
	(2,"ROLE_ADMIN")
	
as new (uid,name)
ON DUPLICATE KEY UPDATE id= uid ^;

INSERT INTO user_role (user_id,role_id)
VALUES
	(2,1)
as new (uid,rid)
ON DUPLICATE KEY UPDATE user_id=uid ^;