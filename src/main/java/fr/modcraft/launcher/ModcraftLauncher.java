package fr.modcraft.launcher;

import com.sun.org.apache.xpath.internal.operations.Mod;
import fr.litarvan.openauth.AuthenticationException;
import fr.modcraft.launcher.alert.AlertBox;
import fr.modcraft.launcher.launcherinfos.ReadLauncherInfos;
import fr.modcraft.launcher.launcherinfos.SaveLauncherInfos;
import fr.modcraft.launcher.options.OptionApp;
import fr.modcraft.launcher.utils.BrowserControl;
import fr.modcraft.launcher.utils.CrashReporter;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.wytrem.wylog.BasicLogger;
import net.wytrem.wylog.LoggerFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class ModcraftLauncher extends Application implements Initializable {

    private static BasicLogger logger = LoggerFactory.getLogger("ModcraftMC");

    private static Stage window;
    private static Scene premiumContent;
    private String defaultText = "Premium Mode: ";
    private boolean premiumMode = true;
    private static boolean savePassword = false;
    private static String serverName = "modcraft"; //Nom du server/launcher
    private static int defaultRamAmout;
    public static final int BLUR_AMOUNT = 20;
    public static final int FADING_TIME = 250; //En millisecondes
    private boolean isUpdating = false;
    @FXML
    private Label baseText;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label infoText;
    @FXML
    private ProgressBar chargementBar;
    @FXML
    private ImageView discordIMG;
    @FXML
    private ImageView switchIMG;
    @FXML
    private ImageView settingsIMG;
    @FXML
    private ImageView modcraftIMG;
    @FXML
    private ImageView closeIMG;
    @FXML
    private ImageView hideIMG;
    @FXML
    private ImageView playIMG;

    private static Parent root;

    @FXML
    private Rectangle windowDrag;

    public static final GameVersion FL_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
    public static final GameInfos FL_INFOS = new GameInfos(serverName, FL_VERSION, new GameTweak[] {GameTweak.FORGE});
    public static final File FL_DIR = GameDirGenerator.createGameDir("modcraft");

    public static boolean maintenance = false;

    public static boolean getSavePassword(){
        return savePassword;
    }

    public static void setSavePassword(boolean value){
        savePassword = value;
        SaveLauncherInfos.saveLauncherInfos(savePassword);
    }

    public static void setSavePasswordValue(boolean value){
        savePassword = value;
    }

    public static String getServerName() {
        return serverName;
    }

    private static boolean getMaintenanceStatus(){
        JSONParser parser = new JSONParser();
        JSONObject Jobject;
        try(InputStreamReader inputStreamReader = new InputStreamReader(new URL("http://v1.modcraftmc.fr/server.json").openStream())){
            ModcraftLauncher.getLogger().info("fetching infos from modcraftmc.fr...");
            Object obj = parser.parse(inputStreamReader);
            Jobject = (JSONObject) obj;
            maintenance = (boolean)Jobject.get("maintenance");
            return maintenance;
        } catch (ParseException | IOException e) {
            return true;
        }
    }

    public static void main(String[] args) {

        try {
            logger.info("Starting modcraft launcher");
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDefaultRamAmount(){
        return defaultRamAmout;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        defaultRamAmout = 2;
        new SaveLauncherInfos(new File(FL_DIR.toString() + "\\Launcher\\launcher_infos.json"));
        new ReadLauncherInfos(new File(FL_DIR.toString() + "\\Launcher\\launcher_infos.json"));

        window = primaryStage;
        window.setResizable(false);
        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("ModCraft Launcher");

        if (!getMaintenanceStatus()) {
            root = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("ModcraftLauncher.fxml"));
            premiumContent = new Scene(root, 1080 - 10, 608 - 10);
        }else {
            root = FXMLLoader.load(ClassLoader.getSystemClassLoader().getResource("Maintenance.fxml"));
            premiumContent = new Scene(root, 300, 200);
        }


        window.getIcons().add(new Image(ClassLoader.getSystemClassLoader().getResource("modcraft.png").toExternalForm()));
        window.setScene(premiumContent);
        window.show();
    }

    public static Scene getPremiumContent(){
        return premiumContent;
    }


    @FXML
    public void switchMode(){
        if (!isUpdating) {
            if (premiumMode) {
                premiumMode = false;
                baseText.setText(defaultText + "Désactivé");
                SaveLauncherInfos.saveLauncherInfosSwitch(false);
            } else {
                premiumMode = true;
                baseText.setText(defaultText + "Activé");
                SaveLauncherInfos.saveLauncherInfosSwitch(true);
            }
        }
    }

    @FXML
    public void showOptions(){
        if (!isUpdating) {
            Transitions.blurAnimation(BLUR_AMOUNT, FADING_TIME, root);
            OptionApp.display();
        }
    }
    @FXML
    private void goToSite(){
            BrowserControl.displayURL("https://modcraftmc.fr/");
    }
    @FXML
    private void goToDiscord(){
            BrowserControl.displayURL("https://discord.modcraftmc.fr/");
    }

    @FXML
    public void Iconified(){
        Platform.runLater(() -> window.setIconified(true));
    }

    @FXML
    private void logButton(){
        if (!isUpdating) {
            if (!(usernameField.getText().replaceAll(" ", "").equals("") || passwordField.getText().replaceAll(" ", "").equals(""))) {
                SaveLauncherInfos.saveLauncherInfos(usernameField.getText(), passwordField.getText());
                infoText.setText("Analyse des fichiers du jeu en cours...");

                String ram = OptionApp.getRamArguments()[0];
                if (ram.equals("-Xmx" + 1 + "G")
                        || ram.equals("-Xmx" + 2 + "G")
                        || ram.equals("-Xmx" + 3 + "G")
                        || ram.equals("-Xmx" + 4 + "G")
                ) {

                    Transitions.blurAnimation(BLUR_AMOUNT, FADING_TIME, root);
                    AlertBox.display("Erreur", "vous n'avez pas alloué assez de ram au jeu ! ram recommandée : 6Gb");
                    return;
                }
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            GameUpdate.auth(usernameField.getText(), passwordField.getText(), premiumMode);
                            new GameUpdate(FL_VERSION, FL_INFOS, FL_DIR);
                            isUpdating = true;
                            usernameField.setDisable(true);
                            passwordField.setDisable(true);
                            GameUpdate.update(infoText, chargementBar);
                        } catch (AuthenticationException e) {
                            Transitions.blurAnimation(BLUR_AMOUNT, FADING_TIME, root);
                            Platform.runLater(() -> AlertBox.display("Erreur", "Identifiant ou mot de passe incorrect."));
                            Platform.runLater(() -> infoText.setText("Bienvenue sur Modcraft !"));
                            usernameField.setDisable(false);
                            passwordField.setDisable(false);
                        } catch (com.azuriom.azauth.AuthenticationException e) {
                            Transitions.blurAnimation(BLUR_AMOUNT, FADING_TIME, root);
                            Platform.runLater(() -> AlertBox.display("Erreur", "Identifiant ou mot de passe incorrect: http://modcraftmc.fr"));
                            Platform.runLater(() -> infoText.setText("Bienvenue sur Modcraft !"));
                            usernameField.setDisable(false);
                            passwordField.setDisable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            usernameField.setDisable(false);
                            passwordField.setDisable(false);
                        }
                    }
                };
                t.start();
            } else {
                Transitions.blurAnimation(BLUR_AMOUNT, FADING_TIME, root);
                AlertBox.display("Erreur", "Veuillez mettre un identifiant et un mot de passe !");
            }
        }
    }

    public static int getBLUR_AMOUNT(){
        return BLUR_AMOUNT;
    }

    public static int getFADING_TIME(){
        return FADING_TIME;
    }

    public static Parent getRoot(){
        return root;
    }

    public static Stage getWindow(){
        return window;
    }

    public void close(){
        window.close();
        System.exit(0);
    }

    //Détails bouton Discord
    @FXML
    private void mouseEnterDiscord(){
        if (!isUpdating)
            discordIMG.setImage(new Image("discordM.png"));
    }

    @FXML
    private void mouseExitDiscord(){
        discordIMG.setImage(new Image("discord.png"));
    }

    @FXML
    private void mouseClickDiscord(){
        if (!isUpdating)
            discordIMG.setImage(new Image("discordC.png"));
    }

    @FXML
    private void mouseReleaseDiscord(){
        if (!isUpdating)
            discordIMG.setImage(new Image("discordM.png"));
    }


    //Détails bouton switch
    @FXML
    private void mouseEnterSwitch(){
        if (!isUpdating)
            switchIMG.setImage(new Image("switchM.png"));
    }

    @FXML
    private void mouseExitSwitch(){
        switchIMG.setImage(new Image("switch.png"));
    }

    @FXML
    private void mouseClickSwitch(){
        if (!isUpdating)
            switchIMG.setImage(new Image("switchC.png"));
    }

    @FXML
    private void mouseReleaseSwitch(){
        if (!isUpdating)
            switchIMG.setImage(new Image("switchM.png"));
    }

    //Détails bouton Options
    @FXML
    private void mouseEnterOptions(){
        if (!isUpdating)
            settingsIMG.setImage(new Image("settingsM.png"));
    }

    @FXML
    private void mouseExitOptions(){
        settingsIMG.setImage(new Image("settings.png"));
    }

    @FXML
    private void mouseClickOptions(){
        if (!isUpdating)
            settingsIMG.setImage(new Image("settingsC.png"));
    }

    @FXML
    private void mouseReleaseOptions(){
        if (!isUpdating)
            settingsIMG.setImage(new Image("settingsM.png"));
    }

    //Détails bouton Modcraft
    @FXML
    private void mouseEnterModcraft(){
        if (!isUpdating)
            modcraftIMG.setImage(new Image("modcraftM.png"));
    }

    @FXML
    private void mouseExitModcraft(){
        modcraftIMG.setImage(new Image("modcraft.png"));
    }

    @FXML
    private void mouseClickModcraft(){
        if (!isUpdating)
            modcraftIMG.setImage(new Image("modcraftC.png"));
    }

    @FXML
    private void mouseReleaseModcraft(){
        if (!isUpdating)
            modcraftIMG.setImage(new Image("modcraftM.png"));
    }


    //Détails bouton close
    @FXML
    private void mouseEnterExit(){
        if (!isUpdating)
            closeIMG.setImage(new Image("closeM.png"));
    }

    @FXML
    private void mouseExitExit(){
        closeIMG.setImage(new Image("close.png"));
    }

    @FXML
    private void mouseClicExit(){
        if (!isUpdating)
            closeIMG.setImage(new Image("closeC.png"));
    }

    @FXML
    private void mouseReleaseExit(){
        if (!isUpdating)
            closeIMG.setImage(new Image("closeM.png"));
    }


    //Détails bouton hide
    @FXML
    private void mouseEnterHide(){
        if (!isUpdating)
            hideIMG.setImage(new Image("hideM.png"));
    }

    @FXML
    private void mouseExitHide(){
        hideIMG.setImage(new Image("hide.png"));
    }

    @FXML
    private void mouseClicHide(){
        if (!isUpdating)
            hideIMG.setImage(new Image("hideC.png"));
    }

    @FXML
    private void mouseReleaseHide(){
        if (!isUpdating)
            hideIMG.setImage(new Image("hideM.png"));
    }


    //Détails bouton play
    @FXML
    private void mouseEnterPlay(){
        if (!isUpdating)
            playIMG.setImage(new Image("playM.png"));
    }

    @FXML
    private void mouseExitPlay(){
        playIMG.setImage(new Image("play.png"));
    }

    @FXML
    private void mouseClicPlay(){
        if (!isUpdating)
            playIMG.setImage(new Image("playC.png"));
    }

    @FXML
    private void mouseReleasePlay(){
        if (!isUpdating)
            playIMG.setImage(new Image("playM.png"));
    }

    private double sx = 0, sy = 0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

            if (!maintenance) {
                infoText.setText("Bienvenue sur ModcraftMC !");
                if (ReadLauncherInfos.getUsername() != null) {
                    usernameField.setText(ReadLauncherInfos.getUsername());
                }

                if (ReadLauncherInfos.getPassword() != null) {
                    passwordField.setText(ReadLauncherInfos.getPassword());
                }

                windowDrag.addEventFilter(MOUSE_PRESSED, e -> {
                    sx = e.getScreenX() - window.getX();
                    sy = e.getScreenY() - window.getY();
                });
                windowDrag.addEventFilter(MOUSE_DRAGGED, e -> {
                    window.setX(e.getScreenX() - sx);
                    window.setY(e.getScreenY() - sy);
                });


                chargementBar.setStyle("-fx-accent: orange;");

                SaveLauncherInfos.setSavePassword(ReadLauncherInfos.getSavePassword());
                SaveLauncherInfos.setPremiumMode(ReadLauncherInfos.getPremiumMode());

                premiumMode = ReadLauncherInfos.getPremiumMode();
                if (premiumMode)
                    baseText.setText(defaultText + "Activé");
                else
                    baseText.setText(defaultText + "Désactivé");
            }
    }

    public static BasicLogger getLogger() {
        return logger;
    }
}