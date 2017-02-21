/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.WorldEditPlugin
 *  com.sk89q.worldedit.bukkit.selections.Selection
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.arena;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.listener.SelectionListener;
import me.maker56.survivalgames.reset.Reset;
import me.maker56.survivalgames.reset.Save;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ArenaManager {
    private static FileConfiguration cfg = SurvivalGames.database;
    public HashMap<String, String[]> selectedarena = new HashMap();

    public static void reinitializeDatabase() {
        cfg = SurvivalGames.database;
    }

    public Arena getArena(Location loc) {
        for (Game game : SurvivalGames.gameManager.getGames()) {
            for (Arena arena : game.getArenas()) {
                if (!arena.containsBlock(loc)) continue;
                return arena;
            }
        }
        return null;
    }

    public void save(Player p) {
        String arenaname;
        Arena arena;
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        if (Save.isSaveing(gamename, arenaname = this.selectedarena.get(p.getName())[1]) || Reset.isResetting(gamename, arenaname)) {
            Bukkit.broadcastMessage((String)(String.valueOf(Boolean.valueOf(Save.isSaveing(gamename, arenaname)).toString()) + " " + Boolean.valueOf(Reset.isResetting(gamename, arenaname))));
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cThis arena is already saveing or resetting.");
            return;
        }
        Game game = SurvivalGames.gameManager.getGame(gamename);
        if (game != null && (arena = game.getArena(arenaname)) != null) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cYou can only save arenas of an unloaded lobby.");
            return;
        }
        Location min = ConfigUtil.parseLocation(cfg.getString("Games." + gamename + ".Arenas." + arenaname + ".Min"));
        Location max = ConfigUtil.parseLocation(cfg.getString("Games." + gamename + ".Arenas." + arenaname + ".Max"));
        if (min == null || max == null) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "The arena isn't defined yet.");
            return;
        }
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Saveing arena... This may take a while. Laggs can be occure. You'll get a message, if the save is completed.");
        new Save(gamename, arenaname, min, max, p.getName()).start();
    }

    public void delete(Player p) {
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        if (!cfg.contains("Games." + gamename)) {
            p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
            return;
        }
        if (!cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cArena " + arenaname + " in lobby " + gamename + " not found!");
            return;
        }
        Game game = SurvivalGames.gameManager.getGame(gamename);
        if (game != null) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cYou can only delete arenas of an unloaded lobby.");
            return;
        }
        cfg.set("Games." + gamename + ".Arenas." + arenaname, (Object)null);
        SurvivalGames.saveDataBase();
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Arena " + arenaname + " was deleted in lobby " + gamename + " successfull!");
    }

    public void check(Player p) {
        int spawns;
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Arena-Check: Arena \u00a7e" + arenaname + "\u00a76, Game \u00a7e" + gamename);
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        boolean enabled = cfg.getBoolean(String.valueOf(path) + "Enabled");
        if (enabled) {
            p.sendMessage("\u00a7aThis arena is ready to play!");
        }
        if ((spawns = cfg.getStringList(String.valueOf(path) + "Spawns").size()) < 2) {
            p.sendMessage(" \u00a78\u00a7l\u27a5 \u00a7bSpawns \u00a77(\u00a7c" + spawns + "\u00a77) \u00a7eAt least 2 Spawns required");
        } else {
            p.sendMessage(" \u00a78\u00a7l\u27a5 \u00a7bSpawns \u00a77(\u00a7a" + spawns + "\u00a77) \u00a7eAt least 2 Spawns required");
        }
        boolean deathmatch = cfg.getBoolean(String.valueOf(path) + "Enable-Deathmatch");
        int dspawns = cfg.getStringList(String.valueOf(path) + "Deathmatch-Spawns").size();
        p.sendMessage(" \u00a78\u00a7l\u2013\u00ba \u00a7bDeathmatch \u00a77(\u00a7a" + deathmatch + "\u00a77) \u00a7e(optional)");
        if (deathmatch) {
            if (dspawns < 1) {
                p.sendMessage(" \u00a78\u00a7l\u27a5 \u00a7bDeathmatch-Spawns \u00a77(\u00a7c" + dspawns + "\u00a77) \u00a7eAt least 1 Deathmatch Spawn required");
            } else {
                p.sendMessage(" \u00a78\u00a7l\u27a5 \u00a7bDeathmatch-Spawns \u00a77(\u00a7a" + dspawns + "\u00a77) \u00a7eAt least 1 Deathmatch Spawn required");
            }
        }
        p.sendMessage("   ");
        p.sendMessage("\u00a7e\u00a7lNext step:");
        if (spawns < 2) {
            p.sendMessage("\u00a7aAt least are 2 Spawns required. Type \u00a7b/sg arena addspawn \u00a7ato add more spawns!");
        } else if (deathmatch && dspawns < 1) {
            p.sendMessage("\u00a7aAt least are 1 Deathmatch-Spawn required. Type \u00a7b/sg arena deathmatch add \u00a7ato add more Deathmatch-Spawns!");
        } else {
            p.sendMessage("\u00a7aThis arena is ready to play. Just type \u00a7b/sg arena finish \u00a7ato finish the setup!");
        }
    }

    public void finishSetup(Player p) {
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        if (SurvivalGames.gameManager.getGame(gamename) != null) {
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cYou can't add an arena to a loaded lobby. Unload the lobby first with /sg lobby unload " + gamename);
        } else {
            cfg.set(String.valueOf(path) + "Enabled", (Object)true);
            SurvivalGames.saveDataBase();
            Game game = SurvivalGames.gameManager.getGame(gamename);
            if (game == null) {
                SurvivalGames.gameManager.unload(game);
            }
            SurvivalGames.gameManager.load(gamename);
            p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7aYou've finished the setup and activated the arena successfully!");
        }
    }

    public void changeDeathmatch(Player p) {
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        boolean deathmatch = cfg.getBoolean(String.valueOf(path) + "Enable-Deathmatch");
        if (deathmatch) {
            cfg.set(String.valueOf(path) + "Enable-Deathmatch", (Object)false);
            p.sendMessage(MessageHandler.getMessage("arena-deathmatch-changed").replace("%0%", "\u00a7cFALSE"));
        } else {
            cfg.set(String.valueOf(path) + "Enable-Deathmatch", (Object)true);
            p.sendMessage(MessageHandler.getMessage("arena-deathmatch-changed").replace("%0%", "\u00a7aTRUE"));
        }
        SurvivalGames.saveDataBase();
    }

    public void addSpawn(Player p, String type) {
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        List l = cfg.getStringList(String.valueOf(path) + type);
        Location loc = p.getLocation();
        l.add(String.valueOf(loc.getWorld().getName()) + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
        cfg.set(String.valueOf(path) + type, (Object)l);
        SurvivalGames.saveDataBase();
        p.sendMessage(MessageHandler.getMessage("arena-spawn-added").replace("%0%", Integer.valueOf(l.size()).toString()));
    }

    public void removeSpawn(Player p, int id, String type) {
        if (!this.selectedarena.containsKey(p.getName())) {
            p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
            return;
        }
        String gamename = this.selectedarena.get(p.getName())[0];
        String arenaname = this.selectedarena.get(p.getName())[1];
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        --id;
        List l = cfg.getStringList(String.valueOf(path) + type);
        try {
            l.get(id);
        }
        catch (IndexOutOfBoundsException e) {
            p.sendMessage(MessageHandler.getMessage("arena-spawn-notfound").replace("%0%", Integer.valueOf(++id).toString()));
            return;
        }
        l.remove(id);
        cfg.set(String.valueOf(path) + type, (Object)l);
        SurvivalGames.saveDataBase();
        p.sendMessage(MessageHandler.getMessage("arena-spawn-removed").replace("%0%", Integer.valueOf(++id).toString()));
    }

    /*
     * Enabled aggressive block sorting
     */
    public void createArena(Player p, String arenaname, String gamename) {
        if (!cfg.contains("Games." + gamename)) {
            p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
            return;
        }
        if (cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
            p.sendMessage(MessageHandler.getMessage("arena-already-exists").replace("%0%", arenaname).replace("%1%", gamename));
            return;
        }
        WorldEditPlugin we = SurvivalGames.getWorldEdit();
        Location min = null;
        Location max = null;
        if (we == null) {
            if (!SelectionListener.selection.containsKey(p.getName())) {
                p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
                return;
            }
            Location[] loc = SelectionListener.selection.get(p.getName());
            if (loc[0] == null || loc[1] == null) {
                p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
                return;
            }
            min = new Location(loc[0].getWorld(), (double)Math.min(loc[0].getBlockX(), loc[1].getBlockX()), (double)Math.min(loc[0].getBlockY(), loc[1].getBlockY()), (double)Math.min(loc[0].getBlockZ(), loc[1].getBlockZ()));
            min = new Location(loc[0].getWorld(), (double)Math.max(loc[0].getBlockX(), loc[1].getBlockX()), (double)Math.max(loc[0].getBlockY(), loc[1].getBlockY()), (double)Math.max(loc[0].getBlockZ(), loc[1].getBlockZ()));
        } else {
            Selection sel = we.getSelection(p);
            if (sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
                p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
                return;
            }
            min = sel.getMinimumPoint();
            max = sel.getMaximumPoint();
        }
        int chesttype = SurvivalGames.instance.getConfig().getInt("Default.Arena.Chests.TypeID");
        int chestdata = SurvivalGames.instance.getConfig().getInt("Default.Arena.Chests.Data");
        String path = "Games." + gamename + ".Arenas." + arenaname + ".";
        cfg.set(String.valueOf(path) + "Enabled", (Object)false);
        cfg.set(String.valueOf(path) + "Grace-Period", (Object)SurvivalGames.instance.getConfig().getInt("Default.Arena.Grace-Period"));
        cfg.set(String.valueOf(path) + "Min", (Object)(String.valueOf(min.getWorld().getName()) + "," + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ()));
        cfg.set(String.valueOf(path) + "Max", (Object)(String.valueOf(max.getWorld().getName()) + "," + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ()));
        cfg.set(String.valueOf(path) + "Allowed-Blocks", (Object)SurvivalGames.instance.getConfig().getIntegerList("Default.Arena.Allowed-Blocks"));
        cfg.set(String.valueOf(path) + "Chest.TypeID", (Object)chesttype);
        cfg.set(String.valueOf(path) + "Chest.Data", (Object)chestdata);
        cfg.set(String.valueOf(path) + "Spawns", new ArrayList());
        cfg.set(String.valueOf(path) + "Enable-Deathmatch", (Object)false);
        cfg.set(String.valueOf(path) + "Player-Deathmatch", (Object)SurvivalGames.instance.getConfig().getInt("Default.Arena.Player-Deathmatch-Start"));
        cfg.set(String.valueOf(path) + "Auto-Deathmatch", (Object)SurvivalGames.instance.getConfig().getInt("Default.Arena.Automaticly-Deathmatch-Time"));
        cfg.set(String.valueOf(path) + "Deathmatch-Spawns", new ArrayList());
        cfg.set(String.valueOf(path) + "Money-on-Kill", (Object)SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Kill"));
        cfg.set(String.valueOf(path) + "Money-on-Win", (Object)SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Win"));
        cfg.set(String.valueOf(path) + "Midnight-chest-refill", (Object)SurvivalGames.instance.getConfig().getBoolean("Default.Midnight-chest-refill"));
        SurvivalGames.saveDataBase();
        this.selectArena(p, arenaname, gamename);
        p.sendMessage(MessageHandler.getMessage("arena-created").replace("%0%", arenaname).replace("%1%", gamename));
        if (SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset")) {
            this.save(p);
        }
        p.sendMessage(MessageHandler.getMessage("arena-check").replace("%0%", "/sg arena check"));
    }

    public void selectArena(Player p, String arenaname, String gamename) {
        if (!cfg.contains("Games." + gamename)) {
            p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
            return;
        }
        if (!cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
            p.sendMessage(MessageHandler.getMessage("arena-not-found").replace("%0%", arenaname).replace("%1%", gamename));
            return;
        }
        this.selectedarena.put(p.getName(), new String[]{gamename, arenaname});
        p.sendMessage(MessageHandler.getMessage("arena-selected").replace("%0%", arenaname).replace("%1%", gamename));
    }

    public Arena getArena(String game, String arenaname) {
        if (!new File("plugins/SurvivalGames/reset/" + game + arenaname + ".map").exists() && SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset")) {
            System.out.println("[SurvivalGames] Cannot load arena " + arenaname + " in lobby " + game + ": Arena map file is missing! To create a map file, select the arena first with /sg arena select " + game + " " + arenaname + " and type /sg arena save!");
            return null;
        }
        String path = "Games." + game + ".Arenas." + arenaname + ".";
        Location min = ConfigUtil.parseLocation(cfg.getString(String.valueOf(path) + "Min"));
        Location max = ConfigUtil.parseLocation(cfg.getString(String.valueOf(path) + "Max"));
        int graceperiod = cfg.getInt(String.valueOf(path) + "Grace-Period");
        Material chesttype = Material.getMaterial((int)cfg.getInt(String.valueOf(path) + "Chest.TypeID"));
        int chestdata = cfg.getInt(String.valueOf(path) + "Chest.Data");
        ArrayList<Location> spawns = new ArrayList<Location>();
        for (String key : cfg.getStringList(String.valueOf(path) + "Spawns")) {
            spawns.add(ConfigUtil.parseLocation(key));
        }
        boolean deathmatch = cfg.getBoolean(String.valueOf(path) + "Enable-Deathmatch");
        ArrayList<Location> deathmatchspawns = new ArrayList<Location>();
        if (deathmatch) {
            for (String key2 : cfg.getStringList(String.valueOf(path) + "Deathmatch-Spawns")) {
                deathmatchspawns.add(ConfigUtil.parseLocation(key2));
            }
        }
        List allowedBlocks = cfg.getIntegerList(String.valueOf(path) + "Allowed-Blocks");
        int autodeathmatch = cfg.getInt(String.valueOf(path) + "Auto-Deathmatch");
        int playerdeathmatch = cfg.getInt(String.valueOf(path) + "Player-Deathmatch");
        if (!cfg.contains(String.valueOf(path) + "Money-on-Kill")) {
            cfg.set(String.valueOf(path) + "Money-on-Kill", (Object)SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Kill"));
            cfg.set(String.valueOf(path) + "Money-on-Win", (Object)SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Win"));
            cfg.set(String.valueOf(path) + "Midnight-chest-refill", (Object)SurvivalGames.instance.getConfig().getBoolean("Default.Midnight-chest-refill"));
            SurvivalGames.saveDataBase();
        }
        double kill = cfg.getDouble(String.valueOf(path) + "Money-on-Kill");
        double win = cfg.getDouble(String.valueOf(path) + "Money-on-Win");
        boolean refill = cfg.getBoolean(String.valueOf(path) + "Midnight-chest-refill");
        return new Arena(min, max, spawns, chesttype, chestdata, graceperiod, arenaname, game, deathmatch, deathmatchspawns, allowedBlocks, autodeathmatch, playerdeathmatch, kill, win, refill);
    }
}

