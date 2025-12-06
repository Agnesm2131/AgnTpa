package me.agntpa;

import me.agntpa.commands.*;
import me.agntpa.listeners.MenuListener;
import me.agntpa.manager.TpaManager;
import me.agntpa.manager.BtpaManager;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class AgnTpa extends JavaPlugin {

    private static AgnTpa instance;
    private TpaManager tpaManager;
    private BtpaManager btpaManager;
    private BtpaCommand btpaCommand;

    private String webhookUrl;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        webhookUrl = getConfig().getString("webhook-url", "");

        tpaManager = new TpaManager();

        try {
            btpaManager = new BtpaManager();
        } catch (SQLException e) {
            getLogger().severe("BtpaManager başlatılamadı!");
            e.printStackTrace();
        }

        btpaCommand = new BtpaCommand(this);

        getCommand("btpa").setExecutor(btpaCommand);
        this.getCommand("agntpa").setExecutor(new ReloadCommand(this));
        getCommand("atpa").setExecutor(new AtpaCommand(this));
        getCommand("tpa").setExecutor(new AtpaCommand(this));
        getCommand("atpaccept").setExecutor(new AtpacceptCommand(this));
        getCommand("tpaccept").setExecutor(new AtpacceptCommand(this));
        getCommand("tpadeny").setExecutor(new TpaDenyCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(this, btpaManager), this);

        getLogger().info("AgnTpa aktif!");
    }

    public static AgnTpa getInstance() {
        return instance;
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public BtpaManager getBtpaManager() {
        return btpaManager;
    }

    public BtpaCommand getBtpaCommand() {
        return btpaCommand;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }
}