package spotifymanager.model;

import java.util.Objects;

// Une chanson du catalogue
public class Chanson {

    private final int id;
    private String titre;
    private String artiste;
    private String album;
    private int anneeSortie;
    private Genre genre;
    private int dureeSecondes;
    private int nombreEcoutes;

    public Chanson(int id, String titre, String artiste, String album,
                    int anneeSortie, Genre genre, int dureeSecondes, int nombreEcoutes) {
        this.id = id;
        this.titre = Objects.requireNonNull(titre, "Le titre est obligatoire");
        this.artiste = Objects.requireNonNull(artiste, "L'artiste est obligatoire");
        this.album = album == null ? "" : album;
        this.anneeSortie = anneeSortie;
        this.genre = Objects.requireNonNull(genre, "Le genre est obligatoire");
        this.dureeSecondes = dureeSecondes;
        this.nombreEcoutes = Math.max(0, nombreEcoutes);
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getArtiste() { return artiste; }
    public String getAlbum() { return album; }
    public int getAnneeSortie() { return anneeSortie; }
    public Genre getGenre() { return genre; }
    public int getDureeSecondes() { return dureeSecondes; }
    public int getNombreEcoutes() { return nombreEcoutes; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setArtiste(String artiste) { this.artiste = artiste; }
    public void setAlbum(String album) { this.album = album; }
    public void setAnneeSortie(int anneeSortie) { this.anneeSortie = anneeSortie; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setDureeSecondes(int dureeSecondes) { this.dureeSecondes = dureeSecondes; }
    public void setNombreEcoutes(int nombreEcoutes) { this.nombreEcoutes = nombreEcoutes; }

    public void incrementerEcoutes() {
        this.nombreEcoutes++;
    }

    // ex: 225 secondes -> "3:45"
    public String getDureeFormatee() {
        int minutes = dureeSecondes / 60;
        int secondes = dureeSecondes % 60;
        return String.format("%d:%02d", minutes, secondes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chanson)) return false;
        return id == ((Chanson) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return titre + " — " + artiste + " (" + getDureeFormatee() + ")";
    }
}
