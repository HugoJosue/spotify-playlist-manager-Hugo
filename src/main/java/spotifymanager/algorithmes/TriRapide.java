package spotifymanager.algorithmes;

import java.util.Comparator;
import java.util.List;

// O(n log n) en moyenne. Pivot = élément du milieu.
public class TriRapide<T> implements AlgorithmeTri<T> {

    @Override
    public void trier(List<T> liste, Comparator<T> comparateur) {
        if (liste.size() < 2) return;
        quicksort(liste, 0, liste.size() - 1, comparateur);
    }

    private void quicksort(List<T> liste, int bas, int haut, Comparator<T> comparateur) {
        if (bas >= haut) return;
        int indexPivot = partitionner(liste, bas, haut, comparateur);
        quicksort(liste, bas, indexPivot - 1, comparateur);
        quicksort(liste, indexPivot + 1, haut, comparateur);
    }

    private int partitionner(List<T> liste, int bas, int haut, Comparator<T> comparateur) {
        int milieu = bas + (haut - bas) / 2;
        echanger(liste, milieu, haut); // pivot déplacé en fin de segment
        T pivot = liste.get(haut);

        int i = bas - 1;
        for (int j = bas; j < haut; j++) {
            if (comparateur.compare(liste.get(j), pivot) <= 0) {
                i++;
                echanger(liste, i, j);
            }
        }
        echanger(liste, i + 1, haut);
        return i + 1;
    }

    private void echanger(List<T> liste, int i, int j) {
        T temp = liste.get(i);
        liste.set(i, liste.get(j));
        liste.set(j, temp);
    }

    @Override
    public String getNom() {
        return "Tri rapide";
    }
}
