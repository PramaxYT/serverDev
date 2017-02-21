/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 */
package me.maker56.survivalgames.database;

import java.io.PrintStream;
import java.util.ArrayList;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.database.DatabaseLoader;
import me.maker56.survivalgames.game.GameState;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;

public class ConfigLoader {
    public void load() {
        ConfigLoader.reloadConfig();
        ConfigLoader.reloadMessages();
        ConfigLoader.reloadDatabase();
        ConfigLoader.reloadSigns();
        ConfigLoader.reloadReset();
        ConfigLoader.reloadChests();
        ConfigLoader.reloadScoreboard();
    }

    public static void reloadScoreboard() {
        FileConfiguration c;
        SurvivalGames.scoreboard = c = new DatabaseLoader("plugins/SurvivalGames", "scoreboard.yml").getFileConfiguration();
        String path = "Phase.Waiting.";
        c.addDefault(String.valueOf(path) + "Enabled", (Object)true);
        c.addDefault(String.valueOf(path) + "Title", (Object)"&b&lWaiting for players");
        ArrayList<String> content = new ArrayList<String>();
        content.add("&eRequired players to start&7://%requiredplayers%");
        content.add("&eCurrent player amount&7://%playing%");
        c.addDefault(String.valueOf(path) + "Scores", content);
        path = "Phase.Voting.";
        c.addDefault(String.valueOf(path) + "Enabled", (Object)true);
        c.addDefault(String.valueOf(path) + "Title", (Object)"&b&lArena Voting");
        content = new ArrayList();
        content.add("&e%arena%//%votecount%");
        content.add("&e%arena%//%votecount%");
        content.add("&e%arena%//%votecount%");
        c.addDefault(String.valueOf(path) + "Scores", content);
        path = "Phase.Cooldown.";
        c.addDefault(String.valueOf(path) + "Enabled", (Object)true);
        c.addDefault(String.valueOf(path) + "Title", (Object)"&b&lCooldown");
        content = new ArrayList();
        content.add("&eTime remaining&7://%time%");
        content.add("&eTributes&7://%playing%");
        c.addDefault(String.valueOf(path) + "Scores", content);
        path = "Phase.Ingame.";
        c.addDefault(String.valueOf(path) + "Enabled", (Object)true);
        c.addDefault(String.valueOf(path) + "Title", (Object)"&b&lIngame");
        content = new ArrayList();
        content.add("&e&lAlive&7://%playing%");
        content.add("&c&lDeath&7://%death%");
        c.addDefault(String.valueOf(path) + "Scores", content);
        path = "Phase.Deathmatch.";
        c.addDefault(String.valueOf(path) + "Enabled", (Object)true);
        c.addDefault(String.valueOf(path) + "Title", (Object)"&b&lDeathmatch");
        content = new ArrayList();
        content.add("&eTime remaining&7://%time%");
        c.addDefault(String.valueOf(path) + "Scores", content);
        c.options().header("##### UltimateSurvivalGames Scoreboard Configuration #####\n\nHow does this work?\nFor each game phase (WAITING,VOTING,COOLDOWN,INGAME and DEATHMATHCH) is a scoreboard design.\nIf you set \"Enabled\" for a phase to false, no scoreboard will shown!\nThe title can be maximal 32 charakters long and cannot contain variables.\n\nIn the \"Scores\" part, you can modify the content of the scoreboard. \"//\" splits the line in name and score.\nThe left part is the name which can be maximal 48 charalters long.\nThe right part is the amount of a score. Here you have to write the variables.\n\nWhat are the variables?\nYou can use many variables. Here is a list:\n\n  %playing% - The current amount of players in a lobby!\n  %requiredplayers% - The amount of required players to start a game automaticly!\n  %death% - The amount of deaths in a round!\n  %spectators% - The amount of spectators in a round!\n  %time% - The remaining time of a game phase!\n  %votecount% - The amount of votes of an arena (Only works in the voting phase)\n  %arena% - The name of the arena (Only works in the score name)\n\nMore help on http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/\n");
        c.options().copyDefaults(true);
        SurvivalGames.saveScoreboard();
    }

    public static void reloadChests() {
        FileConfiguration c;
        SurvivalGames.chestloot = c = new DatabaseLoader("plugins/SurvivalGames", "chestloot.yml").getFileConfiguration();
        ArrayList<String> lvl1 = new ArrayList<String>();
        lvl1.add((String)((Object)Material.WOOD_AXE));
        lvl1.add((String)((Object)Material.LEATHER_BOOTS));
        lvl1.add((String)((Object)Material.GOLD_HELMET));
        lvl1.add((Object)Material.APPLE + " 3");
        lvl1.add((Object)Material.ARROW + " 5");
        c.addDefault("Chestloot.Level 1", lvl1);
        ArrayList<String> lvl2 = new ArrayList<String>();
        lvl2.add((String)((Object)Material.COOKED_BEEF));
        lvl2.add((Object)Material.RAW_CHICKEN + " 2");
        lvl2.add((String)((Object)Material.COOKED_CHICKEN));
        lvl2.add((String)((Object)Material.MUSHROOM_SOUP));
        lvl2.add((String)((Object)Material.WOOD_SWORD));
        lvl2.add((String)((Object)Material.GOLD_HELMET));
        lvl2.add((String)((Object)Material.GOLD_LEGGINGS));
        lvl2.add((String)((Object)Material.LEATHER_BOOTS));
        lvl2.add((Object)Material.GRILLED_PORK + " 2");
        lvl2.add((String)((Object)Material.BOWL));
        lvl2.add((Object)Material.MELON + " 2");
        lvl2.add((String)((Object)Material.RAW_CHICKEN));
        c.addDefault("Chestloot.Level 2", lvl2);
        ArrayList<String> lvl3 = new ArrayList<String>();
        lvl3.add((String)((Object)Material.MELON_BLOCK));
        lvl3.add((String)((Object)Material.IRON_HELMET));
        lvl3.add((Object)Material.MELON + " 4");
        lvl3.add((String)((Object)Material.GOLD_SWORD));
        lvl3.add((Object)Material.WEB + " 3");
        lvl3.add((String)((Object)Material.CHAINMAIL_CHESTPLATE));
        lvl3.add((String)((Object)Material.CHAINMAIL_BOOTS));
        lvl3.add((String)((Object)Material.FISHING_ROD));
        lvl3.add((String)((Object)Material.LEATHER_LEGGINGS));
        lvl3.add((Object)Material.ARROW + " 4");
        lvl3.add((Object)Material.GOLD_INGOT + " 2");
        lvl3.add((Object)Material.TNT + " name:&eInstant_ignition_bomb");
        lvl3.add((String)((Object)Material.DEAD_BUSH));
        c.addDefault("Chestloot.Level 3", lvl3);
        ArrayList<String> lvl4 = new ArrayList<String>();
        lvl4.add((Object)Material.GOLD_INGOT + " 5");
        lvl4.add((String)((Object)Material.IRON_CHESTPLATE));
        lvl4.add((String)((Object)Material.IRON_BOOTS));
        lvl4.add((String)((Object)Material.CHAINMAIL_HELMET));
        lvl4.add((String)((Object)Material.FLINT_AND_STEEL));
        lvl4.add((String)((Object)Material.GOLD_BOOTS));
        lvl4.add((String)((Object)Material.STONE_SWORD));
        lvl4.add((String)((Object)Material.WOOD_SWORD));
        lvl4.add((Object)Material.STRING + " 2");
        c.addDefault("Chestloot.Level 4", lvl4);
        ArrayList<String> lvl5 = new ArrayList<String>();
        lvl5.add((Object)Material.DIAMOND + " 2");
        lvl5.add((String)((Object)Material.IRON_INGOT));
        lvl5.add((Object)Material.STICK + " 2");
        lvl5.add((String)((Object)Material.CAKE));
        lvl5.add((String)((Object)Material.FERMENTED_SPIDER_EYE));
        lvl5.add((Object)Material.BOW + ":168");
        lvl4.add((Object)Material.STONE_SWORD + " name:&eSword_of_Herobrine enchant:KNOCKBACK,1 enchant:DAMAGE_ALL,1");
        lvl5.add((Object)Material.POTION + " effect:regeneration,10,1 name:&cRegeneration");
        lvl5.add((Object)Material.POTION + " effect:jump,18,1 effect:speed,18,2 name:&ePotion_of_a_rabbit lore:&7Give_you_the//&7abilities_of_a_rabbit!");
        c.addDefault("Chestloot.Level 5", lvl5);
        c.addDefault("Chest-Title", (Object)"Survival Chest");
        c.options().header("##### UltimateSurvivalGames Chestloot Configuration #####\n\n## How does this work? ##\nThe chestloot is splitted into 5 lists. You can add unlimited items to each list.\nIn one chest can spawn up to 8 itemstacks. For each itemstack, the plugin choose\none list. The following lists under this text have different spawn changes:\n\nLevel 1: 40 %\nLevel 2: 30 %\nLevel 3: 15 %\nLevel 4: 10 %\nLevel 5: 5 %\n\nIf the plugin has choosed a list for an itemstack, it takes a item random from the list.\n\n## How can I modify the items? ##\nYou can add or remove items from all lists. But at least one item has to be on each list.\n\n## How do I format the items? ##\nMATERIAL/ITEMID[:SUBID] [AMOUNT] [SPECIAL THINGS]\nHere are some examples:\n\n# Normal Item:\n\"BREAD\" - is the same like \"BREAD 1\", \"BREAD:0 1\" or \"297:0 1\"\n\n# If you want to set a predefined durability-level, just change the subid:\n\"STONE_SWORD:10\" - This tool has already 10 uses lost.\n\n# You can also add enchantments to an item:\n\"STONE_SWORD enchant:KNOCKBACK,2 enchant:DAMAGE_ALL,3\" - This item has knockback 2 and sharpness 3! Note: Only the vanilla level of an enchantment can be used!\n\n# You can also set a custom name and lore for an item:\n\"EGG name:&eEaster_Egg lore:&7Throw//&7me!\" - This is an egg with a displayname \"Easter Egg\" and the lore \"Throw me\"! Note: Spaces are \"_\" and line breaks in lore the charakters \"//\"\n");
        c.options().copyDefaults(true);
        SurvivalGames.saveChests();
    }

    public static void reloadSigns() {
        FileConfiguration c;
        SurvivalGames.signs = c = new DatabaseLoader("plugins/SurvivalGames", "signs.yml").getFileConfiguration();
        c.addDefault("Sign.LeftClick.Show current arena", (Object)true);
        c.addDefault("Sign.LeftClick.Show players remain", (Object)true);
        c.addDefault("Sign.Line.1", (Object)"&bSurvivalGames");
        c.addDefault("Sign.Line.2", (Object)"&8[&e%name%&8]");
        c.addDefault("Sign.Line.3", (Object)"&o%state%");
        c.addDefault("Sign.Line.4", (Object)"%currentplayers%/&7%requiredplayers%&r/%maxplayers%");
        c.addDefault("Sign.LeavePrefix", (Object)"&bSurvivalGames");
        c.addDefault("Sign.Leave.Line.2", (Object)"");
        c.addDefault("Sign.Leave.Line.3", (Object)"&oRightclick");
        c.addDefault("Sign.Leave.Line.4", (Object)"&oto leave!");
        GameState[] arrgameState = GameState.values();
        int n = arrgameState.length;
        int n2 = 0;
        while (n2 < n) {
            GameState state = arrgameState[n2];
            c.addDefault("Translations." + state.toString(), (Object)state.toString());
            ++n2;
        }
        c.options().copyDefaults(true);
        SurvivalGames.saveSigns();
    }

    public static void reloadReset() {
        FileConfiguration c;
        SurvivalGames.reset = c = new DatabaseLoader("plugins/SurvivalGames", "reset.yml").getFileConfiguration();
        c.options().header("This is the file for the startup reset.\nIf the server shutdown, reload or crash in a running game, the server reset the arena after enabling survivalgames.");
        c.options().copyDefaults(true);
        SurvivalGames.saveReset();
    }

    public static void reloadConfig() {
        SurvivalGames.instance.reloadConfig();
        FileConfiguration c = SurvivalGames.instance.getConfig();
        c.addDefault("enable-update-check", (Object)true);
        c.addDefault("use-permissions", (Object)true);
        c.addDefault("broadcast-win", (Object)true);
        c.addDefault("Lightning.on-death", (Object)true);
        c.addDefault("Lightning.on-few-players", (Object)true);
        c.addDefault("Lightning.few-players", (Object)3);
        c.addDefault("Lightning.few-players-time", (Object)45);
        c.addDefault("Default.Enable-Voting", (Object)true);
        c.addDefault("Default.Lobby-Time", (Object)120);
        c.addDefault("Default.Max-Voting-Arenas", (Object)3);
        c.addDefault("Default.Required-Players-to-start", (Object)3);
        c.addDefault("Default.Arena.Chests.TypeID", (Object)54);
        c.addDefault("Default.Arena.Chests.Data", (Object)-1);
        c.addDefault("Default.Arena.Grace-Period", (Object)30);
        c.addDefault("Default.Arena.Automaticly-Deathmatch-Time", (Object)1800);
        c.addDefault("Default.Arena.Player-Deathmatch-Start", (Object)3);
        c.addDefault("Default.Money-on-Kill", (Object)2.5);
        c.addDefault("Default.Money-on-Win", (Object)20.0);
        c.addDefault("Default.Midnight-chest-refill", (Object)true);
        ArrayList<Integer> allowedBlocks = new ArrayList<Integer>();
        allowedBlocks.add(18);
        allowedBlocks.add(31);
        allowedBlocks.add(92);
        allowedBlocks.add(103);
        allowedBlocks.add(39);
        allowedBlocks.add(40);
        allowedBlocks.add(86);
        allowedBlocks.add(46);
        allowedBlocks.add(51);
        allowedBlocks.add(30);
        c.addDefault("Default.Arena.Allowed-Blocks", allowedBlocks);
        if (c.contains("Chest")) {
            c.set("Chest", (Object)null);
        }
        if (c.contains("Chestloot")) {
            c.set("Chestloot", (Object)null);
        }
        ArrayList<String> allowedCmds = new ArrayList<String>();
        allowedCmds.add("/sg");
        allowedCmds.add("/hg");
        allowedCmds.add("/hungergames");
        allowedCmds.add("/survivalgames");
        c.addDefault("Allowed-Commands", allowedCmds);
        c.addDefault("Voting.Item", (Object)((Object)Material.CHEST + " name:&eVote_for_an_arena lore:&7Rightclick_to_open//&7the_voting_menu!"));
        c.addDefault("Voting.InventoryTitle", (Object)"Vote for an arena!");
        c.addDefault("Voting.ArenaItem", (Object)((Object)Material.EMPTY_MAP + " 0 lore:&7Click_to_vote//&7for_this_arena!"));
        c.addDefault("Leave-Item", (Object)((Object)Material.MAGMA_CREAM + " name:&eLeave_the_lobby lore:&7Rightclick_to_leave//&7the_lobby!"));
        c.addDefault("Spectating.Enabled", (Object)true);
        c.addDefault("Spectating.Max-Spectators-Per-Arena", (Object)8);
        c.addDefault("Spectating.Player-Navigator.Item", (Object)((Object)Material.COMPASS + " name:&ePlayer Navigator lore:&7Rightclick_to_open//&7the_player_navigator!"));
        c.addDefault("Spectating.Player-Navigator.Inventory-Title", (Object)"Click on a item to spectate!");
        ArrayList<String> joinfull = new ArrayList<String>();
        joinfull.add("sg.donator.vip.iron");
        joinfull.add("sg.donator.vip.gold");
        joinfull.add("sg.donator.moderator");
        joinfull.add("sg.donator.admin");
        c.addDefault("Donator-Permissions.Join-Full-Arena", joinfull);
        ArrayList<String> votePower = new ArrayList<String>();
        votePower.add("sg.donator.vip.iron//2");
        votePower.add("sg.donator.vip.gold//2");
        c.addDefault("Donator-Permissions.Extra-Vote-Power", votePower);
        c.addDefault("TNT-Extra-Damage", (Object)7.0);
        c.addDefault("Enable-Arena-Reset", (Object)true);
        c.options().copyDefaults(true);
        SurvivalGames.instance.saveConfig();
        if (!c.getBoolean("Enable-Arena-Reset")) {
            System.out.println("[SurvivalGames] Warning: Arena map reset ist disabled.");
        }
    }

    public static void reloadDatabase() {
        FileConfiguration c;
        SurvivalGames.database = c = new DatabaseLoader("plugins/SurvivalGames", "database.yml").getFileConfiguration();
    }

    public static void reloadMessages() {
        FileConfiguration c;
        SurvivalGames.messages = c = new DatabaseLoader("plugins/SurvivalGames", "messages.yml").getFileConfiguration();
        c.addDefault("prefix", (Object)"&7[&3SG&7] &6");
        c.addDefault("no-permission", (Object)"&cYou don't have permission to do this!");
        c.addDefault("cmd-error", (Object)"&cError: %0%");
        c.addDefault("join-unknown-game", (Object)"&cThe lobby %0% does not exist!");
        c.addDefault("join-game-running", (Object)"&cThis game is already running!");
        c.addDefault("join-vehicle", (Object)"&cYou can't join SurvivalGames in a vehicle!");
        c.addDefault("join-game-full", (Object)"&cSorry, this lobby is full!");
        c.addDefault("join-success", (Object)"%0% joined the lobby! &7(&e%1%&7/&e%2%&7)");
        c.addDefault("fulljoin-kick", (Object)"&cI'm sorry, you've been kicked to make a free slot for a donator or a team member!");
        c.addDefault("join-already-playing", (Object)"&cYou're already playing!");
        c.addDefault("leave-not-playing", (Object)"&cYou aren't playing!");
        c.addDefault("game-leave", (Object)"%0% left the lobby! &7(&e%1%&7/&e%2%&7)");
        c.addDefault("game-cooldown-big", (Object)"The game starts in %0% seconds");
        c.addDefault("game-cooldown-little", (Object)"The game starts in %0%");
        c.addDefault("spectator-join", (Object)"%0% joined the game as spectator!");
        c.addDefault("spectator-full", (Object)"&cThe lobby is full. There can be up to %0% spectators in a lobby!");
        c.addDefault("spectator-game-running", (Object)"&cThis game isn't running!");
        c.addDefault("spectator-not-living", (Object)"&cPlayer %0% isn't alive anymore.");
        c.addDefault("spectator-new-player", (Object)"You're now specatating %0%!");
        c.addDefault("spectator-disabled", (Object)"&cSpectating is disabled!");
        c.addDefault("game-waiting-cooldown-big", (Object)"The voting ends in %0% seconds");
        c.addDefault("game-waiting-cooldown-little", (Object)"The voting ends in %0%");
        c.addDefault("game-waiting-end", (Object)"The waiting phase has been ended!");
        c.addDefault("game-deathmatch-cooldown-big-minutes", (Object)"&7The final deathmatch starts in %0% minutes!");
        c.addDefault("game-deathmatch-cooldown-big-seconds", (Object)"The final deathmatch starts in %0% seconds");
        c.addDefault("game-deathmatch-cooldown-little", (Object)"The final deathmatch starts in %0%");
        c.addDefault("game-deathmatch-start", (Object)"Let's start the final deathmatch!");
        c.addDefault("game-deathmatch-timeout", (Object)"The deathmatch ends automaticly in %0% seconds!");
        c.addDefault("game-deathmatch-timeout-warning", (Object)"When the deathmatch ends automaticly, the winner will be choosed random!");
        c.addDefault("game-player-die-killer", (Object)"%0% was killed by %1%!");
        c.addDefault("game-player-die-damage", (Object)"%0% has died and gone from us!");
        c.addDefault("game-player-left", (Object)"%0% left the lobby!");
        c.addDefault("game-remainplayers", (Object)"&b%0%&6 tributes remain.");
        c.addDefault("game-grace-period", (Object)"&bYou have %0% seconds grace-period!");
        c.addDefault("game-grace-period-ended", (Object)"&bThe grace-period has been ended!");
        c.addDefault("game-voting-cooldown-big", (Object)"The voting ends in %0% seconds");
        c.addDefault("game-voting-cooldown-little", (Object)"The voting ends in %0%");
        c.addDefault("game-voting-end", (Object)"The voting phrase has been ended!");
        c.addDefault("game-no-vote", (Object)"&cYou can only vote in the voting phase of the game!");
        c.addDefault("game-bad-vote", (Object)"&cThis isn't a valid vote ID!");
        c.addDefault("game-already-vote", (Object)"&cYou've already voted for an arena!");
        c.addDefault("game-no-voting-enabled", (Object)"&cSorry, voting isn't enabled! The arena will choosed random!");
        c.addDefault("game-success-vote", (Object)"You've voted successfully for arena &b%0%&6!");
        c.addDefault("game-extra-vote", (Object)"You've voted with &b%0% &6votes!");
        c.addDefault("game-start-canceled", (Object)"Not enough players are in this lobby. Cancel Timer...");
        c.addDefault("game-start", (Object)"The round begins, &b%0% &6players are playing! &bGood luck&6!");
        c.addDefault("game-chestrefill", (Object)"It's midnight! All chests are refilled!");
        c.addDefault("game-win", (Object)"%0% won the SurvivalGames in arena %1% in lobby %2%!");
        c.addDefault("game-win-winner-message", (Object)"&bCongratulations!&6 You've won the SurvivalGames in arena &b%0%&6!");
        c.addDefault("game-sign-info", (Object)"&7&m-----&r &6Lobby info: &e%0% &7&m-----");
        c.addDefault("game-sign-arena", (Object)"Arena&7: &e%0%");
        c.addDefault("game-sign-playersleft", (Object)"%0% players remain&7: %1%");
        c.addDefault("game-sign-noinfo", (Object)"There aren't any informations now!");
        c.addDefault("game-player-list", (Object)"There are %0% players&7: %1%");
        c.addDefault("game-not-loaded", (Object)"&cLobby %0% isn't loaded!");
        c.addDefault("game-already-loaded", (Object)"&cLobby %0% is already loaded!");
        c.addDefault("game-success-loaded", (Object)"Lobby %0% loaded successfully!");
        c.addDefault("game-success-unloaded", (Object)"Lobby %0% unloaded successfully!");
        c.addDefault("game-load-error", (Object)"&cCan't load lobby %0%! %1%");
        c.addDefault("game-already-exists", (Object)"&cThe lobby %0% already exist!");
        c.addDefault("game-created", (Object)"You've created the lobby %0% successfully!");
        c.addDefault("game-spawn-set", (Object)"You've set the spawn for game %0% successfully!");
        c.addDefault("game-set-spawn", (Object)"To set the spawn of this lobby, type /sg game lobby %0%");
        c.addDefault("game-not-found", (Object)"&cThe Game %0% does not exists!");
        c.addDefault("game-must-enter", (Object)"&cYou must enter a name: %0%");
        c.addDefault("game-vote", (Object)"Vote for an arena: &b/sg vote <ID>");
        c.addDefault("forbidden-command", (Object)"&cYou can't execute this command in SurvivalGames!");
        c.addDefault("forbidden-build", (Object)"&cYou aren't allowed to build in a SurvivalGames arena!");
        c.addDefault("arena-already-exists", (Object)"&cThe arena %0% already exists in lobby %1%!");
        c.addDefault("arena-must-select", (Object)"&cPlease select an arena with %0%!");
        c.addDefault("arena-created", (Object)"You've created arena %0% in lobby %1% successfully!");
        c.addDefault("arena-selected", (Object)"You've selected arena %0% in lobby %1%!");
        c.addDefault("arena-not-found", (Object)"The arena %0% does not exists in lobby %1%!");
        c.addDefault("arena-no-selection", (Object)"&cPlease select two points with the selection item: %0%");
        c.addDefault("arena-check", (Object)"Type %0% to see what you have to do to complete the arena setup!");
        c.addDefault("arena-spawn-added", (Object)"You've added Spawn %0% successfully!");
        c.addDefault("arena-spawn-removed", (Object)"You removed Spawn %0% successfully!");
        c.addDefault("arena-spawn-notfound", (Object)"&cSpawn %0% does not exist!");
        c.addDefault("arena-deathmatch-changed", (Object)"You've changed the deathmatch: %0%!");
        c.addDefault("arena-money-win", (Object)"&eYou've received &a%0% &emoney for winning survival games!");
        c.addDefault("arena-money-kill", (Object)"&eYou've received &a%0% &emoney for killing %1%!");
        c.addDefault("arena-tools", (Object)"Here is the selection tool. Left/Rightclick to set two positions!");
        c.addDefault("arena-tools-worldedit", (Object)"Please use the WorldEdit Wand Tool to set two positions!");
        c.addDefault("config-error-name", (Object)"&cPlease enter a valid configuration name: %0%");
        c.options().copyDefaults(true);
        SurvivalGames.saveMessages();
    }
}

