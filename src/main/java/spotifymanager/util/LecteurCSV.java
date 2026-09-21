package spotifymanager.util;

import spotifymanager.dao.SourceDonnees;
import spotifymanager.model.Chanson;
import spotifymanager.model.Genre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implémentation CSV de SourceDonnees.
// Colonnes attendues : id,titre,artiste,album,annee,genre,duree_sec,ecoutes
public class LecteurCSV implements SourceDonnees {

    private static final String SEPARATEUR = ",";

    private final Path cheminFichier;
    private final List<Chanson> cache = new ArrayList<>();
    private int prochainId = 1;

    public LecteurCSV(Path cheminFichier) {
        this.cheminFichier = cheminFichier;
        charger();
    }

    private void charger() {
        cache.clear();
        if (!Files.exists(cheminFichier)) {
            return;
        }
        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier.toFile()))) {
            String ligne = lecteur.readLine(); // en-tête
            while ((ligne = lecteur.readLine()) != null) {
                if (ligne.isBlank()) continue;
                Chanson chanson = parserLigne(ligne);
                cache.add(chanson);
                prochainId = Math.max(prochainId, chanson.getId() + 1);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire le CSV : " + cheminFichier, e);
        }
    }

    private Chanson parserLigne(String ligne) {
        String[] champs = decouperLigneCsv(ligne);
        int id = Integer.parseInt(champs[0].trim());
        String titre = champs[1].trim();
        String artiste = champs[2].trim();
        String album = champs[3].trim();
        int annee = Integer.parseInt(champs[4].trim());
        Genre genre = Genre.depuisTexte(champs[5].trim());
        int duree = Integer.parseInt(champs[6].trim());
        int ecoutes = Integer.parseInt(champs[7].trim());
        return new Chanson(id, titre, artiste, album, annee, genre, duree, ecoutes);
    }

    // découpage manuel qui respecte les guillemets (ex: album contenant une virgule)
    private String[] decouperLigneCsv(String ligne) {
        List<String> champs = new ArrayList<>();
        StringBuilder courant = new StringBuilder();
        boolean dansGuillemets = false;

        for (int i = 0; i < ligne.length(); i++) {
            char c = ligne.charAt(i);
            if (c == '"') {
                dansGuillemets = !dansGuillemets;
            } else if (c == ',' && !dansGuillemets) {
                champs.add(courant.toString());
                courant.setLength(0);
            } else {
                courant.append(c);
            }
        }
        champs.add(courant.toString());
        return champs.toArray(new String[0]);
    }

    private void sauvegarder() {
        try (var ecrivain = Files.newBufferedWriter(cheminFichier)) {
            ecrivain.write("id,titre,artiste,album,annee,genre,duree_sec,ecoutes\n");
            for (Chanson c : cache) {
                ecrivain.write(String.join(SEPARATEUR,
                        String.valueOf(c.getId()),
                        echapper(c.getTitre()),
                        echapper(c.getArtiste()),
                        echapper(c.getAlbum()),
                        String.valueOf(c.getAnneeSortie()),
                        c.getGenre().name(),
                        String.valueOf(c.getDureeSecondes()),
                        String.valueOf(c.getNombreEcoutes())));
                ecrivain.write("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire le CSV : " + cheminFichier, e);
        }
    }

    private String echapper(String valeur) {
        if (valeur.contains(SEPARATEUR) || valeur.contains("\"")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }

    @Override
    public List<Chanson> trouverTous() {
        return new ArrayList<>(cache);
    }

    @Override
    public Optional<Chanson> trouverParId(int id) {
        return cache.stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public int ajouter(Chanson chanson) {
        Chanson avecId = new Chanson(prochainId++, chanson.getTitre(), chanson.getArtiste(),
                chanson.getAlbum(), chanson.getAnneeSortie(), chanson.getGenre(),
                chanson.getDureeSecondes(), chanson.getNombreEcoutes());
        cache.add(avecId);
        sauvegarder();
        return avecId.getId();
    }

    @Override
    public void modifier(Chanson chanson) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == chanson.getId()) {
                cache.set(i, chanson);
                sauvegarder();
                return;
            }
        }
        throw new IllegalArgumentException("Aucune chanson avec l'id " + chanson.getId());
    }

    @Override
    public void supprimer(int id) {
        cache.removeIf(c -> c.getId() == id);
        sauvegarder();
    }
}
