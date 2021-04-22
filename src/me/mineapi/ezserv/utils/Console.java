package me.mineapi.ezserv.utils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import me.mineapi.ezserv.panel.PanelController;


import java.io.*;
import java.util.TimerTask;

public class Console extends TimerTask {
    Process serverProcess;
    @FXML TextArea output;
    @FXML TextField input;
    @FXML Button submit;

    StringBuilder consoleText = new StringBuilder();

    public Console(Process serverProcess, TextArea output, TextField input, Button submit) {
        this.serverProcess = serverProcess;
        this.output = output;
        this.input = input;
        this.submit = submit;
    }

    @Override
    public void run() {
        if (PanelController.serverStatus != PanelController.ServerStatus.RUNNING) {
            this.cancel();
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
        String line = null;
        try {
            line = r.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (line == null) { return; }
        addConsoleLine(line);
        System.out.println(line);
        output.setText(consoleText.toString());
        output.setScrollTop(Double.MAX_VALUE);
    }

    private void addConsoleLine(String text) {
        consoleText.append(text).append("\n");
    }
}
