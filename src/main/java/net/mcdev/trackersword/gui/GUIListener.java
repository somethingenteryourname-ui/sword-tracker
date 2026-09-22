package net.mcdev.trackersword.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcdev.trackersword.TrackerSwordPlugin;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GUIListener implements Listener {

    private final TrackerSwordPlugin plugin;

    public GUIListener(TrackerSwordPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TrackerGUIHolder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player viewer)) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            plugin.getTrackingMap().remove(viewer.getUniqueId());
            viewer.sendMessage(Component.text("Tracking stopped.", NamedTextColor.YELLOW));
            viewer.closeInventory();
            return;
        }

        if (clicked.getType() == Material.PLAYER_HEAD && clicked.getItemMeta() instanceof SkullMeta skullMeta) {
            OfflinePlayer owner = skullMeta.getOwningPlayer();
            if (owner == null) {
                return;
            }
            plugin.getTrackingMap().put(viewer.getUniqueId(), owner.getUniqueId());
            viewer.sendMessage(Component.text("Now tracking " + owner.getName() + ".", NamedTextColor.GREEN));
            viewer.closeInventory();
        }
    }
}
