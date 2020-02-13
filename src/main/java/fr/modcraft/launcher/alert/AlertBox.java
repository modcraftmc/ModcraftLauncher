package fr.modcraft.launcher.alert;

import fr.modcraft.launcher.ModcraftLauncher;
import fr.modcraft.launcher.Transitions;
import fr.modcraft.launcher.utils.ScreenUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlertBox implements Initializable {

    private static Stage alWin;
    @FXML
    private ImageView alertImage;
    @FXML
    private Label messageLabel;

    private static String messageS;

    public static void display(String title, String message){
        messageS = message;
        alWin = new Stage();
        alWin.initModality(Modality.APPLICATION_MODAL);
        alWin.initStyle(StageStyle.UNDECORATED);
        alWin.setTitle(title);
        Parent root = null;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("AlertWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 250, 150);

        alWin.setScene(scene);
        alWin.setOpacity(0);
        alWin.show();
        alWin.setX(ModcraftLauncher.getPremiumContent().getWidth()/2-250/2+ModcraftLauncher.getPremiumContent().getWindow().getX());
        alWin.setY(ModcraftLauncher.getPremiumContent().getHeight()/2-150/2+ModcraftLauncher.getPremiumContent().getWindow().getY());

        if (scene.getWindow().getX()+scene.getWidth() > ScreenUtils.getScreenDimensions().width)
            alWin.setX(ScreenUtils.getScreenDimensions().width-scene.getWidth());

        if (scene.getWindow().getY()+scene.getHeight() > ScreenUtils.getScreenDimensions().height)
            alWin.setY(ScreenUtils.getScreenDimensions().height-scene.getHeight());
        Transitions.fadeIn(alWin);
    }

    public void okButton(){
        Transitions.deblurAnimation(ModcraftLauncher.BLUR_AMOUNT, ModcraftLauncher.FADING_TIME, ModcraftLauncher.getRoot());
        Transitions.fadeOut(alWin);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("alert.png");
        alertImage.setImage(image);
        messageLabel.setText(messageS);
    }
}