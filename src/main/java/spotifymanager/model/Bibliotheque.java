package spotifymanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Contient toutes les chansons et toutes les playlists
public class Bibliotheque {

    private final List<Chanson> chansons = new ArrayList<>();
    private final List<Playlist> playlists = new ArrayList<>();

    public List<Chanson> getChansons() {
        return Collections.unmodifiableList(chansons);
    }

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    public void ajouterChanson(Chanson chanson) {
        chansons.add(chanson);
    }

    public void ajouterToutesLesChansons(List<Chanson> nouvellesChansons) {
        chansons.addAll(nouvellesChansons);
    }

    public boolean supprimerChanson(Chanson chanson) {
        return chansons.remove(chanson);
    }

    public Optional<Chanson> trouverChansonParId(int id) {
        return chansons.stream().filter(c -> c.getId() == id).findFirst();
    }

    public Playlist creerPlaylist(String nom) {
        Playlist playlist = new Playlist(nom);
        playlists.add(playlist);
        return playlist;
    }

    public boolean supprimerPlaylist(Playlist playlist) {
        return playlists.remove(playlist);
    }

    public Optional<Playlist> trouverPlaylistParNom(String nom) {
        return playlists.stream().filter(p -> p.getNom().equalsIgnoreCase(nom)).findFirst();
    }
}
