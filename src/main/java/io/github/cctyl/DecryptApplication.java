package io.github.cctyl;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;

public class DecryptApplication extends Application {


    public void start(Stage stage) {

        stage.setWidth(500);
        stage.setHeight(300);


        stage.setTitle("微信图片解密");

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);

        Button btOpen = new Button("选择微信存储文件夹");
        btOpen.setPrefWidth(200);
        btOpen.setPrefHeight(100);


        TextArea console = new TextArea();

        System.setOut(new ConsolePrint(console));
        System.out.println("github地址：https://github.com/cctyl/decrypt-wechat-dat-img");

        vBox.getChildren().addAll(btOpen, console);
        vBox.setAlignment(Pos.CENTER);

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("打开微信存储文件夹");
        btOpen.setOnMouseClicked(e -> {
            try {
                File dir = chooser.showDialog(stage);
                DatUtil.start(dir.getAbsolutePath());
            } catch (NullPointerException ex) {
                System.out.println("打开文件夹错误");
                ex.printStackTrace();

            }

        });


        stage.setScene(scene);
        stage.show();
    }


    public class ConsolePrint extends PrintStream {
        TextArea console;

        public ConsolePrint(TextArea console) {
            super(new ByteArrayOutputStream());
            this.console = console;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            print(new String(buf, off, len));
        }

        @Override
        public void print(String s) {
            console.appendText(s);
        }
    }


}