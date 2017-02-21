/*
 * Decompiled with CFR 0_118.
 */
package me.maker56.survivalgames.commands.permission;

public enum Permission {
    ARENA("sg.arena"),
    CONFIG("sg.config"),
    START("sg.start"),
    GAME("sg.game"),
    LOBBY("sg.lobby"),
    LIST("sg.list"),
    JOIN("sg.join"),
    SPECTATE("sg.spectate");
    
    private final String permission;

    private Permission(String permission, int n2, String string2) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }
}

