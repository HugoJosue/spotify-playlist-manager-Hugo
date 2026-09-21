package spotifymanager.algorithmes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// O(n log n) garanti, stable. Découpe en deux, trie chaque moitié, fusionne.
public class TriFusion<T> implements AlgorithmeTri<T> {

    @Override
    public void trier(List<T> liste, Comparator<T> comparateur) {
        if (liste.size() < 2) return;
        List<T> trie = trierRecursif(liste, comparateur);
        for (int i = 0; i < liste.size(); i++) {
            liste.set(i, trie.get(i));
        }
    }

    private List<T> trierRecursif(List<T> liste, Comparator<T> comparateur) {
        if (liste.size() < 2) {
            return liste;
        }
        int milieu = liste.size() / 2;
        List<T> gauche = trierRecursif(new ArrayList<>(liste.subList(0, milieu)), comparateur);
        List<T> droite = trierRecursif(new ArrayList<>(liste.subList(milieu, liste.size())), comparateur);
        return fusionner(gauche, droite, comparateur);
    }

    private List<T> fusionner(List<T> gauche, List<T> droite, Comparator<T> comparateur) {
        List<T> resultat = new ArrayList<>(gauche.size() + droite.size());
        int i = 0, j = 0;
        while (i < gauche.size() && j < droite.size()) {
            if (comparateur.compare(gauche.get(i), droite.get(j)) <= 0) {
                resultat.add(gauche.get(i++));
            } else {
                resultat.add(droite.get(j++));
            }
        }
        while (i < gauche.size()) resultat.add(gauche.get(i++));
        while (j < droite.size()) resultat.add(droite.get(j++));
        return resultat;
    }

    @Override
    public String getNom() {
        return "Tri fusion";
    }
}
