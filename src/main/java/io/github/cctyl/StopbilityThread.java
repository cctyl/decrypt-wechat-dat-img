package io.github.cctyl;

import java.util.List;
import java.util.function.Consumer;

class StopbilityThread <T>{

    private Thread innerThread;

    /**
     * 停止标记
     */
    private volatile boolean stop = false;

    public StopbilityThread(List<T> list, Consumer<T> task,Consumer<Integer> onComplete) {
        innerThread = new Thread(()-> run(list,task,onComplete));
        innerThread.start();
    }

    public void start() {
        innerThread.start();
    }

    private void run(List<T> list, Consumer<T> task,Consumer<Integer> onComplete) {
        int i = 0;
        for (T t : list) {
            if (stop) {
                LogTool.log("线程退出");
                break;
            }
            try {
                task.accept(t);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                //出现异常就终止
                stop = true;
            }
        }

        onComplete.accept(i);
    }

    /**
     * 停止线程
     */
    public void stop() {
        stop = true;
        innerThread.interrupt();
        LogTool.log(innerThread.getName()+"线程终止...." );
    }
}