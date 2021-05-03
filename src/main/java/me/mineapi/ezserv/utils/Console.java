package me.mineapi.ezserv.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import me.mineapi.ezserv.panel.PanelController;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class Console extends TimerTask {
    Process serverProcess;
    @FXML TextArea output;
    @FXML TextField input;
    @FXML Button submit;

    public Console(Process serverProcess, TextArea output, TextField input, Button submit) {
        this.serverProcess = serverProcess;
        this.output = output;
        this.input = input;
        this.submit = submit;
    }

    StringBuilder builder = new StringBuilder();

    @Override
    public void run() {
        try {
            Scanner sc = new Scanner(serverProcess.getInputStream());

            while (sc.hasNextLine()) {
                builder.append(sc.nextLine()).append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            return;
        }
    }

    public String getOutput() {
        return builder.toString();
    }
}
