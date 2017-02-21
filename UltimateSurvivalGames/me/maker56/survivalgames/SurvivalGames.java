/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.WorldEditPlugin
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicesManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.maker56.survivalgames;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import me.maker56.survivalgames.UpdateCheck;
import me.maker56.survivalgames.arena.ArenaManager;
import me.maker56.survivalgames.arena.chest.ChestListener;
import me.maker56.survivalgames.arena.chest.ChestManager;
import me.maker56.survivalgames.commands.CommandSG;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigLoader;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.listener.PlayerListener;
import me.maker56.survivalgames.listener.ResetListener;
import me.maker56.survivalgames.listener.SelectionListener;
import me.maker56.survivalgames.listener.SignListener;
import me.maker56.survivalgames.listener.SpectatorListener;
import me.maker56.survivalgames.listener.UpdateListener;
import me.maker56.survivalgames.metrics.Metrics;
import me.maker56.survivalgames.scoreboard.ScoreBoardManager;
import me.maker56.survivalgames.sign.SignManager;
import me.maker56.survivalgames.user.UserManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SurvivalGames
extends JavaPlugin {
    public static SurvivalGames instance;
    public static FileConfiguration messages;
    public static FileConfiguration database;
    public static FileConfiguration signs;
    public static FileConfiguration reset;
    public static FileConfiguration chestloot;
    public static FileConfiguration scoreboard;
    public static ArenaManager arenaManager;
    public static GameManager gameManager;
    public static ChestManager chestManager;
    public static UserManager userManger;
    public static SignManager signManager;
    public static ScoreBoardManager scoreBoardManager;
    public static Economy econ;
    public static String version;
    private static PluginManager pm;

    static {
        version = "SurvivalGames - Version ";
        pm = Bukkit.getPluginManager();
    }

    public void onDisable() {
        for (Game game : gameManager.getGames()) {
            game.kickall();
        }
    }

    public void onEnable() {
        instance = this;
        version = String.valueOf(version) + this.getDescription().getVersion();
        new ConfigLoader().load();
        PermissionHandler.reinitializeDatabase();
        Game.reinitializeDatabase();
        MessageHandler.reload();
        if (this.setupEconomy()) {
            System.out.println("[SurvivalGames] Vault found!");
        }
        chestManager = new ChestManager();
        scoreBoardManager = new ScoreBoardManager();
        arenaManager = new ArenaManager();
        gameManager = new GameManager();
        userManger = new UserManager();
        signManager = new SignManager();
        this.getCommand("sg").setExecutor((CommandExecutor)new CommandSG());
        pm.registerEvents((Listener)new SelectionListener(), (Plugin)this);
        pm.registerEvents((Listener)new PlayerListener(), (Plugin)this);
        pm.registerEvents((Listener)new ChestListener(), (Plugin)this);
        pm.registerEvents((Listener)new SignListener(), (Plugin)this);
        pm.registerEvents((Listener)new ResetListener(), (Plugin)this);
        pm.registerEvents((Listener)new UpdateListener(), (Plugin)this);
        pm.registerEvents((Listener)new SpectatorListener(), (Plugin)this);
        try {
            new Metrics((Plugin)this).start();
        }
        catch (IOException e) {
            System.err.println("[SurvivalGames] Cannot load metrics: " + e.getMessage());
        }
        if (SurvivalGames.getWorldEdit() != null) {
            System.out.println("[SurvivalGames] Plugin enabled. WorldEdit found!");
        } else {
            System.out.println("[SurvivalGames] Plugin enabled.");
        }
        signManager.updateSigns();
        new me.maker56.survivalgames.UpdateCheck((Plugin)this, 61788);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider economyProvider;
        if (Bukkit.getPluginManager().isPluginEnabled("Vault") && (economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class)) != null) {
            econ = (Economy)economyProvider.getProvider();
        }
        if (econ != null) {
            return true;
        }
        return false;
    }

    public static void saveMessages() {
        try {
            messages.save("plugins/SurvivalGames/messages.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveDataBase() {
        try {
            database.save("plugins/SurvivalGames/database.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSigns() {
        try {
            signs.save("plugins/SurvivalGames/signs.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveReset() {
        try {
            reset.save("plugins/SurvivalGames/reset.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveChests() {
        try {
            chestloot.save("plugins/SurvivalGames/chestloot.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScoreboard() {
        try {
            scoreboard.save("plugins/SurvivalGames/scoreboard.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WorldEditPlugin getWorldEdit() {
        if (!pm.isPluginEnabled("WorldEdit")) {
            return null;
        }
        return (WorldEditPlugin)pm.getPlugin("WorldEdit");
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static ChestManager getChestManager() {
        return chestManager;
    }

    public static UserManager getUserManager() {
        return userManger;
    }

    public static SignManager getSignManager() {
        return signManager;
    }

    public static ScoreBoardManager getScoreboardManager() {
        return scoreBoardManager;
    }
}

