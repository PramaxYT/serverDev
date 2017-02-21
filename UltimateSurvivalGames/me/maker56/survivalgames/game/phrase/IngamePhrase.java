/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  net.milkbowl.vault.economy.EconomyResponse
 *  org.bukkit.Bukkit
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.LightningStrike
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package me.maker56.survivalgames.game.phrase;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.DeathmatchPhrase;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class IngamePhrase {
    private Game game;
    public BukkitTask task;
    private boolean running;
    private int time;
    private UserManager um = SurvivalGames.userManger;
    private boolean braodcastWin = SurvivalGames.instance.getConfig().getBoolean("broadcast-win");
    private boolean lightningOD = SurvivalGames.instance.getConfig().getBoolean("Lightning.on-death");
    private boolean lightningFP = SurvivalGames.instance.getConfig().getBoolean("Lightning.on-few-players");
    private int lightningFPc = SurvivalGames.instance.getConfig().getInt("Lightning.few-players");
    private int lightningFPt = SurvivalGames.instance.getConfig().getInt("Lightning.few-players-time");
    private BukkitTask lTask;
    public boolean grace = false;
    private int period;
    private BukkitTask deathmatch;
    private BukkitTask chestrefill;
    private BukkitTask gracetask;

    public IngamePhrase(Game game) {
        this.game = game;
        this.period = game.getCurrentArena().getGracePeriod();
        this.time = game.getCurrentArena().getAutomaticlyDeathmatchTime();
    }

    public void load() {
        this.game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.INGAME));
        this.start();
    }

    public void start() {
        this.game.setState(GameState.INGAME);
        this.game.sendMessage(MessageHandler.getMessage("game-start").replace("%0%", Integer.valueOf(this.game.getPlayingUsers()).toString()));
        this.running = true;
        this.game.redefinePlayerNavigatorInventory();
        this.game.getCurrentArena().getMinimumLocation().getWorld().setTime(0);
        if (this.game.getCurrentArena().chestRefill()) {
            this.chestrefill = Bukkit.getScheduler().runTaskLater((Plugin)SurvivalGames.instance, new Runnable(){

                @Override
                public void run() {
                    long time = IngamePhrase.this.game.getCurrentArena().getMinimumLocation().getWorld().getTime();
                    if (time >= 18000 && time <= 18200) {
                        for (Chest c : IngamePhrase.this.game.getRegisteredChests()) {
                            c.getLocation().getWorld().playEffect(c.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                            c.getLocation().getWorld().playSound(c.getLocation(), Sound.LEVEL_UP, 4.0f, 1.0f);
                        }
                        IngamePhrase.this.game.getRegisteredChests().clear();
                        IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-chestrefill"));
                    }
                }
            }, 18001);
        }
        if (this.period != 0) {
            this.game.sendMessage(MessageHandler.getMessage("game-grace-period").replace("%0%", Integer.valueOf(this.period).toString()));
            this.grace = true;
            this.gracetask = Bukkit.getScheduler().runTaskLater((Plugin)SurvivalGames.instance, new Runnable(){

                @Override
                public void run() {
                    IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-grace-period-ended"));
                    IngamePhrase.this.grace = false;
                    IngamePhrase.this.startTask();
                }
            }, (long)(this.period * 20));
        } else {
            this.startTask();
        }
    }

    private void startTask() {
        if (this.lightningFP) {
            this.startLightningTask();
        }
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                if (IngamePhrase.this.game.getCurrentArena().isDeathmatchEnabled()) {
                    if (IngamePhrase.this.time % 600 == 0 && IngamePhrase.this.time != 0) {
                        IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-minutes").replace("%0%", Integer.valueOf(IngamePhrase.this.time / 60).toString()));
                    } else if (IngamePhrase.this.time < 301 && IngamePhrase.this.time % 300 == 0 && IngamePhrase.this.time != 0) {
                        IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-minutes").replace("%0%", Integer.valueOf(IngamePhrase.this.time / 60).toString()));
                    } else if (IngamePhrase.this.time < 60 && IngamePhrase.this.time % 10 == 0 && IngamePhrase.this.time > 10) {
                        IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-seconds").replace("%0%", Integer.valueOf(IngamePhrase.this.time).toString()));
                    } else if (IngamePhrase.this.time <= 10 && IngamePhrase.this.time > 0) {
                        IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-little").replace("%0%", Integer.valueOf(IngamePhrase.this.time).toString()));
                    } else if (IngamePhrase.this.time == 0) {
                        IngamePhrase.this.cancelTask();
                        IngamePhrase.this.cancelLightningTask();
                        IngamePhrase.this.game.startDeathmatch();
                        return;
                    }
                }
                IngamePhrase.this.game.updateScoreboard();
                IngamePhrase ingamePhrase = IngamePhrase.this;
                IngamePhrase.access$3(ingamePhrase, ingamePhrase.time - 1);
            }
        }, 0, 20);
    }

    public void killUser(User user, User killer, boolean leave) {
        int remain = this.game.getUsers().size() - 1;
        if (leave) {
            this.game.sendMessage(MessageHandler.getMessage("game-player-left").replace("%0%", user.getName()));
        } else if (killer == null) {
            this.game.sendMessage(MessageHandler.getMessage("game-player-die-damage").replace("%0%", user.getName()));
        } else {
            this.game.sendMessage(MessageHandler.getMessage("game-player-die-killer").replace("%0%", user.getName()).replace("%1%", killer.getName()));
            double killMoney = this.game.getCurrentArena().getMoneyOnKill();
            if (killMoney > 0.0 && SurvivalGames.econ != null) {
                SurvivalGames.econ.depositPlayer(killer.getName(), killMoney);
                killer.sendMessage(MessageHandler.getMessage("arena-money-kill").replace("%0%", Double.valueOf(killMoney).toString()).replace("%1%", user.getName()));
            }
        }
        this.game.sendMessage(MessageHandler.getMessage("game-remainplayers").replace("%0%", Integer.valueOf(remain).toString()));
        if (this.lightningOD) {
            user.getPlayer().getWorld().strikeLightningEffect(user.getPlayer().getLocation());
        }
        ItemStack[] arritemStack = user.getPlayer().getInventory().getContents();
        int n = arritemStack.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack is = arritemStack[n2];
            if (is != null && is.getType() != Material.AIR) {
                user.getPlayer().getWorld().dropItemNaturally(user.getPlayer().getLocation(), is);
            }
            ++n2;
        }
        arritemStack = user.getPlayer().getInventory().getArmorContents();
        n = arritemStack.length;
        n2 = 0;
        while (n2 < n) {
            ItemStack is = arritemStack[n2];
            if (is != null && is.getType() != Material.AIR) {
                user.getPlayer().getWorld().dropItemNaturally(user.getPlayer().getLocation(), is);
            }
            ++n2;
        }
        final Player p = user.getPlayer();
        this.um.leaveGame(p);
        this.game.setDeathAmount(this.game.getDeathAmount() + 1);
        this.game.updateScoreboard();
        if (remain == 1) {
            User winner = this.game.getUsers().get(0);
            if (this.braodcastWin) {
                Bukkit.broadcastMessage((String)MessageHandler.getMessage("game-win").replace("%0%", winner.getName()).replace("%1%", this.game.getCurrentArena().getName()).replace("%2%", this.game.getName()));
            }
            winner.sendMessage(MessageHandler.getMessage("game-win-winner-message").replace("%0%", this.game.getCurrentArena().getName()));
            double winMoney = this.game.getCurrentArena().getMoneyOnWin();
            if (winMoney > 0.0 && SurvivalGames.econ != null) {
                SurvivalGames.econ.depositPlayer(winner.getName(), winMoney);
                winner.sendMessage(MessageHandler.getMessage("arena-money-win").replace("%0%", Double.valueOf(winMoney).toString()));
            }
            this.um.leaveGame(winner.getPlayer());
            this.game.end();
        } else {
            if (PermissionHandler.hasPermission((CommandSender)p, Permission.SPECTATE)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)SurvivalGames.instance, new Runnable(){

                    @Override
                    public void run() {
                        IngamePhrase.this.um.joinGameAsSpectator(p, IngamePhrase.this.game.getName());
                    }
                }, 2);
            }
            if (remain == this.game.getCurrentArena().getPlayerDeathmatchAmount() && this.game.getCurrentArena().isDeathmatchEnabled()) {
                this.startDeathmatchTask();
            }
        }
    }

    public void startDeathmatchTask() {
        if (this.game.getDeathmatch() != null) {
            return;
        }
        this.cancelTask();
        this.deathmatch = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){
            int time;

            @Override
            public void run() {
                if (this.time % 10 == 0 && this.time > 10) {
                    IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-seconds").replace("%0%", Integer.valueOf(this.time).toString()));
                } else if (this.time <= 10 && this.time > 0) {
                    IngamePhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-little").replace("%0%", Integer.valueOf(this.time).toString()));
                } else if (this.time == 0) {
                    IngamePhrase.this.cancelDeathmatchTask();
                    IngamePhrase.this.cancelLightningTask();
                    IngamePhrase.this.game.startDeathmatch();
                    return;
                }
                --this.time;
            }
        }, 0, 20);
    }

    public int getTime() {
        return this.time;
    }

    public void startLightningTask() {
        this.lTask = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                if (IngamePhrase.this.game.getPlayingUsers() <= IngamePhrase.this.lightningFPc && !IngamePhrase.this.game.getCurrentArena().isDeathmatchEnabled()) {
                    for (User user : IngamePhrase.this.game.getUsers()) {
                        user.getPlayer().getWorld().strikeLightningEffect(user.getPlayer().getLocation());
                    }
                }
            }
        }, (long)this.lightningFPt * 20, (long)this.lightningFPt * 20);
    }

    public void cancelLightningTask() {
        if (this.lTask != null) {
            this.lTask.cancel();
        }
    }

    public void cancelTask() {
        if (this.task != null) {
            this.task.cancel();
        }
        if (this.chestrefill != null) {
            this.chestrefill.cancel();
        }
        if (this.gracetask != null) {
            this.gracetask.cancel();
        }
    }

    public void cancelDeathmatchTask() {
        if (this.deathmatch != null) {
            this.deathmatch.cancel();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    static /* synthetic */ void access$3(IngamePhrase ingamePhrase, int n) {
        ingamePhrase.time = n;
    }

}

