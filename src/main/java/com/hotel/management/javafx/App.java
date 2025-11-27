package com.hotel.management.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = loadFXML("login");          // login.fxml is first
        scene = new Scene(root, 640, 480);       // size for both screens
        scene.getStylesheets().add(
                App.class.getResource("styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setTitle("Hotel Login");
        stage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}