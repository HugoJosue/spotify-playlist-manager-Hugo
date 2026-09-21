package spotifymanager.service;

import spotifymanager.model.Chanson;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

// Les comparateurs utilisés avec les tris. Collator plutôt que compareTo
// pour un tri alphabétique correct en français (accents, majuscules).
public final class ComparateursChanson {

    private static final Collator COLLATOR = Collator.getInstance(Locale.FRENCH);
    static {
        COLLATOR.setStrength(Collator.PRIMARY);
    }

    private ComparateursChanson() {
    }

    public static Comparator<Chanson> parTitre() {
        return (a, b) -> COLLATOR.compare(a.getTitre(), b.getTitre());
    }

    public static Comparator<Chanson> parArtiste() {
        return (a, b) -> COLLATOR.compare(a.getArtiste(), b.getArtiste());
    }

    public static Comparator<Chanson> parDureeCroissante() {
        return Comparator.comparingInt(Chanson::getDureeSecondes);
    }

    public static Comparator<Chanson> parAnneeRecenteDabord() {
        return (a, b) -> Integer.compare(b.getAnneeSortie(), a.getAnneeSortie());
    }

    public static Comparator<Chanson> parAnneeAncienneDabord() {
        return Comparator.comparingInt(Chanson::getAnneeSortie);
    }

    public static Comparator<Chanson> parEcoutesDecroissant() {
        return (a, b) -> Integer.compare(b.getNombreEcoutes(), a.getNombreEcoutes());
    }

    public static Comparator<Chanson> parGenre() {
        return Comparator.<Chanson, String>comparing(c -> c.getGenre().name())
                .thenComparing(parTitre());
    }
}
