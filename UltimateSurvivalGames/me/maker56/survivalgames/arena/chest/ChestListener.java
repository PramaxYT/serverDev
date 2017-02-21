/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 */
package me.maker56.survivalgames.arena.chest;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.arena.chest.ChestManager;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ChestListener
implements Listener {
    private UserManager um = SurvivalGames.userManger;
    private ChestManager cm = SurvivalGames.chestManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.um.isPlaying((p = event.getPlayer()).getName())) {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                event.setCancelled(true);
                return;
            }
            Arena arena = game.getCurrentArena();
            Block b = event.getClickedBlock();
            if (b.getType() == arena.getChestType() && (arena.getChestData() >= 0 && b.getData() == arena.getChestData() || arena.getChestData() < 0)) {
                Location loc = b.getLocation();
                event.setCancelled(true);
                if (game.isChestRegistered(loc)) {
                    p.openInventory(game.getChest(loc).getInventory());
                } else {
                    Chest chest = this.cm.getRandomChest(p, loc);
                    game.registerChest(chest);
                    p.openInventory(chest.getInventory());
                    p.playSound(p.getEyeLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                }
            }
        }
    }
}

