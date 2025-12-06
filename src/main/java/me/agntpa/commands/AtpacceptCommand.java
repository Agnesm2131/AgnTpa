package me.agntpa.commands;

import me.agntpa.AgnTpa;
import me.agntpa.gui.TpaAcceptMenu;
import me.agntpa.manager.TpaManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AtpacceptCommand implements CommandExecutor {

    private final AgnTpa plugin;
    private final TpaManager manager;

    public AtpacceptCommand(AgnTpa plugin) {
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

        if (!manager.hasPendingRequest(p)) {
            p.sendMessage(msg("no-pending-request"));
            return true;
        }

        TpaAcceptMenu.open(p);
        return true;
    }
}