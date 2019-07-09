CREATE SEQUENCE person_id_seq;
CREATE TABLE person (
  person_id     bigint default nextval('person_id_seq'),
  name          varchar(255) NOT NULL,
  CONSTRAINT person_pk PRIMARY KEY (person_id)
);

CREATE SEQUENCE film_id_seq;
CREATE TABLE film (
    film_id     bigint default nextval('film_id_seq'),
    title       varchar(40),
    date_prod   date,
    kind        varchar(10),
    len         interval hour to minute,
    CONSTRAINT film_pk PRIMARY KEY(film_id)
);

CREATE SEQUENCE cinema_id_seq;
CREATE TABLE cinema (
    cinema_id   bigint default nextval('cinema_id_seq;'),
    name        text,
    location    text,
    CONSTRAINT cinema_pk PRIMARY KEY(cinema_id)
);

CREATE TABLE cinema_films (
    cinema_id      bigint,
    film_id        bigint,
    CONSTRAINT cinema_films_pk PRIMARY KEY(cinema_id,film_id),
    CONSTRAINT cinema_films_fk_cinema FOREIGN KEY (cinema_id) REFERENCES cinema (cinema_id),
    CONSTRAINT cinema_films_fk_film FOREIGN KEY (film_id) REFERENCES film (film_id)
);


