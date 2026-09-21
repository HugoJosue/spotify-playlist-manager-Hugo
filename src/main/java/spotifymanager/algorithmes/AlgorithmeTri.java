package spotifymanager.algorithmes;

import java.util.Comparator;
import java.util.List;

// Contrat commun aux tris. Générique pour pouvoir trier les chansons selon
// n'importe quel critère (titre, durée, écoutes...) sans dupliquer le code de tri.
public interface AlgorithmeTri<T> {

    // trie en place, la liste doit être mutable (ArrayList)
    void trier(List<T> liste, Comparator<T> comparateur);

    String getNom();
}
