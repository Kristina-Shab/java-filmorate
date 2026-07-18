INSERT INTO genre (name)
SELECT 'Комедия' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Комедия');

INSERT INTO genre (name)
SELECT 'Драма' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Драма');

INSERT INTO genre (name)
SELECT 'Мультфильм' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Мультфильм');

INSERT INTO genre (name)
SELECT 'Триллер' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Триллер');

INSERT INTO genre (name)
SELECT 'Документальный' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Документальный');

INSERT INTO genre (name)
SELECT 'Боевик' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Боевик');


INSERT INTO mpa_rating (name, description)
SELECT 'G', 'у фильма нет возрастных ограничений' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE name = 'G');

INSERT INTO mpa_rating (name, description)
SELECT 'PG', 'детям рекомендуется смотреть фильм с родителями' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE name = 'PG');

INSERT INTO mpa_rating (name, description)
SELECT 'PG-13', 'детям до 13 лет просмотр не желателен' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE name = 'PG-13');

INSERT INTO mpa_rating (name, description)
SELECT 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE name = 'R');

INSERT INTO mpa_rating (name, description)
SELECT 'NC-17', 'лицам до 18 лет просмотр запрещён' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_rating WHERE name = 'NC-17');


-- INSERT INTO mpa_rating (name, description)
-- VALUES ('G', 'у фильма нет возрастных ограничений'),
--        ('PG', 'детям рекомендуется смотреть фильм с родителями'),
--        ('PG-13', 'детям до 13 лет просмотр не желателен'),
--        ('R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
--        ('NC-17', 'лицам до 18 лет просмотр запрещён');