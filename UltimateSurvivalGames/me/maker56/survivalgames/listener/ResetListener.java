/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockBurnEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockGrowEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.LeavesDecayEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 */
package me.maker56.survivalgames.listener;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.events.ResetDoneEvent;
import me.maker56.survivalgames.events.SaveDoneEvent;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ResetListener
implements Listener {
    private GameManager gm = SurvivalGames.gameManager;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onFromToEvent(BlockFromToEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getToBlock().getLocation());
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockGrow(BlockGrowEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!event.isCancelled()) {
            this.logChunk(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockExplode(EntityExplodeEvent event) {
        List blocks = event.blockList();
        if (blocks.size() > 0) {
            Location loc = ((Block)blocks.get(0)).getLocation();
            block0 : for (Game game : this.gm.getGames()) {
                for (Arena a : game.getArenas()) {
                    if (!a.containsBlock(loc)) continue;
                    blocks.clear();
                    continue block0;
                }
            }
        }
    }

    private void logChunk(Location loc) {
        for (Game game : this.gm.getGames()) {
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) continue;
            for (Arena a : game.getArenas()) {
                if (!a.containsBlock(loc)) continue;
                String chunkKey = String.valueOf(loc.getChunk().getX()) + "," + loc.getChunk().getZ();
                if (!game.getChunksToReset().contains(chunkKey)) {
                    game.getChunksToReset().add(chunkKey);
                    List reset = SurvivalGames.reset.getStringList("Startup-Reset." + game.getName() + "." + a.getName());
                    reset.add(chunkKey);
                    SurvivalGames.reset.set("Startup-Reset." + game.getName() + "." + a.getName(), (Object)reset);
                    SurvivalGames.saveReset();
                }
                return;
            }
        }
    }

    @EventHandler
    public void onSaveComplete(SaveDoneEvent event) {
        Player[] arrplayer = Bukkit.getOnlinePlayers();
        int n = arrplayer.length;
        int n2 = 0;
        while (n2 < n) {
            Player p = arrplayer[n2];
            if (PermissionHandler.hasPermission((CommandSender)p, Permission.ARENA)) {
                p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Done saveing arena " + event.getArena() + " in lobby " + event.getLobby() + "! It took " + event.getTime() + " seconds! The file is " + event.getFileSize() + " " + event.getFileSizeFormat() + " big.");
            }
            ++n2;
        }
    }

    @EventHandler
    public void onResetComplete(ResetDoneEvent event) {
        Game game = this.gm.getGame(event.getLobby());
        if (game != null) {
            this.gm.unload(game);
        }
        this.gm.load(event.getLobby());
        SurvivalGames.signManager.updateSigns();
    }
}

