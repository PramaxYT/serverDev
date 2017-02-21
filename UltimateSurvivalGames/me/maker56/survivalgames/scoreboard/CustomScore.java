/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.scoreboard.Score
 *  org.bukkit.scoreboard.Team
 */
package me.maker56.survivalgames.scoreboard;

import java.util.List;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.CooldownPhrase;
import me.maker56.survivalgames.game.phrase.DeathmatchPhrase;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.user.SpectatorUser;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class CustomScore {
    private String regex;
    private String name;
    private String extra;
    private Team team;
    private Score score;
    private static /* synthetic */ int[] $SWITCH_TABLE$me$maker56$survivalgames$game$GameState;

    public CustomScore(Score score, String name, String regex, Team team) {
        this(score, name, regex, team, null);
    }

    public CustomScore(Score score, String name, String regex, Team team, String extra) {
        this.regex = regex.toLowerCase();
        this.name = name;
        this.team = team;
        this.score = score;
        this.extra = extra;
    }

    public Score getScore() {
        return this.score;
    }

    public String getName() {
        return this.name;
    }

    public String getRegex() {
        return this.regex;
    }

    public Team getTeam() {
        return this.team;
    }

    public void update(Game game) {
        this.score.setScore(this.getData(game));
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public int getData(Game game) {
        i = -1;
        var3_3 = this.regex;
        switch (var3_3.hashCode()) {
            case -571916068: {
                if (var3_3.equals("%playing%")) break;
                ** break;
            }
            case 789486076: {
                if (!var3_3.equals("%spectators%")) {
                    ** break;
                }
                ** GOTO lbl24
            }
            case 1437093014: {
                if (!var3_3.equals("%death%")) {
                    ** break;
                }
                ** GOTO lbl22
            }
            case 1967630199: {
                if (var3_3.equals("%requiredplayers%")) {
                    i = game.getRequiredPlayers();
                    ** break;
                }
                ** GOTO lbl25
            }
        }
        i = game.getPlayingUsers();
        ** break;
lbl22: // 1 sources:
        i = game.getDeathAmount();
        ** break;
lbl24: // 1 sources:
        i = game.getSpecators().size();
lbl25: // 9 sources:
        if (i != -1) return i;
        if (this.extra != null && game.getState() == GameState.VOTING && this.regex.equals("%votecount%")) {
            i = game.getArena(this.extra).getVotes();
        }
        if (this.regex.equals("%time%") == false) return i;
        switch (CustomScore.$SWITCH_TABLE$me$maker56$survivalgames$game$GameState()[game.getState().ordinal()]) {
            case 3: {
                return game.getCooldownPhrase().getTime();
            }
            case 2: {
                return game.getVotingPhrase().getTime();
            }
            case 4: {
                return game.getIngamePhrase().getTime();
            }
            case 5: {
                i = game.getDeathmatch().getTime();
                break;
            }
        }
        return i;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$me$maker56$survivalgames$game$GameState() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$me$maker56$survivalgames$game$GameState;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[GameState.values().length];
        try {
            arrn[GameState.COOLDOWN.ordinal()] = 3;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[GameState.DEATHMATCH.ordinal()] = 5;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[GameState.INGAME.ordinal()] = 4;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[GameState.RESET.ordinal()] = 6;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[GameState.VOTING.ordinal()] = 2;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[GameState.WAITING.ordinal()] = 1;
        }
        catch (NoSuchFieldError v6) {}
        $SWITCH_TABLE$me$maker56$survivalgames$game$GameState = arrn;
        return $SWITCH_TABLE$me$maker56$survivalgames$game$GameState;
    }
}

