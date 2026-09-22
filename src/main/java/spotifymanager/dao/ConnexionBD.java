package spotifymanager.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Seule classe du projet à connaître l'URL JDBC. Les identifiants viennent de database.properties .
public class ConnexionBD {

    private final String url;
    private final String utilisateur;
    private final String motDePasse;

    public ConnexionBD(Path fichierProperties) {
        Properties props = new Properties();
        try (var flux = Files.newInputStream(fichierProperties)) {
            props.load(flux);
        } catch (IOException e) {
            throw new DataAccessException("Impossible de lire " + fichierProperties
                    + " — copie database.properties.example vers database.properties et remplis-le.", e);
        }
        this.url = props.getProperty("db.url");
        this.utilisateur = props.getProperty("db.user");
        this.motDePasse = props.getProperty("db.password");
    }

    public Connection obtenirConnexion() {
        try {
            return DriverManager.getConnection(url, utilisateur, motDePasse);
        } catch (SQLException e) {
            throw new DataAccessException("Connexion à la base impossible : " + e.getMessage(), e);
        }
    }
}
