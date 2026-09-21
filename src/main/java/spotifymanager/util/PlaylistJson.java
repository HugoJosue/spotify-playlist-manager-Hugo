package spotifymanager.util;

import spotifymanager.model.Bibliotheque;
import spotifymanager.model.Chanson;
import spotifymanager.model.Playlist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Export/import d'une playlist en JSON, écrit à la main (pas de librairie externe).
// Format : {"nom":"Mon Top","chansonsIds":[3,17,42]}
public final class PlaylistJson {

    private static final Pattern PATTERN_NOM = Pattern.compile("\"nom\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern PATTERN_IDS = Pattern.compile("\"chansonsIds\"\\s*:\\s*\\[([^]]*)]");

    private PlaylistJson() {
    }

    public static void exporter(Playlist playlist, Path cheminFichier) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"nom\": \"").append(echapper(playlist.getNom())).append("\",\n");
        json.append("  \"chansonsIds\": [");
        List<Chanson> chansons = playlist.getChansons();
        for (int i = 0; i < chansons.size(); i++) {
            json.append(chansons.get(i).getId());
            if (i < chansons.size() - 1) json.append(", ");
        }
        json.append("]\n");
        json.append("}\n");
        Files.writeString(cheminFichier, json.toString());
    }

    // les ids qui n'existent pas dans la bibliothèque cible sont juste ignorés
    public static Playlist importer(Path cheminFichier, Bibliotheque bibliotheque) throws IOException {
        String contenu = Files.readString(cheminFichier);

        Matcher matcherNom = PATTERN_NOM.matcher(contenu);
        String nom = matcherNom.find() ? deEchapper(matcherNom.group(1)) : "Playlist importée";

        String nomFinal = nom;
        int suffixe = 2;
        while (bibliotheque.trouverPlaylistParNom(nomFinal).isPresent()) {
            nomFinal = nom + " (" + suffixe++ + ")";
        }

        Playlist playlist = bibliotheque.creerPlaylist(nomFinal);

        Matcher matcherIds = PATTERN_IDS.matcher(contenu);
        if (matcherIds.find()) {
            String[] idsTexte = matcherIds.group(1).split(",");
            for (String idTexte : idsTexte) {
                String nettoye = idTexte.trim();
                if (nettoye.isEmpty()) continue;
                int id = Integer.parseInt(nettoye);
                Optional<Chanson> chanson = bibliotheque.trouverChansonParId(id);
                chanson.ifPresent(playlist::ajouter);
            }
        }
        return playlist;
    }

    private static String echapper(String texte) {
        return texte.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String deEchapper(String texte) {
        return texte.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
