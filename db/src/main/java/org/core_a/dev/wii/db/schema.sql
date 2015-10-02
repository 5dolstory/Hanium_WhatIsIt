-- Data
DROP TABLE IF EXIST article;
CREATE TABLE article(
CREATE TABLE article(
    project STRING,
    title STRING,
    content STRING,
    author STRING,
    no STRING,
    url STRING,
    morpheme_title ARRAY<STRING>,
    morpheme_content ARRAY<STRING>,
    created_at DATE,
    parsed_at DATE
);

CREATE TABLE keyword(
    project STRING,
    keyword STRING,
    article_no STRING
);
