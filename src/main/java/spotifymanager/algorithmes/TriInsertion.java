package spotifymanager.algorithmes;

import java.util.Comparator;
import java.util.List;

// O(n²) au pire, mais rapide si la liste est presque déjà triée
public class TriInsertion<T> implements AlgorithmeTri<T> {

    @Override
    public void trier(List<T> liste, Comparator<T> comparateur) {
        int n = liste.size();
        for (int i = 1; i < n; i++) {
            T courant = liste.get(i);
            int j = i - 1;
            while (j >= 0 && comparateur.compare(liste.get(j), courant) > 0) {
                liste.set(j + 1, liste.get(j));
                j--;
            }
            liste.set(j + 1, courant);
        }
    }

    @Override
    public String getNom() {
        return "Tri par insertion";
    }
}
