/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.IllegalPluginAccessException
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scoreboard.Scoreboard
 */
package me.maker56.survivalgames.user;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

public class UserManager {
    private GameManager gm = SurvivalGames.gameManager;

    public void leaveGame(SpectatorUser su) {
        Game g = su.getGame();
        for (User u : g.getUsers()) {
            u.getPlayer().showPlayer(su.getPlayer());
        }
        g.leaveSpectator(su);
        if (g.getScoreboardPhase() != null) {
            su.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        this.setState(su.getPlayer(), su);
    }

    public void joinGameAsSpectator(Player p, String gamename) {
        if (!SurvivalGames.instance.getConfig().getBoolean("Spectating.Enabled")) {
            p.sendMessage(MessageHandler.getMessage("spectator-disabled"));
            return;
        }
        if (!PermissionHandler.hasPermission((CommandSender)p, Permission.SPECTATE)) {
            p.sendMessage(MessageHandler.getMessage("no-permission"));
            return;
        }
        if (this.isSpectator(p.getName()) || this.isPlaying(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("join-already-playing"));
            return;
        }
        if (p.getVehicle() != null) {
            p.sendMessage(MessageHandler.getMessage("join-vehicle"));
            return;
        }
        Game g = this.gm.getGame(gamename);
        if (g == null) {
            p.sendMessage(MessageHandler.getMessage("join-unknown-game").replace("%0%", gamename));
            return;
        }
        int max = SurvivalGames.instance.getConfig().getInt("Spectating.Max-Spectators-Per-Arena", 8);
        if (g.getSpecators().size() >= max) {
            p.sendMessage(MessageHandler.getMessage("spectator-full").replace("%0%", Integer.valueOf(max).toString()));
            return;
        }
        GameState state = g.getState();
        if (state == GameState.VOTING || state == GameState.WAITING || state == GameState.COOLDOWN) {
            p.sendMessage(MessageHandler.getMessage("spectator-game-running"));
            return;
        }
        g.joinSpectator(new SpectatorUser(p, g));
    }

    public void joinGame(Player p, String gamename) {
        if (!PermissionHandler.hasPermission((CommandSender)p, Permission.JOIN)) {
            p.sendMessage(MessageHandler.getMessage("no-permission"));
            return;
        }
        if (this.isPlaying(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("join-already-playing"));
            return;
        }
        if (p.getVehicle() != null) {
            p.sendMessage(MessageHandler.getMessage("join-vehicle"));
            return;
        }
        Game g = this.gm.getGame(gamename);
        if (g == null) {
            p.sendMessage(MessageHandler.getMessage("join-unknown-game").replace("%0%", gamename));
            return;
        }
        GameState state = g.getState();
        if (state != GameState.VOTING && state != GameState.WAITING && state != GameState.COOLDOWN) {
            if (SurvivalGames.instance.getConfig().getBoolean("Spectating.Enabled")) {
                this.joinGameAsSpectator(p, gamename);
            } else {
                p.sendMessage(MessageHandler.getMessage("join-game-running"));
            }
            return;
        }
        if (g.getUsers().size() >= g.getMaximumPlayers()) {
            User kick = PermissionHandler.canJoin(p, g);
            if (kick != null) {
                kick.sendMessage(MessageHandler.getMessage("fulljoin-kick"));
                this.leaveGame(kick.getPlayer());
            } else {
                p.sendMessage(MessageHandler.getMessage("join-game-full"));
                return;
            }
        }
        User user = new User(p, g);
        g.join(user);
    }

    public void leaveGame(final Player p) {
        if (!this.isPlaying(p.getName())) {
            SpectatorUser su = this.getSpectator(p.getName());
            if (su != null) {
                this.leaveGame(su);
                return;
            }
            p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
            return;
        }
        final User user = this.getUser(p.getName());
        user.clear();
        Game game = user.getGame();
        for (SpectatorUser su : game.getSpecators()) {
            p.showPlayer(su.getPlayer());
        }
        if (game.getState() == GameState.WAITING || game.getState() == GameState.VOTING || game.getState() == GameState.COOLDOWN) {
            game.sendMessage(MessageHandler.getMessage("game-leave").replace("%0%", p.getName()).replace("%1%", Integer.valueOf(game.getPlayingUsers() - 1).toString()).replace("%2%", Integer.valueOf(game.getMaximumPlayers()).toString()));
        }
        game.leave(user);
        if (game.getScoreboardPhase() != null) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        if (p.isDead()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)SurvivalGames.instance, new Runnable(){

                @Override
                public void run() {
                    UserManager.this.setState(p, user);
                }
            }, 1);
        } else {
            this.setState(p, user);
        }
    }

    public void setState(Player p, UserState state) {
        p.teleport(state.getLocation());
        p.setFallDistance(state.getFallDistance());
        p.setGameMode(state.getGameMode());
        p.setAllowFlight(state.getAllowFlight());
        p.setFlying(state.isFlying());
        p.setLevel(state.getLevel());
        p.setExp(state.getExp());
        p.setHealth(state.getHealth());
        p.setFoodLevel(state.getFoodLevel());
        p.setWalkSpeed(state.getWalkSpeed());
        p.setFlySpeed(state.getFlySpeed());
        Iterator i = p.getActivePotionEffects().iterator();
        while (i.hasNext()) {
            p.removePotionEffect(((PotionEffect)i.next()).getType());
        }
        p.addPotionEffects(state.getActivePotionEffects());
        final String name = p.getName();
        final ItemStack[] contents = state.getContents();
        final ItemStack[] armorcontents = state.getArmorContents();
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)SurvivalGames.instance, new Runnable(){

                @Override
                public void run() {
                    Player fp = Bukkit.getPlayer((String)name);
                    if (fp != null) {
                        fp.getInventory().setContents(contents);
                        fp.getInventory().setArmorContents(armorcontents);
                        fp.updateInventory();
                    }
                }
            }, 2);
        }
        catch (IllegalPluginAccessException e) {
            p.getInventory().setContents(contents);
            p.getInventory().setArmorContents(armorcontents);
            p.updateInventory();
        }
    }

    public boolean isSpectator(String name) {
        if (this.getSpectator(name) != null) {
            return true;
        }
        return false;
    }

    public SpectatorUser getSpectator(String name) {
        for (Game game : this.gm.getGames()) {
            for (SpectatorUser su : game.getSpecators()) {
                if (!su.getName().equals(name)) continue;
                return su;
            }
        }
        return null;
    }

    public boolean isPlaying(String name) {
        if (this.getUser(name) != null) {
            return true;
        }
        return false;
    }

    public User getUser(String name) {
        for (Game game : this.gm.getGames()) {
            User u = game.getUser(name);
            if (u == null) continue;
            return u;
        }
        return null;
    }

}

