/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Hanging
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.hanging.HangingBreakEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerBucketFillEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.event.weather.WeatherChangeEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.maker56.survivalgames.listener;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import me.maker56.survivalgames.user.UserState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener
implements Listener {
    private UserManager um = SurvivalGames.userManger;
    private static double tntdamage;
    public static List<String> allowedCmds;

    public PlayerListener() {
        PlayerListener.reinitializeDatabase();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        ItemStack is;
        Player p = (Player)event.getWhoClicked();
        int slot = event.getRawSlot();
        try {
            is = event.getInventory().getItem(slot);
            if (is == null || is.getType() == Material.AIR) {
                return;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
        ItemMeta im = is.getItemMeta();
        String name = im.getDisplayName();
        if (name == null) {
            return;
        }
        User u = this.um.getUser(p.getName());
        if (u != null) {
            Game g = u.getGame();
            if (g.getState() == GameState.VOTING || g.getState() == GameState.WAITING || g.getState() == GameState.COOLDOWN) {
                event.setCancelled(true);
                String[] split = name.split(". ");
                if (split.length >= 2) {
                    p.closeInventory();
                    Arena a = g.getVotingPhrase().vote(p, Integer.parseInt(split[0]));
                    if (a != null) {
                        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 4.0f, 2.0f);
                    } else {
                        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cAn interal error occured!");
                    }
                }
            }
        } else {
            SpectatorUser su = this.um.getSpectator(p.getName());
            if (su != null && is.getType() == Material.SKULL_ITEM && name.startsWith("\u00a7e")) {
                String pname = name.substring(2, name.length());
                event.setCancelled(true);
                Game g = su.getGame();
                User user = g.getUser(pname);
                if (user == null) {
                    p.sendMessage(MessageHandler.getMessage("spectator-not-living").replace("%0%", pname));
                    return;
                }
                p.closeInventory();
                p.teleport(user.getPlayer().getLocation());
                p.sendMessage(MessageHandler.getMessage("spectator-new-player").replace("%0%", pname));
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            User u;
            Player p = event.getPlayer();
            ItemStack hand = p.getItemInHand();
            if (hand == null || hand.getType() == Material.AIR) {
                return;
            }
            if (!this.um.isPlaying(p.getName()) && !this.um.isSpectator(p.getName())) {
                return;
            }
            UserState us = this.um.getUser(p.getName());
            if (us == null) {
                us = this.um.getSpectator(p.getName());
            }
            if (hand.equals((Object)Game.getLeaveItem())) {
                if ((System.currentTimeMillis() - us.getJoinTime()) / 1000 <= 2) {
                    return;
                }
                this.um.leaveGame(p);
                event.setCancelled(true);
            }
            if ((u = this.um.getUser(p.getName())) != null) {
                Game g = u.getGame();
                if (hand.equals((Object)VotingPhrase.getVotingOpenItemStack())) {
                    if (g.getState() != GameState.VOTING) {
                        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cVoting isn't active right now!");
                        return;
                    }
                    if (!g.getVotingPhrase().canVote(p.getName())) {
                        p.sendMessage(MessageHandler.getMessage("game-already-vote"));
                        return;
                    }
                    event.setCancelled(true);
                    p.openInventory(g.getVotingPhrase().getVotingInventory());
                }
            } else {
                SpectatorUser su = this.um.getSpectator(p.getName());
                if (su != null && hand.equals((Object)Game.getPlayerNavigatorItem())) {
                    event.setCancelled(true);
                    su.getPlayer().openInventory(su.getGame().getPlayerNavigatorInventory());
                }
            }
        }
    }

    public static void reinitializeDatabase() {
        tntdamage = SurvivalGames.instance.getConfig().getDouble("TNT-Extra-Damage", 7.0);
        allowedCmds = SurvivalGames.instance.getConfig().getStringList("Allowed-Commands");
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        Player p;
        if (!event.isCancelled() && event.getDamager() instanceof TNTPrimed && event.getEntity() instanceof Player && this.um.isPlaying((p = (Player)event.getEntity()).getName())) {
            event.setDamage(event.getDamage() + tntdamage);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p;
        if (event.getInventory().getType() == InventoryType.CHEST && this.um.isPlaying((p = (Player)event.getPlayer()).getName())) {
            p.playSound(p.getEyeLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (p.getKiller() instanceof Player) {
            Player pkiller = p.getKiller();
            if (this.um.isPlaying(p.getName())) {
                User user = this.um.getUser(p.getName());
                Game game = user.getGame();
                event.setDeathMessage(null);
                if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                    this.um.leaveGame(p);
                    return;
                }
                IngamePhrase ip = game.getIngamePhrase();
                for (Entity entity : p.getWorld().getEntities()) {
                    Projectile pr;
                    if (!(entity instanceof Projectile) || !p.equals((Object)(pr = (Projectile)entity).getShooter())) continue;
                    pr.remove();
                }
                for (ItemStack is : event.getDrops()) {
                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
                }
                event.getDrops().clear();
                if (this.um.isPlaying(pkiller.getName())) {
                    ip.killUser(user, this.um.getUser(pkiller.getName()), false);
                } else {
                    ip.killUser(user, null, false);
                }
            }
        } else if (this.um.isPlaying(p.getName())) {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            event.setDeathMessage(null);
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                this.um.leaveGame(p);
                return;
            }
            IngamePhrase ip = game.getIngamePhrase();
            for (ItemStack is : event.getDrops()) {
                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            }
            event.getDrops().clear();
            ip.killUser(user, null, false);
        }
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        GameState gs;
        User u = this.um.getUser(event.getPlayer().getName());
        if (u != null && ((gs = u.getGame().getState()) == GameState.WAITING || gs == GameState.VOTING || gs == GameState.COOLDOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemPickup(PlayerPickupItemEvent event) {
        GameState gs;
        User u = this.um.getUser(event.getPlayer().getName());
        if (u != null && ((gs = u.getGame().getState()) == GameState.WAITING || gs == GameState.VOTING || gs == GameState.COOLDOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!event.getWorld().equals((Object)arena.getMinimumLocation().getWorld()) || !event.toWeatherState()) continue;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingDestroy(HangingBreakEvent event) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!arena.containsBlock(event.getEntity().getLocation())) continue;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!arena.containsBlock(event.getRightClicked().getLocation())) continue;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            for (Game game : SurvivalGames.gameManager.getGames()) {
                for (Arena arena : game.getArenas()) {
                    if (!arena.containsBlock(event.getEntity().getLocation())) continue;
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerWaterPlace(PlayerBucketEmptyEvent event) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!arena.containsBlock(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) continue;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerWaterPlace(PlayerBucketFillEvent event) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!arena.containsBlock(event.getBlockClicked().getLocation())) continue;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p;
        if (event.getAction() != Action.PHYSICAL && this.um.isPlaying((p = event.getPlayer()).getName())) {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            if (game.getState() == GameState.COOLDOWN) {
                event.setCancelled(true);
            } else if (game.getArenas().size() == 1 && game.getState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Player p;
        if (event.getEntity() instanceof Player && this.um.isPlaying((p = (Player)event.getEntity()).getName())) {
            Game game = this.um.getUser(p.getName()).getGame();
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                event.setCancelled(true);
                return;
            }
            if (game.getIngamePhrase().grace) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (this.um.isPlaying(p.getName())) {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                this.um.leaveGame(p);
                return;
            }
            IngamePhrase ip = game.getIngamePhrase();
            ip.killUser(user, null, true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        if (this.um.isPlaying(p.getName()) && (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        Game game;
        Player p;
        if (event.getEntity() instanceof Player && this.um.isPlaying((p = (Player)event.getEntity()).getName()) && ((game = this.um.getUser(p.getName()).getGame()).getState() == GameState.WAITING || game.getState() == GameState.VOTING || game.getState() == GameState.COOLDOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if ((from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) && this.um.isPlaying(p.getName())) {
            Game game = this.um.getUser(p.getName()).getGame();
            if (game.getState() == GameState.COOLDOWN) {
                p.teleport(from);
                event.setCancelled(true);
            } else if (game.getArenas().size() == 1 && game.getState() == GameState.WAITING) {
                p.teleport(from);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        if (!this.um.isPlaying(p.getName())) {
            for (Game game : SurvivalGames.gameManager.getGames()) {
                for (Arena arena : game.getArenas()) {
                    if (!arena.containsBlock(loc)) continue;
                    event.setCancelled(true);
                    p.sendMessage(MessageHandler.getMessage("forbidden-build"));
                    return;
                }
            }
        } else {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            if (game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
                event.setCancelled(true);
            } else {
                if (game.getCurrentArena().getAllowedMaterials().contains(event.getBlock().getTypeId())) {
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (this.um.isPlaying(p.getName())) {
            User user = this.um.getUser(p.getName());
            Game game = user.getGame();
            if (game.getArenas().size() == 1) {
                if (game.getState() == GameState.WAITING || game.getState() == GameState.COOLDOWN) {
                    event.setCancelled(true);
                }
            } else if (game.getState() == GameState.COOLDOWN) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        if (!this.um.isPlaying(p.getName())) {
            for (Game game : SurvivalGames.gameManager.getGames()) {
                for (Arena arena : game.getArenas()) {
                    if (!arena.containsBlock(loc)) continue;
                    event.setCancelled(true);
                    p.sendMessage(MessageHandler.getMessage("forbidden-build"));
                    return;
                }
            }
        } else {
            User user = this.um.getUser(p.getName());
            Arena arena = user.getGame().getCurrentArena();
            if (arena == null) {
                event.setCancelled(true);
            } else {
                if (arena.getAllowedMaterials().contains(event.getBlock().getTypeId())) {
                    if (event.getBlock().getType() == Material.TNT) {
                        event.getBlock().setType(Material.AIR);
                        event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
                    }
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        if (this.um.isPlaying(p.getName()) || this.um.isSpectator(p.getName())) {
            String message = event.getMessage().toLowerCase();
            for (String cmd : allowedCmds) {
                if (!message.startsWith(cmd)) continue;
                return;
            }
            if (message.startsWith("/list")) {
                UserState u = this.um.getUser(p.getName());
                if (u == null) {
                    u = this.um.getSpectator(p.getName());
                }
                Game g = u.getGame();
                p.sendMessage(MessageHandler.getMessage("game-player-list").replace("%0%", Integer.valueOf(g.getPlayingUsers()).toString()).replace("%1%", g.getAlivePlayers()));
                event.setCancelled(true);
            } else if (message.startsWith("/vote")) {
                p.chat("/sg " + message.replace("/", ""));
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                p.sendMessage(MessageHandler.getMessage("forbidden-command"));
            }
        }
    }
}

