/*
 * Decompiled with CFR 0_118.
 */
package me.maker56.survivalgames.database;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.ArenaManager;
import me.maker56.survivalgames.arena.chest.ChestManager;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigLoader;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.listener.PlayerListener;
import me.maker56.survivalgames.scoreboard.ScoreBoardManager;
import me.maker56.survivalgames.sign.SignManager;

public class ConfigReloader {
    public static void reloadConfig() {
        ConfigLoader.reloadConfig();
        PermissionHandler.reinitializeUsePermission();
        VotingPhrase.reinitializeDatabase();
        Game.reinitializeDatabase();
        PermissionHandler.reinitializeDatabase();
        PlayerListener.reinitializeDatabase();
    }

    public static void reloadDatabase() {
        ConfigLoader.reloadDatabase();
        GameManager.reinitializeDatabase();
        ArenaManager.reinitializeDatabase();
    }

    public static void reloadSigns() {
        ConfigLoader.reloadSigns();
        SurvivalGames.signManager.reload();
        SurvivalGames.signManager.updateSigns();
    }

    public static void reloadMessage() {
        ConfigLoader.reloadMessages();
        MessageHandler.reload();
    }

    public static void reloadChestloot() {
        ConfigLoader.reloadChests();
        ChestManager.reinitializeConfig();
    }

    public static void reloadScoreboard() {
        ScoreBoardManager.reinitializeDatabase();
    }
}

