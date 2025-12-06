package me.agntpa.gui;

import me.agntpa.AgnTpa;
import me.agntpa.util.ItemBuilderFromConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class TpaMenu {

    public static void open(Player sender, Player target) {

        String title = AgnTpa.getInstance().getConfig().getString("gui.tpa-send.title");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        int headSlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-send.items.target-head.slot");
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        head.setItemMeta(meta);
        inv.setItem(headSlot, head);

        int acceptSlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-send.items.accept.slot");
        ItemStack accept = ItemBuilderFromConfig.loadItem("gui.tpa-send.items.accept");
        inv.setItem(acceptSlot, accept);

        int denySlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-send.items.deny.slot");
        ItemStack deny = ItemBuilderFromConfig.loadItem("gui.tpa-send.items.deny");
        inv.setItem(denySlot, deny);

        sender.openInventory(inv);
    }
}
