package spotifymanager.dao;

import spotifymanager.model.Chanson;

import java.util.List;
import java.util.Optional;

// Contrat d'accès aux données. LecteurCSV l'implémente pour l'instant,
// une version PostgreSQL viendra s'ajouter derrière la même interface plus tard.
public interface SourceDonnees {

    List<Chanson> trouverTous();

    Optional<Chanson> trouverParId(int id);

    int ajouter(Chanson chanson);

    void modifier(Chanson chanson);

    void supprimer(int id);
}
