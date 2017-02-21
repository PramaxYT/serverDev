/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 */
package me.maker56.survivalgames.scoreboard;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ScoreBoardManager {
    private static HashMap<GameState, ScoreboardPhase> phases = new HashMap();

    public ScoreBoardManager() {
        ScoreBoardManager.reinitializeDatabase();
    }

    public ScoreboardPhase getNewScoreboardPhase(GameState state) {
        if (phases.containsKey((Object)state)) {
            return phases.get((Object)state).clone();
        }
        return null;
    }

    public static void reinitializeDatabase() {
        FileConfiguration c = SurvivalGames.scoreboard;
        phases.clear();
        if (c.contains("Phase")) {
            for (String key : c.getConfigurationSection("Phase.").getKeys(false)) {
                try {
                    if (!c.getBoolean("Phase." + key + ".Enabled")) continue;
                    GameState state = GameState.valueOf(key.toUpperCase());
                    String title = c.getString("Phase." + key + ".Title");
                    List scores = c.getStringList("Phase." + key + ".Scores");
                    ScoreboardPhase sp = new ScoreboardPhase(title, scores);
                    phases.put(state, sp);
                    continue;
                }
                catch (Exception e) {
                    System.err.println("[SurvivalGames] Cannot load Scoreboard phase " + key + " - Mabye this is the reason: " + e.toString());
                }
            }
        }
        System.out.println("[SurvivalGames] " + phases.size() + " scoreboard phases loaded!");
    }
}

