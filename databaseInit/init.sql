drop table myuser;
create table myuser(
	id int not null primary key auto_increment,
	user_session varchar(100),
	user_code	varchar(100),
	access_token varchar(250),
	expires_in int,
	uid bigint unsigned,
	created_time timestamp
); 
drop table weibo;
create table weibo(
	id int not null primary key auto_increment,
	created_at timestamp,
	weibo_id varchar(50),
	mid varchar(50),
	idstr bigint,
	weibo_text text,
	weibo_source_id int,
	favorited char(1),
	truncated char(1),
	in_reply_to_status_id bigint ,
	in_reply_to_user_id bigint ,
	in_reply_to_screen_name varchar(50),
	thumbnail_pic	varchar(500),
	bmiddle_pic		varchar(500),
	orginal_pic		varchar(500),
	retweeted_weibo_id int,
	user_id int,
	geo varchar(500),
	longitude double,
	latitude double,
	reposts_count int,
	comments_count int,
	annotation varchar(500),
	mlevel int,
	visible_id int,
	created_time timestamp
) ;
drop table weibo_source;
create table weibo_source (
	id int not null primary key auto_increment,
	url	varchar(50),
	relation_ship varchar(50),
	weibo_source_name varchar(50)
);
drop table visible;
create table visible (
	id int not null primary key auto_increment,
	visible_type int,
	list_id int
);
 
drop table user;
create table user(
	id int not null primary key auto_increment,
	origin_id varchar(50),
	screen_name varchar(100),
	name	varchar(100),
	province	int,
	city		int,
	location  varchar(250),
	description	varchar(500),
	url		varchar(500),
	profile_image_url varchar(500),
	domain	varchar(100),
	gender	char(1),
	followers_count int,
	friends_count	int,
	statuses_count	int,
	favourites_count int,
	created_at	timestamp,
	following	char(1),
	verified	char(1),
	verified_type int,
	created_time timestamp
)