/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package me.maker56.survivalgames.game.phrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class VotingPhrase {
    private static ItemStack voteItem;
    private static ItemStack arenaItem;
    private static String title;
    private Game game;
    private BukkitTask task;
    private boolean running = false;
    private int time;
    public ArrayList<Arena> voteArenas = new ArrayList();
    private Inventory voteInventory;

    public static ItemStack getVotingOpenItemStack() {
        return voteItem;
    }

    public static String getVotingInventoryTitle() {
        return title;
    }

    public static void reinitializeDatabase() {
        voteItem = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.Item"));
        arenaItem = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.ArenaItem"));
        title = SurvivalGames.instance.getConfig().getString("Voting.InventoryTitle");
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        title = ChatColor.translateAlternateColorCodes((char)'&', (String)title);
    }

    public VotingPhrase(Game game) {
        VotingPhrase.reinitializeDatabase();
        this.game = game;
        this.time = game.getLobbyTime();
    }

    public void load() {
        this.game.setState(GameState.VOTING);
        this.chooseRandomArenas();
        this.game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.VOTING));
        this.start();
    }

    public void start() {
        this.running = true;
        if (this.game.isVotingEnabled() && voteItem != null) {
            this.generateInventory();
            for (User user : this.game.getUsers()) {
                this.equipPlayer(user);
            }
        }
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                for (User user2 : VotingPhrase.this.game.getUsers()) {
                    user2.getPlayer().setLevel(VotingPhrase.this.time);
                    user2.getPlayer().setExp(0.0f);
                }
                if (VotingPhrase.this.time % 10 == 0 && VotingPhrase.this.time != 10 && VotingPhrase.this.time != 0 && VotingPhrase.this.time != VotingPhrase.this.game.getLobbyTime()) {
                    VotingPhrase.this.game.sendMessage(MessageHandler.getMessage("game-voting-cooldown-big").replace("%0%", Integer.valueOf(VotingPhrase.this.time).toString()));
                } else if (VotingPhrase.this.time % 15 == 0 && VotingPhrase.this.time != 0) {
                    VotingPhrase.this.sendVoteMessage();
                } else if (VotingPhrase.this.time <= 10 && VotingPhrase.this.time > 0) {
                    VotingPhrase.this.game.sendMessage(MessageHandler.getMessage("game-voting-cooldown-little").replace("%0%", Integer.valueOf(VotingPhrase.this.time).toString()));
                } else if (VotingPhrase.this.time == 0) {
                    for (User user2 : VotingPhrase.this.game.getUsers()) {
                        user2.getPlayer().getInventory().setItem(1, null);
                        user2.getPlayer().updateInventory();
                    }
                    VotingPhrase.this.task.cancel();
                    VotingPhrase.access$4(VotingPhrase.this, false);
                    VotingPhrase.access$5(VotingPhrase.this, VotingPhrase.this.game.getLobbyTime());
                    VotingPhrase.this.game.sendMessage(MessageHandler.getMessage("game-voting-end"));
                    Arena winner = VotingPhrase.this.getMostVotedArena();
                    winner.getSpawns().get(0).getWorld().setTime(0);
                    VotingPhrase.this.game.startCooldown(winner);
                    for (Arena arena : VotingPhrase.this.voteArenas) {
                        arena.setVotes(0);
                    }
                    VotingPhrase.this.voteArenas.clear();
                    VotingPhrase.this.game.getVotedUsers().clear();
                    return;
                }
                VotingPhrase.this.game.updateScoreboard();
                VotingPhrase votingPhrase = VotingPhrase.this;
                VotingPhrase.access$5(votingPhrase, votingPhrase.time - 1);
            }
        }, 0, 20);
    }

    public Inventory getVotingInventory() {
        return this.voteInventory;
    }

    public void equipPlayer(User user) {
        user.getPlayer().getInventory().setItem(1, voteItem);
        user.getPlayer().updateInventory();
    }

    public List<Arena> getArenas() {
        return this.voteArenas;
    }

    public void generateInventory() {
        int arenas = this.voteArenas.size();
        int size = 9;
        if (arenas >= 9) {
            size = 9;
        } else if (arenas >= 18) {
            size = 18;
        } else if (arenas >= 27) {
            size = 27;
        } else if (arenas >= 36) {
            size = 36;
        } else if (arenas >= 45) {
            size = 45;
        } else if (arenas >= 54) {
            size = 54;
        }
        this.voteInventory = Bukkit.createInventory((InventoryHolder)null, (int)size, (String)title);
        int place = size / arenas;
        int c = 0;
        int i = 0;
        while (i < size) {
            Arena a;
            try {
                a = this.voteArenas.get(i);
            }
            catch (IndexOutOfBoundsException e) {
                break;
            }
            if (a == null) break;
            ItemStack is = arenaItem.clone();
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(String.valueOf(i + 1) + ". \u00a7e\u00a7l" + a.getName());
            is.setItemMeta(im);
            this.voteInventory.setItem(c, is);
            c += place;
            ++i;
        }
    }

    public int getTime() {
        return this.time;
    }

    public Arena getMostVotedArena() {
        Arena mostVoted = null;
        int votes = 0;
        for (Arena arena : this.voteArenas) {
            if (arena.getVotes() <= votes) continue;
            votes = arena.getVotes();
            mostVoted = arena;
        }
        if (mostVoted == null) {
            mostVoted = this.voteArenas.get(0);
        }
        return mostVoted;
    }

    public boolean canVote(String player) {
        return !this.game.getVotedUsers().contains(player);
    }

    public Arena vote(Player p, int id) {
        try {
            Arena a = this.voteArenas.get(id - 1);
            if (a != null) {
                int amount = PermissionHandler.getVotePower(p);
                a.setVotes(a.getVotes() + amount);
                this.game.getVotedUsers().add(p.getName());
                p.sendMessage(MessageHandler.getMessage("game-success-vote").replace("%0%", a.getName()));
                if (amount > 1) {
                    p.sendMessage(MessageHandler.getMessage("game-extra-vote").replace("%0%", Integer.valueOf(amount).toString()));
                }
                this.game.updateScoreboard();
            }
            return a;
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void sendVoteMessage() {
        if (this.game.isVotingEnabled()) {
            if (voteItem == null) {
                this.game.sendMessage(MessageHandler.getMessage("game-vote"));
            }
            int i = 1;
            for (Arena arena : this.voteArenas) {
                this.game.sendMessage("\u00a73" + i + "\u00a77. \u00a76" + arena.getName() + " \u00a77(\u00a7e" + arena.getVotes() + "\u00a77)");
                ++i;
            }
        }
    }

    private void chooseRandomArenas() {
        List<Arena> arenas = this.game.getArenas();
        this.voteArenas.clear();
        Collections.shuffle(arenas);
        int i = 0;
        for (Arena a : arenas) {
            if (i == this.game.getMaxVotingArenas()) break;
            this.voteArenas.add(a);
            ++i;
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void cancelTask() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.running = false;
        this.voteArenas.clear();
        this.time = this.game.getLobbyTime();
    }

    static /* synthetic */ void access$4(VotingPhrase votingPhrase, boolean bl) {
        votingPhrase.running = bl;
    }

    static /* synthetic */ void access$5(VotingPhrase votingPhrase, int n) {
        votingPhrase.time = n;
    }

}

