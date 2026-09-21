package spotifymanager.service;

import spotifymanager.algorithmes.AlgorithmeTri;
import spotifymanager.model.Bibliotheque;
import spotifymanager.model.Chanson;
import spotifymanager.model.Genre;
import spotifymanager.model.Playlist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

// Logique métier au-dessus de Bibliotheque : filtres, tri, pagination, playlists
public class BibliothequeService {

    private static final int TAILLE_PAGE_DEFAUT = 25;

    private final Bibliotheque bibliotheque;

    public BibliothequeService(Bibliotheque bibliotheque) {
        this.bibliotheque = bibliotheque;
    }

    public Bibliotheque getBibliotheque() {
        return bibliotheque;
    }

    public List<Chanson> filtrer(FiltreChansons filtre) {
        List<Chanson> resultat = new ArrayList<>();
        for (Chanson c : bibliotheque.getChansons()) {
            if (filtre.accepte(c)) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    public List<String> getArtistesDistincts() {
        TreeSet<String> artistes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Chanson c : bibliotheque.getChansons()) {
            artistes.add(c.getArtiste());
        }
        return new ArrayList<>(artistes);
    }

    public List<Integer> getDecenniesDisponibles() {
        TreeSet<Integer> decennies = new TreeSet<>();
        for (Chanson c : bibliotheque.getChansons()) {
            decennies.add((c.getAnneeSortie() / 10) * 10);
        }
        return new ArrayList<>(decennies);
    }

    public void trier(List<Chanson> liste, AlgorithmeTri<Chanson> algorithme, Comparator<Chanson> comparateur) {
        algorithme.trier(liste, comparateur);
    }

    public List<Chanson> paginer(List<Chanson> listeComplete, int numeroPage) {
        return paginer(listeComplete, numeroPage, TAILLE_PAGE_DEFAUT);
    }

    public List<Chanson> paginer(List<Chanson> listeComplete, int numeroPage, int taillePage) {
        int debut = numeroPage * taillePage;
        if (debut >= listeComplete.size() || debut < 0) {
            return List.of();
        }
        int fin = Math.min(debut + taillePage, listeComplete.size());
        return new ArrayList<>(listeComplete.subList(debut, fin));
    }

    public int getNombreDePages(int tailleListe) {
        return getNombreDePages(tailleListe, TAILLE_PAGE_DEFAUT);
    }

    public int getNombreDePages(int tailleListe, int taillePage) {
        return Math.max(1, (int) Math.ceil(tailleListe / (double) taillePage));
    }

    public Playlist creerPlaylist(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de la playlist est obligatoire");
        }
        if (bibliotheque.trouverPlaylistParNom(nom).isPresent()) {
            throw new IllegalArgumentException("Une playlist nommée \"" + nom + "\" existe déjà");
        }
        return bibliotheque.creerPlaylist(nom);
    }

    public void supprimerPlaylist(Playlist playlist) {
        bibliotheque.supprimerPlaylist(playlist);
    }

    public boolean ajouterChansonAPlaylist(Playlist playlist, Chanson chanson) {
        return playlist.ajouter(chanson);
    }

    public boolean retirerChansonDePlaylist(Playlist playlist, Chanson chanson) {
        return playlist.retirer(chanson);
    }

    // top N des chansons les plus écoutées
    public List<Chanson> getTopEcoutes(int n) {
        List<Chanson> copie = new ArrayList<>(bibliotheque.getChansons());
        new spotifymanager.algorithmes.TriFusion<Chanson>()
                .trier(copie, ComparateursChanson.parEcoutesDecroissant());
        return copie.subList(0, Math.min(n, copie.size()));
    }

    // le genre dont la somme des écoutes est la plus élevée
    public Genre getGenrePlusEcoute() {
        java.util.Map<Genre, Long> totalParGenre = new java.util.EnumMap<>(Genre.class);
        for (Chanson c : bibliotheque.getChansons()) {
            totalParGenre.merge(c.getGenre(), (long) c.getNombreEcoutes(), Long::sum);
        }
        return totalParGenre.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }

    private static final String NOM_MIX_QUOTIDIEN = "Mix quotidien";

    // régénère la playlist "Mix quotidien" : 20 chansons au hasard parmi
    // le tiers le plus écouté du catalogue
    public Playlist genererMixQuotidien() {
        List<Chanson> tries = new ArrayList<>(bibliotheque.getChansons());
        new spotifymanager.algorithmes.TriFusion<Chanson>()
                .trier(tries, ComparateursChanson.parEcoutesDecroissant());

        int tailleBassin = Math.max(1, tries.size() / 3);
        List<Chanson> preferees = new ArrayList<>(tries.subList(0, tailleBassin));
        java.util.Collections.shuffle(preferees);

        Playlist mix = bibliotheque.trouverPlaylistParNom(NOM_MIX_QUOTIDIEN).orElse(null);
        if (mix == null) {
            mix = bibliotheque.creerPlaylist(NOM_MIX_QUOTIDIEN);
        } else {
            mix.vider();
        }
        for (Chanson c : preferees.subList(0, Math.min(20, preferees.size()))) {
            mix.ajouter(c);
        }
        return mix;
    }
}
