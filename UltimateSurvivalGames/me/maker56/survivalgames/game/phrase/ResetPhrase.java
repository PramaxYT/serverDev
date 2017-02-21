/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Animals
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Monster
 */
package me.maker56.survivalgames.game.phrase;

import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.reset.Reset;
import me.maker56.survivalgames.sign.SignManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

public class ResetPhrase {
    private Game game;

    public ResetPhrase(Game game) {
        this.game = game;
        this.start();
    }

    private void start() {
        this.game.kickall();
        this.game.setState(GameState.RESET);
        World w = this.game.getCurrentArena().getMinimumLocation().getWorld();
        for (Entity e : w.getEntities()) {
            if (!(e instanceof Item) && !(e instanceof Animals) && !(e instanceof Monster) && !(e instanceof Arrow) || !this.game.getCurrentArena().containsBlock(e.getLocation())) continue;
            e.remove();
        }
        if (this.game.isResetEnabled()) {
            new Reset(w, this.game.getName(), this.game.getCurrentArena().getName(), this.game.getChunksToReset()).start();
        } else {
            String name = this.game.getName();
            SurvivalGames.gameManager.unload(this.game);
            SurvivalGames.gameManager.load(name);
            SurvivalGames.signManager.updateSigns();
        }
    }
}

