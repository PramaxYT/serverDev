/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package me.maker56.survivalgames.game.phrase;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CooldownPhrase {
    private Game game;
    private BukkitTask task;
    private boolean running;
    private int time;
    private Arena arena;

    public CooldownPhrase(Game game, Arena arena) {
        this.game = game;
        this.arena = arena;
    }

    public void load() {
        this.game.setCurrentArena(this.arena);
        this.time = this.game.getCooldownTime();
        this.game.setState(GameState.COOLDOWN);
        this.game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.COOLDOWN));
        this.game.updateScoreboard();
        this.start();
    }

    public void start() {
        this.running = true;
        if (this.game.getArenas().size() > 1) {
            int i = 0;
            while (i < this.game.getUsers().size()) {
                User user = this.game.getUsers().get(i);
                user.setSpawnIndex(i);
                user.getPlayer().teleport(this.arena.getSpawns().get(i));
                for (User ouser : this.game.getUsers()) {
                    user.getPlayer().showPlayer(ouser.getPlayer());
                }
                ++i;
            }
        }
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                for (User user2 : CooldownPhrase.this.game.getUsers()) {
                    user2.getPlayer().setLevel(CooldownPhrase.this.time);
                    user2.getPlayer().setExp(0.0f);
                }
                if (CooldownPhrase.this.time == 27) {
                    CooldownPhrase.this.game.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "MAPINFO \u00a77- \u00a7eName: \u00a7b" + CooldownPhrase.this.arena.getName());
                }
                if (CooldownPhrase.this.time % 5 == 0 && CooldownPhrase.this.time != 10 && CooldownPhrase.this.time != 5 && CooldownPhrase.this.time != 0) {
                    CooldownPhrase.this.game.sendMessage(MessageHandler.getMessage("game-cooldown-big").replace("%0%", Integer.valueOf(CooldownPhrase.this.time).toString()));
                } else if (CooldownPhrase.this.time <= 10 && CooldownPhrase.this.time > 0) {
                    CooldownPhrase.this.game.sendMessage(MessageHandler.getMessage("game-cooldown-little").replace("%0%", Integer.valueOf(CooldownPhrase.this.time).toString()));
                    if (CooldownPhrase.this.time <= 5) {
                        for (User user2 : CooldownPhrase.this.game.getUsers()) {
                            user2.getPlayer().playSound(user2.getPlayer().getLocation(), Sound.NOTE_STICKS, 8.0f, 1.0f);
                        }
                    }
                } else if (CooldownPhrase.this.time == 0) {
                    for (User user2 : CooldownPhrase.this.game.getUsers()) {
                        user2.getPlayer().playSound(user2.getPlayer().getLocation(), Sound.NOTE_PLING, 8.0f, 1.0f);
                        user2.clearInventory();
                    }
                    CooldownPhrase.this.task.cancel();
                    CooldownPhrase.access$4(CooldownPhrase.this, false);
                    CooldownPhrase.access$5(CooldownPhrase.this, CooldownPhrase.this.game.getCooldownTime());
                    CooldownPhrase.this.game.startIngame();
                    return;
                }
                CooldownPhrase.this.game.updateScoreboard();
                CooldownPhrase cooldownPhrase = CooldownPhrase.this;
                CooldownPhrase.access$5(cooldownPhrase, cooldownPhrase.time - 1);
            }
        }, 0, 20);
    }

    public int getTime() {
        return this.time;
    }

    public void cancelTask() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.running = false;
        this.time = this.game.getCooldownTime();
    }

    public boolean isRunning() {
        return this.running;
    }

    static /* synthetic */ void access$4(CooldownPhrase cooldownPhrase, boolean bl) {
        cooldownPhrase.running = bl;
    }

    static /* synthetic */ void access$5(CooldownPhrase cooldownPhrase, int n) {
        cooldownPhrase.time = n;
    }

}

