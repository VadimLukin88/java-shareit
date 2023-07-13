INSERT INTO users (name,email) VALUES
	 ('updateName','updateName@user.com'),
	 ('user','user@user.com'),
	 ('other','other@other.com'),
	 ('practicum','practicum@yandex.ru'),
	 ('user5','user5@ya.ru'),
	 ('user6','user6@ya.ru');
INSERT INTO requests (description,requestor_id,created) VALUES
	 ('Хотел бы воспользоваться щёткой для обуви',1,'2023-07-03 22:33:04.442891');
INSERT INTO items (name,description,is_available,owner_id,request_id) VALUES
	 ('Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1,NULL),
	 ('Клей Момент','Тюбик суперклея марки Момент',true,4,NULL),
	 ('Отвертка','Аккумуляторная отвертка',true,4,NULL),
	 ('Кухонный стол','Стол для празднования',true,6,NULL),
	 ('Щётка для обуви','Стандартная щётка для обуви',true,4,1);
INSERT INTO bookings (start_date,end_date,item_id,booker_id,status) VALUES
	 ('2023-07-03 22:32:47','2023-07-03 22:32:48',2,1,'APPROVED'),
	 ('2023-07-04 22:32:44','2023-07-05 22:32:44',2,1,'APPROVED'),
	 ('2023-07-04 22:32:48','2023-07-04 23:32:48',1,4,'REJECTED'),
	 ('2023-07-03 23:32:48','2023-07-04 00:32:48',2,5,'APPROVED'),
	 ('2023-07-03 22:32:56','2023-07-04 22:32:53',3,1,'REJECTED'),
	 ('2023-07-03 22:32:56','2023-07-03 22:32:57',2,1,'APPROVED'),
	 ('2023-07-03 22:32:57','2023-07-03 23:32:55',4,1,'APPROVED'),
	 ('2023-07-13 22:32:54','2023-07-14 22:32:54',1,5,'APPROVED');
INSERT INTO comments (text,item_id,author_id,created) VALUES
	 ('Add comment from user1',2,1,'2023-07-03 22:33:01.862842');