package spotifymanager.service;

import spotifymanager.model.Chanson;

import java.util.List;
import java.util.Random;

// Lecteur simulé : pas d'audio réel, juste l'état "chanson en cours" + navigation
public class LecteurSimule {

    private final Random aleatoire = new Random();

    private List<Chanson> file = List.of();
    private int indexCourant = -1;
    private boolean enLecture = false;
    private boolean modeAleatoire = false;

    public void definirFile(List<Chanson> nouvelleFile) {
        Chanson courante = getChansonCourante();
        this.file = nouvelleFile;
        this.indexCourant = (courante != null) ? file.indexOf(courante) : -1;
        if (indexCourant < 0 && !file.isEmpty()) {
            indexCourant = 0;
        }
    }

    public Chanson getChansonCourante() {
        if (indexCourant < 0 || indexCourant >= file.size()) return null;
        return file.get(indexCourant);
    }

    public boolean isEnLecture() { return enLecture; }
    public boolean isModeAleatoire() { return modeAleatoire; }

    public void togglePlayPause() {
        if (getChansonCourante() != null) {
            enLecture = !enLecture;
        }
    }

    public void toggleShuffle() {
        modeAleatoire = !modeAleatoire;
    }

    // incrémente les écoutes de la chanson qu'on quitte
    public void suivant() {
        if (file.isEmpty()) return;
        Chanson quittee = getChansonCourante();
        if (quittee != null) {
            quittee.incrementerEcoutes();
        }
        indexCourant = modeAleatoire ? aleatoire.nextInt(file.size()) : (indexCourant + 1) % file.size();
        enLecture = true;
    }

    public void precedent() {
        if (file.isEmpty()) return;
        indexCourant = modeAleatoire
                ? aleatoire.nextInt(file.size())
                : (indexCourant - 1 + file.size()) % file.size();
        enLecture = true;
    }
}
