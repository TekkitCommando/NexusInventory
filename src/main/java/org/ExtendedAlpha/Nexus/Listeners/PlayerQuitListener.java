/**
 * NexusInventory is a multi-world inventory plugin.
 * Copyright (C) 2014 - 2015 Gnat008
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ExtendedAlpha.Nexus.Listeners;

import org.ExtendedAlpha.Nexus.Serialization.PlayerSerialization;
import org.ExtendedAlpha.Nexus.NexusInventory;
import org.ExtendedAlpha.Nexus.Config.defaults.ConfigValues;
import org.ExtendedAlpha.Nexus.Groups.Group;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private NexusInventory plugin;

    public PlayerQuitListener(NexusInventory plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String logoutWorld = player.getWorld().getName();
        Group group = plugin.getGroupManager().getGroupFromWorld(logoutWorld);
        if (group == null) {
            group = new Group(logoutWorld, null, null);
        }

        if (ConfigValues.SEPARATE_GAMEMODE_INVENTORIES.getBoolean()) {
            plugin.getSerializer().writePlayerDataToFile(player,
                    PlayerSerialization.serializePlayer(player, plugin),
                    group,
                    player.getGameMode());
        } else {
            plugin.getSerializer().writePlayerDataToFile(player,
                    PlayerSerialization.serializePlayer(player, plugin),
                    group,
                    GameMode.SURVIVAL);
        }
    }
}
