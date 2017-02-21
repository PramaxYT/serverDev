/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package me.maker56.survivalgames.reset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.events.ResetDoneEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class Reset
extends Thread {
    private static int sleep = 10;
    private static List<String> resets = new ArrayList<String>();
    private String lobby;
    private String arena;
    private World world;
    private long start;
    private List<String> chunks = new ArrayList<String>();
    private List<String> cReset = new ArrayList<String>();
    private boolean build = false;

    public static boolean isResetting(String lobby, String arena) {
        return resets.contains(String.valueOf(lobby) + arena);
    }

    public static boolean isResseting(String lobby) {
        for (String key : resets) {
            if (!key.startsWith(lobby)) continue;
            return true;
        }
        return false;
    }

    public Reset(World w, String lobby, String arena, List<String> chunks) {
        this.world = w;
        this.lobby = lobby;
        this.arena = arena;
        this.chunks = chunks;
    }

    @Override
    public void run() {
        if (Reset.isResetting(this.lobby, this.arena)) {
            return;
        }
        System.out.println("[SurvivalGames] Start arena reset... (arena " + this.arena + ", lobby " + this.lobby + ")");
        this.setPriority(1);
        resets.add(String.valueOf(this.lobby) + this.arena);
        this.start = System.currentTimeMillis();
        File file = new File("plugins/SurvivalGames/reset/" + this.lobby + this.arena + ".map");
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            boolean add = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(";")) {
                    String chunkKey;
                    if (add) {
                        this.reset();
                        while (this.build) {
                            Reset.sleep(sleep);
                        }
                        this.cReset.clear();
                        add = false;
                    }
                    if (!this.chunks.contains(chunkKey = line.substring(1))) continue;
                    add = true;
                    continue;
                }
                if (!add) continue;
                this.cReset.add(line);
            }
            br.close();
        }
        catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            resets.remove(String.valueOf(this.lobby) + this.arena);
            return;
        }
        Bukkit.getScheduler().callSyncMethod((Plugin)SurvivalGames.instance, (Callable)new Callable<Void>(){

            @Override
            public Void call() {
                resets.remove(String.valueOf(Reset.this.lobby) + Reset.this.arena);
                SurvivalGames.reset.set("Startup-Reset." + Reset.this.lobby + "." + Reset.this.arena, (Object)null);
                SurvivalGames.saveReset();
                int time = (int)((System.currentTimeMillis() - Reset.this.start) / 1000);
                System.out.println("[SurvivalGames] Finished arena reset! (arena " + Reset.this.arena + ", lobby " + Reset.this.lobby + ") Time: " + time + " seconds!");
                Bukkit.getPluginManager().callEvent((Event)new ResetDoneEvent(Reset.this.lobby, Reset.this.arena, time));
                return null;
            }
        });
    }

    private void reset() {
        this.build = true;
        Bukkit.getScheduler().callSyncMethod((Plugin)SurvivalGames.instance, (Callable)new Callable<Void>(){

            @Override
            public Void call() {
                for (String restoreKey : Reset.this.cReset) {
                    int i;
                    if (restoreKey.startsWith(";")) continue;
                    String[] key = restoreKey.split(",");
                    Location loc = new Location(Reset.this.world, (double)Integer.parseInt(key[0]), (double)Integer.parseInt(key[1]), (double)Integer.parseInt(key[2]));
                    Block b = loc.getBlock();
                    if (key.length == 5) {
                        i = Integer.parseInt(key[3]);
                        byte by = Byte.parseByte(key[4]);
                        if (b.getTypeId() == i && b.getData() == by) continue;
                        b.setTypeIdAndData(i, by, false);
                        continue;
                    }
                    if (key.length == 4) {
                        i = Integer.parseInt(key[3]);
                        if (b.getTypeId() == i) continue;
                        b.setTypeId(i, false);
                        continue;
                    }
                    if (b.getTypeId() == 0) continue;
                    b.setTypeId(0, false);
                }
                Reset.access$6(Reset.this, false);
                return null;
            }
        });
    }

    static /* synthetic */ void access$6(Reset reset, boolean bl) {
        reset.build = bl;
    }

}

