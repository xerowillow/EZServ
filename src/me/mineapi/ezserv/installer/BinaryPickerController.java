package me.mineapi.ezserv.installer;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.mineapi.ezserv.Main;
import me.mineapi.ezserv.UIControls.UIControls;
import me.mineapi.ezserv.downloader.Downloader;
import me.mineapi.ezserv.downloader.PaperVersion;
import me.mineapi.ezserv.downloader.SpigotVersion;
import me.mineapi.ezserv.downloader.VanillaVersion;
import me.mineapi.ezserv.panel.PanelController;

import java.awt.print.Paper;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BinaryPickerController implements Initializable {
    @FXML Button spigotButton;
    @FXML Button vanillaButton;
    @FXML Button paperButton;
    @FXML Button installButton;
    @FXML ChoiceBox<String> versionPicker;

    static Stage primaryStage;

    Image spigotImage = new Image(UIControls.class.getResourceAsStream("spigot.png"));
    Image vanillaImage = new Image(UIControls.class.getResourceAsStream("vanilla.png"));
    Image paperImage = new Image(UIControls.class.getResourceAsStream("paper.png"));

    Button pickedButton;

    ObservableList<String> versions = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spigotButton.setGraphic(new ImageView(spigotImage));
        vanillaButton.setGraphic(new ImageView(vanillaImage));
        paperButton.setGraphic(new ImageView(paperImage));

        versions.add("Please select a version above.");
        versionPicker.setItems(versions);
    }

    public static void show() throws IOException {
        Parent root = FXMLLoader.load(BinaryPickerController.class.getResource("BinaryPicker.fxml"));
        primaryStage = new Stage();
        primaryStage.setTitle("EZServ Installer");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void hide() {
        primaryStage.hide();
    }

    public void onInstall(ActionEvent event) {
        try {
            if (pickedButton == spigotButton) {
                Downloader.downloadSpigot(versionPicker.getValue());
                hide();
                PanelController.show();
            } else if (pickedButton == vanillaButton) {
                Downloader.downloadVanilla(versionPicker.getValue());
                hide();
                PanelController.show();
            } else if (pickedButton == paperButton) {
                Downloader.downloadPaper(versionPicker.getValue());
                hide();
                PanelController.show();
            }
        } catch (Exception e) {
            Alert fatalError = new Alert(Alert.AlertType.ERROR);
            fatalError.setHeaderText("The application encountered a fatal error!");
            fatalError.setContentText(e.getMessage());
            fatalError.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void spigotPicked(ActionEvent event) {
        try {
            Gson gson = new Gson();

            Reader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/mineapi/EZServ/master/src/me/mineapi/ezserv/downloader/spigot.json").openStream()));

            SpigotVersion[] spigotVersions = gson.fromJson(reader, SpigotVersion[].class);

            pickedButton = spigotButton;

            versions.clear();

            for (SpigotVersion v : spigotVersions) {
                versions.add(v.getVersion());
            }

            versionPicker.setItems(versions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vanillaPicked(ActionEvent event) {
        try {
            Gson gson = new Gson();

            Reader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/mineapi/EZServ/master/src/me/mineapi/ezserv/downloader/vanilla.json").openStream()));

            VanillaVersion[] vanillaVersions = gson.fromJson(reader, VanillaVersion[].class);

            pickedButton = vanillaButton;

            versions.clear();

            for (VanillaVersion v : vanillaVersions) {
                versions.add(v.getVersion());
            }

            versionPicker.setItems(versions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paperPicked(ActionEvent event) {
        try {
            Gson gson = new Gson();

            Reader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/mineapi/EZServ/master/src/me/mineapi/ezserv/downloader/paper.json").openStream()));

            PaperVersion[] paperVersions = gson.fromJson(reader, PaperVersion[].class);

            pickedButton = paperButton;

            versions.clear();

            for (PaperVersion v : paperVersions) {
                versions.add(v.getVersion());
            }

            versionPicker.setItems(versions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
