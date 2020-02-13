package fr.modcraft.launcher;

import com.azuriom.azauth.AzAuthenticator;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.modcraft.launcher.Supdatemanager.SupdateManager;
import fr.modcraft.launcher.alert.AlertBox;
import fr.modcraft.launcher.options.OptionApp;
import fr.modcraft.launcher.utils.CrashReporter;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class GameUpdate {

    private static Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
    public GameVersion FL_VERSION;
    public static GameInfos FL_INFOS;
    public static File FL_DIR;
    private static AuthInfos authInfos;


    public GameUpdate(GameVersion gameVersion, GameInfos gameInfos, File gameDir){
        FL_VERSION = gameVersion;
        FL_INFOS = gameInfos;
        FL_DIR = gameDir;
    }

    public static void auth(String username, String password, boolean premiumMode) throws AuthenticationException, IOException, com.azuriom.azauth.AuthenticationException {
        if (premiumMode) {
            AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
            authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
        } else {
            AzAuthenticator authenticatorModcraft = new AzAuthenticator("https://modcraftmc.fr");
            authInfos = authenticatorModcraft.authenticate(username, password, AuthInfos.class);
        }
    }


    public static void update(Label infoText, ProgressBar chargementBar){
        Thread updateThread = new Thread(() -> {
            try {
                SupdateManager supdateManager = new SupdateManager("http://149.202.65.157:100/", FL_DIR, chargementBar, infoText);
                supdateManager.setDefaultText("Téléchargement des fichiers");

                supdateManager.supdate().progressProperty().addListener((observable, oldValue, newValue) -> {
                    int number = (int) (newValue.doubleValue() * 100);
                    infoText.setText("Téléchargement des fichiers " + number + "%");

                });

                supdateManager.getTask().setOnSucceeded(event ->
                {
                    infoText.setText("Lancement du jeu");

                    Thread starter = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                GameUpdate.launch();
                            } catch (LaunchException e) {
                                e.printStackTrace();
                                String id = CrashReporter.generate();
                                Transitions.blurAnimation(ModcraftLauncher.BLUR_AMOUNT, ModcraftLauncher.FADING_TIME, ModcraftLauncher.getRoot());
                                Platform.runLater(() -> AlertBox.display("Erreur", "Erreur lors du lancement du jeu. \n ID du crash report: "+id));
                            }
                        }
                    };
                    starter.start();
                });
                supdateManager.start();
            } catch (Exception e) {
                e.printStackTrace();
                Transitions.blurAnimation(ModcraftLauncher.BLUR_AMOUNT, ModcraftLauncher.FADING_TIME, ModcraftLauncher.getRoot());
                Platform.runLater(() -> AlertBox.display("Erreur", "Mise à jour du jeu impossible"));
                Platform.runLater(() -> infoText.setText("Bienvenue sur Modcraft !"));
            }
        });
        updateThread.start();
        updateThread.interrupt();
    }

    public static void launch() throws LaunchException {
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(FL_INFOS, GameFolder.BASIC, authInfos);
        profile.getVmArgs().addAll(Arrays.asList(OptionApp.getRamArguments()));
        ExternalLauncher launcher = new ExternalLauncher(profile);
        Process p = launcher.launch();

        try {
            sleep(5000);
            Platform.runLater(() -> ModcraftLauncher.getWindow().hide());
            p.waitFor();
        } catch (InterruptedException e) {
            CrashReporter.generate();
            e.printStackTrace();
        }
        System.exit(0);
    }
}
