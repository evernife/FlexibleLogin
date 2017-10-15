/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 games647 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.games647.flexiblelogin;

import com.github.games647.flexiblelogin.config.SpawnTeleportConfig;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ProtectionManager {

    private final Map<UUID, Location<World>> oldLocations = Maps.newHashMap();
    private final FlexibleLogin plugin;

    public ProtectionManager(FlexibleLogin plugin) {
        this.plugin = plugin;
    }

    public void protect(Player player) {
        SpawnTeleportConfig teleportConfig = plugin.getConfigManager().getGeneral().getTeleport();
        if (teleportConfig.isEnabled()) {
            Optional<Location<World>> spawnLocation = teleportConfig.getSpawnLocation();
            spawnLocation.ifPresent(worldLocation -> {
                oldLocations.put(player.getUniqueId(), player.getLocation());
                safeTeleport(player, worldLocation);
            });
        } else {
            Location<World> oldLoc = player.getLocation();

            //sometimes players stuck in a wall
            safeTeleport(player, oldLoc);
        }
    }

    public void unprotect(Player player) {
        Location<World> oldLocation = oldLocations.remove(player.getUniqueId());
        if (oldLocation == null) {
            return;
        }

        safeTeleport(player, oldLocation);
    }

    private void safeTeleport(Player player, Location<World> location) {
        if (plugin.getConfigManager().getGeneral().isSafeLocation()) {
            Sponge.getTeleportHelper().getSafeLocation(location).ifPresent(player::setLocation);
        } else {
            player.setLocation(location);
        }
    }
}
