create sequence if not exists commentid start 1;
create sequence if not exists postid start 1;
create sequence if not exists uid start 1;
create sequence if not exists imageid start 1;
CREATE DOMAIN GENDER CHAR(1)
    CHECK (value IN ( 'F' , 'M' ) );

CREATE TABLE fbuser (
	uid		int default nextval('uid'),
	firstname	VARCHAR(20) not null,
	surname		VARCHAR(20) not null,
	email		VARCHAR(30),
	birthday 	date,
	gender      	GENDER,
	PRIMARY KEY (uid)
);

CREATE TABLE password (
	uid        	int references fbuser on delete cascade,
	email       	VARCHAR(30),
	password	VARCHAR(1024),
	PRIMARY KEY (uid),
	FOREIGN KEY (uid) REFERENCES fbuser(uid)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE follows (
	uid1		int references fbuser on delete cascade,
	uid2		int references fbuser on delete cascade,
	PRIMARY KEY (uid1, uid2),
	FOREIGN KEY (uid1) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (uid2) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE requests (
	uid1		int references fbuser on delete cascade,
	uid2		int references fbuser on delete cascade,
	PRIMARY KEY (uid1, uid2),
	FOREIGN KEY (uid1) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (uid2) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE friends (
	uid1		int references fbuser on delete cascade,
	uid2		int references fbuser on delete cascade,
	PRIMARY KEY (uid1, uid2),
	FOREIGN KEY (uid1) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (uid2) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE post (
	postid 		int primary key default nextval('postid'),
	uid		int references fbuser on delete cascade,
	timestamp	TIMESTAMP,
	text		TEXT,
	likes       	int,
	hasImage	BOOLEAN DEFAULT FALSE,
	FOREIGN KEY (uid) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE image (
	imageid 	int primary key default nextval('imageid'),
	postid 		int references post on delete cascade,
	imgpath		VARCHAR(30) not null,
	FOREIGN KEY (postid) REFERENCES post(postid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE comments (
	commentid 	int primary key default nextval('commentid'),
	uid		int references fbuser on delete cascade,
	postid  	int references post on delete cascade,
	timestamp	TIMESTAMP,
	text		TEXT,
	replies     	int default 0,
	FOREIGN KEY (uid) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (postid) REFERENCES post(postid)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);

CREATE TABLE subcomments (
	subcommentid 	int primary key default nextval('commentid'),
	uid		int references fbuser on delete cascade,
	timestamp	TIMESTAMP,
	text		TEXT,
	commentid  	int references comments on delete cascade,
	FOREIGN KEY (uid) REFERENCES fbuser(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (commentid) REFERENCES comments(commentid)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);
