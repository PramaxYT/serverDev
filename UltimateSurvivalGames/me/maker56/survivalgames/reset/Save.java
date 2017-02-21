/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.events.SaveDoneEvent;
import me.maker56.survivalgames.reset.Reset;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Save
extends Thread {
    private PrintWriter pw;
    private String lobby;
    private String arena;
    private Location min;
    private Location max;
    private long start;
    private BukkitTask task;
    private double writeStepsDone;
    private double writeSteps;
    private String player;
    private List<Chunk> chunks = new ArrayList<Chunk>();
    private Chunk chunk;
    private World world;
    int size;
    String format;
    private static List<String> saves = new ArrayList<String>();

    public Save(String lobby, String arena, Location min, Location max, String player) {
        this.lobby = lobby;
        this.arena = arena;
        this.min = min;
        this.max = max;
        this.player = player;
    }

    private void startPercentTask() {
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)SurvivalGames.instance, new Runnable(){

            @Override
            public void run() {
                float percent = Math.round((float)(Save.this.writeStepsDone / (Save.this.writeSteps / 100.0)));
                if (percent > 100.0f) {
                    return;
                }
                Player p = Bukkit.getPlayer((String)Save.this.player);
                if (p != null) {
                    p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7eArena save lobby " + Save.this.lobby + " arena " + Save.this.arena + ": " + percent + "% done...");
                }
            }
        }, 100, 200);
    }

    @Override
    public void run() {
        saves.add(String.valueOf(this.lobby) + this.arena);
        this.start = System.currentTimeMillis();
        while (Reset.isResetting(this.lobby, this.arena)) {
            try {
                Save.sleep(50);
                continue;
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        File file = new File("plugins/SurvivalGames/reset/" + this.lobby + this.arena + ".map");
        file.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        try {
            this.pw = new PrintWriter(new FileWriter("plugins/SurvivalGames/reset/" + this.lobby + this.arena + ".map", true), true);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
            this.pw.close();
            return;
        }
        final int xMin = Math.min(this.min.getBlockX(), this.max.getBlockX());
        final int zMin = Math.min(this.min.getBlockZ(), this.max.getBlockZ());
        final int xMax = Math.max(this.min.getBlockX(), this.max.getBlockX());
        final int zMax = Math.max(this.min.getBlockZ(), this.max.getBlockZ());
        Bukkit.getScheduler().callSyncMethod((Plugin)SurvivalGames.instance, (Callable)new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                Location chunkloc = new Location(Save.this.min.getWorld(), 0.0, 0.0, 0.0);
                int x = xMin;
                while (x <= xMax) {
                    chunkloc.setX((double)x);
                    int z = zMin;
                    while (z <= zMax) {
                        chunkloc.setZ((double)z);
                        Chunk c = Save.this.min.getWorld().getChunkAt(chunkloc);
                        if (!Save.this.chunks.contains((Object)c)) {
                            Save.this.chunks.add(c);
                        }
                        ++z;
                    }
                    ++x;
                }
                Save.access$7(Save.this, Save.this.min.getWorld());
                return null;
            }
        });
        while (this.world == null) {
            try {
                Save.sleep(1);
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.writeSteps = this.chunks.size();
        this.startPercentTask();
        Iterator<Chunk> iterator = this.chunks.iterator();
        while (iterator.hasNext()) {
            Chunk c;
            this.chunk = c = iterator.next();
            Bukkit.getScheduler().callSyncMethod((Plugin)SurvivalGames.instance, (Callable)new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    Save.this.pw.println(";" + Save.this.chunk.getX() + "," + Save.this.chunk.getZ());
                    int x = 0;
                    while (x < 16) {
                        int z = 0;
                        while (z < 16) {
                            int y = 0;
                            while (y < Save.this.world.getMaxHeight()) {
                                Block b = Save.this.chunk.getBlock(x, y, z);
                                Location loc = b.getLocation();
                                String save = String.valueOf(loc.getBlockX()) + "," + loc.getBlockY() + "," + loc.getBlockZ();
                                int id = b.getTypeId();
                                if (id != 0) {
                                    save = String.valueOf(save) + "," + id;
                                    byte data = b.getData();
                                    if (data != 0) {
                                        save = String.valueOf(save) + "," + data;
                                    }
                                }
                                Save.this.pw.println(save);
                                ++y;
                            }
                            ++z;
                        }
                        ++x;
                    }
                    Save save = Save.this;
                    Save.access$11(save, save.writeStepsDone + 1.0);
                    Save.access$12(Save.this, null);
                    return null;
                }
            });
            while (this.chunk != null) {
                try {
                    Save.sleep(20);
                    continue;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.chunks.clear();
        this.task.cancel();
        this.pw.close();
        saves.remove(String.valueOf(this.lobby) + this.arena);
        this.size = (int)(file.length() / 1000);
        this.format = "KiloByte";
        if (this.size >= 1000) {
            this.size /= 1000;
            this.format = "MegaByte";
        }
        Bukkit.getScheduler().callSyncMethod((Plugin)SurvivalGames.instance, (Callable)new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                Bukkit.getPluginManager().callEvent((Event)new SaveDoneEvent(Save.this.lobby, Save.this.arena, (System.currentTimeMillis() - Save.this.start) / 1000, Save.this.size, Save.this.format));
                return null;
            }
        });
    }

    public static boolean isSaveing(String lobby, String arena) {
        return saves.contains(String.valueOf(lobby) + arena);
    }

    static /* synthetic */ void access$7(Save save, World world) {
        save.world = world;
    }

    static /* synthetic */ void access$11(Save save, double d) {
        save.writeStepsDone = d;
    }

    static /* synthetic */ void access$12(Save save, Chunk chunk) {
        save.chunk = chunk;
    }

}

