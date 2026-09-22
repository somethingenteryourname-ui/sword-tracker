package net.mcdev.trackersword;

import net.mcdev.trackersword.command.TrackerSwordCommand;
import net.mcdev.trackersword.gui.GUIListener;
import net.mcdev.trackersword.task.TrackingTask;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TrackerSwordPlugin extends JavaPlugin {

    private NamespacedKey swordKey;

    // tracker UUID -> target UUID
    private final Map<UUID, UUID> trackingMap = new HashMap<>();
    private CooldownManager cooldownManager;

    // config values, loaded in onEnable / reloadSettings
    private int buffDurationTicks;
    private long buffCooldownMillis;
    private int strengthAmplifier;
    private int resistanceAmplifier;
    private int regenerationAmplifier;
    private long trackingIntervalTicks;
    private boolean requireHoldingSword;

    @Override
    public void onEnable() {
        this.swordKey = new NamespacedKey(this, "tracker_sword");

        saveDefaultConfig();
        reloadSettings();

        this.cooldownManager = new CooldownManager();

        getServer().getPluginManager().registerEvents(new SwordListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        TrackerSwordCommand commandExecutor = new TrackerSwordCommand(this);
        getCommand("trackersword").setExecutor(commandExecutor);
        getCommand("trackersword").setTabCompleter(commandExecutor);

        new TrackingTask(this).runTaskTimer(this, 0L, trackingIntervalTicks);

        getLogger().info("TrackerSword enabled: buff " + (buffDurationTicks / 20)
                + "s / cooldown " + (buffCooldownMillis / 1000) + "s.");
    }

    @Override
    public void onDisable() {
        trackingMap.clear();
    }

    /** Re-reads config.yml into the fields used by the rest of the plugin. */
    public void reloadSettings() {
        reloadConfig();
        this.buffDurationTicks = getConfig().getInt("buff-ability.duration-seconds", 10) * 20;
        this.buffCooldownMillis = getConfig().getInt("buff-ability.cooldown-seconds", 90) * 1000L;
        this.strengthAmplifier = Math.max(0, getConfig().getInt("buff-ability.strength-level", 3) - 1);
        this.resistanceAmplifier = Math.max(0, getConfig().getInt("buff-ability.resistance-level", 2) - 1);
        this.regenerationAmplifier = Math.max(0, getConfig().getInt("buff-ability.regeneration-level", 2) - 1);
        this.trackingIntervalTicks = getConfig().getLong("tracking-ability.update-interval-ticks", 10);
        this.requireHoldingSword = getConfig().getBoolean("tracking-ability.require-holding-sword", true);
    }

    public NamespacedKey getSwordKey() {
        return swordKey;
    }

    public Map<UUID, UUID> getTrackingMap() {
        return trackingMap;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public int getBuffDurationTicks() {
        return buffDurationTicks;
    }

    public long getBuffCooldownMillis() {
        return buffCooldownMillis;
    }

    public int getStrengthAmplifier() {
        return strengthAmplifier;
    }

    public int getResistanceAmplifier() {
        return resistanceAmplifier;
    }

    public int getRegenerationAmplifier() {
        return regenerationAmplifier;
    }

    public boolean isRequireHoldingSword() {
        return requireHoldingSword;
    }
}
