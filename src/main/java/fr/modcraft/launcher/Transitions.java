package fr.modcraft.launcher;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;

public class Transitions {

    public static void fadeIn(Stage stage){
        Thread t = new Thread(() ->{
            for (int i = 0; i <= 100; i++){
                int finalI = i;
                Platform.runLater(() -> stage.setOpacity((double)finalI /100));
                try {
                    Thread.sleep(250/100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public static void fadeOut(Stage stage){
        Thread t = new Thread(() -> {
            for (int i = 100; i >= 0; i--){
                int finalI = i;
                Platform.runLater(() -> stage.setOpacity((double)finalI /100));
                try {
                    Thread.sleep(250/100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(stage::close);
        });
        t.start();
    }

    public static void blurAnimation(int blurAmount, int fadingTime, Parent root){
        Thread t = new Thread(() -> {
            for (int i = 0; i < blurAmount; i++){
                int finalI = i;
                Platform.runLater(() -> root.setEffect(new GaussianBlur(finalI)));
                try {
                    Thread.sleep(fadingTime/blurAmount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public static void deblurAnimation(int blurAmount, int fadingTime, Parent root){
        Thread t = new Thread(() -> {
            for (int i = blurAmount; i > 0; i--){
                int finalI = i;
                Platform.runLater(() -> root.setEffect(new GaussianBlur(finalI)));
                try {
                    Thread.sleep(fadingTime/blurAmount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
