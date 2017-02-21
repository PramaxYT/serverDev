/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.user;

import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.UserState;
import org.bukkit.entity.Player;

public class User
extends UserState {
    private int spawnIndex = Integer.MIN_VALUE;

    public User(Player player, Game game) {
        super(player, game);
        this.player = player;
    }

    public void setSpawnIndex(int index) {
        this.spawnIndex = index;
    }

    public int getSpawnIndex() {
        return this.spawnIndex;
    }
}

