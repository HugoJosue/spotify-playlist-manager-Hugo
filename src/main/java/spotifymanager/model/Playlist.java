package spotifymanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Playlist perso : liste ordonnée de chansons, sans doublon
public class Playlist {

    private String nom;
    private final List<Chanson> chansons;
    private final LocalDate dateCreation;

    public Playlist(String nom) {
        this.nom = nom;
        this.chansons = new ArrayList<>();
        this.dateCreation = LocalDate.now();
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDate getDateCreation() { return dateCreation; }

    public List<Chanson> getChansons() {
        return Collections.unmodifiableList(chansons);
    }

    // pas de doublon
    public boolean ajouter(Chanson chanson) {
        if (chansons.contains(chanson)) {
            return false;
        }
        return chansons.add(chanson);
    }

    public boolean retirer(Chanson chanson) {
        return chansons.remove(chanson);
    }

    public boolean deplacerVersLeHaut(Chanson chanson) {
        int index = chansons.indexOf(chanson);
        if (index <= 0) return false;
        Collections.swap(chansons, index, index - 1);
        return true;
    }

    public boolean deplacerVersLeBas(Chanson chanson) {
        int index = chansons.indexOf(chanson);
        if (index < 0 || index >= chansons.size() - 1) return false;
        Collections.swap(chansons, index, index + 1);
        return true;
    }

    public void vider() {
        chansons.clear();
    }

    public int getDureeTotaleSecondes() {
        return chansons.stream().mapToInt(Chanson::getDureeSecondes).sum();
    }

    public String getDureeTotaleFormatee() {
        int total = getDureeTotaleSecondes();
        int h = total / 3600;
        int m = (total % 3600) / 60;
        int s = total % 60;
        return h > 0 ? String.format("%d:%02d:%02d", h, m, s) : String.format("%d:%02d", m, s);
    }

    @Override
    public String toString() {
        return nom + " (" + chansons.size() + " chansons)";
    }
}
