/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.commands.arguments;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigReloader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigArgument {
    private CommandSender sender;
    private String[] args;

    public ConfigArgument(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public boolean execute() {
        if (!(this.sender instanceof Player)) {
            this.sender.sendMessage("\u00a7cThe lobby argument can only execute as a Player!");
            return true;
        }
        Player p = (Player)this.sender;
        if (!PermissionHandler.hasPermission((CommandSender)p, Permission.CONFIG)) {
            p.sendMessage(MessageHandler.getMessage("no-permission"));
            return true;
        }
        if (this.args.length == 1) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Configuration \u00a77\u00a7m---\u00a7r \u00a76Helpsite");
            p.sendMessage("\u00a78/\u00a76sg config reload [MESSAGES/SIGNS/DATABASE/CONFIG/CHESTLOOT/SCOREBOARD] \u00a77- \u00a7eReloads the specify config!");
        } else if (this.args[1].equalsIgnoreCase("reload")) {
            if (this.args.length == 2) {
                ConfigReloader.reloadMessage();
                ConfigReloader.reloadConfig();
                ConfigReloader.reloadDatabase();
                ConfigReloader.reloadSigns();
                ConfigReloader.reloadChestloot();
                ConfigReloader.reloadScoreboard();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded all configuration files successfully!");
                return true;
            }
            String con = this.args[2];
            if (con.equalsIgnoreCase("messages")) {
                ConfigReloader.reloadMessage();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded the messages.yml successfully!");
            } else if (con.equalsIgnoreCase("signs")) {
                ConfigReloader.reloadSigns();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded the signs.yml successfully!");
            } else if (con.equalsIgnoreCase("database")) {
                ConfigReloader.reloadDatabase();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "The settings are applied to a lobby after a lobby-reload or the end of a survival game.");
            } else if (con.equalsIgnoreCase("config")) {
                ConfigReloader.reloadConfig();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded the config.yml successfully!");
            } else if (con.equalsIgnoreCase("chestloot")) {
                ConfigReloader.reloadChestloot();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded the chestloot.yml successfully!");
            } else if (con.equalsIgnoreCase("scoreboard")) {
                ConfigReloader.reloadScoreboard();
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've reloaded the scoreboard.yml successfully!");
            } else {
                p.sendMessage(MessageHandler.getMessage("config-error-name").replace("%0%", "/sg config reload [MESSAGES/SIGNS/DATABASE/CONFIG/CHESTLOOT]"));
                return true;
            }
        }
        return true;
    }
}

