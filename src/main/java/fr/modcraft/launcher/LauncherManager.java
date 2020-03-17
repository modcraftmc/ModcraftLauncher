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
import ma.forix.gameupdater.GameUpdater;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class LauncherManager {

    private static Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
    public GameVersion FL_VERSION;
    public static GameInfos FL_INFOS;
    public static File FL_DIR;
    private static AuthInfos authInfos;

    public LauncherManager(GameVersion gameVersion, GameInfos gameInfos, File gameDir){
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
        Thread gameUpdateThread = new Thread(() -> {
            GameUpdater updater = new GameUpdater("http://v1.modcraftmc.fr:100/gameupdatertester/", FL_DIR, chargementBar, infoText);
            updater.Suppresser(true);
            updater.updater().progressProperty().addListener((observable, oldValue, newValue) -> {
                int number = (int) (newValue.doubleValue() * 100);
                infoText.setText("Téléchargement de Modcraft " + number + "%");
            });

            updater.getTask().setOnSucceeded(event -> {
                infoText.setText("Lancement du jeu");
                Thread gameStarter = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            LauncherManager.launch();
                        } catch (LaunchException e) {
                            e.printStackTrace();
                            String id = CrashReporter.generate();
                            Transitions.blurAnimation(ModcraftLauncher.BLUR_AMOUNT, ModcraftLauncher.FADING_TIME, ModcraftLauncher.getRoot());
                            Platform.runLater(() -> AlertBox.display("Erreur", "Erreur lors du lancement du jeu. \n ID du crash report: "+id));
                        }
                    }
                };
                gameStarter.start();
            });
            updater.start();
        });
        gameUpdateThread.start();
        gameUpdateThread.interrupt();
    }

    public static void launch() throws LaunchException {
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(FL_INFOS, GameFolder.BASIC, authInfos);
        profile.getVmArgs().addAll(Arrays.asList(OptionApp.getRamArguments()));
        profile.getArgs().add("-Dfml.readTimeout=60");
        ExternalLauncher launcher = new ExternalLauncher(profile);
        Process p = null;
        try {
            p = launcher.launch();
            sleep(5000);
            Platform.runLater(() -> ModcraftLauncher.getWindow().hide());
            p.waitFor();
        } catch (InterruptedException | LaunchException e) {
            e.printStackTrace();
        } finally {
            if (p.exitValue() == 1 ) {
                CrashReporter.generate();
            }
            System.exit(0);
        }
    }
}
