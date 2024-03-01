package io.github.cctyl;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.concurrent.*;

public class LogTool {
    public static BlockingQueue<String> mq = new ArrayBlockingQueue<>(10);
    private static final ExecutorService msgTaskPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService msgPutPool = Executors.newSingleThreadExecutor();


    public static void log(String msg){

            msgPutPool.execute(() -> {
                try {
                    //不能阻塞，特别是在ui线程中
                    mq.put(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


    }

    public static void print(TextArea console) {

        msgTaskPool.submit(() -> {
            while (true){
                String take = mq.take();
                Platform.runLater(() ->   console.setText(take));
            }
        });
    }
}
