package me.agntpa.listeners;

import me.agntpa.AgnTpa;
import me.agntpa.manager.BtpaManager;
import me.agntpa.manager.TpaManager;
import me.agntpa.util.WebhookUtil;
import me.agntpa.gui.TpaAcceptMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;

import java.sql.SQLException;

public class MenuListener implements Listener {

    private final AgnTpa plugin;
    private final TpaManager manager;
    private final BtpaManager btpaManager;

    public MenuListener(AgnTpa plugin, BtpaManager btpaManager) {
        this.plugin = plugin;
        this.manager = plugin.getTpaManager();
        this.btpaManager = btpaManager;
    }

    private String msg(String key) {
        return plugin.getConfig()
                .getString("messages." + key, "§cMesaj bulunamadı: " + key)
                .replace("&", "§");
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        String clickedName = e.getCurrentItem().getItemMeta().getDisplayName();

        if (title.equals(plugin.getConfig().getString("gui.tpa-send.title")) ||
                title.equals(plugin.getConfig().getString("gui.tpa-accept.title"))) {
            fillEmptySlotsWithBlackGlass(p);
        }

        if (title.equals(plugin.getConfig().getString("gui.tpa-send.title"))) {
            e.setCancelled(true);

            String acceptName = plugin.getConfig().getString("gui.tpa-send.items.accept.name").replace("&", "§");
            String denyName = plugin.getConfig().getString("gui.tpa-send.items.deny.name").replace("&", "§");

            if (clickedName.equals(acceptName)) {

                SkullMeta meta = (SkullMeta) e.getInventory()
                        .getItem(plugin.getConfig().getInt("gui.tpa-send.items.target-head.slot"))
                        .getItemMeta();

                Player target = Bukkit.getPlayer(meta.getOwningPlayer().getName());
                if (target == null) {
                    p.sendMessage(msg("player-offline"));
                    return;
                }

                p.closeInventory();

                boolean isBuddy = false;
                try {
                    isBuddy = btpaManager.isBuddy(target.getUniqueId(), p.getUniqueId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if (isBuddy) {
                    startTeleport(p, target);
                    p.sendMessage(msg("buddy-tpa-start"));
                    target.sendMessage(msg("buddy-tpa-notify").replace("%player%", p.getName()));
                } else {
                    manager.sendRequest(p, target);
                    TpaAcceptMenu.open(target);

                    target.sendMessage(msg("request-received").replace("%player%", p.getName()));
                    WebhookUtil.sendTPALog("send", p, target);
                }
            }

            if (clickedName.equals(denyName)) {
                p.closeInventory();
            }
        }

        if (title.equals(plugin.getConfig().getString("gui.tpa-accept.title"))) {
            e.setCancelled(true);

            Player sender = manager.getRequester(p);
            if (sender == null) {
                p.sendMessage(msg("request-none"));
                return;
            }

            int acceptSlot = plugin.getConfig().getInt("gui.tpa-accept.items.accept.slot");
            int denySlot = plugin.getConfig().getInt("gui.tpa-accept.items.deny.slot");

            if (e.getSlot() == acceptSlot) {
                p.closeInventory();
                manager.remove(p);

                WebhookUtil.sendTPALog("accept", sender, p);
                startTeleport(sender, p);
            }

            if (e.getSlot() == denySlot) {
                p.closeInventory();
                manager.remove(p);
                manager.denyRequest(p);

                p.sendMessage(msg("request-denied"));
                sender.sendMessage(msg("request-denied-sender"));
                WebhookUtil.sendTPALog("deny", sender, p);
            }
        }
    }

    private void fillEmptySlotsWithBlackGlass(Player p) {
        var inv = p.getOpenInventory().getTopInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta meta = blackGlass.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(" "); // boş isim
                    blackGlass.setItemMeta(meta);
                }
                inv.setItem(i, blackGlass);
            }
        }
    }

    private void startTeleport(Player sender, Player target) {
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
                    sender.sendMessage(msg("teleport-move-cancel"));
                    this.cancel();
                    return;
                }

                sender.sendTitle(
                        msg("teleport-start-title").replace("%time%", String.valueOf(time)),
                        msg("teleport-start-subtitle").replace("%player%", target.getName()),
                        0, 20, 0
                );

                sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                if (time <= 0) {
                    sender.teleport(target.getLocation());

                    sender.sendMessage(msg("teleport-complete-sender"));
                    target.sendMessage(msg("teleport-complete-target").replace("%player%", sender.getName()));

                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                    cancelled = true;
                    this.cancel();
                }

                time--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}