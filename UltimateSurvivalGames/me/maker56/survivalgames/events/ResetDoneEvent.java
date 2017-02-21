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

public class ResetDoneEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private long time;
    private String arena;
    private String lobby;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ResetDoneEvent(String lobby, String arena, long time) {
        this.lobby = lobby;
        this.arena = arena;
        this.time = time;
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

