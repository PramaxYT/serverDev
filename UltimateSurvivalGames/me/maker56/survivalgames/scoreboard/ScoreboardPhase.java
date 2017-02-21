/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Score
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package me.maker56.survivalgames.scoreboard;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.scoreboard.CustomScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardPhase {
    private String title;
    private List<String> scores = new ArrayList<String>();
    private Scoreboard scoreboard;
    private Objective sidebar;
    private List<CustomScore> Sscore;

    protected ScoreboardPhase(String title, List<String> scores) {
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        this.title = ChatColor.translateAlternateColorCodes((char)'&', (String)title);
        Iterator<String> iterator = scores.iterator();
        while (iterator.hasNext()) {
            String score = iterator.next();
            String[] split = score.split("//");
            if (split[0].length() > 48) {
                split[0] = split[0].substring(0, 48);
            }
            score = String.valueOf(split[0]) + "//" + split[1];
            this.scores.add(ChatColor.translateAlternateColorCodes((char)'&', (String)score));
        }
    }

    public Scoreboard initScoreboard(Game game) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.sidebar.setDisplayName(this.title);
        this.Sscore = new ArrayList<CustomScore>();
        int tName = 0;
        int i = 0;
        while (i < this.scores.size()) {
            String score = this.scores.get(i);
            try {
                String[] split = score.split("//");
                String name = split[0];
                String extra = null;
                if (name.contains("%arena%")) {
                    List<Arena> arenas;
                    Arena a = null;
                    a = game.getState() == GameState.VOTING ? (i >= (arenas = game.getVotingPhrase().getArenas()).size() ? arenas.get(arenas.size() - 1) : arenas.get(i)) : game.getCurrentArena();
                    if (a != null) {
                        extra = a.getName();
                        if ((name = name.replace("%arena%", a.getName())).length() > 48) {
                            name = name.substring(0, 48);
                        }
                    }
                }
                String regex = split[1];
                String scoreName = name;
                Team team = null;
                if (name.length() > 16) {
                    team = this.scoreboard.registerNewTeam(Integer.valueOf(tName).toString());
                    team.setPrefix(name.substring(0, 16));
                    if (name.length() > 32) {
                        scoreName = name.substring(16, 32);
                        team.setSuffix(name.substring(32));
                    } else {
                        scoreName = name.substring(16);
                    }
                    ++tName;
                }
                Score s = this.sidebar.getScore(Bukkit.getOfflinePlayer((String)scoreName));
                if (team != null) {
                    team.addPlayer(s.getPlayer());
                }
                s.setScore(-1);
                this.Sscore.add(new CustomScore(s, name, regex, team, extra));
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println("[SurvivalGames] Cannot load Scoreboard phase " + this.title + " - Mabye this is the reason: " + e.toString());
                return null;
            }
            ++i;
        }
        return this.scoreboard;
    }

    public List<CustomScore> getScores() {
        return this.Sscore;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public ScoreboardPhase clone() {
        return new ScoreboardPhase(this.title, this.scores);
    }
}

