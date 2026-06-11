package UITest1.UITest1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {


    public static int    currentUserId   = -1;
    public static String currentUserRole = "";

 
    public static long REVIEW_EDIT_WINDOW_MINUTES = 5;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Home"), 1440, 900);
        stage.setScene(scene);
        stage.setTitle("Pacific Reviews");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        java.net.URL url = App.class.getResource("/UITest1/UITest1/" + fxml + ".fxml");
        if (url == null) {
            url = App.class.getResource("/" + fxml + ".fxml");
        }
        if (url == null) {
            throw new IOException("Cannot find FXML: " + fxml);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}