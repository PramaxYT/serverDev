/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package me.maker56.survivalgames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SaveDoneEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private long time;
    private int size;
    private String arena;
    private String lobby;
    private String format;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public SaveDoneEvent(String lobby, String arena, long time, int size, String format) {
        this.lobby = lobby;
        this.arena = arena;
        this.time = time;
        this.size = size;
        this.format = format;
    }

    public int getFileSize() {
        return this.size;
    }

    public String getFileSizeFormat() {
        return this.format;
    }

    public long getTime() {
        return this.time;
    }

    public String getLobby() {
        return this.lobby;
    }

    public String getArena() {
        return this.arena;
    }
}

