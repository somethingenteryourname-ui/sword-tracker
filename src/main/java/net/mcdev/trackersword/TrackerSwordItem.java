package net.mcdev.trackersword;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class TrackerSwordItem {

    private TrackerSwordItem() {
    }

    public static ItemStack create(TrackerSwordPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Tracker Blade", NamedTextColor.AQUA, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(List.of(
                Component.text("Right-Click: ", NamedTextColor.GRAY)
                        .append(Component.text("Strength III / Resistance II / Regen II", NamedTextColor.LIGHT_PURPLE))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("  for 10s  (90s cooldown)", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Shift + Right-Click: ", NamedTextColor.GRAY)
                        .append(Component.text("Select a player to track", NamedTextColor.LIGHT_PURPLE))
                        .decoration(TextDecoration.ITALIC, false)
        ));

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
        meta.setEnchantmentGlintOverride(true);

        int customModelData = plugin.getConfig().getInt("item.custom-model-data", 0);
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }

        meta.getPersistentDataContainer().set(plugin.getSwordKey(), PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isTrackerSword(TrackerSwordPlugin plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        Byte flag = meta.getPersistentDataContainer().get(plugin.getSwordKey(), PersistentDataType.BYTE);
        return flag != null && flag == (byte) 1;
    }
}
