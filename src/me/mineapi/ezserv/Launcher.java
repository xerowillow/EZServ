package me.mineapi.ezserv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Launcher extends Application {
    private static FXMLLoader loader;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("load/Load.fxml"));
        loader = new FXMLLoader(getClass().getResource("load/Load.fxml"));
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static File loadServer() {
        return new File("./server/server.jar");
    }

    public static FXMLLoader getLoader() { return loader; }
}
