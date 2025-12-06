package me.agntpa.manager;

import me.agntpa.AgnTpa;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TpaManager {

    private final Map<Player, Player> requests = new HashMap<>();

    private String msg(String key) {
        return AgnTpa.getInstance().getConfig()
                .getString("messages." + key, "§cMesaj bulunamadı: " + key)
                .replace("&", "§");
    }

    private int getRequestTimeout() {
        return AgnTpa.getInstance().getConfig().getInt("tpa.request-timeout", 120);
    }

    public void sendRequest(Player sender, Player target) {
        requests.put(target, sender);

        sender.sendMessage(
                msg("request-sent-sender").replace("%player%", target.getName())
        );

        target.sendMessage(
                msg("request-received").replace("%player%", sender.getName())
        );

        int timeout = getRequestTimeout();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (requests.containsKey(target) && requests.get(target).equals(sender)) {
                    requests.remove(target);

                    sender.sendMessage(msg("request-timeout-sender").replace("%player%", target.getName()));
                    target.sendMessage(msg("request-timeout-target").replace("%player%", sender.getName()));
                }
            }
        }.runTaskLater(AgnTpa.getInstance(), 20L * timeout); 
    }

    public boolean hasActiveSender(Player sender) {
        return requests.containsValue(sender);
    }

    public Player getRequester(Player target) {
        return requests.get(target);
    }

    public void remove(Player target) {
        requests.remove(target);
    }

    public void acceptRequest(Player target) {
        Player sender = requests.get(target);
        if (sender != null) {
            sender.teleport(target.getLocation());
            requests.remove(target);
        }
    }

    public boolean hasPendingRequest(Player target) {
        return requests.containsKey(target);
    }

    public void denyRequest(Player target) {
        Player sender = requests.get(target);
        if (sender != null) {
            requests.remove(target);

            sender.sendMessage(msg("request-denied-sender"));
            target.sendMessage(msg("request-denied-target"));
        }
    }
}
