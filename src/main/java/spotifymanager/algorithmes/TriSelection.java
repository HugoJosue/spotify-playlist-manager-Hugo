package spotifymanager.algorithmes;

import java.util.Comparator;
import java.util.List;

// O(n²), mais peu d'échanges (au plus n-1)
public class TriSelection<T> implements AlgorithmeTri<T> {

    @Override
    public void trier(List<T> liste, Comparator<T> comparateur) {
        int n = liste.size();
        for (int i = 0; i < n - 1; i++) {
            int indexMin = i;
            for (int j = i + 1; j < n; j++) {
                if (comparateur.compare(liste.get(j), liste.get(indexMin)) < 0) {
                    indexMin = j;
                }
            }
            if (indexMin != i) {
                echanger(liste, i, indexMin);
            }
        }
    }

    private void echanger(List<T> liste, int i, int j) {
        T temp = liste.get(i);
        liste.set(i, liste.get(j));
        liste.set(j, temp);
    }

    @Override
    public String getNom() {
        return "Tri par sélection";
    }
}
