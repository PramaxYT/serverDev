/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package me.maker56.survivalgames.sign;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SignManager {
    private String[] design = new String[4];
    private String[] leaveDesign = new String[4];
    private HashMap<Location, String> signs = new HashMap();
    private HashMap<GameState, String> translations = new HashMap();
    private boolean arena;
    private boolean playersleft;

    public SignManager() {
        this.reload();
    }

    public void reload() {
        FileConfiguration c = SurvivalGames.signs;
        int i = 1;
        while (i <= 4) {
            this.design[i - 1] = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("Sign.Line." + i));
            ++i;
        }
        this.arena = c.getBoolean("Sign.LeftClick.Show current arena");
        this.playersleft = c.getBoolean("Sign.LeftClick.Show players remain");
        this.leaveDesign[0] = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("Sign.LeavePrefix"));
        i = 2;
        while (i <= 4) {
            this.leaveDesign[i - 1] = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("Sign.Leave.Line." + i));
            ++i;
        }
        for (String key : c.getConfigurationSection("Translations.").getKeys(false)) {
            this.translations.put(GameState.valueOf(key), ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("Translations." + key)));
        }
        List s = c.getStringList("Sign.List");
        int a = 0;
        for (String key2 : s) {
            String[] split = key2.split(":");
            Location loc = ConfigUtil.parseLocation(split[0]);
            if (loc != null) {
                this.signs.put(loc, split[1]);
            }
            ++a;
        }
        System.out.println("[SurvivalGames] " + a + " signs loaded!");
    }

    public void addSign(Player p, final Location loc, final String lobby) {
        if (!this.signs.containsKey((Object)loc)) {
            List signs = SurvivalGames.signs.getStringList("Sign.List");
            signs.add(String.valueOf(ConfigUtil.serializeLocation(loc, false)) + ":" + lobby);
            SurvivalGames.signs.set("Sign.List", (Object)signs);
            SurvivalGames.saveSigns();
            this.signs.put(loc, lobby);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)SurvivalGames.instance, new Runnable(){

                @Override
                public void run() {
                    SignManager.this.updateSign(loc, lobby);
                }
            }, 1);
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've created the join sign successfully!");
        }
    }

    public void removeSign(Player p, Location loc) {
        if (this.signs.containsKey((Object)loc)) {
            String lobby = this.signs.get((Object)loc);
            this.signs.remove((Object)loc);
            List signs = SurvivalGames.signs.getStringList("Sign.List");
            signs.remove(String.valueOf(ConfigUtil.serializeLocation(loc, false)) + ":" + lobby);
            SurvivalGames.signs.set("Sign.List", (Object)signs);
            SurvivalGames.saveSigns();
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've removed the join sign successfully!");
        }
    }

    public String[] getLeaveSignDesign() {
        return this.leaveDesign;
    }

    public String getLobby(Location loc) {
        if (this.signs.containsKey((Object)loc)) {
            return this.signs.get((Object)loc);
        }
        return null;
    }

    public void sendInfo(CommandSender sender, String lobby) {
        Game g = SurvivalGames.gameManager.getGame(lobby);
        if (g != null) {
            sender.sendMessage(MessageHandler.getMessage("game-sign-info").replace("%0%", lobby));
            if (g.getState() == GameState.INGAME || g.getState() == GameState.DEATHMATCH || g.getState() == GameState.COOLDOWN) {
                if (this.arena) {
                    sender.sendMessage(MessageHandler.getMessage("game-sign-arena").replace("%0%", g.getCurrentArena().getName()));
                }
                if (this.playersleft) {
                    String s = g.getAlivePlayers();
                    sender.sendMessage(MessageHandler.getMessage("game-sign-playersleft").replace("%1%", s).replace("%0%", Integer.valueOf(g.getPlayingUsers()).toString()));
                }
            } else {
                sender.sendMessage(MessageHandler.getMessage("game-sign-noinfo"));
            }
        } else {
            sender.sendMessage(MessageHandler.getMessage("join-unknown-game").replace("%0%", lobby));
        }
    }

    public void updateSigns() {
        for (Map.Entry<Location, String> s : this.signs.entrySet()) {
            Location loc = s.getKey();
            if (loc == null || loc.getWorld() == null) continue;
            this.updateSign(loc, s.getValue());
        }
    }

    public void updateSign(Location loc, String lobby) {
        Block b = loc.getBlock();
        if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
            Sign s = (Sign)b.getState();
            Game g = SurvivalGames.gameManager.getGame(lobby);
            if (g != null) {
                String state = this.translations.get((Object)g.getState());
                int i = 0;
                while (i < 4) {
                    s.setLine(i, this.design[i].replace("%name%", g.getName()).replace("%state%", state).replace("%currentplayers%", Integer.valueOf(g.getPlayingUsers()).toString()).replace("%requiredplayers%", Integer.valueOf(g.getRequiredPlayers()).toString()).replace("%maxplayers%", Integer.valueOf(g.getMaximumPlayers()).toString()));
                    ++i;
                }
                s.update();
            } else {
                if (SurvivalGames.database.contains("Games." + lobby)) {
                    s.setLine(1, "\u00a74Game not");
                    s.setLine(2, "\u00a74loaded!");
                } else {
                    s.setLine(1, "\u00a74Game not");
                    s.setLine(2, "\u00a74found!");
                }
                s.update();
            }
        }
    }

}

