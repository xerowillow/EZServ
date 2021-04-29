package me.mineapi.ezserv.load;

import javafx.fxml.Initializable;
import me.mineapi.ezserv.Main;
import me.mineapi.ezserv.downloader.Downloader;
import me.mineapi.ezserv.installer.BinaryPickerController;
import me.mineapi.ezserv.panel.PanelController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class LoadController implements Initializable { //This class's sole purpose is to redirect the user to it's respective window.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File dir = new File(Main.loadServer().getParent());
        if (!dir.exists()) {
            dir.mkdir();
        }

        if (Main.loadServer().getAbsoluteFile().exists()) {
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
}
