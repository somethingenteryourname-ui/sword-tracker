package net.mcdev.trackersword.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.mcdev.trackersword.TrackerSwordPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class PlayerTrackerGUI {

    /** Slot used for the "stop tracking" button. */
    public static final int CLEAR_SLOT = 53;

    private PlayerTrackerGUI() {
    }

    public static void open(TrackerSwordPlugin plugin, Player viewer) {
        List<? extends Player> targets = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getUniqueId().equals(viewer.getUniqueId()))
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();

        int rows = Math.max(1, Math.min(6, (targets.size() / 9) + 1));
        int size = rows * 9;
        // Always leave the last row for the clear button when there's room; otherwise expand.
        if (targets.size() >= size - 9 && size < 54) {
            size += 9;
            rows += 1;
        }

        TrackerGUIHolder holder = new TrackerGUIHolder();
        Inventory gui = Bukkit.createInventory(holder, size,
                Component.text("Select a Player to Track", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
        holder.setInventory(gui);

        int slot = 0;
        for (Player target : targets) {
            if (slot >= size - 9) {
                break; // reserve the last row
            }
            gui.setItem(slot++, buildHead(target));
        }

        ItemStack clear = new ItemStack(Material.BARRIER);
        var meta = clear.getItemMeta();
        meta.displayName(Component.text("Stop Tracking", NamedTextColor.RED, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        clear.setItemMeta(meta);
        gui.setItem(size - 1, clear);

        viewer.openInventory(gui);
    }

    private static ItemStack buildHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(Component.text(target.getName(), NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Click to track this player", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        head.setItemMeta(meta);
        return head;
    }
}
