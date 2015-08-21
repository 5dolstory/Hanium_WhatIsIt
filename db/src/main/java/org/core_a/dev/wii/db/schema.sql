-- Data
CREATE TABLE article(
    article_no STRING,
    author STRING,
    title STRING,
    context STRING,
    morpheme_title ARRAY<STRING>,
    morpheme_context ARRAY<STRING>,
    created_at DATE,
    parsed_at DATE
);

CREATE TABLE schedule(
    name STRING,
    schedule STRING
);

CREATE TABLE keyword(
    keyword STRING,
    article_no STRING
);



-- Interim findings
-- -- for user recommandation
CREATE TABLE interim_data_author(
    begin_at DATE,
    end_at DATE,
    author STRING,
    morpheme_title MAP<STRING,INT>,
    morpheme_context MAP<STRING,INT>,
    create_at DATE
);

-- -- for article recommandation, tf/idf
-- -- -- or reiteration jaccard coefficient 
CREATE TABLE interim_data_article(
    begin_at DATE,
    end_at DATE,
    article_no STRING,
    morpheme_title MAP<STRING,FLOAT>,
    morpheme_context MAP<STRING,FLOAT>,
    create_at DATE
);



-- Report
-- -- author
-- -- -- relative user recommandation
CREATE TABLE report_author(
    begin_at DATE,
    end_at DATE,
    author STRING,
    recommand_author MAP<STRING,FLOAT>,
    create_at DATE
);

-- -- article
-- -- -- relative article recommandation
CREATE TABLE report_article(
    begin_at DATE,
    end_at DATE,
    article_no STRING,
    recommand_article_no MAP<STRING,FLOAT>,
    create_at DATE
);
