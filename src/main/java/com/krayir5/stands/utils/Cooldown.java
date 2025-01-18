package com.krayir5.stands.utils;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    private final HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap<>();

    public void setCooldown(String standName, UUID playerId, long cooldownTime) {
        cooldowns.putIfAbsent(standName, new HashMap<>());
        cooldowns.get(standName).put(playerId, System.currentTimeMillis() + cooldownTime);
    }

    public boolean isOnCooldown(String standName, UUID playerId) {
        if (!cooldowns.containsKey(standName)) return false;

        HashMap<UUID, Long> standCooldowns = cooldowns.get(standName);
        if (!standCooldowns.containsKey(playerId)) return false;

        long remainingTime = standCooldowns.get(playerId) - System.currentTimeMillis();
        if (remainingTime <= 0) {
            standCooldowns.remove(playerId);
            return false;
        }
        return true;
    }

    public long getRemainingTime(String standName, UUID playerId) {
        if (!isOnCooldown(standName, playerId)) return 0;

        return (cooldowns.get(standName).get(playerId) - System.currentTimeMillis()) / 1000;
    }
}
