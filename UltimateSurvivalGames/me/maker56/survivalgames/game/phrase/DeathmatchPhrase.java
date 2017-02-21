/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package me.maker56.survivalgames.game.phrase;

import java.util.Collections;
import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class DeathmatchPhrase {
    private int time = 600;
    private BukkitTask task;
    private Game game;

    public DeathmatchPhrase(Game game) {
        this.game = game;
    }

    public void load() {
        this.game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.DEATHMATCH));
        this.start();
    }

    public void start() {
        this.game.setState(GameState.DEATHMATCH);
        List<Location> spawns = this.game.getCurrentArena().getDeathmatchSpawns();
        int i = 0;
        for (User user : this.game.getUsers()) {
            if (i >= spawns.size()) {
                i = 0;
            }
            user.getPlayer().teleport(spawns.get(i));
            ++i;
        }
        Location suloc = spawns.get(0);
        for (SpectatorUser su : this.game.getSpecators()) {
            su.getPlayer().teleport(suloc);
            Vector v = new Vector(0, 2, 0);
            v.multiply(1.25);
            su.getPlayer().getLocation().setDirection(v);
        }
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                if (DeathmatchPhrase.this.time == 60) {
                    DeathmatchPhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout-warning"));
                }
                if (DeathmatchPhrase.this.time % 60 == 0 && DeathmatchPhrase.this.time != 0) {
                    DeathmatchPhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(DeathmatchPhrase.this.time).toString()));
                } else if (DeathmatchPhrase.this.time % 10 == 0 && DeathmatchPhrase.this.time < 60 && DeathmatchPhrase.this.time > 10) {
                    DeathmatchPhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(DeathmatchPhrase.this.time).toString()));
                } else if (DeathmatchPhrase.this.time <= 10 && DeathmatchPhrase.this.time > 0) {
                    DeathmatchPhrase.this.game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(DeathmatchPhrase.this.time).toString()));
                } else if (DeathmatchPhrase.this.time == 0) {
                    List<User> users = DeathmatchPhrase.this.game.getUsers();
                    Collections.shuffle(users);
                    int i = 1;
                    while (i < users.size()) {
                        DeathmatchPhrase.this.game.getIngamePhrase().killUser(users.get(i), users.get(0), false);
                        ++i;
                    }
                }
                DeathmatchPhrase.this.game.updateScoreboard();
                DeathmatchPhrase deathmatchPhrase = DeathmatchPhrase.this;
                DeathmatchPhrase.access$2(deathmatchPhrase, deathmatchPhrase.time - 1);
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
    }

    static /* synthetic */ void access$2(DeathmatchPhrase deathmatchPhrase, int n) {
        deathmatchPhrase.time = n;
    }

}

