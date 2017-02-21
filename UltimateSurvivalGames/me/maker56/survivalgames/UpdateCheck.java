/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 *  org.json.simple.JSONValue
 */
package me.maker56.survivalgames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.listener.UpdateListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateCheck {
    private Plugin plugin;
    private String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;
    private URL url;
    private Thread thread;
    private int id = -1;
    private String apiKey = null;
    private static final String TITLE_VALUE = "name";
    private static final String LINK_VALUE = "downloadUrl";
    private static final String TYPE_VALUE = "releaseType";
    private static final String VERSION_VALUE = "gameVersion";
    private static final String QUERY = "/servermods/files?projectIds=";
    private static final String HOST = "https://api.curseforge.com";
    private static final String USER_AGENT = "Updater (by Gravity)";
    private UpdateResult result = UpdateResult.SUCCESS;

    public UpdateCheck(Plugin plugin, int id) {
        this.plugin = plugin;
        this.id = id;
        if (!SurvivalGames.instance.getConfig().getBoolean("enable-update-check")) {
            System.out.println("[SurvivalGames] Update checking is disabled.");
            this.result = UpdateResult.DISABLED;
            return;
        }
        try {
            this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + id);
        }
        catch (MalformedURLException e) {
            plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + id + " is invalid.", e);
            this.result = UpdateResult.FAIL_BADID;
        }
        this.thread = new Thread(new UpdateRunnable(this, null));
        this.thread.start();
    }

    public UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    public ReleaseType getLatestType() {
        this.waitForThread();
        if (this.versionType != null) {
            ReleaseType[] arrreleaseType = ReleaseType.values();
            int n = arrreleaseType.length;
            int n2 = 0;
            while (n2 < n) {
                ReleaseType type = arrreleaseType[n2];
                if (this.versionType.equals(type.name().toLowerCase())) {
                    return type;
                }
                ++n2;
            }
        }
        return null;
    }

    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }

    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            }
            catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }

    private boolean read() {
        JSONArray array;
        block6 : {
            try {
                URLConnection conn = this.url.openConnection();
                conn.setConnectTimeout(5000);
                if (this.apiKey != null) {
                    conn.addRequestProperty("X-API-Key", this.apiKey);
                }
                conn.addRequestProperty("User-Agent", "Updater (by Gravity)");
                conn.setDoOutput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                array = (JSONArray)JSONValue.parse((String)response);
                if (array.size() != 0) break block6;
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }
            catch (IOException e) {
                if (e.getMessage().contains("HTTP response code: 403")) {
                    this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                    this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                    this.result = UpdateResult.FAIL_APIKEY;
                } else {
                    this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
                    this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                    this.result = UpdateResult.FAIL_DBO;
                }
                return false;
            }
        }
        this.versionName = (String)((JSONObject)array.get(array.size() - 1)).get((Object)"name");
        this.versionLink = (String)((JSONObject)array.get(array.size() - 1)).get((Object)"downloadUrl");
        this.versionType = (String)((JSONObject)array.get(array.size() - 1)).get((Object)"releaseType");
        this.versionGameVersion = (String)((JSONObject)array.get(array.size() - 1)).get((Object)"gameVersion");
        return true;
    }

    public static enum ReleaseType {
        ALPHA,
        BETA,
        RELEASE;
        

        private ReleaseType(String string2, int n2) {
        }
    }

    public static enum UpdateResult {
        SUCCESS,
        NO_UPDATE,
        DISABLED,
        FAIL_DOWNLOAD,
        FAIL_DBO,
        FAIL_NOVERSION,
        FAIL_BADID,
        FAIL_APIKEY,
        UPDATE_AVAILABLE;
        

        private UpdateResult(String string2, int n2) {
        }
    }

    private class UpdateRunnable
    implements Runnable {
        final /* synthetic */ UpdateCheck this$0;

        private UpdateRunnable(UpdateCheck updateCheck) {
            this.this$0 = updateCheck;
        }

        @Override
        public void run() {
            if (this.this$0.url != null && this.this$0.read() && !SurvivalGames.version.equals(this.this$0.versionName)) {
                UpdateListener.update(this.this$0.versionName);
            }
        }

        /* synthetic */ UpdateRunnable(UpdateCheck updateCheck, UpdateRunnable updateRunnable) {
            UpdateRunnable updateRunnable2;
            updateRunnable2(updateCheck);
        }
    }

    public static enum UpdateType {
        DEFAULT,
        NO_VERSION_CHECK,
        NO_DOWNLOAD;
        

        private UpdateType(String string2, int n2) {
        }
    }

}

