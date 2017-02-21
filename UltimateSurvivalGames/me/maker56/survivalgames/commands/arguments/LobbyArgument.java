/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.commands.arguments;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.chat.Helper;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.sign.SignManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LobbyArgument {
    private CommandSender sender;
    private String[] args;

    public LobbyArgument(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public boolean execute() {
        if (!(this.sender instanceof Player)) {
            this.sender.sendMessage("\u00a7cThe lobby argument can only execute as a Player!");
            return true;
        }
        Player p = (Player)this.sender;
        if (!PermissionHandler.hasPermission((CommandSender)p, Permission.GAME) && !PermissionHandler.hasPermission((CommandSender)p, Permission.LOBBY)) {
            p.sendMessage(MessageHandler.getMessage("no-permission"));
            return true;
        }
        if (this.args.length != 1) {
            if (this.args[1].equalsIgnoreCase("delete")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby create <NAME>"));
                    return true;
                }
                if (SurvivalGames.gameManager.getGame(this.args[2]) != null) {
                    p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cYou must unload the lobby first! /sg lobby unload " + this.args[2]);
                    return true;
                }
                if (!SurvivalGames.database.contains("Games." + this.args[2])) {
                    p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cLobby " + this.args[2] + " does not exist!");
                    return true;
                }
                SurvivalGames.database.set("Games." + this.args[2], (Object)null);
                SurvivalGames.saveDataBase();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've removed lobby " + this.args[2] + " successfully!");
                return true;
            }
            if (this.args[1].equalsIgnoreCase("create")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby create <NAME>"));
                    return true;
                }
                SurvivalGames.gameManager.createGame(p, this.args[2]);
                return true;
            }
            if (this.args[1].equalsIgnoreCase("setspawn")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby setspawn <NAME>"));
                    return true;
                }
                SurvivalGames.gameManager.setSpawn(p, this.args[2]);
                return true;
            }
            if (this.args[1].equalsIgnoreCase("unload")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby unload <NAME>"));
                    return true;
                }
                Game game = SurvivalGames.gameManager.getGame(this.args[2]);
                if (game == null) {
                    p.sendMessage(MessageHandler.getMessage("game-not-loaded").replace("%0%", this.args[2]));
                    return true;
                }
                game.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7c\u00a7lYour lobby was stopped by an admin!");
                if (game.getState() == GameState.INGAME || game.getState() == GameState.DEATHMATCH) {
                    p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cIt my can be that the blocks of arena " + game.getCurrentArena().getName() + " aren't reseted yet. It will reset while loading lobby.");
                }
                SurvivalGames.gameManager.unload(game);
                p.sendMessage(MessageHandler.getMessage("game-success-unloaded").replace("%0%", this.args[2]));
                SurvivalGames.signManager.updateSigns();
                return true;
            }
            if (this.args[1].equalsIgnoreCase("load")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby unload <NAME>"));
                    return true;
                }
                Game game = SurvivalGames.gameManager.getGame(this.args[2]);
                if (game != null) {
                    p.sendMessage(MessageHandler.getMessage("game-already-loaded").replace("%0%", this.args[2]));
                    return true;
                }
                boolean success = SurvivalGames.gameManager.load(this.args[2]);
                if (!success) {
                    p.sendMessage(MessageHandler.getMessage("game-load-error").replace("%0%", this.args[2]).replace("%1%", "See console for informations! It may can be that a few arenas have to be reset. When this happens, the game will automatically load after all arenas were reset."));
                } else {
                    p.sendMessage(MessageHandler.getMessage("game-success-loaded").replace("%0%", this.args[2]));
                    SurvivalGames.signManager.updateSigns();
                }
                return true;
            }
            if (this.args[1].equalsIgnoreCase("reload")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby unload <NAME>"));
                    return true;
                }
                p.performCommand("sg lobby unload " + this.args[2]);
                p.performCommand("sg lobby load " + this.args[2]);
                return true;
            }
            if (this.args[1].equalsIgnoreCase("list")) {
                if (this.args.length == 2) {
                    p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg lobby list <NAME>"));
                    return true;
                }
                Game game = SurvivalGames.gameManager.getGame(this.args[2]);
                if (game == null) {
                    p.sendMessage(MessageHandler.getMessage("game-not-loaded").replace("%0%", this.args[2]));
                    return true;
                }
                List<Arena> arenas = game.getArenas();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Arenas in lobby " + game.getName() + "\u00a78: \u00a77(\u00a7b" + arenas.size() + "\u00a77)");
                for (Arena a : arenas) {
                    p.sendMessage("\u00a77- \u00a76" + a.getName());
                }
                return true;
            }
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cCommand not found! Type /sg lobby for help!");
            return true;
        }
        Helper.showLobbyHelpsite(p);
        return true;
    }
}

