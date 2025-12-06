package me.agntpa.gui;

import me.agntpa.AgnTpa;
import me.agntpa.manager.TpaManager;
import me.agntpa.util.ItemBuilderFromConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class TpaAcceptMenu {

    public static void open(Player target) {

        TpaManager manager = AgnTpa.getInstance().getTpaManager();
        Player sender = manager.getRequester(target);
        if (sender == null) return;

        String title = AgnTpa.getInstance().getConfig().getString("gui.tpa-accept.title");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        int headSlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-accept.items.target-head.slot");
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(sender);
        meta.setDisplayName("§a" + sender.getName());
        head.setItemMeta(meta);
        inv.setItem(headSlot, head);

        int acceptSlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-accept.items.accept.slot");
        ItemStack accept = ItemBuilderFromConfig.loadItem("gui.tpa-accept.items.accept");
        inv.setItem(acceptSlot, accept);

        int denySlot = AgnTpa.getInstance().getConfig().getInt("gui.tpa-accept.items.deny.slot");
        ItemStack deny = ItemBuilderFromConfig.loadItem("gui.tpa-accept.items.deny");
        inv.setItem(denySlot, deny);

        target.openInventory(inv);
    }
}
