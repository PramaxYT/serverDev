/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.game;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.arena.ArenaManager;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.phrase.CooldownPhrase;
import me.maker56.survivalgames.game.phrase.DeathmatchPhrase;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.reset.Reset;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GameManager {
    private List<Game> games = new ArrayList<Game>();
    private static FileConfiguration cfg;

    public GameManager() {
        GameManager.reinitializeDatabase();
        this.loadAll();
    }

    public static void reinitializeDatabase() {
        cfg = SurvivalGames.database;
    }

    public void createGame(Player p, String lobbyname) {
        String path = "Games." + lobbyname;
        if (cfg.contains(path)) {
            p.sendMessage(MessageHandler.getMessage("game-already-exists").replace("%0%", lobbyname));
            return;
        }
        path = String.valueOf(path) + ".";
        FileConfiguration config = SurvivalGames.instance.getConfig();
        boolean enableVoting = config.getBoolean("Default.Enable-Voting");
        int lobbytime = config.getInt("Default.Lobby-Time");
        int maxVotingArenas = config.getInt("Default.Max-Voting-Arenas");
        int reqPlayers = config.getInt("Default.Required-Players-to-start");
        cfg.set(String.valueOf(path) + "Enable-Voting", (Object)enableVoting);
        cfg.set(String.valueOf(path) + "Lobby-Time", (Object)lobbytime);
        cfg.set(String.valueOf(path) + "Max-Voting-Arenas", (Object)maxVotingArenas);
        cfg.set(String.valueOf(path) + "Required-Players-to-start", (Object)reqPlayers);
        cfg.set(String.valueOf(path) + "Lobby", (Object)ConfigUtil.serializeLocation(p.getLocation(), true));
        SurvivalGames.saveDataBase();
        p.sendMessage(MessageHandler.getMessage("game-created").replace("%0%", lobbyname));
        p.sendMessage(MessageHandler.getMessage("game-set-spawn").replace("%0%", lobbyname));
    }

    public void setSpawn(Player p, String lobbyname) {
        if (!cfg.contains("Games." + lobbyname)) {
            p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", lobbyname));
            return;
        }
        Location loc = p.getLocation();
        String s = String.valueOf(loc.getWorld().getName()) + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
        cfg.set("Games." + lobbyname + ".Lobby", (Object)s);
        SurvivalGames.saveDataBase();
        p.sendMessage(MessageHandler.getMessage("game-spawn-set").replace("%0%", lobbyname));
    }

    public void loadAll() {
        int loaded = 0;
        if (cfg.contains("Games")) {
            for (String key : cfg.getConfigurationSection("Games.").getKeys(false)) {
                if (!this.load(key)) continue;
                ++loaded;
            }
        }
        System.out.println("[SurvivalGames] " + loaded + " lobbys loaded!");
    }

    public void unload(Game game) {
        if (game != null) {
            DeathmatchPhrase dp;
            IngamePhrase ip;
            CooldownPhrase cp;
            VotingPhrase vp;
            if (game.getPlayingUsers() > 0) {
                game.kickall();
            }
            if ((vp = game.getVotingPhrase()) != null) {
                vp.cancelTask();
            }
            if ((cp = game.getCooldownPhrase()) != null) {
                cp.cancelTask();
            }
            if ((ip = game.getIngamePhrase()) != null) {
                ip.cancelDeathmatchTask();
                ip.cancelLightningTask();
                ip.cancelTask();
            }
            if ((dp = game.getDeathmatch()) != null) {
                dp.cancelTask();
            }
            this.games.remove(game);
        }
    }

    public boolean load(String name) {
        if (this.getGame(name) != null) {
            System.out.println("[SurvivalGames] Lobby " + name + " is already loaded!");
            return false;
        }
        String path = "Games." + name;
        if (!cfg.contains(path)) {
            System.out.println("[SurvivalGames] Lobby " + name + " does not exist!");
            return false;
        }
        if (!cfg.contains(String.valueOf(path = new StringBuilder(String.valueOf(path)).append(".").toString()) + "Arenas")) {
            System.out.println("[SurvivalGames] Lobby " + name + " has no arenas!");
            return false;
        }
        boolean reset = false;
        if (SurvivalGames.reset.contains("Startup-Reset." + name)) {
            for (String key : SurvivalGames.reset.getConfigurationSection("Startup-Reset." + name + ".").getKeys(false)) {
                reset = true;
                new Reset(ConfigUtil.parseLocation(cfg.getString(String.valueOf(path) + "Arenas." + key + ".Min")).getWorld(), name, key, SurvivalGames.reset.getStringList("Startup-Reset." + name + "." + key)).start();
            }
        }
        if (reset) {
            System.out.println("[SurvivalGames] Lobby " + name + " does not exist!");
            return false;
        }
        ArrayList<Arena> arenas = new ArrayList<Arena>();
        for (String key : cfg.getConfigurationSection(String.valueOf(path) + "Arenas.").getKeys(false)) {
            Arena arena;
            if (!cfg.getBoolean(String.valueOf(path) + "Arenas." + key + ".Enabled") || (arena = SurvivalGames.arenaManager.getArena(name, key)) == null) continue;
            arenas.add(arena);
        }
        if (arenas.size() == 0) {
            System.out.println("[SurvivalGames] No arena in lobby " + name + " loaded!");
            return false;
        }
        if (!cfg.contains(String.valueOf(path) + "Lobby") && arenas.size() != 1) {
            System.out.println("[SurvivalGames] The spawn point in lobby " + name + " isn't defined!");
            return false;
        }
        Location lobby = ConfigUtil.parseLocation(cfg.getString(String.valueOf(path) + "Lobby"));
        boolean voting = cfg.getBoolean(String.valueOf(path) + "Enable-Voting");
        int lobbytime = cfg.getInt(String.valueOf(path) + "Lobby-Time");
        int maxVotingArenas = cfg.getInt(String.valueOf(path) + "Max-Voting-Arenas");
        int reqplayers = cfg.getInt(String.valueOf(path) + "Required-Players-to-start");
        boolean resetEnabled = SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset");
        this.games.add(new Game(name, lobby, voting, lobbytime, maxVotingArenas, reqplayers, arenas, resetEnabled));
        return true;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public Game getGame(String name) {
        for (Game game : this.games) {
            if (!game.getName().equalsIgnoreCase(name)) continue;
            return game;
        }
        return null;
    }
}

