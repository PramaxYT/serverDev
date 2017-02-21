/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Achievement
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Statistic
 *  org.bukkit.Statistic$Type
 *  org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.maker56.survivalgames.chat;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import me.maker56.survivalgames.chat.MessagePart;
import me.maker56.survivalgames.chat.ReflectionUtil;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JSONMessage {
    private final List<MessagePart> messageParts = new ArrayList<MessagePart>();
    private String jsonString;
    private boolean dirty;
    private Class<?> nmsChatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
    private Class<?> nmsTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound");
    private Class<?> nmsPacketPlayOutChat = ReflectionUtil.getNMSClass("PacketPlayOutChat");
    private Class<?> nmsAchievement = ReflectionUtil.getNMSClass("Achievement");
    private Class<?> nmsStatistic = ReflectionUtil.getNMSClass("Statistic");
    private Class<?> nmsItemStack = ReflectionUtil.getNMSClass("ItemStack");
    private Class<?> obcStatistic = ReflectionUtil.getOBCClass("CraftStatistic");
    private Class<?> obcItemStack = ReflectionUtil.getOBCClass("inventory.CraftItemStack");

    public JSONMessage(String firstPartText) {
        this.messageParts.add(new MessagePart(firstPartText));
        this.jsonString = null;
        this.dirty = false;
    }

    public JSONMessage color(ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(String.valueOf(color.name()) + " is not a color");
        }
        this.latest().color = color;
        this.dirty = true;
        return this;
    }

    public /* varargs */ JSONMessage style(ChatColor ... styles) {
        ChatColor[] arrchatColor = styles;
        int n = arrchatColor.length;
        int n2 = 0;
        while (n2 < n) {
            ChatColor style = arrchatColor[n2];
            if (!style.isFormat()) {
                throw new IllegalArgumentException(String.valueOf(style.name()) + " is not a style");
            }
            ++n2;
        }
        this.latest().styles = styles;
        this.dirty = true;
        return this;
    }

    public JSONMessage file(String path) {
        this.onClick("open_file", path);
        return this;
    }

    public JSONMessage link(String url) {
        this.onClick("open_url", url);
        return this;
    }

    public JSONMessage suggest(String command) {
        this.onClick("suggest_command", command);
        return this;
    }

    public JSONMessage command(String command) {
        this.onClick("run_command", command);
        return this;
    }

    public JSONMessage achievementTooltip(String name) {
        this.onHover("show_achievement", "achievement." + name);
        return this;
    }

    public JSONMessage achievementTooltip(Achievement which) {
        try {
            Object achievement = ReflectionUtil.getMethod(this.obcStatistic, "getNMSAchievement", new Class[0]).invoke(null, new Object[]{which});
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsAchievement, "name").get(achievement));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public JSONMessage statisticTooltip(Statistic which) {
        Statistic.Type type = which.getType();
        if (type != Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic requires an additional " + (Object)type + " parameter!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getNMSStatistic", new Class[0]).invoke(null, new Object[]{which});
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public JSONMessage statisticTooltip(Statistic which, Material item) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type == Statistic.Type.BLOCK && item.isBlock() || type == Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + (Object)type + "!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getMaterialStatistic", new Class[0]).invoke(null, new Object[]{which, item});
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public JSONMessage statisticTooltip(Statistic which, EntityType entity) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type != Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + (Object)type + "!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getEntityStatistic", new Class[0]).invoke(null, new Object[]{which, entity});
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public JSONMessage itemTooltip(String itemJSON) {
        this.onHover("show_item", itemJSON);
        return this;
    }

    public JSONMessage itemTooltip(ItemStack itemStack) {
        try {
            Object nmsItem = ReflectionUtil.getMethod(this.obcItemStack, "asNMSCopy", ItemStack.class).invoke(null, new Object[]{itemStack});
            return this.itemTooltip(ReflectionUtil.getMethod(this.nmsItemStack, "save", new Class[0]).invoke(nmsItem, this.nmsTagCompound.newInstance()).toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public JSONMessage tooltip(String text) {
        return this.tooltip(text.split("\\n"));
    }

    public JSONMessage tooltip(List<String> lines) {
        return this.tooltip((String[])lines.toArray());
    }

    public /* varargs */ JSONMessage tooltip(String ... lines) {
        if (lines.length == 1) {
            this.onHover("show_text", lines[0]);
        } else {
            this.itemTooltip(this.makeMultilineTooltip(lines));
        }
        return this;
    }

    public JSONMessage then(Object obj) {
        this.messageParts.add(new MessagePart(obj.toString()));
        this.dirty = true;
        return this;
    }

    public String toJSONString() {
        if (!this.dirty && this.jsonString != null) {
            return this.jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter((Writer)string);
        try {
            if (this.messageParts.size() == 1) {
                this.latest().writeJson(json);
            } else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (MessagePart part : this.messageParts) {
                    part.writeJson(json);
                }
                json.endArray().endObject();
                json.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("invalid message");
        }
        this.jsonString = string.toString();
        this.dirty = false;
        return this.jsonString;
    }

    public void send(Player player) {
        try {
            Object handle = ReflectionUtil.getHandle((Object)player);
            Object connection = ReflectionUtil.getField(handle.getClass(), "playerConnection").get(handle);
            Object serialized = ReflectionUtil.getMethod(this.nmsChatSerializer, "a", String.class).invoke(null, this.toJSONString());
            Object packet = this.nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(serialized);
            ReflectionUtil.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Player player, String json) {
        try {
            Object handle = ReflectionUtil.getHandle((Object)player);
            Object connection = ReflectionUtil.getField(handle.getClass(), "playerConnection").get(handle);
            Object serialized = ReflectionUtil.getMethod(this.nmsChatSerializer, "a", String.class).invoke(null, json);
            Object packet = this.nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(serialized);
            ReflectionUtil.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Iterable<Player> players) {
        for (Player player : players) {
            this.send(player, this.toJSONString());
        }
    }

    public String toOldMessageFormat() {
        StringBuilder result = new StringBuilder();
        for (MessagePart part : this.messageParts) {
            result.append((Object)part.color).append(part.text);
        }
        return result.toString();
    }

    private MessagePart latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }

    private String makeMultilineTooltip(String[] lines) {
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter((Writer)string);
        try {
            json.beginObject().name("id").value(1);
            json.name("tag").beginObject().name("display").beginObject();
            json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
            json.name("Lore").beginArray();
            int i = 1;
            while (i < lines.length) {
                String line = lines[i];
                json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
                ++i;
            }
            json.endArray().endObject().endObject().endObject();
            json.close();
        }
        catch (Exception e) {
            throw new RuntimeException("invalid tooltip");
        }
        return string.toString();
    }

    private void onClick(String name, String data) {
        MessagePart latest = this.latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }

    private void onHover(String name, String data) {
        MessagePart latest = this.latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }
}

