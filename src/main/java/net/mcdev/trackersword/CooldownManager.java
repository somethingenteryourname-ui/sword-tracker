package net.mcdev.trackersword;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player cooldown expiry timestamps (System.currentTimeMillis()-based)
 * for the sword's buff ability.
 */
public class CooldownManager {

    private final Map<UUID, Long> cooldownExpiry = new HashMap<>();

    public boolean isOnCooldown(UUID playerId) {
        Long expiry = cooldownExpiry.get(playerId);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    /** Milliseconds remaining, or 0 if not on cooldown. */
    public long getRemainingMillis(UUID playerId) {
        Long expiry = cooldownExpiry.get(playerId);
        if (expiry == null) {
            return 0L;
        }
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(remaining, 0L);
    }

    public void startCooldown(UUID playerId, long durationMillis) {
        cooldownExpiry.put(playerId, System.currentTimeMillis() + durationMillis);
    }

    public void clear(UUID playerId) {
        cooldownExpiry.remove(playerId);
    }

    /** Formats remaining time as m:ss, e.g. "1:30" or "0:07". */
    public static String formatMillis(long millis) {
        long totalSeconds = (millis + 999) / 1000; // round up so it doesn't show 0:00 while still blocked
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}
