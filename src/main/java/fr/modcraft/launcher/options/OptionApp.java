package fr.modcraft.launcher.options;

import fr.modcraft.launcher.ModcraftLauncher;
import fr.modcraft.launcher.Transitions;
import fr.modcraft.launcher.launcherinfos.ReadLauncherInfos;
import fr.modcraft.launcher.launcherinfos.SaveLauncherInfos;
import fr.modcraft.launcher.utils.ScreenUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionApp implements Initializable {

    private static Stage window;
    private static int maxRam;
    @FXML
    private ComboBox ramChoice;
    @FXML
    private CheckBox savePassword;
    @FXML
    private ImageView logIMG;

    public static void display(){
        window = new Stage();
        window.setTitle("Options");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        Parent root = null;
        try {
            root = FXMLLoader.load(OptionApp.class.getResource("OptionApp.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 400, 130);
        window.setScene(scene);
        window.show();

        window.setX(ModcraftLauncher.getPremiumContent().getWidth()/2-400/2+ModcraftLauncher.getPremiumContent().getWindow().getX());
        window.setY(ModcraftLauncher.getPremiumContent().getHeight()/2-130/2+ModcraftLauncher.getPremiumContent().getWindow().getY());

        if (scene.getWindow().getX()+ scene.getWidth() > ScreenUtils.getScreenDimensions().width)
            window.setX(ScreenUtils.getScreenDimensions().width- scene.getWidth());

        if (scene.getWindow().getY()+ scene.getHeight() > ScreenUtils.getScreenDimensions().height)
            window.setY(ScreenUtils.getScreenDimensions().height- scene.getHeight());
        Transitions.fadeIn(window);
    }

    public void setMaxRam(){
        maxRam = Integer.parseInt(ramChoice.getValue().toString().replaceAll("Go",""));
        SaveLauncherInfos.saveLauncherInfos(maxRam);
    }

    @FXML
    private void close(){
        Transitions.deblurAnimation(ModcraftLauncher.BLUR_AMOUNT, ModcraftLauncher.FADING_TIME, ModcraftLauncher.getRoot());
        Transitions.fadeOut(window);
    }

    @FXML
    private void changeSavePassword(){
        if (ModcraftLauncher.getSavePassword()){
            ModcraftLauncher.setSavePassword(false);
        } else {
            ModcraftLauncher.setSavePassword(true);
        }
    }

    public static boolean isShowing(){
        return window.isShowing();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ramChoice.getItems().addAll("1Go", "2Go", "3Go", "4Go", "5Go", "6Go", "7Go", "8Go", "9Go", "10Go");
        ramChoice.setValue(ReadLauncherInfos.getRAM()+"Go");
        savePassword.setSelected(ReadLauncherInfos.getSavePassword());
        if (savePassword.isSelected())
            ModcraftLauncher.setSavePasswordValue(true);
        else
            ModcraftLauncher.setSavePasswordValue(false);
    }

    public static String[] getRamArguments() {
        if (maxRam == 0)
            maxRam = ReadLauncherInfos.getRAM();
        String[] ramArgs = new String[]{"-Xmx" + maxRam + "G"};
        return ramArgs;
    }

    @FXML
    private void openLogs(){
        try {
            Desktop.getDesktop().open(new File(System.getProperty("user.home")+"\\AppData\\Roaming\\."+ModcraftLauncher.getServerName()+"\\logs"));
            System.out.println("Ouverture des logs dans; "+System.getProperty("user.home")+"\\AppData\\Roaming\\."+ModcraftLauncher.getServerName()+"\\logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Détails bouton logs
    @FXML
    private void mouseEnterLogs(){
        logIMG.setImage(new javafx.scene.image.Image("/resources/logButtonM.png"));
    }

    @FXML
    private void mouseExitLogs(){
        logIMG.setImage(new javafx.scene.image.Image("/resources/logButton.png"));
    }

    @FXML
    private void mouseClicLogs(){
        logIMG.setImage(new javafx.scene.image.Image("/resources/logButtonC.png"));
    }

    @FXML
    private void mouseReleaseLogs(){
        logIMG.setImage(new Image("/resources/logButtonM.png"));
    }
}
