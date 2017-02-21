/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package me.maker56.survivalgames.listener;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.sign.SignManager;
import me.maker56.survivalgames.user.UserManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener
implements Listener {
    private SignManager sm = SurvivalGames.signManager;

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
        if (event.getLine(0).equalsIgnoreCase("[SurvivalGames]")) {
            if (!PermissionHandler.hasPermission((CommandSender)p, Permission.LOBBY)) {
                p.sendMessage(MessageHandler.getMessage("no-permission"));
                event.getBlock().breakNaturally();
                return;
            }
            if (event.getLine(1).equalsIgnoreCase("join")) {
                SurvivalGames.signManager.addSign(p, event.getBlock().getLocation(), event.getLine(2));
            } else if (event.getLine(1).equalsIgnoreCase("quit") || event.getLine(1).equalsIgnoreCase("leave")) {
                int i = 0;
                while (i < this.sm.getLeaveSignDesign().length) {
                    event.setLine(i, this.sm.getLeaveSignDesign()[i]);
                    ++i;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }
        Block b = event.getClickedBlock();
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
            String lobby = this.sm.getLobby(b.getLocation());
            if (lobby != null) {
                Player p = event.getPlayer();
                if (event.getAction() == Action.LEFT_CLICK_BLOCK && p.getGameMode() != GameMode.CREATIVE) {
                    this.sm.sendInfo((CommandSender)p, lobby);
                    event.setCancelled(true);
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    SurvivalGames.userManger.joinGame(p, lobby);
                    event.setCancelled(true);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Sign s = (Sign)b.getState();
                String[] design = this.sm.getLeaveSignDesign();
                int i = 0;
                while (i < design.length) {
                    if (!s.getLine(i).equals(design[i])) {
                        return;
                    }
                    ++i;
                }
                SurvivalGames.userManger.leaveGame(event.getPlayer());
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block b = event.getBlock();
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
            this.sm.removeSign(event.getPlayer(), b.getLocation());
        }
    }
}

