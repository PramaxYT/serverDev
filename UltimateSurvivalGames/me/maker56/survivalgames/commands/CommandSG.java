/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package me.maker56.survivalgames.commands;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.arguments.ArenaArgument;
import me.maker56.survivalgames.commands.arguments.ConfigArgument;
import me.maker56.survivalgames.commands.arguments.LobbyArgument;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CommandSG
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sg")) {
            if (args.length == 0) {
                sender.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Version " + SurvivalGames.instance.getDescription().getVersion() + " \u00a77\u00a7m--\u00a7r \u00a7ePlugin developed by maker56");
                if (PermissionHandler.hasPermission(sender, Permission.JOIN)) {
                    sender.sendMessage("\u00a78/\u00a76sg join <LOBBY> \u00a77- \u00a7eJoin a game!");
                    sender.sendMessage("\u00a78/\u00a76sg leave \u00a77- \u00a7eLeave a game!");
                    sender.sendMessage("\u00a78/\u00a76sg vote <ID> \u00a77- \u00a7eVote for an arena!");
                }
                if (PermissionHandler.hasPermission(sender, Permission.LIST)) {
                    sender.sendMessage("\u00a78/\u00a76sg list \u00a77- \u00a7eList of all available lobbys!");
                }
                if (PermissionHandler.hasPermission(sender, Permission.START)) {
                    sender.sendMessage("\u00a78/\u00a76sg start \u00a77- \u00a7eForce a lobby to start!");
                }
                if (PermissionHandler.hasPermission(sender, Permission.GAME)) {
                    sender.sendMessage("\u00a78/\u00a76sg lobby \u00a77- \u00a7eShows the lobby helpsite!");
                }
                if (PermissionHandler.hasPermission(sender, Permission.ARENA)) {
                    sender.sendMessage("\u00a78/\u00a76sg arena \u00a77- \u00a7eShows the arena helpsite!");
                }
                if (PermissionHandler.hasPermission(sender, Permission.CONFIG)) {
                    sender.sendMessage("\u00a78/\u00a76sg config \u00a77- \u00a7eShows the configuration management helpsite!");
                }
            } else {
                if (args[0].equalsIgnoreCase("arena")) {
                    return new ArenaArgument(sender, args).execute();
                }
                if (args[0].equalsIgnoreCase("lobby") || args[0].equalsIgnoreCase("game")) {
                    return new LobbyArgument(sender, args).execute();
                }
                if (args[0].equalsIgnoreCase("config")) {
                    return new ConfigArgument(sender, args).execute();
                }
                if (args[0].equalsIgnoreCase("join")) {
                    Player p = (Player)sender;
                    if (args.length == 1) {
                        p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg join <GAMENAME>"));
                        return true;
                    }
                    SurvivalGames.userManger.joinGame(p, args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("leave")) {
                    Player p = (Player)sender;
                    User user = SurvivalGames.userManger.getUser(p.getName());
                    if (user == null) {
                        p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
                        return true;
                    }
                    Game game = user.getGame();
                    if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                        SurvivalGames.userManger.leaveGame(p);
                        return true;
                    }
                    IngamePhrase ip = game.getIngamePhrase();
                    ip.killUser(user, null, true);
                    return true;
                }
                if (args[0].equalsIgnoreCase("vote")) {
                    Player p = (Player)sender;
                    if (!SurvivalGames.userManger.isPlaying(p.getName())) {
                        p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
                        return true;
                    }
                    if (args.length == 1) {
                        p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a Arena-ID!"));
                        return true;
                    }
                    User user = SurvivalGames.userManger.getUser(p.getName());
                    if (!user.getGame().isVotingEnabled()) {
                        p.sendMessage(MessageHandler.getMessage("game-no-voting-enabled"));
                        return true;
                    }
                    if (user.getGame().getState() != GameState.VOTING) {
                        p.sendMessage(MessageHandler.getMessage("game-no-vote"));
                        return true;
                    }
                    VotingPhrase vp = user.getGame().getVotingPhrase();
                    if (!vp.canVote(p.getName())) {
                        p.sendMessage(MessageHandler.getMessage("game-already-vote"));
                        return true;
                    }
                    int mapid = 0;
                    try {
                        mapid = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", String.valueOf(args[1]) + " ist not a valid number!"));
                        return true;
                    }
                    Arena arena = vp.vote(p, mapid);
                    if (arena == null) {
                        p.sendMessage(MessageHandler.getMessage("game-bad-vote"));
                        return true;
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if (!PermissionHandler.hasPermission(sender, Permission.LIST)) {
                        sender.sendMessage(MessageHandler.getMessage("no-permission"));
                        return true;
                    }
                    List<Game> games = SurvivalGames.gameManager.getGames();
                    sender.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "List of all loaded lobbys\u00a78: \u00a77(\u00a7b" + games.size() + "\u00a77)");
                    for (Game game : games) {
                        sender.sendMessage("\u00a77- \u00a76" + game.getName() + "\u00a78: \u00a7e" + game.getState().toString() + " \u00a77(\u00a7e" + game.getPlayingUsers() + "\u00a77/\u00a7e" + game.getMaximumPlayers() + "\u00a77)");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("start")) {
                    if (!PermissionHandler.hasPermission(sender, Permission.START)) {
                        sender.sendMessage(MessageHandler.getMessage("no-permission"));
                        return true;
                    }
                    Player p = (Player)sender;
                    Game game = null;
                    if (args.length > 1) {
                        game = SurvivalGames.gameManager.getGame(args[1]);
                    } else {
                        UserManager um = SurvivalGames.userManger;
                        User u = um.getUser(p.getName());
                        if (u != null) {
                            game = u.getGame();
                        }
                    }
                    if (game == null) {
                        p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", args.length <= 1 ? "" : args[1]));
                        return true;
                    }
                    game.forceStart(p);
                    return true;
                }
                sender.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cCommand not found! Type /sg for help!");
                return true;
            }
        }
        return false;
    }
}

