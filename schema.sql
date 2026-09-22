-- Schéma relationnel pour Spotify Playlist Manager
-- Rejouable depuis une base vide : DROP puis CREATE dans le bon ordre.

DROP TABLE IF EXISTS playlist_chanson;
DROP TABLE IF EXISTS playlist;
DROP TABLE IF EXISTS chanson;
DROP TABLE IF EXISTS artiste;

CREATE TABLE artiste (
    id_artiste  SERIAL PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE chanson (
    id_chanson      SERIAL PRIMARY KEY,
    titre           VARCHAR(200) NOT NULL,
    id_artiste      INTEGER NOT NULL REFERENCES artiste(id_artiste) ON DELETE RESTRICT,
    album           VARCHAR(200) NOT NULL DEFAULT '',
    annee           SMALLINT NOT NULL CHECK (annee BETWEEN 1600 AND 2100),
    genre           VARCHAR(20) NOT NULL CHECK (genre IN (
                        'POP','ROCK','HIP_HOP','JAZZ','CLASSIQUE',
                        'ELECTRONIC','METAL','COUNTRY','RNB','REGGAE'
                    )),
    duree_secondes  INTEGER NOT NULL CHECK (duree_secondes > 0),
    ecoutes         INTEGER NOT NULL DEFAULT 0 CHECK (ecoutes >= 0)
);

CREATE INDEX idx_chanson_artiste ON chanson(id_artiste);

CREATE TABLE playlist (
    id_playlist     SERIAL PRIMARY KEY,
    nom             VARCHAR(150) NOT NULL UNIQUE,
    date_creation   DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Table de liaison playlist <-> chanson (many-to-many), avec l'ordre des
-- chansons dans la playlist (colonne position).
CREATE TABLE playlist_chanson (
    id_playlist     INTEGER NOT NULL REFERENCES playlist(id_playlist) ON DELETE CASCADE,
    id_chanson      INTEGER NOT NULL REFERENCES chanson(id_chanson) ON DELETE CASCADE,
    position        INTEGER NOT NULL,
    PRIMARY KEY (id_playlist, id_chanson)
);

-- artiste -> chanson : ON DELETE RESTRICT (empêche de supprimer un artiste qui a encore des chansons)
-- playlist_chanson : ON DELETE CASCADE des deux côtés (une ligne de liaison n'a pas de sens sans sa playlist ou sa chanson)
