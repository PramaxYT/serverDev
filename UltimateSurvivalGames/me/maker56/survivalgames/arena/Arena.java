/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 */
package me.maker56.survivalgames.arena;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Arena
implements Cloneable {
    private Location min;
    private Location max;
    private List<Location> spawns;
    private int graceperiod;
    private String name;
    private String game;
    private List<Integer> allowedBlocks;
    private double moneyKill;
    private double moneyWin;
    private Material chesttype;
    private int chestdata;
    private boolean deathmatch;
    private boolean refill;
    private List<Location> deathmatchSpawns;
    private int autodeathmatch;
    private int playerdeathmatch;
    private int votes = 0;

    public Arena(Location min, Location max, List<Location> spawns, Material chesttype, int chestdata, int graceperiod, String name, String game, boolean deathmatch, List<Location> deathmatchspawns, List<Integer> allowedBlocks, int autodeathmatch, int playerdeathmatch, double moneyKill, double moneyWin, boolean chestrefill) {
        this.min = min;
        this.max = max;
        this.spawns = spawns;
        this.graceperiod = graceperiod;
        this.name = name;
        this.game = game;
        this.allowedBlocks = allowedBlocks;
        this.chesttype = chesttype;
        this.chestdata = chestdata;
        this.deathmatch = deathmatch;
        this.deathmatchSpawns = deathmatchspawns;
        this.autodeathmatch = autodeathmatch;
        this.playerdeathmatch = playerdeathmatch;
        this.moneyKill = moneyKill;
        this.moneyWin = moneyWin;
        this.refill = chestrefill;
        min.getWorld().setStorm(false);
    }

    public int getAutomaticlyDeathmatchTime() {
        return this.autodeathmatch;
    }

    public int getPlayerDeathmatchAmount() {
        return this.playerdeathmatch;
    }

    public boolean isDeathmatchEnabled() {
        return this.deathmatch;
    }

    public List<Location> getDeathmatchSpawns() {
        return this.deathmatchSpawns;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getVotes() {
        return this.votes;
    }

    public Material getChestType() {
        return this.chesttype;
    }

    public int getChestData() {
        return this.chestdata;
    }

    public List<Integer> getAllowedMaterials() {
        return this.allowedBlocks;
    }

    public double getMoneyOnKill() {
        return this.moneyKill;
    }

    public double getMoneyOnWin() {
        return this.moneyWin;
    }

    public boolean chestRefill() {
        return this.refill;
    }

    public boolean containsBlock(Location loc) {
        if (!loc.getWorld().equals((Object)this.min.getWorld())) {
            return false;
        }
        if (loc.getBlockX() >= this.min.getBlockX() && loc.getBlockX() <= this.max.getBlockX() && loc.getBlockY() >= this.min.getBlockY() && loc.getBlockY() <= this.max.getBlockY() && loc.getBlockZ() >= this.min.getBlockZ() && loc.getBlockX() <= this.max.getBlockZ()) {
            return true;
        }
        return false;
    }

    public Location getMinimumLocation() {
        return this.min;
    }

    public Location getMaximumLocation() {
        return this.max;
    }

    public List<Location> getSpawns() {
        return this.spawns;
    }

    public int getGracePeriod() {
        return this.graceperiod;
    }

    public String getName() {
        return this.name;
    }

    public String getGame() {
        return this.game;
    }
}

