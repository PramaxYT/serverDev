/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package me.maker56.survivalgames.listener;

import java.io.PrintStream;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener
implements Listener {
    private static String version = null;

    public static void update(String version) {
        UpdateListener.version = version;
        System.out.println("[SurvivalGames] A newer version of survivalgames is available. (" + version + ") You can download it here: http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ You're using " + SurvivalGames.version);
        Player[] arrplayer = Bukkit.getOnlinePlayers();
        int n = arrplayer.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = arrplayer[n2];
            if (p.isOp()) {
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7eA newer version of SurvivalGames is available. \u00a77(\u00a7b" + version + "\u00a77) \u00a7eYou can download it here: \u00a7bhttp://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ \u00a77You're using \u00a7o" + SurvivalGames.version);
            }
            ++n2;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (version != null && event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7eA newer version of SurvivalGames is available. \u00a77(\u00a7b" + version + "\u00a77) \u00a7eYou can download it here: \u00a7bhttp://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ \u00a77You're using \u00a7o" + SurvivalGames.version);
        }
    }
}

