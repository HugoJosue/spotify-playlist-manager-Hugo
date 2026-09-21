package spotifymanager.algorithmes;

import java.util.Comparator;
import java.util.List;

// O(n²) dans le pire cas, s'arrête tôt si la liste est déjà triée
public class TriBulles<T> implements AlgorithmeTri<T> {

    @Override
    public void trier(List<T> liste, Comparator<T> comparateur) {
        int n = liste.size();
        boolean permutationFaite;
        do {
            permutationFaite = false;
            for (int i = 0; i < n - 1; i++) {
                if (comparateur.compare(liste.get(i), liste.get(i + 1)) > 0) {
                    echanger(liste, i, i + 1);
                    permutationFaite = true;
                }
            }
            n--; // le plus grand élément est déjà à sa place
        } while (permutationFaite);
    }

    private void echanger(List<T> liste, int i, int j) {
        T temp = liste.get(i);
        liste.set(i, liste.get(j));
        liste.set(j, temp);
    }

    @Override
    public String getNom() {
        return "Tri à bulles";
    }
}
