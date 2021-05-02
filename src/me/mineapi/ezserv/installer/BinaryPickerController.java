package me.mineapi.ezserv.installer;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.mineapi.ezserv.UIControls.UIControls;
import me.mineapi.ezserv.downloader.Downloader;
import me.mineapi.ezserv.utils.PaperVersion;
import me.mineapi.ezserv.utils.SpigotVersion;
import me.mineapi.ezserv.utils.VanillaVersion;
import me.mineapi.ezserv.panel.PanelController;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
        primaryStage.setMaxWidth(600);
        primaryStage.setMaxHeight(400);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> {
            System.exit(1);
        });

        primaryStage.show();
    }

    public static void hide() {
        primaryStage.hide();
    }

    public void onInstall(ActionEvent event) {
            if (pickedButton == spigotButton) {
                Downloader.downloadSpigot(versionPicker.getValue());

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if (Downloader.downloaderStatus == Downloader.Status.IDLE) {
                                try {
                                    hide();
                                    PanelController.show();
                                    this.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        });
                    }
                }, 1L, 1000L);
            } else if (pickedButton == vanillaButton) {
                Downloader.downloadVanilla(versionPicker.getValue());

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if (Downloader.downloaderStatus == Downloader.Status.IDLE) {
                                try {
                                    hide();
                                    PanelController.show();
                                    this.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        });
                    }
                }, 1L, 1000L);
            } else if (pickedButton == paperButton) {
                Downloader.downloadPaper(versionPicker.getValue());

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if (Downloader.downloaderStatus == Downloader.Status.IDLE) {
                                try {
                                    hide();
                                    PanelController.show();
                                    this.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        });
                    }
                }, 1L, 1000L);
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
