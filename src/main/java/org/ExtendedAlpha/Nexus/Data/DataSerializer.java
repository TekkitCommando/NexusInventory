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

package org.ExtendedAlpha.Nexus.Data;

import org.ExtendedAlpha.Nexus.Groups.Group;
import org.ExtendedAlpha.Nexus.NexusInventory;
import org.ExtendedAlpha.Nexus.TacoSerialization.PlayerSerialization;
import org.ExtendedAlpha.Nexus.TacoSerialization.Serializer;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class DataSerializer {

    private NexusInventory plugin;

    private final String FILE_PATH;

    private static DataSerializer instance = null;

    private DataSerializer(NexusInventory plugin) {
        this.plugin = plugin;
        FILE_PATH = plugin.getDataFolder() + File.separator + "data" + File.separator;
    }

    public static DataSerializer getInstance(NexusInventory plugin) {
        if (instance == null) {
            instance = new DataSerializer(plugin);
        }

        return instance;
    }

    public static void disable() {
        instance = null;
    }

    public void writePlayerDataToFile(OfflinePlayer player, JSONObject data, Group group, GameMode gamemode) {
        File file;
        switch (gamemode) {
            case ADVENTURE:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_adventure.json");
                break;
            case CREATIVE:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_creative.json");
                break;
            case SPECTATOR:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_creative.json");
                break;
            default:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + ".json");
                break;
        }

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            writeData(file, Serializer.toString(data));
        } catch (IOException ex) {
            NexusInventory.log.warning("Error creating file '" + FILE_PATH +
                    player.getUniqueId().toString() + File.separator + group.getName() + ".json': " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void writeData(final File file, final String data) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    writer.write(data);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void getPlayerDataFromFile(Player player, Group group, GameMode gamemode) {
        File file;
        switch(gamemode) {
            case ADVENTURE:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_adventure.json");
                break;
            case CREATIVE:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_creative.json");
                break;
            case SPECTATOR:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + "_creative.json");
                break;
            default:
                file = new File(FILE_PATH + player.getUniqueId().toString(), group.getName() + ".json");
                break;
        }

        try {
            JSONObject data = Serializer.getObjectFromFile(file);
            PlayerSerialization.setPlayer(data, player, plugin);
        } catch (FileNotFoundException | JSONException ex) {
            try {
                file.createNewFile();
                JSONObject defaultGroupData = Serializer.getObjectFromFile(
                        new File(FILE_PATH + "simpleplayerdata" + File.separator + group.getName() + ".json"));
                PlayerSerialization.setPlayer(defaultGroupData, player, plugin);
            } catch (FileNotFoundException ex2) {
                try {
                    JSONObject defaultData = Serializer.getObjectFromFile(
                            new File(FILE_PATH + "simpleplayerdata" + File.separator + "simpleplayerdata.json"));
                    PlayerSerialization.setPlayer(defaultData, player, plugin);
                } catch (FileNotFoundException ex3) {
                    plugin.getPlayerMessenger().sendMessage(player, "Something went horribly wrong when loading your inventory! " +
                            "Please notify a server administrator!");
                    NexusInventory.log.info("Unable to find inventory data for player '" + player.getName() +
                            "' for group '" + group.getName() + "': " + ex3.getMessage());
                }
            } catch (IOException exIO) {
                NexusInventory.log.info("Error creating file '" + FILE_PATH +
                        player.getUniqueId().toString() + File.separator + group.getName() + ".json': " + ex.getMessage());
            }
        }
    }
}
