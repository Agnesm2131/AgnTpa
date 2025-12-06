package me.agntpa.util;

import me.agntpa.AgnTpa;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilderFromConfig {

    public static ItemStack loadItem(String path) {
        ConfigurationSection sec = AgnTpa.getInstance().getConfig().getConfigurationSection(path);

        String matStr = sec.getString("material", "STONE");
        Material material;

        try {
            material = Material.valueOf(matStr.toUpperCase());
        } catch (Exception e) {
            material = Material.STONE;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (sec.contains("name"))
            meta.setDisplayName(sec.getString("name").replace("&", "§"));

        if (sec.contains("lore")) {
            List<String> lore = sec.getStringList("lore")
                    .stream()
                    .map(s -> s.replace("&", "§"))
                    .toList();
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }
}
