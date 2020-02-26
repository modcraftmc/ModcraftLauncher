package fr.modcraft.launcher.Supdatemanager;

import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import javafx.concurrent.Task;

import java.io.File;

public class SupdateTask extends Task<Void> {

    public static SupdateTask instance;
    private  String url;
    private File file;
    private Thread updateThread;
    private SupdateManager supdateManager;
    private static FileDeleter deleter;
    private static SUpdate su;


    public SupdateTask(String url, File file, SupdateManager supdateManager) {
        instance = this;
        this.url = url;
        this.file = file;
        this.supdateManager = supdateManager;
    }


    @Override
    protected Void call() throws Exception {
         su = new SUpdate(url, file);
        su.getServerRequester().setRewriteEnabled(true);
        su.addApplication(deleter = new FileDeleter());
        updateThread = new Thread() {
            long val;
            long max;
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        continue;
                    }
                    val = (long) (BarAPI.getNumberOfTotalDownloadedBytes());
                    max = (long) (BarAPI.getNumberOfTotalBytesToDownload());

                    instance.updateProgress(val, max);

                }
            }
        };
        updateThread.start();
        su.start();
        updateThread.interrupt();

        return null;
    }

    @Override
    protected void succeeded() {
        System.out.println("succes.");
        super.succeeded();


    }

    @Override
    protected void cancelled() {
        super.cancelled();
    }

    public static FileDeleter getDeleter() {
        return deleter;
    }

    public static SUpdate getSu() {
        return su;
    }
}
