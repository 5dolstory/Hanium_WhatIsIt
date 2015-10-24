DROP TABLE IF EXISTS article;
CREATE TABLE article(
    project STRING,
    title STRING,
    content STRING,
    author STRING,
    no STRING,
    url STRING,
    morpheme_title ARRAY<STRING>,
    morpheme_content ARRAY<STRING>,
    created_at TIMESTAMP
) PARTITIONED BY (datestamp STRING);

DROP TABLE IF EXISTS keyword;
CREATE TABLE keyword(
    project STRING,
    keyword STRING,
    article_no STRING
);

DROP TABLE IF EXISTS dummy;
CREATE TABLE dummy(
    seq STRING
);
insert into dummy values ('1');
