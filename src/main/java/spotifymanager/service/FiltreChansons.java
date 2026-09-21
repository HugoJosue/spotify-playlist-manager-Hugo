package spotifymanager.service;

import spotifymanager.model.Chanson;
import spotifymanager.model.Genre;
import spotifymanager.model.Playlist;
import spotifymanager.util.TexteUtil;

// Regroupe les critères de filtre. null = pas de filtre sur ce critère.
// accepte() combine tout avec un ET logique.
public class FiltreChansons {

    private Genre genre;
    private Integer decennie;
    private String artiste;
    private Integer dureeMaxSecondes;
    private Integer ecoutesMin;
    private Playlist playlist;
    private String texteRecherche;

    public void setGenre(Genre genre) { this.genre = genre; }
    public void setDecennie(Integer decennie) { this.decennie = decennie; }
    public void setArtiste(String artiste) { this.artiste = (artiste == null || artiste.isBlank()) ? null : artiste; }
    public void setDureeMaxSecondes(Integer dureeMaxSecondes) { this.dureeMaxSecondes = dureeMaxSecondes; }
    public void setEcoutesMin(Integer ecoutesMin) { this.ecoutesMin = ecoutesMin; }
    public void setPlaylist(Playlist playlist) { this.playlist = playlist; }
    public void setTexteRecherche(String texte) { this.texteRecherche = (texte == null || texte.isBlank()) ? null : texte; }

    public Genre getGenre() { return genre; }
    public Integer getDecennie() { return decennie; }
    public String getArtiste() { return artiste; }
    public Playlist getPlaylist() { return playlist; }

    public void reinitialiser() {
        genre = null;
        decennie = null;
        artiste = null;
        dureeMaxSecondes = null;
        ecoutesMin = null;
        playlist = null;
        texteRecherche = null;
    }

    public boolean accepte(Chanson chanson) {
        if (genre != null && chanson.getGenre() != genre) return false;

        if (decennie != null) {
            int decennieChanson = (chanson.getAnneeSortie() / 10) * 10;
            if (decennieChanson != decennie) return false;
        }

        if (artiste != null && !chanson.getArtiste().equalsIgnoreCase(artiste)) return false;

        if (dureeMaxSecondes != null && chanson.getDureeSecondes() > dureeMaxSecondes) return false;

        if (ecoutesMin != null && chanson.getNombreEcoutes() < ecoutesMin) return false;

        if (playlist != null && !playlist.getChansons().contains(chanson)) return false;

        if (texteRecherche != null) {
            boolean matchTitre = TexteUtil.contient(chanson.getTitre(), texteRecherche);
            boolean matchArtiste = TexteUtil.contient(chanson.getArtiste(), texteRecherche);
            boolean matchAlbum = TexteUtil.contient(chanson.getAlbum(), texteRecherche);
            if (!matchTitre && !matchArtiste && !matchAlbum) return false;
        }

        return true;
    }
}
