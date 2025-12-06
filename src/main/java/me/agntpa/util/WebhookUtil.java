package me.agntpa.util;

import me.agntpa.AgnTpa;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebhookUtil {

    public static void sendTPALog(String type, Player sender, Player target) {
        String webhookUrl = AgnTpa.getInstance().getConfig().getString("discord.webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(webhookUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            int color;
            String title;
            switch (type.toLowerCase()) {
                case "accept" -> {
                    color = 0x00FF00;
                    title = "TPA kabul edildi";
                }
                case "deny" -> {
                    color = 0xFF0000; 
                    title = "TPA reddedildi";
                }
                default -> {
                    color = 0xFFFF00; 
                    title = "TPA gönderildi";
                }
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            JSONObject embed = new JSONObject();
            embed.put("title", title);
            embed.put("color", color);
            embed.put("timestamp", timestamp);

            JSONObject senderField = new JSONObject();
            senderField.put("name", "Gönderen");
            senderField.put("value", sender.getName());
            senderField.put("inline", true);

            JSONObject targetField = new JSONObject();
            targetField.put("name", "Hedef");
            targetField.put("value", target.getName());
            targetField.put("inline", true);

            embed.put("fields", new org.json.JSONArray().put(senderField).put(targetField));

            JSONObject author = new JSONObject();
            author.put("name", sender.getName());
            author.put("icon_url", "https://minotar.net/avatar/" + sender.getName() + "/64");
            embed.put("author", author);

            JSONObject payload = new JSONObject();
            payload.put("embeds", new org.json.JSONArray().put(embed));

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }

            connection.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
