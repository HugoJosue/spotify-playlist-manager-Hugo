"""
Génère data/chansons.csv : mélange de vraies chansons connues + chansons
générées (artiste réel x titre inventé).
Usage : python3 generate_data.py
"""
import csv
import random

random.seed(42)

GENRES = ["POP", "ROCK", "HIP_HOP", "JAZZ", "CLASSIQUE", "ELECTRONIC", "METAL", "COUNTRY", "RNB", "REGGAE"]

# 100 chansons réelles connues (titre, artiste, album, annee, genre, duree_sec)
CHANSONS_REELLES = [
    ("Bohemian Rhapsody", "Queen", "A Night at the Opera", 1975, "ROCK", 354),
    ("Hotel California", "Eagles", "Hotel California", 1976, "ROCK", 391),
    ("Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", 1971, "ROCK", 482),
    ("Smells Like Teen Spirit", "Nirvana", "Nevermind", 1991, "ROCK", 301),
    ("Sweet Child O' Mine", "Guns N' Roses", "Appetite for Destruction", 1987, "ROCK", 356),
    ("Billie Jean", "Michael Jackson", "Thriller", 1982, "POP", 294),
    ("Like a Prayer", "Madonna", "Like a Prayer", 1989, "POP", 339),
    ("Shape of You", "Ed Sheeran", "Divide", 2017, "POP", 233),
    ("Blinding Lights", "The Weeknd", "After Hours", 2020, "RNB", 200),
    ("Rolling in the Deep", "Adele", "21", 2011, "POP", 228),
    ("Hey Jude", "The Beatles", "Hey Jude", 1968, "ROCK", 431),
    ("Let It Be", "The Beatles", "Let It Be", 1970, "ROCK", 243),
    ("Come Together", "The Beatles", "Abbey Road", 1969, "ROCK", 259),
    ("Here Comes the Sun", "The Beatles", "Abbey Road", 1969, "ROCK", 185),
    ("Yesterday", "The Beatles", "Help!", 1965, "ROCK", 125),
    ("Lose Yourself", "Eminem", "8 Mile", 2002, "HIP_HOP", 326),
    ("Sicko Mode", "Travis Scott", "Astroworld", 2018, "HIP_HOP", 312),
    ("HUMBLE.", "Kendrick Lamar", "DAMN.", 2017, "HIP_HOP", 177),
    ("God's Plan", "Drake", "Scorpion", 2018, "HIP_HOP", 198),
    ("Empire State of Mind", "Jay-Z", "The Blueprint 3", 2009, "HIP_HOP", 276),
    ("Take Five", "Dave Brubeck", "Time Out", 1959, "JAZZ", 324),
    ("So What", "Miles Davis", "Kind of Blue", 1959, "JAZZ", 545),
    ("Feeling Good", "Nina Simone", "I Put a Spell on You", 1965, "JAZZ", 176),
    ("Fly Me to the Moon", "Frank Sinatra", "It Might as Well Be Swing", 1964, "JAZZ", 148,),
    ("What a Wonderful World", "Louis Armstrong", "What a Wonderful World", 1967, "JAZZ", 139),
    ("Clair de Lune", "Claude Debussy", "Suite Bergamasque", 1905, "CLASSIQUE", 300),
    ("Symphony No. 5", "Ludwig van Beethoven", "Symphony No. 5", 1808, "CLASSIQUE", 420),
    ("The Four Seasons: Spring", "Antonio Vivaldi", "The Four Seasons", 1725, "CLASSIQUE", 210),
    ("Canon in D", "Johann Pachelbel", "Canon and Gigue", 1680, "CLASSIQUE", 300),
    ("One More Time", "Daft Punk", "Discovery", 2000, "ELECTRONIC", 320),
    ("Levels", "Avicii", "True", 2011, "ELECTRONIC", 203),
    ("Strobe", "Deadmau5", "For Lack of a Better Name", 2009, "ELECTRONIC", 636),
    ("Titanium", "David Guetta", "Nothing but the Beat", 2011, "ELECTRONIC", 245),
    ("Enter Sandman", "Metallica", "Metallica", 1991, "METAL", 331),
    ("Master of Puppets", "Metallica", "Master of Puppets", 1986, "METAL", 515),
    ("Paranoid", "Black Sabbath", "Paranoid", 1970, "METAL", 168),
    ("Breaking the Law", "Judas Priest", "British Steel", 1980, "METAL", 154),
    ("Jolene", "Dolly Parton", "Jolene", 1973, "COUNTRY", 161),
    ("Ring of Fire", "Johnny Cash", "Ring of Fire", 1963, "COUNTRY", 156),
    ("Take Me Home, Country Roads", "John Denver", "Poems, Prayers & Promises", 1971, "COUNTRY", 195),
    ("No Woman No Cry", "Bob Marley", "Natty Dread", 1974, "REGGAE", 265),
    ("Three Little Birds", "Bob Marley", "Exodus", 1977, "REGGAE", 180),
    ("Red Red Wine", "UB40", "Labour of Love", 1983, "REGGAE", 187),
    ("I Want It That Way", "Backstreet Boys", "Millennium", 1999, "POP", 213),
    ("Toxic", "Britney Spears", "In the Zone", 2003, "POP", 199),
    ("Uptown Funk", "Bruno Mars", "Uptown Special", 2014, "POP", 269),
    ("Bad Guy", "Billie Eilish", "When We All Fall Asleep", 2019, "POP", 194),
    ("As It Was", "Harry Styles", "Harry's House", 2022, "POP", 167),
    ("Flowers", "Miley Cyrus", "Endless Summer Vacation", 2023, "POP", 200),
]

# 30 artistes distincts pour la génération (certains partagés avec la liste réelle)
ARTISTES = [
    "Queen", "Eagles", "Led Zeppelin", "Nirvana", "Guns N' Roses",
    "Michael Jackson", "Madonna", "Ed Sheeran", "The Weeknd", "Adele",
    "The Beatles", "Eminem", "Travis Scott", "Kendrick Lamar", "Drake",
    "Jay-Z", "Miles Davis", "Nina Simone", "Frank Sinatra", "Louis Armstrong",
    "Ludwig van Beethoven", "Antonio Vivaldi", "Daft Punk", "Avicii", "Deadmau5",
    "Metallica", "Black Sabbath", "Dolly Parton", "Johnny Cash", "Bob Marley",
]

GENRE_PAR_ARTISTE = {
    "Queen": "ROCK", "Eagles": "ROCK", "Led Zeppelin": "ROCK", "Nirvana": "ROCK", "Guns N' Roses": "ROCK",
    "Michael Jackson": "POP", "Madonna": "POP", "Ed Sheeran": "POP", "The Weeknd": "RNB", "Adele": "POP",
    "The Beatles": "ROCK", "Eminem": "HIP_HOP", "Travis Scott": "HIP_HOP", "Kendrick Lamar": "HIP_HOP", "Drake": "HIP_HOP",
    "Jay-Z": "HIP_HOP", "Miles Davis": "JAZZ", "Nina Simone": "JAZZ", "Frank Sinatra": "JAZZ", "Louis Armstrong": "JAZZ",
    "Ludwig van Beethoven": "CLASSIQUE", "Antonio Vivaldi": "CLASSIQUE", "Daft Punk": "ELECTRONIC", "Avicii": "ELECTRONIC", "Deadmau5": "ELECTRONIC",
    "Metallica": "METAL", "Black Sabbath": "METAL", "Dolly Parton": "COUNTRY", "Johnny Cash": "COUNTRY", "Bob Marley": "REGGAE",
}

MOTS_TITRE = [
    "Midnight", "Fading", "Broken", "Golden", "Silent", "Electric", "Lonely", "Wild",
    "Neon", "Distant", "Endless", "Fallen", "Burning", "Hollow", "Crystal", "Velvet",
    "Shadows", "Whispers", "Echoes", "Ashes", "Horizon", "Static", "Reckless", "Fragile",
    "Sunset", "Storm", "Ghost", "Fever", "Wonder", "Chaos",
]
MOTS_TITRE_2 = [
    "Dreams", "Heart", "Nights", "Road", "Rain", "Fire", "Sky", "Love",
    "Streets", "Light", "Waves", "Truth", "Memory", "Tears", "Freedom", "Illusion",
    "Silence", "Reflection", "Motion", "Fate",
]


def genere_titre():
    return f"{random.choice(MOTS_TITRE)} {random.choice(MOTS_TITRE_2)}"


def genere_dataset(nb_total=400):
    lignes = []
    idx = 1

    # 1. Les chansons réelles telles quelles
    for titre, artiste, album, annee, genre, duree in CHANSONS_REELLES:
        lignes.append([idx, titre, artiste, album, annee, genre, duree, random.randint(1000, 2_000_000)])
        idx += 1

    # 2. Le reste : générées, artiste réel x titre inventé
    while len(lignes) < nb_total:
        artiste = random.choice(ARTISTES)
        genre = GENRE_PAR_ARTISTE[artiste]
        titre = genere_titre()
        album = f"{random.choice(MOTS_TITRE)} Sessions"
        annee = random.randint(1970, 2024)
        duree = random.randint(120, 360)  # 2 à 6 minutes
        ecoutes = random.randint(0, 800_000)
        lignes.append([idx, titre, artiste, album, annee, genre, duree, ecoutes])
        idx += 1

    return lignes


def main():
    lignes = genere_dataset(400)

    genres_presents = {l[5] for l in lignes}
    artistes_presents = {l[2] for l in lignes}
    print(f"{len(lignes)} chansons, {len(genres_presents)} genres, {len(artistes_presents)} artistes distincts")
    assert len(lignes) >= 300 and len(lignes) <= 500
    assert len(genres_presents) >= 8
    assert len(artistes_presents) >= 30

    with open("data/chansons.csv", "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["id", "titre", "artiste", "album", "annee", "genre", "duree_sec", "ecoutes"])
        writer.writerows(lignes)


if __name__ == "__main__":
    main()
