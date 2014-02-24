create table user(
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
	weibo_text varchar(1000),
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
	geo varchar(500),
	longitude double,
	latitude double,
	reposts_count int,
	comments_count int,
	annotation varchar(500),
	mlevel int,
	visible_id int,
	created_time timestamp
);

create table weibo_source (
	id int not null primary key auto_increment,
	url	varchar(50),
	relation_ship varchar(50),
	weibo_source_name varchar(50)
);

create table visible (
	id int not null primary key auto_increment,
	visible_type int,
	list_id int
);
 

create table author(
	id int not null primary key auto_increment,
	origin_id bigint unsigned,
	idstr varchar(50),
	screen_name varchar(100),
	author_name	varchar(100),
	province	int,
	city		int,
	location  varchar(250),
	description	varchar(500),
	url		varchar(50),
	profile_image_url varchar(50),
	profile_url	varchar(50),
	domain	varchar(50),
	weihao	varchar(50),
	gender	char(1),
	followers_count int,
	friends_count	int,
	statuses_count	int,
	favourites_count int,
	created_at	varchar(50),
	following	char(1),
	allow_all_act_msg char(1),
	geo_enabled	char(1),
	verified	char(1),
	verified_type int,
	remark	varchar(50),
	weibo_id bigint unsigned,
	allow_all_comment char(1),
	avatar_large	varchar(50),
	avatar_hd		varchar(50),
	verified_reason	varchar(250),
	follow_me	char(1),
	online_status	int,
	bi_followers_count int,
	lang	varchar(10),
	created_time timestamp
)