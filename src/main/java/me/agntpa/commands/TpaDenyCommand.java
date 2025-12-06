package me.agntpa.commands;

import me.agntpa.AgnTpa;
import me.agntpa.manager.TpaManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpaDenyCommand implements CommandExecutor {

    private final AgnTpa plugin;
    private final TpaManager manager;

    public TpaDenyCommand(AgnTpa plugin) {
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
        Player requester = manager.getRequester(p);

        if (requester == null) {
            p.sendMessage(msg("no-request-to-deny"));
            return true;
        }

        manager.denyRequest(p);
        return true;
    }
}