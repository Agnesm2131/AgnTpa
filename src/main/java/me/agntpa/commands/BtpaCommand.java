package me.agntpa.commands;

import me.agntpa.AgnTpa;
import me.agntpa.manager.BtpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BtpaCommand implements CommandExecutor {

    private final AgnTpa plugin;
    private final BtpaManager manager;
    private final Set<UUID> enabledPlayers;

    public BtpaCommand(AgnTpa plugin) {
        this.plugin = plugin;
        this.manager = plugin.getBtpaManager();
        this.enabledPlayers = new HashSet<>();
    }

    private String msg(String key) {
        return plugin.getConfig()
                .getString("messages." + key, "§cMesaj bulunamadı: " + key)
                .replace("&", "§");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("only-players"));
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage(msg("btpa-usage"));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            case "on":
                enabledPlayers.add(p.getUniqueId());
                p.sendMessage(msg("btpa-on"));
                break;

            case "off":
                enabledPlayers.remove(p.getUniqueId());
                p.sendMessage(msg("btpa-off"));
                break;

            case "add":
                if (args.length != 2) {
                    p.sendMessage(msg("btpa-add-usage"));
                    return true;
                }

                Player toAdd = Bukkit.getPlayer(args[1]);
                if (toAdd == null) {
                    p.sendMessage(msg("btpa-player-not-found"));
                    return true;
                }

                try {
                    manager.addBuddy(p.getUniqueId(), toAdd.getUniqueId());
                    p.sendMessage(msg("btpa-add-success").replace("%player%", toAdd.getName()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(msg("btpa-error"));
                }
                break;

            case "remove":
                if (args.length != 2) {
                    p.sendMessage(msg("btpa-remove-usage"));
                    return true;
                }

                Player toRemove = Bukkit.getPlayer(args[1]);
                if (toRemove == null) {
                    p.sendMessage(msg("btpa-player-not-found"));
                    return true;
                }

                try {
                    manager.removeBuddy(p.getUniqueId(), toRemove.getUniqueId());
                    p.sendMessage(msg("btpa-remove-success").replace("%player%", toRemove.getName()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(msg("btpa-error"));
                }
                break;

            case "list":
                try {
                    Set<UUID> buddies = manager.getBuddies(p.getUniqueId());

                    if (buddies.isEmpty()) {
                        p.sendMessage(msg("btpa-list-empty"));
                        return true;
                    }

                    p.sendMessage(msg("btpa-list-header"));

                    for (UUID id : buddies) {
                        Player buddy = Bukkit.getPlayer(id);
                        if (buddy != null) {
                            p.sendMessage("§7- §f" + buddy.getName());
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(msg("btpa-error"));
                }
                break;

            default:
                p.sendMessage(msg("btpa-usage"));
                break;
        }

        return true;
    }

    public boolean isEnabled(UUID player) {
        return enabledPlayers.contains(player);
    }

    public BtpaManager getManager() {
        return manager;
    }
}