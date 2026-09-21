package spotifymanager.model;

// Les genres musicaux supportés dans le catalogue
public enum Genre {
    POP,
    ROCK,
    HIP_HOP,
    JAZZ,
    CLASSIQUE,
    ELECTRONIC,
    METAL,
    COUNTRY,
    RNB,
    REGGAE;

    // permet de lire un genre depuis le csv sans se soucier de la casse
    public static Genre depuisTexte(String texte) {
        if (texte == null) {
            throw new IllegalArgumentException("Le genre ne peut pas être null");
        }
        String normalise = texte.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        return Genre.valueOf(normalise);
    }
}
