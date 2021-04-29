package me.mineapi.ezserv.panel;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.mineapi.ezserv.Main;
import me.mineapi.ezserv.utils.Console;
import me.mineapi.ezserv.utils.ServerProperty;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PanelController implements Initializable {
    @FXML Button start;
    @FXML Button stop;
    @FXML Button reload;
    @FXML TextArea consoleArea;
    @FXML TextArea whitelistedPlayers;
    @FXML TextField consoleInput;
    @FXML TextField whitelistPlayerInput;
    @FXML Button consoleSubmit;
    @FXML Button saveProperties;
    @FXML Button whitelistPlayerSubmit;
    @FXML Text statusText;
    @FXML TableView properties;
    @FXML TableColumn colProperty;
    @FXML TableColumn colValue;
    @FXML Tab serverPropertiesTab;

    public enum ServerStatus { RUNNING, STARTING, STOPPING, OFFLINE }

    public static ServerStatus serverStatus = ServerStatus.OFFLINE;

    Console console;
    Process process;

    TimerTask internalUpdater;

    HashMap<Integer, Long> memoryUsages = new HashMap<Integer, Long>();
    HashMap<String, String> propertiesList = new HashMap<String, String>();

    boolean propertiesExists = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) { //Run this when the panel is initialized.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { //Run resources graph.
            int timeSinceStart = 0;

            @Override
            public void run() {
                statusText.setText("Server Status: " + serverStatus);

                if (!isAlive(process)) {
                    start.setDisable(false);
                    reload.setDisable(true);
                    stop.setDisable(true);
                } else {
                    start.setDisable(true);
                    reload.setDisable(false);
                    stop.setDisable(false);
                }

                checkForProperties();

                if (propertiesExists) {
                    serverPropertiesTab.setDisable(false);
                } else {
                    serverPropertiesTab.setDisable(true);
                }

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(loadProperties()));
                    propertiesExists = true;
                } catch (FileNotFoundException e) {
                    propertiesExists = false;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isAlive(process)) {
                                serverStatus = ServerStatus.RUNNING;
                            } else {
                                serverStatus = ServerStatus.OFFLINE;
                            }
                        } catch (NullPointerException e) {
                            return; //nothing to see here lmao
                        }
                    }
                });

                if (serverStatus == ServerStatus.RUNNING) {
                    timeSinceStart++;
                } else if (serverStatus == ServerStatus.STOPPING || serverStatus == ServerStatus.OFFLINE) {
                    timeSinceStart = 0;
                }
            }
        }, 0L, 1000L);

        updateWhitelistArea();
    }

    public static void show() throws IOException {
        Parent root = FXMLLoader.load(PanelController.class.getResource("Panel.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("EZServ Panel");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public void onStart(ActionEvent event) {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStop(ActionEvent event) {
        stopServer();
    }

    public void onReload(ActionEvent event) {
        if (!process.isAlive()) {
            stopServer();
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onConsoleInput(ActionEvent event) {
        inputText(consoleInput.getText());
        consoleInput.setText("");
    }

    public void onInputKeyPress(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            inputText(consoleInput.getText());
            consoleInput.setText("");
        }
    }

    public void onWhitelist(ActionEvent event) {
        whitelistPlayer();
    }

    public void onWhitelistKey(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            whitelistPlayer();
        }
    }

    void startServer() throws IOException {
        File eula = new File(Main.loadServer().getAbsoluteFile().getParent() + "/eula.txt");
        try {
            getUUIDfromAPI("MineAPI");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (eula.createNewFile()) {
            PrintStream writer = new PrintStream(eula);
            writer.println("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            writer.println("#Mon Apr 19 20:56:03 EDT 2021");
            writer.println("eula=false");

            Alert eulaConf = new Alert(Alert.AlertType.CONFIRMATION);
            eulaConf.setTitle("Minecraft EULA");
            eulaConf.setHeaderText("Please agree");
            eulaConf.setContentText("You must accept the Minecraft EULA, you can find it at: https://account.mojang.com/documents/minecraft_eula");

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            eulaConf.getButtonTypes().clear();
            eulaConf.getButtonTypes().add(yes);
            eulaConf.getButtonTypes().add(no);

            ButtonBar.setButtonUniformSize(eulaConf.getDialogPane().lookupButton(yes), false);
            eulaConf.getDialogPane().lookupButton(yes).addEventFilter(ActionEvent.ACTION, event -> {
                try {
                    FileWriter fw = new FileWriter(eula, false);
                    PrintWriter pw = new PrintWriter(fw, false);
                    pw.flush(); //
                    pw.close(); // Clear the file.
                    fw.close(); //
                    PrintStream eulaTrue = new PrintStream(eula);
                    eulaTrue.println("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
                    eulaTrue.println("#Mon Apr 19 20:56:03 EDT 2021");
                    eulaTrue.println("eula=true");
                    eulaConf.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            ButtonBar.setButtonUniformSize(eulaConf.getDialogPane().lookupButton(no), false);

            Optional<ButtonType> option = eulaConf.showAndWait();
        } else {
            String[] lines = new String[3];
            BufferedReader reader = new BufferedReader(new FileReader(eula.getAbsolutePath()));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                lines[i] = line;
                line = reader.readLine();
                i++;
            }

            if (lines[2].equals("eula=false")) {
                Alert eulaConf = new Alert(Alert.AlertType.CONFIRMATION);
                eulaConf.setTitle("Minecraft EULA");
                eulaConf.setHeaderText("Please agree");
                eulaConf.setContentText("You must accept the Minecraft EULA, you can find it at: https://account.mojang.com/documents/minecraft_eula");

                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                eulaConf.getButtonTypes().clear();
                eulaConf.getButtonTypes().add(yes);
                eulaConf.getButtonTypes().add(no);

                ButtonBar.setButtonUniformSize(eulaConf.getDialogPane().lookupButton(yes), false);
                eulaConf.getDialogPane().lookupButton(yes).addEventFilter(ActionEvent.ACTION, event -> {
                    try {
                        FileWriter fw = new FileWriter(eula, false);
                        PrintWriter pw = new PrintWriter(fw, false);
                        pw.flush(); //
                        pw.close(); // Clear the file.
                        fw.close(); //
                        PrintStream eulaTrue = new PrintStream(eula);
                        eulaTrue.println("#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
                        eulaTrue.println("#Mon Apr 19 20:56:03 EDT 2021");
                        eulaTrue.println("eula=true");
                        eulaConf.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

                ButtonBar.setButtonUniformSize(eulaConf.getDialogPane().lookupButton(no), false);

                Optional<ButtonType> option = eulaConf.showAndWait();
            }
        }

        try {
            serverStatus = ServerStatus.STARTING;
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", Main.loadServer().getAbsolutePath(), "--nogui");
            pb.directory(new File(Main.loadServer().getAbsoluteFile().getParent()));
            pb.redirectErrorStream(true);

            process = pb.start(); //Start the process.

            serverStatus = ServerStatus.RUNNING; //Set state to running for console.

            console = new Console(process, consoleArea, consoleInput, consoleSubmit);

            new Timer().schedule(console, 0L, 1000L); //Constantly update the console.
        } catch (IOException e) {
            displayError(e);
        }
    }

    void stopServer() { //Destroys the server process.
        try {
            serverStatus = ServerStatus.STOPPING;
            process.destroy();
            serverStatus = ServerStatus.OFFLINE;
        } catch (Exception e) {
            displayError(e);
        }
    }

    void inputText(String text) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        try {
            writer.write(text);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            displayError(e);
        }
    }

    boolean isAlive(Process p) { //Check if the process is alive.
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    Long getMemory() { //See how much memory the server is using.
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    void displayError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Caught an Exception");
        alert.setHeaderText("We caught an exception so you can keep going!");
        alert.setContentText(e.getMessage());

        alert.show();
    }

    void listProperties() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadProperties()));
            ArrayList<String> lines = new ArrayList<>();

            colProperty.setCellFactory(TextFieldTableCell.forTableColumn());
            colProperty.setCellValueFactory(new PropertyValueFactory<>("property"));
            colValue.setCellFactory(TextFieldTableCell.forTableColumn());
            colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
            String line = null;

            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i > 1) {
                    lines.add(line);
                }
                i++;
            }

            ObservableList<ServerProperty> propertyList = FXCollections.observableArrayList();

            for (int j = 0; j < lines.size(); j++) {
                String[] splitLine = lines.get(j).split("=");
                try {
                    propertyList.add(new ServerProperty(splitLine[0], splitLine[1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    propertyList.add(new ServerProperty(splitLine[0], " "));
                }
            }

            properties.setItems(propertyList);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File loadProperties() {
        return new File(Main.loadServer().getAbsoluteFile().getParent() + "/server.properties");
    }

    boolean hasChecked = false;
    void checkForProperties() {
        if (propertiesExists && !hasChecked) {
            hasChecked = true;
            listProperties();
        }
    }

    @FXML
    public void onEditCommitSelectedProductTable(TableColumn.CellEditEvent<?,?> event) {
        ObservableList<ServerProperty> propertyList = properties.getItems();
        ArrayList<String> lines = new ArrayList<String>();

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        TableView view = event.getTableView();
        TablePosition<?,?> position = event.getTablePosition();
        int row = position.getRow();

        for (int i = 0; i < view.getItems().size(); i++) {
            if (row == i) {
                ServerProperty property = (ServerProperty) view.getItems().get(i);
                property.setValue((String) newValue);
            }
        }

        for (ServerProperty p:
                propertyList) {
            StringBuilder builder = new StringBuilder();

            builder.append(p.getProperty()).append("=").append(p.getValue());
            lines.add(builder.toString());
        }

        try {
            FileWriter fw = new FileWriter(loadProperties());
            PrintWriter pw = new PrintWriter(loadProperties());
            pw.flush();
            pw.close();
            fw.close();
            PrintStream stream = new PrintStream(loadProperties());
            stream.println("#Minecraft server properties");
            stream.println("#Tue Apr 20 17:15:48 EDT 2021");
            for (String l : lines) {
                stream.println(l);
            }
        } catch (IOException e) {
            displayError(e);
        }
    }

    public String getUUIDfromAPI(String userName) throws Exception {
        Gson gson = new Gson();

        Reader reader = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + userName).openStream()));

        WhitelistPlayer player = gson.fromJson(reader, WhitelistPlayer.class);

        System.out.println(player);

        return player.getId();
    }

    void whitelistPlayer() {
        try {
            if (whitelistPlayerInput.getText() != null) {
                File whitelistJson = new File("./server/whitelist.json");

                Gson gson = new Gson();

                Reader reader = new BufferedReader(new FileReader(whitelistJson));

                WhitelistedPlayer[] whitelistPlayers = gson.fromJson(reader, WhitelistedPlayer[].class);

                if (whitelistPlayers != null) {
                    for (WhitelistedPlayer p : whitelistPlayers) {
                        if (whitelistPlayerInput.getText().equals(p.getName())) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Player Invalid");
                            alert.setTitle("Player Invalid");
                            alert.setContentText("The player you attempted to whitelist is already whitelisted!");
                            alert.show();
                            return;
                        }
                    }

                    FileWriter fw = new FileWriter(whitelistJson);
                    PrintWriter pw = new PrintWriter(whitelistJson);
                    pw.flush();
                    pw.close();
                    fw.close();

                    PrintStream ps1 = new PrintStream(whitelistJson);
                    ps1.println("[]");

                    Map<String, String> config = new HashMap<String, String>();
                    config.put("javax.json.stream.JsonGenerator.prettyPrinting", "javax.json.stream.JsonGenerator.prettyPrinting");
                    JsonBuilderFactory factory = Json.createBuilderFactory(config);
                    JsonArrayBuilder array = factory.createArrayBuilder();

                    if (whitelistPlayers != null) {
                        for (WhitelistedPlayer whitelistPlayer : whitelistPlayers) {
                            array.add(factory.createObjectBuilder().add("uuid", whitelistPlayer.getUuid()).add("name", whitelistPlayer.getName()).build());
                        }
                    }

                    try {
                        if (getUUIDfromAPI(whitelistPlayerInput.getText()) != null) {
                            array.add(factory.createObjectBuilder().add("uuid", getUUIDfromAPI(whitelistPlayerInput.getText())).add("name", whitelistPlayerInput.getText()).build());
                        }
                    } catch (NullPointerException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Player Invalid");
                        alert.setHeaderText("Player Invalid");
                        alert.setContentText("The player provided is invalid. Please check spelling.");

                        alert.show();
                    }

                    JsonArray finishedArray = array.build();
                    System.out.println(finishedArray.toString());
                    PrintStream ps = new PrintStream(whitelistJson);
                    ps.print(finishedArray.toString());

                    whitelistPlayerInput.setText("");

                    updateWhitelistArea();
                }
            }
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Player Invalid");
            alert.setHeaderText("Player Invalid");
            alert.setContentText("The player provided is invalid. Please check spelling.");

            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateWhitelistArea() {
        File file = new File("./server/whitelist.json");

        try {
            if (file.exists()) {
                Gson gson = new Gson();

                Reader reader = new BufferedReader(new FileReader(file));

                WhitelistedPlayer[] whitelistedPlayersList = gson.fromJson(reader, WhitelistedPlayer[].class);

                StringBuilder stringBuilder = new StringBuilder();

                if (whitelistedPlayersList != null) {
                    for (WhitelistedPlayer p : whitelistedPlayersList) {
                        stringBuilder.append(p.getName()).append(System.getProperty("line.separator"));
                    }
                }

                whitelistedPlayers.setText(stringBuilder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}