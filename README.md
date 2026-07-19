# java-filmorate
Template repository for Filmorate project.

## Схема базы данных

### ER-диаграмма
![ER-диаграмма](docs/ER_diagram.svg)

### Описание
В проекте используется реляционная база данных. 
Описание таблиц:
- **`user`** — пользователи (логин, почта, имя, дата рождения).
- **`film`** — фильмы (название, описание, дата релиза, продолжительность, рейтинг MPA).
- **`genre`** — жанры фильмов.
- **`user_film_like`** — связь пользователей и фильмов (лайки).
- **`user_friend`** — связь пользователей (дружба).
- **`film_genre`** — связь фильмов и жанров (многие ко многим).

### Примеры запросов для основных операций приложения

#### 1. Получить список всех фильмов
```sql
SELECT *
FROM film;
```

#### 2. Получить список всех пользователей
```sql
SELECT *
FROM user;
```

#### 3. Получить топ-10 фильмов по количеству лайков

```sql
SELECT 
    f.film_id,
    f.name,
    COUNT(l.user_id) AS likes_count
FROM film f
INNER JOIN user_film_like l ON f.film_id = l.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

#### 4. Получить список общих друзей двух пользователей
```sql
SELECT u.user_id, u.login
FROM user_friend f1
JOIN user_friend f2 ON f1.friend_id = f2.friend_id
JOIN user u ON u.user_id = f1.friend_id
WHERE f1.user_id = 1
  AND f2.user_id = 2;
```

#### 5. Получить фильм по id
```sql
SELECT film_id id,
       name,
       description,
       release_date,
       duration,
       mpa_rating
FROM film
WHERE film_id = 1;
```

#### 6. Получить список всех друзей пользователя
```sql
SELECT uf.friend_id id,
       u.login
FROM user_friend uf
LEFT JOIN user u ON uf.friend_id = u.user_id
WHERE uf.user_id = 1;
```

#### 7. Получить пользователя по id
```sql
SELECT user_id id,
       login,
       email,
       name,
       birthday
FROM user
WHERE user_id = 1;
```