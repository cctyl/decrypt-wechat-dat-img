package io.github.cctyl;

import javafx.application.Platform;
import javafx.scene.control.TextArea;


public class LogTool {


    private static TextArea console;

    public static void log(String msg) {
        Platform.runLater(() -> console.appendText(msg + "\n"));
    }

    public static void print(TextArea console) {
        LogTool.console = console;
    }

    public static void shutdown() {
    }
}
