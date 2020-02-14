package fr.modcraft.launcher.launcherinfos;

import fr.modcraft.launcher.ModcraftLauncher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ReadLauncherInfos {

    private static File launcherFile;
    private static JSONObject launcherInfos;
    private static Object obj;

    public ReadLauncherInfos(File launcherFile){
        ReadLauncherInfos.launcherFile = launcherFile;
        loadInfos();
    }

    private static void loadInfos(){
        if (launcherFile.exists()) {
            try (FileReader reader = new FileReader(launcherFile)) {
                obj = new JSONParser().parse(reader);
                launcherInfos = (JSONObject) obj;
            } catch (FileNotFoundException e) {
                ModcraftLauncher.getLogger().error("Le fichier n'existe pas");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getUsername(){
        loadInfos();
        if (launcherFile.exists()) {
            if (launcherInfos.containsKey("username")) {
                return launcherInfos.get("username").toString();
            } else return null;
        } else return null;
    }

    public static String getPassword(){
        loadInfos();
        if (launcherFile.exists()) {
            if (launcherInfos.containsKey("password")) {
                String passwordDecoded = new String(Base64.getDecoder().decode(launcherInfos.get("password").toString()), StandardCharsets.UTF_8);
                return passwordDecoded;
            } else return null;
        } else return null;
    }

    public static boolean getSavePassword(){
        loadInfos();
        if (launcherFile.exists()){
            if (launcherInfos.containsKey("savepassword")) {
                return (boolean) launcherInfos.get("savepassword");
            }else return false;
        } else return false;
    }

    public static int getRAM(){
        loadInfos();
        if (launcherFile.exists()) {
            if (launcherInfos.containsKey("ram")) {
                return Integer.parseInt(launcherInfos.get("ram").toString());
            } else return ModcraftLauncher.getDefaultRamAmount();
        } else return ModcraftLauncher.getDefaultRamAmount();
    }

    public static boolean getPremiumMode(){
        loadInfos();
        if (launcherFile.exists()){
            if (launcherInfos.containsKey("premiumMode")){
                return (boolean) launcherInfos.get("premiumMode");
            } else return true;
        } else return true;
    }
}
