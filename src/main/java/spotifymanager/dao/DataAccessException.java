package spotifymanager.dao;

// Enveloppe les SQLException pour que SourceDonnees n'ait pas besoin de les déclarer,
// tout en gardant l'appelant capable de l'attraper explicitement(contrairement à une RuntimeException).
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
