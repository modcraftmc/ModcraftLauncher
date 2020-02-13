package fr.modcraft.launcher.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fr.modcraft.launcher.ModcraftLauncher;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class CrashReporter {

    private static String crash_url;
    private static File crash_log;
    private static String pasteURL = "http://v1.modcraftmc.fr:7777/", id = "";

    public static String generate(){
        crash_log = new File(ModcraftLauncher.FL_DIR.toString() + "\\logs\\latest.log");
        try {
            crash_url = paste(IOUtils.toString(new FileInputStream(crash_log)));
            id = randomCaracters();
            System.out.println("Génération du crash report terminé: "+crash_url);
            System.out.println("ID: "+id);

            String time = getTime("GMT+1");


            WebhookClientBuilder builder = new WebhookClientBuilder("https://discordapp.com/api/webhooks/677237941432483840/mZkhVd0P08KosUQ2FefMa6rnyB6YIn_vF78FAduOa5DYtmCtqLFprfJSUp_9KqFUNFiN"); // or id, token
            builder.setThreadFactory((job) -> {
                Thread thread = new Thread(job);
                thread.setName("Hello");
                thread.setDaemon(true);
                return thread;
            });
            builder.setWait(true);
            WebhookClient client = builder.build();

            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setColor(0xFE3523)
                    .addField(new WebhookEmbed.EmbedField(true, "Crash report link", crash_url))
                    .addField(new WebhookEmbed.EmbedField(true, "Crash report id", id))
                    .setThumbnailUrl("https://k8h4n2u5.stackpathcdn.com/wp-content/uploads/2019/02/crash-hotel-logo-white.png")
                    .setFooter(new WebhookEmbed.EmbedFooter(time, "https://kryptongta.com/images/kryptonlogodark.png"))
                    .build();

            client.send(embed);
            client.close();

            return id;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTime(String timeZone){
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEE d MMMMMMMMM yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(currentTime);
    }

    private static String randomCaracters(){
        Random rand = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12356789";
        String caracters = "";
        int longueur = alphabet.length();
        for(int i = 0; i < 10; i++) {
            int k = rand.nextInt(longueur);
            caracters+=alphabet.charAt(k);
        }
        return caracters;
    }

    public synchronized static String paste(String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(pasteURL + "documents");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return pasteURL + new JSONObject(rd.readLine()).getString("key");

        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection == null) return null;
            connection.disconnect();
        }
    }
}
