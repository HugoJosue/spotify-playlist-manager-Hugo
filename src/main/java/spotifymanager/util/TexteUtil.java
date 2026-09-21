package spotifymanager.util;

import java.text.Normalizer;

// pour la recherche insensible casse + accents (ex: "ecole" trouve "école")
public final class TexteUtil {

    private TexteUtil() {
    }

    public static String normaliser(String texte) {
        if (texte == null) return "";
        String sansAccents = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.toLowerCase();
    }

    public static boolean contient(String texteComplet, String recherche) {
        return normaliser(texteComplet).contains(normaliser(recherche));
    }
}
