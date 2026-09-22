package net.mcdev.trackersword;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcdev.trackersword.gui.PlayerTrackerGUI;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwordListener implements Listener {

    private final TrackerSwordPlugin plugin;

    public SwordListener(TrackerSwordPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        // The event fires once per hand; only handle the main-hand copy to avoid double triggers.
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> { /* continue */ }
            default -> {
                return;
            }
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!TrackerSwordItem.isTrackerSword(plugin, item)) {
            return;
        }
        if (!player.hasPermission("trackersword.use")) {
            return;
        }

        // Prevent this click from also opening blocks (chests, doors, etc.) or placing blocks.
        event.setCancelled(true);
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);

        if (player.isSneaking()) {
            PlayerTrackerGUI.open(plugin, player);
        } else {
            triggerBuffAbility(player);
        }
    }

    private void triggerBuffAbility(Player player) {
        CooldownManager cooldowns = plugin.getCooldownManager();

        if (cooldowns.isOnCooldown(player.getUniqueId())) {
            long remaining = cooldowns.getRemainingMillis(player.getUniqueId());
            player.sendActionBar(Component.text(
                    "Tracker Blade on cooldown: " + CooldownManager.formatMillis(remaining),
                    NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.6f);
            return;
        }

        int duration = plugin.getBuffDurationTicks();
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, plugin.getStrengthAmplifier(), false, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, plugin.getResistanceAmplifier(), false, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, plugin.getRegenerationAmplifier(), false, true, true));

        cooldowns.startCooldown(player.getUniqueId(), plugin.getBuffCooldownMillis());

        player.sendActionBar(Component.text("Tracker Blade activated!", NamedTextColor.GREEN));
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.7f, 1.4f);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 40, 0.5, 0.8, 0.5, 0.05);
    }
}
