package me.agntpa.commands;

import me.agntpa.AgnTpa;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final AgnTpa plugin;

    public ReloadCommand(AgnTpa plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("agntpa.reload")) {
            sender.sendMessage("§cBu komutu kullanmak için yetkin yok!");
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage("§aAgnTpa config.yml yeniden yüklendi!");

        return true;
    }
}
