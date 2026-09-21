"""
Génère donnees.sql à partir de data/chansons.csv, en éclatant l'artiste
dans sa propre table (le CSV a l'artiste en texte, le schéma normalisé
a besoin d'un id_artiste).
Usage : python3 generate_donnees_sql.py
"""
import csv


def echapper(texte):
    # échappe les apostrophes pour du SQL littéral (ex: "Guns N' Roses")
    return texte.replace("'", "''")


def main():
    with open("data/chansons.csv", newline="", encoding="utf-8") as f:
        lignes = list(csv.DictReader(f))

    artistes = {}  # nom -> id, dans l'ordre de première apparition
    for ligne in lignes:
        nom = ligne["artiste"]
        if nom not in artistes:
            artistes[nom] = len(artistes) + 1

    with open("donnees.sql", "w", encoding="utf-8") as sortie:
        sortie.write("-- Généré depuis data/chansons.csv par generate_donnees_sql.py\n\n")

        sortie.write("INSERT INTO artiste (id_artiste, nom) VALUES\n")
        valeurs = [f"({idx}, '{echapper(nom)}')" for nom, idx in artistes.items()]
        sortie.write(",\n".join(valeurs))
        sortie.write(";\n\n")

        sortie.write("INSERT INTO chanson (titre, id_artiste, album, annee, genre, duree_secondes, ecoutes) VALUES\n")
        valeurs = []
        for ligne in lignes:
            id_artiste = artistes[ligne["artiste"]]
            valeurs.append(
                "({}, {}, '{}', {}, '{}', {}, {})".format(
                    f"'{echapper(ligne['titre'])}'",
                    id_artiste,
                    echapper(ligne["album"]),
                    ligne["annee"],
                    ligne["genre"],
                    ligne["duree_sec"],
                    ligne["ecoutes"],
                )
            )
        sortie.write(",\n".join(valeurs))
        sortie.write(";\n\n")

        # remet le compteur auto-incrémenté d'artiste après les inserts manuels
        sortie.write(f"SELECT setval('artiste_id_artiste_seq', {len(artistes)});\n")

    print(f"{len(lignes)} chansons, {len(artistes)} artistes -> donnees.sql")


if __name__ == "__main__":
    main()
