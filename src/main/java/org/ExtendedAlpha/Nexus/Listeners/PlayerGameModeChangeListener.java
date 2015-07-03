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
import org.ExtendedAlpha.Nexus.Groups.Group;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeListener implements Listener {

    private NexusInventory plugin;

    public PlayerGameModeChangeListener(NexusInventory plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode oldGameMode = player.getGameMode();
        GameMode newGameMode = event.getNewGameMode();
        Group group = plugin.getGroupManager().getGroupFromWorld(player.getWorld().getName());
        if (group == null) {
            group = new Group(player.getWorld().getName(), null, null);
        }

        plugin.getSerializer().writePlayerDataToFile(player,
                PlayerSerialization.serializePlayer(player, plugin),
                group,
                oldGameMode);

        plugin.getSerializer().getPlayerDataFromFile(
                player,
                group,
                newGameMode);
    }
}
