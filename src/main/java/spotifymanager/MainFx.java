package spotifymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage stagePrincipale) throws Exception {
        Parent racine = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        Scene scene = new Scene(racine, 1100, 700);

        stagePrincipale.setTitle("Spotify Playlist Manager");
        stagePrincipale.setScene(scene);
        stagePrincipale.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
