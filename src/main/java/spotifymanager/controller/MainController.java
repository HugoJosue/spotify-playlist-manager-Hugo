package spotifymanager.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import spotifymanager.algorithmes.*;
import spotifymanager.dao.ChansonDaoPostgres;
import spotifymanager.dao.ConnexionBD;
import spotifymanager.dao.DataAccessException;
import spotifymanager.dao.SourceDonnees;
import spotifymanager.model.Bibliotheque;
import spotifymanager.model.Chanson;
import spotifymanager.model.Genre;
import spotifymanager.model.Playlist;
import spotifymanager.service.BibliothequeService;
import spotifymanager.service.FiltreChansons;
import spotifymanager.service.LecteurSimule;
import spotifymanager.util.LecteurCSV;
import spotifymanager.util.PlaylistJson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MainController {

    // recherche / filtres
    @FXML private TextField champRecherche;
    @FXML private ComboBox<String> comboGenre;
    @FXML private ComboBox<String> comboDecennie;
    @FXML private ComboBox<String> comboArtiste;
    @FXML private Slider sliderDureeMax;
    @FXML private Label labelDureeMax;
    @FXML private Slider sliderEcoutesMin;
    @FXML private Label labelEcoutesMin;
    @FXML private ComboBox<Playlist> comboFiltrePlaylist;
    @FXML private Button boutonReinitialiserFiltres;

    // tri / thème / stats
    @FXML private ComboBox<String> comboCritereTri;
    @FXML private ComboBox<String> comboAlgorithme;
    @FXML private Button boutonTrier;
    @FXML private ComboBox<String> comboTheme;
    @FXML private Button boutonStatistiques;

    // table
    @FXML private BorderPane racine;
    @FXML private TableView<Chanson> tableChansons;
    @FXML private TableColumn<Chanson, String> colTitre;
    @FXML private TableColumn<Chanson, String> colArtiste;
    @FXML private TableColumn<Chanson, String> colAlbum;
    @FXML private TableColumn<Chanson, Integer> colAnnee;
    @FXML private TableColumn<Chanson, Genre> colGenre;
    @FXML private TableColumn<Chanson, String> colDuree;
    @FXML private TableColumn<Chanson, Integer> colEcoutes;

    // pagination
    @FXML private Button boutonPagePrecedente;
    @FXML private Button boutonPageSuivante;
    @FXML private Label labelPage;
    @FXML private Button boutonAjouterChanson;
    @FXML private Button boutonModifierChanson;
    @FXML private Button boutonSupprimerChanson;

    // playlists
    @FXML private ListView<Playlist> listePlaylists;
    @FXML private ListView<Chanson> listeContenuPlaylist;
    @FXML private TextField champNomPlaylist;
    @FXML private Button boutonCreerPlaylist;
    @FXML private Button boutonSupprimerPlaylist;
    @FXML private Button boutonAjouterAPlaylist;
    @FXML private Button boutonRetirerDePlaylist;
    @FXML private Button boutonMonter;
    @FXML private Button boutonDescendre;
    @FXML private Button boutonMixQuotidien;
    @FXML private Button boutonExporterJson;
    @FXML private Button boutonImporterJson;
    @FXML private Label labelDureeTotalePlaylist;

    // lecteur simulé
    @FXML private Label labelChansonEnCours;
    @FXML private Button boutonPlayPause;
    @FXML private Button boutonPrecedent;
    @FXML private Button boutonSuivant;
    @FXML private ToggleButton boutonShuffle;
    @FXML private Slider sliderProgression;

    private BibliothequeService service;
    private SourceDonnees source;
    private final LecteurSimule lecteur = new LecteurSimule();
    private final FiltreChansons filtre = new FiltreChansons();

    private List<Chanson> vueCourante = new ArrayList<>(); // résultat filtré+trié, avant pagination
    private int pageCourante = 0;
    private static final int TAILLE_PAGE = 25;

    @FXML
    public void initialize() {
        // Une seule ligne à changer pour passer de l'un à l'autre.
        // SourceDonnees CSV (Lab 2) :
        // source = new LecteurCSV(Path.of("data/chansons.csv"));
        // SourceDonnees PostgreSQL (Lab 3) :
        source = new ChansonDaoPostgres(new ConnexionBD(Path.of("database.properties")));

        Bibliotheque bibliotheque = new Bibliotheque();
        bibliotheque.ajouterToutesLesChansons(source.trouverTous());
        service = new BibliothequeService(bibliotheque);

        configurerColonnes();
        configurerFiltres();
        configurerTri();
        configurerTheme();
        configurerPlaylists();
        configurerLecteur();
        configurerStatistiques();
        configurerCrudChansons();

        rafraichir();
    }

    private void configurerColonnes() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colArtiste.setCellValueFactory(new PropertyValueFactory<>("artiste"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeSortie"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuree.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDureeFormatee()));
        colEcoutes.setCellValueFactory(new PropertyValueFactory<>("nombreEcoutes"));
    }

    private void configurerFiltres() {
        ObservableList<String> genres = FXCollections.observableArrayList("Tous");
        for (Genre g : Genre.values()) genres.add(g.name());
        comboGenre.setItems(genres);
        comboGenre.getSelectionModel().selectFirst();

        ObservableList<String> decennies = FXCollections.observableArrayList("Toutes");
        for (Integer d : service.getDecenniesDisponibles()) decennies.add(d + "s");
        comboDecennie.setItems(decennies);
        comboDecennie.getSelectionModel().selectFirst();

        ObservableList<String> artistes = FXCollections.observableArrayList("Tous");
        artistes.addAll(service.getArtistesDistincts());
        comboArtiste.setItems(artistes);
        comboArtiste.getSelectionModel().selectFirst();

        sliderDureeMax.setMin(60);
        sliderDureeMax.setMax(700);
        sliderDureeMax.setValue(700);
        labelDureeMax.setText("Durée max : illimitée");
        sliderDureeMax.valueProperty().addListener((obs, ancien, nouveau) -> {
            int val = nouveau.intValue();
            labelDureeMax.setText(val >= 700 ? "Durée max : illimitée" : "Durée max : " + val + "s");
        });

        sliderEcoutesMin.setMin(0);
        sliderEcoutesMin.setMax(2_000_000);
        sliderEcoutesMin.setValue(0);
        labelEcoutesMin.setText("Écoutes min : 0");
        sliderEcoutesMin.valueProperty().addListener((obs, ancien, nouveau) ->
                labelEcoutesMin.setText("Écoutes min : " + nouveau.intValue()));

        comboFiltrePlaylist.setItems(FXCollections.observableArrayList());

        champRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            pageCourante = 0;
            rafraichir();
        });

        // chaque filtre relance le filtrage complet
        comboGenre.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });
        comboDecennie.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });
        comboArtiste.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });
        comboFiltrePlaylist.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });
        sliderDureeMax.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });
        sliderEcoutesMin.valueProperty().addListener((o, a, n) -> { pageCourante = 0; rafraichir(); });

        boutonReinitialiserFiltres.setOnAction(e -> reinitialiserFiltres());
        boutonPagePrecedente.setOnAction(e -> changerPage(-1));
        boutonPageSuivante.setOnAction(e -> changerPage(1));
    }

    private void configurerTri() {
        comboCritereTri.setItems(FXCollections.observableArrayList(
                "Titre (A-Z)", "Artiste (A-Z)", "Durée (courte→longue)",
                "Année (récent→ancien)", "Année (ancien→récent)",
                "Écoutes (populaire→rare)", "Genre (A-Z)"
        ));
        comboCritereTri.getSelectionModel().selectFirst();

        comboAlgorithme.setItems(FXCollections.observableArrayList(
                "Tri à bulles", "Tri par sélection", "Tri par insertion", "Tri fusion", "Tri rapide"
        ));
        comboAlgorithme.getSelectionModel().select("Tri fusion");

        boutonTrier.setOnAction(e -> { pageCourante = 0; rafraichir(); });
    }

    private void configurerTheme() {
        comboTheme.setItems(FXCollections.observableArrayList(
                "Sombre (Spotify)", "Clair", "Personnalisé"
        ));
        comboTheme.getSelectionModel().select("Sombre (Spotify)");
        appliquerTheme("Sombre (Spotify)");

        comboTheme.valueProperty().addListener((o, a, n) -> appliquerTheme(n));
    }

    private void appliquerTheme(String nomTheme) {
        String feuille = switch (nomTheme) {
            case "Clair" -> "/spotifymanager/view/style-clair.css";
            case "Personnalisé" -> "/spotifymanager/view/style-personnalise.css";
            default -> "/spotifymanager/view/style.css";
        };
        racine.getStylesheets().setAll(
                getClass().getResource(feuille).toExternalForm());
    }

    private void configurerPlaylists() {
        listePlaylists.setItems(FXCollections.observableArrayList());
        listeContenuPlaylist.setItems(FXCollections.observableArrayList());

        listePlaylists.getSelectionModel().selectedItemProperty().addListener((o, a, n) -> {
            comboFiltrePlaylist.getSelectionModel().select(n);
            afficherContenuPlaylist(n);
        });

        boutonCreerPlaylist.setOnAction(e -> {
            try {
                Playlist p = service.creerPlaylist(champNomPlaylist.getText());
                ajouterPlaylistAuxListes(p);
                champNomPlaylist.clear();
            } catch (IllegalArgumentException ex) {
                afficherAlerte(Alert.AlertType.WARNING, "Playlist", ex.getMessage());
            }
        });

        boutonSupprimerPlaylist.setOnAction(e -> {
            Playlist selection = listePlaylists.getSelectionModel().getSelectedItem();
            if (selection == null) return;
            service.supprimerPlaylist(selection);
            listePlaylists.getItems().remove(selection);
            comboFiltrePlaylist.getItems().remove(selection);
            listeContenuPlaylist.getItems().clear();
        });

        boutonAjouterAPlaylist.setOnAction(e -> {
            Playlist playlist = listePlaylists.getSelectionModel().getSelectedItem();
            Chanson chanson = tableChansons.getSelectionModel().getSelectedItem();
            if (playlist == null || chanson == null) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Playlist",
                        "Sélectionnez une playlist et une chanson.");
                return;
            }
            boolean ajoutee = service.ajouterChansonAPlaylist(playlist, chanson);
            if (!ajoutee) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Playlist",
                        "\"" + chanson.getTitre() + "\" est déjà dans cette playlist.");
            }
            afficherContenuPlaylist(playlist);
        });

        boutonRetirerDePlaylist.setOnAction(e -> {
            Playlist playlist = listePlaylists.getSelectionModel().getSelectedItem();
            Chanson chanson = listeContenuPlaylist.getSelectionModel().getSelectedItem();
            if (playlist == null || chanson == null) return;
            service.retirerChansonDePlaylist(playlist, chanson);
            afficherContenuPlaylist(playlist);
            rafraichir(); // au cas où le filtre "playlist" est actif
        });

        boutonMonter.setOnAction(e -> {
            Playlist playlist = listePlaylists.getSelectionModel().getSelectedItem();
            Chanson chanson = listeContenuPlaylist.getSelectionModel().getSelectedItem();
            if (playlist == null || chanson == null) return;
            playlist.deplacerVersLeHaut(chanson);
            afficherContenuPlaylist(playlist);
            listeContenuPlaylist.getSelectionModel().select(chanson);
        });

        boutonDescendre.setOnAction(e -> {
            Playlist playlist = listePlaylists.getSelectionModel().getSelectedItem();
            Chanson chanson = listeContenuPlaylist.getSelectionModel().getSelectedItem();
            if (playlist == null || chanson == null) return;
            playlist.deplacerVersLeBas(chanson);
            afficherContenuPlaylist(playlist);
            listeContenuPlaylist.getSelectionModel().select(chanson);
        });

        boutonMixQuotidien.setOnAction(e -> {
            Playlist mix = service.genererMixQuotidien();
            if (!listePlaylists.getItems().contains(mix)) {
                ajouterPlaylistAuxListes(mix);
            }
            listePlaylists.getSelectionModel().select(mix);
            afficherContenuPlaylist(mix);
            afficherAlerte(Alert.AlertType.INFORMATION, "Mix quotidien",
                    "\"Mix quotidien\" régénéré avec " + mix.getChansons().size() + " chansons.");
        });

        boutonExporterJson.setOnAction(e -> exporterPlaylistJson());
        boutonImporterJson.setOnAction(e -> importerPlaylistJson());
    }

    private void ajouterPlaylistAuxListes(Playlist p) {
        listePlaylists.getItems().add(p);
        ObservableList<Playlist> items = FXCollections.observableArrayList(comboFiltrePlaylist.getItems());
        items.add(p);
        comboFiltrePlaylist.setItems(items);
    }

    private void exporterPlaylistJson() {
        Playlist playlist = listePlaylists.getSelectionModel().getSelectedItem();
        if (playlist == null) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Export JSON", "Sélectionnez d'abord une playlist.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter la playlist en JSON");
        chooser.setInitialFileName(playlist.getNom().replaceAll("\\s+", "_") + ".json");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier JSON", "*.json"));
        File fichier = chooser.showSaveDialog(racine.getScene().getWindow());
        if (fichier == null) return;
        try {
            PlaylistJson.exporter(playlist, fichier.toPath());
        } catch (IOException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Export JSON", "Échec de l'export : " + ex.getMessage());
        }
    }

    private void importerPlaylistJson() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importer une playlist JSON");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier JSON", "*.json"));
        File fichier = chooser.showOpenDialog(racine.getScene().getWindow());
        if (fichier == null) return;
        try {
            Playlist importee = PlaylistJson.importer(fichier.toPath(), service.getBibliotheque());
            ajouterPlaylistAuxListes(importee);
            listePlaylists.getSelectionModel().select(importee);
            afficherContenuPlaylist(importee);
        } catch (IOException | NumberFormatException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Import JSON", "Échec de l'import : " + ex.getMessage());
        }
    }

    private void configurerLecteur() {
        boutonPlayPause.setOnAction(e -> {
            lecteur.togglePlayPause();
            boutonPlayPause.setText(lecteur.isEnLecture() ? "⏸ Pause" : "▶ Play");
        });
        boutonSuivant.setOnAction(e -> {
            lecteur.suivant();
            tableChansons.refresh(); // pour voir le compteur d'écoutes se mettre à jour
            mettreAJourLabelLecteur();
        });
        boutonPrecedent.setOnAction(e -> {
            lecteur.precedent();
            mettreAJourLabelLecteur();
        });
        boutonShuffle.setOnAction(e -> lecteur.toggleShuffle());
        sliderProgression.setValue(0); // factice
    }

    private void configurerStatistiques() {
        boutonStatistiques.setOnAction(e -> {
            List<Chanson> top10 = service.getTopEcoutes(10);
            Genre genreTop = service.getGenrePlusEcoute();

            StringBuilder texte = new StringBuilder();
            texte.append("Genre le plus écouté : ").append(genreTop != null ? genreTop.name() : "n/a").append("\n\n");
            texte.append("Top 10 des chansons les plus écoutées :\n");
            for (int i = 0; i < top10.size(); i++) {
                Chanson c = top10.get(i);
                texte.append(i + 1).append(". ").append(c.getTitre())
                        .append(" — ").append(c.getArtiste())
                        .append(" (").append(c.getNombreEcoutes()).append(" écoutes)\n");
            }

            Alert alerte = new Alert(Alert.AlertType.INFORMATION, texte.toString());
            alerte.setTitle("Statistiques d'écoute");
            alerte.setHeaderText(null);
            alerte.getDialogPane().setPrefWidth(420);
            alerte.showAndWait();
        });
    }

    // CRUD chanson (section 4.6 du Lab 3) : ajouter/modifier/supprimer, validé,
    // avec gestion des erreurs SQL sans jamais planter l'appli.
    private void configurerCrudChansons() {
        boutonAjouterChanson.setOnAction(e -> {
            Optional<Chanson> resultat = ouvrirFormulaireChanson(null);
            resultat.ifPresent(chanson -> {
                try {
                    int id = source.ajouter(chanson);
                    Chanson avecId = new Chanson(id, chanson.getTitre(), chanson.getArtiste(),
                            chanson.getAlbum(), chanson.getAnneeSortie(), chanson.getGenre(),
                            chanson.getDureeSecondes(), chanson.getNombreEcoutes());
                    service.getBibliotheque().ajouterChanson(avecId);
                    reconstruireArtistesDisponibles();
                    rafraichir();
                } catch (DataAccessException ex) {
                    afficherAlerte(Alert.AlertType.ERROR, "Ajouter une chanson", ex.getMessage());
                }
            });
        });

        boutonModifierChanson.setOnAction(e -> {
            Chanson selection = tableChansons.getSelectionModel().getSelectedItem();
            if (selection == null) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Modifier", "Sélectionne d'abord une chanson dans le tableau.");
                return;
            }
            Optional<Chanson> resultat = ouvrirFormulaireChanson(selection);
            resultat.ifPresent(modif -> {
                try {
                    // Chanson est mutable : on modifie l'objet existant en place,
                    // ce qui met aussi à jour toute playlist qui le référence.
                    selection.setTitre(modif.getTitre());
                    selection.setArtiste(modif.getArtiste());
                    selection.setAlbum(modif.getAlbum());
                    selection.setAnneeSortie(modif.getAnneeSortie());
                    selection.setGenre(modif.getGenre());
                    selection.setDureeSecondes(modif.getDureeSecondes());
                    selection.setNombreEcoutes(modif.getNombreEcoutes());
                    source.modifier(selection);
                    reconstruireArtistesDisponibles();
                    rafraichir();
                } catch (DataAccessException ex) {
                    afficherAlerte(Alert.AlertType.ERROR, "Modifier une chanson", ex.getMessage());
                }
            });
        });

        boutonSupprimerChanson.setOnAction(e -> {
            Chanson selection = tableChansons.getSelectionModel().getSelectedItem();
            if (selection == null) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Supprimer", "Sélectionne d'abord une chanson dans le tableau.");
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Supprimer définitivement \"" + selection.getTitre() + "\" du catalogue ?");
            confirmation.setHeaderText(null);
            confirmation.setTitle("Confirmer la suppression");
            if (confirmation.showAndWait().filter(b -> b == ButtonType.OK).isEmpty()) {
                return;
            }
            try {
                source.supprimer(selection.getId());
                service.getBibliotheque().supprimerChanson(selection);
                for (Playlist p : service.getBibliotheque().getPlaylists()) {
                    p.retirer(selection);
                }
                rafraichir();
            } catch (DataAccessException ex) {
                afficherAlerte(Alert.AlertType.ERROR, "Supprimer une chanson", ex.getMessage());
            }
        });
    }

    private void reconstruireArtistesDisponibles() {
        ObservableList<String> artistes = FXCollections.observableArrayList("Tous");
        artistes.addAll(service.getArtistesDistincts());
        comboArtiste.setItems(artistes);
    }

    /** Formulaire modal Ajouter/Modifier. chansonExistante == null -> mode ajout. */
    private Optional<Chanson> ouvrirFormulaireChanson(Chanson chansonExistante) {
        Dialog<Chanson> dialog = new Dialog<>();
        dialog.setTitle(chansonExistante == null ? "Ajouter une chanson" : "Modifier une chanson");

        TextField champTitre = new TextField();
        TextField champArtiste = new TextField();
        TextField champAlbum = new TextField();
        TextField champAnnee = new TextField();
        ComboBox<Genre> champGenre = new ComboBox<>(FXCollections.observableArrayList(Genre.values()));
        TextField champDuree = new TextField();
        TextField champEcoutes = new TextField();

        if (chansonExistante != null) {
            champTitre.setText(chansonExistante.getTitre());
            champArtiste.setText(chansonExistante.getArtiste());
            champAlbum.setText(chansonExistante.getAlbum());
            champAnnee.setText(String.valueOf(chansonExistante.getAnneeSortie()));
            champGenre.getSelectionModel().select(chansonExistante.getGenre());
            champDuree.setText(String.valueOf(chansonExistante.getDureeSecondes()));
            champEcoutes.setText(String.valueOf(chansonExistante.getNombreEcoutes()));
        } else {
            champGenre.getSelectionModel().selectFirst();
            champAnnee.setText("2024");
            champDuree.setText("200");
            champEcoutes.setText("0");
        }

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(8);
        grille.addRow(0, new Label("Titre*"), champTitre);
        grille.addRow(1, new Label("Artiste*"), champArtiste);
        grille.addRow(2, new Label("Album"), champAlbum);
        grille.addRow(3, new Label("Année*"), champAnnee);
        grille.addRow(4, new Label("Genre*"), champGenre);
        grille.addRow(5, new Label("Durée (secondes)*"), champDuree);
        grille.addRow(6, new Label("Écoutes"), champEcoutes);
        dialog.getDialogPane().setContent(grille);

        ButtonType boutonValider = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(boutonValider, ButtonType.CANCEL);

        // Validation : bloque la fermeture du dialogue tant que la saisie n'est pas correcte,
        // au lieu de planter ou d'accepter n'importe quoi.
        Button boutonOk = (Button) dialog.getDialogPane().lookupButton(boutonValider);
        boutonOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            StringBuilder erreurs = new StringBuilder();
            if (champTitre.getText().isBlank()) erreurs.append("- Le titre est obligatoire\n");
            if (champArtiste.getText().isBlank()) erreurs.append("- L'artiste est obligatoire\n");
            if (champGenre.getValue() == null) erreurs.append("- Le genre est obligatoire\n");

            Integer annee = parseEntierPositif(champAnnee.getText());
            if (annee == null || annee < 1600 || annee > 2100) erreurs.append("- Année invalide (1600-2100)\n");

            Integer duree = parseEntierPositif(champDuree.getText());
            if (duree == null || duree <= 0) erreurs.append("- Durée invalide (nombre de secondes > 0)\n");

            Integer ecoutes = parseEntierPositif(champEcoutes.getText());
            if (ecoutes == null) erreurs.append("- Écoutes invalide (nombre entier >= 0)\n");

            if (erreurs.length() > 0) {
                afficherAlerte(Alert.AlertType.WARNING, "Saisie invalide", erreurs.toString());
                event.consume(); // empêche la fermeture du dialogue
            }
        });

        dialog.setResultConverter(bouton -> {
            if (bouton != boutonValider) return null;
            int id = chansonExistante != null ? chansonExistante.getId() : 0;
            return new Chanson(
                    id,
                    champTitre.getText().trim(),
                    champArtiste.getText().trim(),
                    champAlbum.getText().trim(),
                    Integer.parseInt(champAnnee.getText().trim()),
                    champGenre.getValue(),
                    Integer.parseInt(champDuree.getText().trim()),
                    Integer.parseInt(champEcoutes.getText().trim())
            );
        });

        return dialog.showAndWait();
    }

    private Integer parseEntierPositif(String texte) {
        try {
            int valeur = Integer.parseInt(texte.trim());
            return valeur < 0 ? null : valeur;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private void rafraichir() {
        appliquerFiltresDepuisUI();
        vueCourante = service.filtrer(filtre);
        appliquerTri();

        int nbPages = service.getNombreDePages(vueCourante.size(), TAILLE_PAGE);
        if (pageCourante >= nbPages) pageCourante = nbPages - 1;
        if (pageCourante < 0) pageCourante = 0;

        List<Chanson> page = service.paginer(vueCourante, pageCourante, TAILLE_PAGE);
        tableChansons.setItems(FXCollections.observableArrayList(page));
        labelPage.setText("Page " + (pageCourante + 1) + " / " + nbPages + "  (" + vueCourante.size() + " résultats)");

        lecteur.definirFile(vueCourante);
        mettreAJourLabelLecteur();
    }

    private void appliquerFiltresDepuisUI() {
        filtre.setTexteRecherche(champRecherche.getText());

        String genreSel = comboGenre.getValue();
        filtre.setGenre((genreSel == null || genreSel.equals("Tous")) ? null : Genre.valueOf(genreSel));

        String decennieSel = comboDecennie.getValue();
        filtre.setDecennie((decennieSel == null || decennieSel.equals("Toutes"))
                ? null : Integer.parseInt(decennieSel.replace("s", "")));

        String artisteSel = comboArtiste.getValue();
        filtre.setArtiste((artisteSel == null || artisteSel.equals("Tous")) ? null : artisteSel);

        int dureeMax = (int) sliderDureeMax.getValue();
        filtre.setDureeMaxSecondes(dureeMax >= 700 ? null : dureeMax);

        int ecoutesMin = (int) sliderEcoutesMin.getValue();
        filtre.setEcoutesMin(ecoutesMin <= 0 ? null : ecoutesMin);

        filtre.setPlaylist(comboFiltrePlaylist.getValue());
    }

    private void appliquerTri() {
        Comparator<Chanson> comparateur = switch (comboCritereTri.getValue()) {
            case "Artiste (A-Z)" -> spotifymanager.service.ComparateursChanson.parArtiste();
            case "Durée (courte→longue)" -> spotifymanager.service.ComparateursChanson.parDureeCroissante();
            case "Année (récent→ancien)" -> spotifymanager.service.ComparateursChanson.parAnneeRecenteDabord();
            case "Année (ancien→récent)" -> spotifymanager.service.ComparateursChanson.parAnneeAncienneDabord();
            case "Écoutes (populaire→rare)" -> spotifymanager.service.ComparateursChanson.parEcoutesDecroissant();
            case "Genre (A-Z)" -> spotifymanager.service.ComparateursChanson.parGenre();
            default -> spotifymanager.service.ComparateursChanson.parTitre();
        };

        AlgorithmeTri<Chanson> algorithme = switch (comboAlgorithme.getValue()) {
            case "Tri à bulles" -> new TriBulles<>();
            case "Tri par sélection" -> new TriSelection<>();
            case "Tri par insertion" -> new TriInsertion<>();
            case "Tri rapide" -> new TriRapide<>();
            default -> new TriFusion<>();
        };

        service.trier(vueCourante, algorithme, comparateur);
    }

    private void changerPage(int delta) {
        pageCourante += delta;
        rafraichir();
    }

    private void reinitialiserFiltres() {
        champRecherche.clear();
        comboGenre.getSelectionModel().selectFirst();
        comboDecennie.getSelectionModel().selectFirst();
        comboArtiste.getSelectionModel().selectFirst();
        sliderDureeMax.setValue(700);
        sliderEcoutesMin.setValue(0);
        comboFiltrePlaylist.getSelectionModel().clearSelection();
        pageCourante = 0;
        rafraichir();
    }

    private void afficherContenuPlaylist(Playlist playlist) {
        listeContenuPlaylist.setItems(playlist == null
                ? FXCollections.observableArrayList()
                : FXCollections.observableArrayList(playlist.getChansons()));
        labelDureeTotalePlaylist.setText(playlist == null
                ? ""
                : playlist.getChansons().size() + " chansons — " + playlist.getDureeTotaleFormatee());
    }

    private void mettreAJourLabelLecteur() {
        Chanson courante = lecteur.getChansonCourante();
        labelChansonEnCours.setText(courante == null
                ? "Aucune chanson"
                : courante.getTitre() + " — " + courante.getArtiste());
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type, message);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.showAndWait();
    }
}
