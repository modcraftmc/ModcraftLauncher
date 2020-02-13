package fr.modcraft.launcher.Supdatemanager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;

public class SupdateManager {
    private String url;
    private File file;

    private ProgressBar bar;
    private Label label;

    private long percent;
    private static Thread thread;
    private  String defaultText;
    Task<Void> task;


    public SupdateManager(String url, File file, ProgressBar progressBar, Label label) {
        this.url = url;
        this.file = file;
        this.bar = progressBar;
        this.label = label;
    }

    public Task supdate() {

         task = new SupdateTask(url, file,this);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bar.progressProperty().unbind();
                bar.progressProperty().bind(task.progressProperty());
            }
        });

        thread = new Thread(task);
        thread.setDaemon(true);

        return task;

    }

    public void start() {
        thread.start();
        System.out.println("starting s-update task.");
    }

    public  Thread getThread() {
        return thread;
    }

    public void setDefaultText(String text) {
        this.defaultText = text;
    }

    public Task<Void> getTask() {
        return task;
    }
}
