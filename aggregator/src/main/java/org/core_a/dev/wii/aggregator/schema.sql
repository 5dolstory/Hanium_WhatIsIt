DROP TABLE IF EXISTS project;
CREATE TABLE project(
    seq int NOT NULL AUTO_INCREMENT,
    name varchar(32),
    cycle varchar(32),
    url varchar(256),
    status varchar(32),

    PRIMARY KEY (seq)
);
 -- insert into project (name, cycle, url, status) values ('test01', '00,30', 'http://web2.ruliweb.daum.net/daum/rss.htm?bbs=2&id=101&bbsId=G005&itemId=421&c1=1&c2=1&menu=1', 'active');

DROP TABLE IF EXISTS report;
CREATE TABLE report(
    seq int NOT NULL AUTO_INCREMENT,
    project_seq int,
    amount int,
    start_at varchar(32),
    end_at varchar(32),
    keyword text,
    create_at timestamp default current_timestamp,

    PRIMARY KEY (seq)
);
