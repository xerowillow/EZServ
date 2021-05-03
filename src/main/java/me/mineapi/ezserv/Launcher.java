package me.mineapi.ezserv;

import javafx.application.Application;
import javafx.stage.Stage;
import me.mineapi.ezserv.installer.BinaryPickerController;
import me.mineapi.ezserv.panel.PanelController;

import java.io.File;
import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        File dir = new File(Launcher.loadServer().getParent());
        if (!dir.exists()) {
            dir.mkdir();
        }

        if (Launcher.loadServer().getAbsoluteFile().exists()) {
            //Redirect user to the panel.
            try {
                PanelController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Redirect the user to the installer.
            try {
                BinaryPickerController.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static File loadServer() {
        return new File("./server/server.jar");
    }
}
