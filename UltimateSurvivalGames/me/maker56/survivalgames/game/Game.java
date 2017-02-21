/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scoreboard.Scoreboard
 */
package me.maker56.survivalgames.game;

import java.util.ArrayList;
import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.CooldownPhrase;
import me.maker56.survivalgames.game.phrase.DeathmatchPhrase;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.ResetPhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.scoreboard.CustomScore;
import me.maker56.survivalgames.scoreboard.ScoreboardPhase;
import me.maker56.survivalgames.sign.SignManager;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import me.maker56.survivalgames.user.UserState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

public class Game {
    private static ItemStack leaveItem;
    private String name;
    private Location lobby;
    private boolean voting;
    private boolean reset;
    private int maxVotingArenas;
    private List<Arena> arenas;
    private int reqplayers;
    private int maxplayers;
    private GameState state;
    private int lobbytime;
    private int cooldown = 30;
    private int death = 0;
    private VotingPhrase votingPhrase;
    private CooldownPhrase cooldownPhrase;
    private IngamePhrase ingamePhrase;
    private DeathmatchPhrase deathmatchPhrase;
    private Arena arena;
    private List<User> users = new ArrayList<User>();
    private List<Chest> chests = new ArrayList<Chest>();
    private List<String> rChunks = new ArrayList<String>();
    public ArrayList<String> voted = new ArrayList();
    private Inventory playerNavigatorInventory;
    private List<SpectatorUser> spectators = new ArrayList<SpectatorUser>();
    private static ItemStack playerNavigator;
    private static String inventoryTitle;
    private boolean forcedStart = false;
    private ScoreboardPhase sp;

    public static ItemStack getLeaveItem() {
        return leaveItem;
    }

    public static void reinitializeDatabase() {
        leaveItem = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Leave-Item"));
        playerNavigator = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Spectating.Player-Navigator.Item"));
        String s = SurvivalGames.instance.getConfig().getString("Spectating.Player-Navigator.Inventory-Title");
        if (s.length() > 32) {
            s = s.substring(0, 32);
        }
        inventoryTitle = ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }

    public Game(String name, Location lobby, boolean voting, int lobbytime, int maxVotingArenas, int reqplayers, List<Arena> arenas, boolean reset) {
        this.name = name;
        this.lobby = lobby;
        this.voting = voting;
        this.lobbytime = lobbytime;
        this.maxVotingArenas = maxVotingArenas;
        this.arenas = arenas;
        this.reset = reset;
        if (reqplayers < 2) {
            reqplayers = 2;
        }
        this.reqplayers = reqplayers;
        this.maxplayers = this.getFewestArena().getSpawns().size();
        this.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.WAITING));
        this.setState(GameState.WAITING);
    }

    public List<String> getVotedUsers() {
        return this.voted;
    }

    public static ItemStack getPlayerNavigatorItem() {
        return playerNavigator;
    }

    public static String getPlayerNavigatorInventoryTitle() {
        return inventoryTitle;
    }

    public Inventory getPlayerNavigatorInventory() {
        return this.playerNavigatorInventory;
    }

    public void redefinePlayerNavigatorInventory() {
        if (playerNavigator != null) {
            int amount = this.getPlayingUsers();
            int inv = 54;
            if (amount <= 9) {
                inv = 9;
            } else if (amount <= 18) {
                inv = 18;
            } else if (amount <= 27) {
                inv = 27;
            } else if (amount <= 36) {
                inv = 36;
            } else if (amount <= 45) {
                inv = 45;
            } else if (amount <= 54) {
                inv = 54;
            }
            this.playerNavigatorInventory = Bukkit.createInventory((InventoryHolder)null, (int)inv, (String)inventoryTitle);
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 0);
            head.setDurability(3);
            ItemMeta im = head.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("\u00a77Click to spectate!");
            int i = 0;
            while (i < this.users.size()) {
                if (i >= inv) break;
                User u = this.users.get(i);
                im.setDisplayName("\u00a7e" + u.getName());
                head.setItemMeta(im);
                this.playerNavigatorInventory.setItem(i, head);
                ++i;
            }
        }
    }

    public void joinSpectator(final SpectatorUser user) {
        this.spectators.add(user);
        Arena a = this.getCurrentArena();
        if (this.getState() == GameState.DEATHMATCH) {
            user.getPlayer().teleport(a.getDeathmatchSpawns().get(0));
        } else {
            user.getPlayer().teleport(a.getSpawns().get(0));
        }
        for (User u : this.users) {
            u.getPlayer().hidePlayer(user.getPlayer());
        }
        user.clear();
        user.getPlayer().setAllowFlight(true);
        user.getPlayer().setFlying(true);
        user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                user.getPlayer().getInventory().setItem(8, leaveItem);
                user.getPlayer().getInventory().setItem(7, playerNavigator);
                user.getPlayer().updateInventory();
            }
        }, 2);
        this.sendSpectators(MessageHandler.getMessage("spectator-join").replace("%0%", user.getName()));
        this.updateScoreboard();
    }

    public void leaveSpectator(SpectatorUser user) {
        this.spectators.remove(user);
        this.updateScoreboard();
    }

    public void sendSpectators(String msg) {
        for (SpectatorUser su : this.spectators) {
            su.sendMessage(msg);
        }
    }

    public List<SpectatorUser> getSpecators() {
        return this.spectators;
    }

    public User getUser(String name) {
        for (User u : this.users) {
            if (!u.getName().equals(name)) continue;
            return u;
        }
        return null;
    }

    public int getDeathAmount() {
        return this.death;
    }

    public void setDeathAmount(int death) {
        this.death = death;
    }

    public void join(User user) {
        Player p;
        this.users.add(user);
        p = user.getPlayer();
        if (this.arenas.size() == 1) {
            Arena arena = this.arenas.get(0);
            int i = 0;
            while (i < arena.getSpawns().size()) {
                if (!this.hasUserIndex(i)) {
                    p.teleport(arena.getSpawns().get(i));
                    user.setSpawnIndex(i);
                    break;
                }
                ++i;
            }
        } else if (this.getState() == GameState.COOLDOWN) {
            int i = 0;
            while (i < this.getCurrentArena().getSpawns().size()) {
                if (!this.hasUserIndex(i)) {
                    p.teleport(this.getCurrentArena().getSpawns().get(i));
                    user.setSpawnIndex(i);
                    break;
                }
                ++i;
            }
        } else {
            p.teleport(this.lobby);
        }
        user.clear();
        p.getInventory().setItem(7, leaveItem);
        p.updateInventory();
        if (this.getState() == GameState.VOTING) {
            this.getVotingPhrase().equipPlayer(user);
        }
        this.sendMessage(MessageHandler.getMessage("join-success").replace("%0%", p.getName()).replace("%1%", Integer.valueOf(this.users.size()).toString()).replace("%2%", Integer.valueOf(this.maxplayers).toString()));
        SurvivalGames.signManager.updateSigns();
        this.updateScoreboard();
        this.checkForStart();
    }

    public void forceStart(Player p) {
        if (this.users.size() < 2) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cAt least 2 players are required to start the game!");
            return;
        }
        if (this.getVotingPhrase() != null && this.getVotingPhrase().isRunning() || this.getCooldownPhrase() != null && this.getCooldownPhrase().isRunning()) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cThe game is already starting!");
            return;
        }
        this.forcedStart = true;
        this.checkForStart();
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "You've started the game in lobby " + this.getName() + " successfully!");
    }

    public void leave(User user) {
        this.users.remove(user);
        if (this.getState() == GameState.INGAME || this.getState() == GameState.DEATHMATCH) {
            this.redefinePlayerNavigatorInventory();
        }
        this.checkForCancelStart();
        this.updateScoreboard();
        SurvivalGames.signManager.updateSigns();
    }

    public void kickall() {
        int i;
        int size;
        if (this.users.size() != 0) {
            size = this.users.size();
            i = 0;
            while (i < size) {
                try {
                    SurvivalGames.userManger.leaveGame(this.users.get(0).getPlayer());
                }
                catch (IndexOutOfBoundsException e) {
                    break;
                }
                ++i;
            }
        }
        if (this.spectators.size() != 0) {
            size = this.spectators.size();
            i = 0;
            while (i < size) {
                try {
                    SurvivalGames.userManger.leaveGame(this.spectators.get(0));
                }
                catch (IndexOutOfBoundsException e) {
                    break;
                }
                ++i;
            }
        }
    }

    public void end() {
        new me.maker56.survivalgames.game.phrase.ResetPhrase(this);
    }

    public DeathmatchPhrase getDeathmatch() {
        return this.deathmatchPhrase;
    }

    public void startDeathmatch() {
        this.deathmatchPhrase = new DeathmatchPhrase(this);
        this.deathmatchPhrase.load();
    }

    public void startIngame() {
        this.ingamePhrase = new IngamePhrase(this);
        this.ingamePhrase.load();
    }

    public void startCooldown(Arena arena) {
        this.cooldownPhrase = new CooldownPhrase(this, arena);
        this.cooldownPhrase.load();
    }

    public boolean isResetEnabled() {
        return this.reset;
    }

    public void checkForStart() {
        if (this.users.size() == this.reqplayers || this.forcedStart) {
            if (this.cooldownPhrase != null && this.cooldownPhrase.isRunning()) {
                return;
            }
            if (this.votingPhrase != null && this.votingPhrase.isRunning()) {
                return;
            }
            if (this.getArenas().size() == 1) {
                this.startCooldown(this.getArenas().get(0));
            } else if (this.cooldownPhrase != null) {
                this.startCooldown(this.getArenas().get(0));
            } else {
                this.votingPhrase = new VotingPhrase(this);
                this.votingPhrase.load();
            }
        }
    }

    public void checkForCancelStart() {
        if (this.state != GameState.VOTING && this.state != GameState.COOLDOWN) {
            return;
        }
        if (this.forcedStart) {
            if (this.users.size() == 1) {
                if (this.getState() == GameState.COOLDOWN) {
                    this.cooldownPhrase.cancelTask();
                    this.sendMessage(MessageHandler.getMessage("game-start-canceled"));
                } else if (this.getState() == GameState.VOTING) {
                    this.votingPhrase.cancelTask();
                    this.sendMessage(MessageHandler.getMessage("game-start-canceled"));
                }
                this.forcedStart = false;
            }
        } else if (this.users.size() == this.reqplayers - 1) {
            if (this.getState() == GameState.COOLDOWN) {
                this.cooldownPhrase.cancelTask();
                this.sendMessage(MessageHandler.getMessage("game-start-canceled"));
            } else if (this.getState() == GameState.VOTING) {
                this.votingPhrase.cancelTask();
                this.sendMessage(MessageHandler.getMessage("game-start-canceled"));
            }
        }
    }

    public IngamePhrase getIngamePhrase() {
        return this.ingamePhrase;
    }

    public VotingPhrase getVotingPhrase() {
        return this.votingPhrase;
    }

    public CooldownPhrase getCooldownPhrase() {
        return this.cooldownPhrase;
    }

    public Arena getFewestArena() {
        int slot = Integer.MAX_VALUE;
        Arena arena = null;
        for (Arena a : this.arenas) {
            if (a.getSpawns().size() >= slot) continue;
            slot = a.getSpawns().size();
            arena = a;
        }
        return arena;
    }

    public Arena getArena(String name) {
        for (Arena arena : this.arenas) {
            if (!arena.getName().equals(name)) continue;
            return arena;
        }
        return null;
    }

    public void setCurrentArena(Arena arena) {
        this.arena = arena;
    }

    public Arena getCurrentArena() {
        return this.arena;
    }

    public void setState(GameState state) {
        this.state = state;
        if (SurvivalGames.signManager != null) {
            SurvivalGames.signManager.updateSigns();
        }
    }

    public List<User> getUsers() {
        return this.users;
    }

    public int getPlayingUsers() {
        return this.users.size();
    }

    public String getName() {
        return this.name;
    }

    public Location getLobby() {
        return this.lobby;
    }

    public int getLobbyTime() {
        return this.lobbytime;
    }

    public int getRequiredPlayers() {
        return this.reqplayers;
    }

    public int getMaximumPlayers() {
        return this.maxplayers;
    }

    public boolean isVotingEnabled() {
        return this.voting;
    }

    public int getMaxVotingArenas() {
        return this.maxVotingArenas;
    }

    public List<Arena> getArenas() {
        return this.arenas;
    }

    public GameState getState() {
        return this.state;
    }

    public int getCooldownTime() {
        return this.cooldown;
    }

    public void sendMessage(String message) {
        for (User user : this.users) {
            user.sendMessage(message);
        }
        for (SpectatorUser su : this.spectators) {
            su.sendMessage(message);
        }
    }

    public boolean hasUserIndex(int index) {
        for (User user : this.users) {
            if (user.getSpawnIndex() != index) continue;
            return true;
        }
        return false;
    }

    public void registerChest(Chest chest) {
        this.chests.add(chest);
    }

    public List<Chest> getRegisteredChests() {
        return this.chests;
    }

    public Chest getChest(Location loc) {
        for (Chest chest : this.chests) {
            if (!chest.getLocation().equals((Object)loc)) continue;
            return chest;
        }
        return null;
    }

    public boolean isChestRegistered(Location loc) {
        for (Chest chest : this.chests) {
            if (!chest.getLocation().equals((Object)loc)) continue;
            return true;
        }
        return false;
    }

    public List<String> getChunksToReset() {
        return this.rChunks;
    }

    public String getAlivePlayers() {
        String s = new String();
        List<User> users = this.getUsers();
        int i = 0;
        while (i < users.size()) {
            s = String.valueOf(s) + "\u00a7e" + users.get(i).getName();
            if (i != users.size() - 1) {
                s = String.valueOf(s) + "\u00a77, ";
            }
            ++i;
        }
        return s;
    }

    public void setScoreboardPhase(ScoreboardPhase sp) {
        if (this.sp != null && sp == null) {
            for (User user2 : this.users) {
                user2.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            for (SpectatorUser user : this.spectators) {
                user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
        this.sp = sp;
        if (sp != null) {
            sp.initScoreboard(this);
            this.updateScoreboard();
        }
    }

    public ScoreboardPhase getScoreboardPhase() {
        return this.sp;
    }

    public void updateScoreboard() {
        if (this.sp != null) {
            for (CustomScore cs : this.sp.getScores()) {
                cs.update(this);
            }
            for (User user : this.users) {
                this.updateScoreboard(user);
            }
            for (SpectatorUser su : this.spectators) {
                this.updateScoreboard(su);
            }
        }
    }

    public void updateScoreboard(UserState user) {
        user.getPlayer().setScoreboard(this.sp.getScoreboard());
    }

}

