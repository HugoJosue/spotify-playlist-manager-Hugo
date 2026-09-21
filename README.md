# Spotify Playlist Manager

Projet pour le cours 420-930-MA (Lab 2, sujet 3)

Appli JavaFX qui gère une bibliothèque de chansons + des playlists perso.

## Équipe
- Hugo Josue Alcin

## Ça contient quoi
- model -> Chanson, Genre, Playlist, Bibliotheque
- dao -> interface SourceDonnees (pour pas être coincé avec le CSV)
- util -> LecteurCSV, lit/écrit le csv
- service -> filtres, tri, pagination, playlists, lecteur simulé
- algorithmes -> les tris faits à la main (bulles, sélection, insertion, fusion, rapide)
- controller -> le controller JavaFX + la fenêtre (MainView.fxml)

## Comment lancer ça
Dans IntelliJ, panneau Maven à droite -> spotify-playlist-manager -> Plugins -> javafx -> javafx:run

Ou en ligne de commande, à partir du dossier du projet :
```
mvn clean javafx:run
```

## Les données
400 chansons dans data/chansons.csv (généré avec generate_data.py, mélange de vraies
chansons + générées). Le sujet demandait 300-500 chansons, 8 genres, 30 artistes -> c'est bon.

## Fonctionnalités
- recherche en temps réel (titre/artiste/album)
- filtres qui se combinent : genre, décennie, artiste, durée max, écoutes min, playlist
- 6 façons de trier, avec le choix de l'algo (bulles/sélection/insertion/fusion/rapide)
- pagination 25 par page
- playlists : créer, supprimer, ajouter/retirer une chanson, réordonner, voir le contenu
- lecteur simulé (play/pause/next/prev/shuffle)
- bonus : stats (top 10 + genre le + écouté), export/import playlist en json, mix quotidien,
  3 thèmes (sombre/clair/perso)

## Reste à faire
- compléter la liste des membres plus haut
- tests unitaires si c'est demandé dans les consignes générales

## Lab 3 (PostgreSQL)
- schema.sql -> crée les tables (artiste, chanson, playlist, playlist_chanson)
- generate_donnees_sql.py -> génère donnees.sql à partir de data/chansons.csv
- database.properties.example -> copier en database.properties (jamais commit, déjà dans .gitignore) et mettre tes vrais identifiants
- dao/ConnexionBD.java -> lit database.properties, seule classe qui connaît l'URL JDBC
- dao/ChansonDaoPostgres.java -> implémente SourceDonnees avec du JDBC (PreparedStatement partout, try-with-resources, Optional)
- Le controller utilise ChansonDaoPostgres par défaut maintenant (voir le commentaire dans initialize()) ; LecteurCSV reste dispo, une seule ligne à changer pour revenir au CSV
- Formulaires Ajouter/Modifier/Supprimer une chanson dans l'UI, avec validation et gestion des erreurs (Alert, pas de crash)
- Testé en réel : schema.sql + donnees.sql rejoués sur une base PostgreSQL fraîche, DAO testé (ajouter/modifier/trouverParId/supprimer), contrainte ON DELETE RESTRICT vérifiée

### Pour lancer avec PostgreSQL
1. Installer PostgreSQL, créer une base (ex: `createdb spotify_db`)
2. `psql -d spotify_db -f schema.sql`
3. `python3 generate_donnees_sql.py` puis `psql -d spotify_db -f donnees.sql`
4. Copier database.properties.example vers database.properties, mettre les bons identifiants
5. Lancer l'appli normalement (mvn javafx:run)
