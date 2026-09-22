package net.mcdev.trackersword.task;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcdev.trackersword.TrackerSwordItem;
import net.mcdev.trackersword.TrackerSwordPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TrackingTask extends BukkitRunnable {

    // 8-direction arrows, index 0 = straight ahead, going clockwise.
    private static final String[] ARROWS = {"\u2191", "\u2197", "\u2192", "\u2198", "\u2193", "\u2199", "\u2190", "\u2196"};

    private final TrackerSwordPlugin plugin;

    public TrackingTask(TrackerSwordPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Map<UUID, UUID> tracking = plugin.getTrackingMap();
        if (tracking.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, UUID>> iterator = tracking.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, UUID> entry = iterator.next();
            Player tracker = Bukkit.getPlayer(entry.getKey());
            if (tracker == null || !tracker.isOnline()) {
                iterator.remove();
                continue;
            }

            if (plugin.isRequireHoldingSword()
                    && !TrackerSwordItem.isTrackerSword(plugin, tracker.getInventory().getItemInMainHand())) {
                continue; // stay subscribed, just don't show anything while the sword isn't held
            }

            Player target = Bukkit.getPlayer(entry.getValue());
            if (target == null || !target.isOnline()) {
                tracker.sendActionBar(Component.text("Tracked player is offline", NamedTextColor.DARK_GRAY));
                continue;
            }

            Location from = tracker.getLocation();
            Location to = target.getLocation();

            if (from.getWorld() == null || !from.getWorld().equals(to.getWorld())) {
                tracker.sendActionBar(Component.text(target.getName() + " is in another world", NamedTextColor.DARK_GRAY));
                continue;
            }

            double distance = from.distance(to);
            String arrow = directionArrow(from, to);

            tracker.sendActionBar(Component.text(arrow + " ", NamedTextColor.AQUA)
                    .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("  " + Math.round(distance) + "m", NamedTextColor.GRAY)));
        }
    }

    private static String directionArrow(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        // Bearing to target in Minecraft's yaw convention (0 = south, 90 = west, -90 = east, 180 = north).
        double bearing = Math.toDegrees(Math.atan2(-dx, dz));
        double relative = normalizeAngle(bearing - from.getYaw());

        int index = (int) Math.round(relative / 45.0) & 7;
        return ARROWS[index];
    }

    private static double normalizeAngle(double angle) {
        angle %= 360.0;
        if (angle < -180.0) angle += 360.0;
        if (angle >= 180.0) angle -= 360.0;
        return angle;
    }
}
