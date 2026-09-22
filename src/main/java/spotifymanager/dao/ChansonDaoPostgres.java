package spotifymanager.dao;

import spotifymanager.model.Chanson;
import spotifymanager.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implémentation PostgreSQL de SourceDonnees, derrière la même interface que LecteurCSV.
// Le schéma normalise l'artiste dans sa propre table (voir schema.sql), donc chaque méthode fait un JOIN ou résout l'id de l'artiste au besoin.
public class ChansonDaoPostgres implements SourceDonnees {

    private final ConnexionBD connexion;

    private static final String SELECT_BASE =
            "SELECT c.id_chanson, c.titre, a.nom AS artiste, c.album, c.annee, " +
            "c.genre, c.duree_secondes, c.ecoutes " +
            "FROM chanson c JOIN artiste a ON c.id_artiste = a.id_artiste ";

    public ChansonDaoPostgres(ConnexionBD connexion) {
        this.connexion = connexion;
    }

    @Override
    public List<Chanson> trouverTous() {
        String sql = SELECT_BASE + "ORDER BY c.id_chanson";
        try (Connection conn = connexion.obtenirConnexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Chanson> resultat = new ArrayList<>();
            while (rs.next()) {
                resultat.add(mapper(rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DataAccessException("Échec de trouverTous", e);
        }
    }

    @Override
    public Optional<Chanson> trouverParId(int id) {
        String sql = SELECT_BASE + "WHERE c.id_chanson = ?";
        try (Connection conn = connexion.obtenirConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Échec de trouverParId", e);
        }
    }

    @Override
    public int ajouter(Chanson chanson) {
        String sql = "INSERT INTO chanson (titre, id_artiste, album, annee, genre, duree_secondes, ecoutes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_chanson";
        try (Connection conn = connexion.obtenirConnexion()) {
            int idArtiste = trouverOuCreerArtiste(conn, chanson.getArtiste());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, chanson.getTitre());
                ps.setInt(2, idArtiste);
                ps.setString(3, chanson.getAlbum());
                ps.setInt(4, chanson.getAnneeSortie());
                ps.setString(5, chanson.getGenre().name());
                ps.setInt(6, chanson.getDureeSecondes());
                ps.setInt(7, chanson.getNombreEcoutes());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Échec de ajouter", e);
        }
    }

    @Override
    public void modifier(Chanson chanson) {
        String sql = "UPDATE chanson SET titre = ?, id_artiste = ?, album = ?, annee = ?, " +
                "genre = ?, duree_secondes = ?, ecoutes = ? WHERE id_chanson = ?";
        try (Connection conn = connexion.obtenirConnexion()) {
            int idArtiste = trouverOuCreerArtiste(conn, chanson.getArtiste());
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, chanson.getTitre());
                ps.setInt(2, idArtiste);
                ps.setString(3, chanson.getAlbum());
                ps.setInt(4, chanson.getAnneeSortie());
                ps.setString(5, chanson.getGenre().name());
                ps.setInt(6, chanson.getDureeSecondes());
                ps.setInt(7, chanson.getNombreEcoutes());
                ps.setInt(8, chanson.getId());
                int lignes = ps.executeUpdate();
                if (lignes == 0) {
                    throw new DataAccessException("Aucune chanson avec l'id " + chanson.getId(), null);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Échec de modifier", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM chanson WHERE id_chanson = ?";
        try (Connection conn = connexion.obtenirConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Échec de supprimer", e);
        }
    }

    // cherche l'artiste par nom, le crée s'il n'existe pas encore
    private int trouverOuCreerArtiste(Connection conn, String nom) throws SQLException {
        String select = "SELECT id_artiste FROM artiste WHERE nom = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        String insert = "INSERT INTO artiste (nom) VALUES (?) RETURNING id_artiste";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Chanson mapper(ResultSet rs) throws SQLException {
        return new Chanson(
                rs.getInt("id_chanson"),
                rs.getString("titre"),
                rs.getString("artiste"),
                rs.getString("album"),
                rs.getInt("annee"),
                Genre.valueOf(rs.getString("genre")),
                rs.getInt("duree_secondes"),
                rs.getInt("ecoutes")
        );
    }
}
