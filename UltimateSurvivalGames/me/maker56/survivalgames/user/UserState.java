/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.maker56.survivalgames.user;

import java.util.Collection;
import java.util.Iterator;
import me.maker56.survivalgames.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class UserState {
    protected Player player;
    private double health;
    private float walk;
    private float fly;
    private int food;
    private float exp;
    private int level;
    private int fireticks;
    private ItemStack[][] inventory;
    private Location loc;
    private GameMode gamemode;
    private boolean allowFlying;
    private boolean flying;
    private Collection<PotionEffect> ape;
    private float fall;
    private long joinTime = System.currentTimeMillis();
    private Game game;

    public UserState(Player p, Game game) {
        this.game = game;
        this.player = p;
        this.health = p.getHealth();
        this.food = p.getFoodLevel();
        this.exp = p.getExp();
        this.level = p.getLevel();
        this.fireticks = p.getFireTicks();
        this.loc = p.getLocation();
        this.gamemode = p.getGameMode();
        this.allowFlying = p.getAllowFlight();
        this.flying = p.isFlying();
        this.ape = p.getActivePotionEffects();
        this.fall = p.getFallDistance();
        this.walk = p.getWalkSpeed();
        this.fly = p.getFlySpeed();
        ItemStack[][] store = new ItemStack[2][1];
        store[0] = p.getInventory().getContents();
        store[1] = p.getInventory().getArmorContents();
        this.inventory = store;
    }

    public Game getGame() {
        return this.game;
    }

    public long getJoinTime() {
        return this.joinTime;
    }

    public float getFallDistance() {
        return this.fall;
    }

    public float getWalkSpeed() {
        return this.walk;
    }

    public float getFlySpeed() {
        return this.fly;
    }

    public ItemStack[] getContents() {
        return this.inventory[0];
    }

    public ItemStack[] getArmorContents() {
        return this.inventory[1];
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.ape;
    }

    public GameMode getGameMode() {
        return this.gamemode;
    }

    public boolean getAllowFlight() {
        return this.allowFlying;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public Location getLocation() {
        return this.loc;
    }

    public double getHealth() {
        return this.health;
    }

    public int getFoodLevel() {
        return this.food;
    }

    public float getExp() {
        return this.exp;
    }

    public int getLevel() {
        return this.level;
    }

    public int getFireTicks() {
        return this.fireticks;
    }

    public ItemStack[][] getInventory() {
        return this.inventory;
    }

    public void clear() {
        Iterator i = this.player.getActivePotionEffects().iterator();
        while (i.hasNext()) {
            this.player.removePotionEffect(((PotionEffect)i.next()).getType());
        }
        this.player.setWalkSpeed(0.2f);
        this.player.setFlySpeed(0.1f);
        this.player.setHealth(20.0);
        this.player.setFoodLevel(20);
        this.player.setLevel(0);
        this.player.setExp(0.0f);
        this.player.setFireTicks(0);
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setFlying(false);
        this.player.setAllowFlight(false);
        this.clearInventory();
    }

    public void clearInventory() {
        ItemStack[] inv = this.player.getInventory().getContents();
        int i = 0;
        while (i < inv.length) {
            inv[i] = null;
            ++i;
        }
        this.player.getInventory().setContents(inv);
        inv = this.player.getInventory().getArmorContents();
        i = 0;
        while (i < inv.length) {
            inv[i] = null;
            ++i;
        }
        this.player.getInventory().setArmorContents(inv);
        this.player.updateInventory();
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getName() {
        return this.player.getName();
    }

    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }
}

