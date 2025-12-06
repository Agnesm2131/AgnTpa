package me.agntpa.commands;

import me.agntpa.AgnTpa;
import me.agntpa.gui.TpaMenu;
import me.agntpa.manager.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class AtpaCommand implements CommandExecutor {

    private final AgnTpa plugin;
    private final TpaManager manager;

    public AtpaCommand(AgnTpa plugin) {
        this.plugin = plugin;
        this.manager = plugin.getTpaManager();
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

        if (args.length != 1) {
            p.sendMessage(msg("tpa-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(msg("player-not-found"));
            return true;
        }

        if (target.equals(p)) {
            p.sendMessage(msg("cant-tpa-self"));
            return true;
        }
        try {
            if (plugin.getBtpaCommand().isEnabled(target.getUniqueId()) &&
                    plugin.getBtpaCommand().getManager().isBuddy(target.getUniqueId(), p.getUniqueId())) {

                startBtpaCountdown(p, target);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(msg("btpa-check-error"));
            return true;
        }

        TpaMenu.open(p, target);
        return true;
    }

    private void startBtpaCountdown(Player sender, Player target) {

        sender.sendMessage(msg("btpa-start"));
        Location start = sender.getLocation();

        new BukkitRunnable() {
            int time = 5;
            boolean cancelled = false;

            @Override
            public void run() {

                if (cancelled) {
                    this.cancel();
                    return;
                }

                if (sender.getLocation().distance(start) > 0.15) {
                    cancelled = true;
                    sender.sendMessage(msg("btpa-move-cancel"));
                    this.cancel();
                    return;
                }

                sender.sendTitle(
                        msg("countdown-title").replace("%time%", String.valueOf(time)),
                        msg("countdown-subtitle").replace("%player%", target.getName()),
                        0, 20, 0
                );

                sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                if (time <= 0) {
                    sender.teleport(target.getLocation());
                    sender.sendMessage(msg("btpa-complete"));

                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                    cancelled = true;
                    this.cancel();
                    return;
                }

                time--;
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }
}