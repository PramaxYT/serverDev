package me.Pramax.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
	
		this.getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	public void setScoreboard(Player p) {
		
	ScoreboardManager sm = Bukkit.getScoreboardManager();
	final Scoreboard board = sm.getNewScoreboard();
	final Objective o = board.registerNewObjective("test", "dummy");
	
	o.setDisplaySlot(DisplaySlot.SIDEBAR);
	o.setDisplayName("§7§bCraft§7");
	
	o.getScore("§a ").setScore(11);
	o.getScore("§cRang:").setScore(10);
	
	if(p.hasPermission("*" ) || p.hasPermission(""))
	if (p.hasPermission("Prefix.owner")) {
		o.getScore("§4Owner").setScore(9);
	} else if (p.hasPermission("Prefix.admin")) {
		o.getScore("§cAdmin").setScore(9);
	} else if (p.hasPermission("Prefix.developer")) {
		o.getScore("§9Developer").setScore(9);
	} else if (p.hasPermission("Prefix.moderator")) {
		o.getScore("§1Moderator").setScore(9);
	} else if (p.hasPermission("Prefix.supporter")) {
		o.getScore("§2Supporter").setScore(9);
	} else if (p.hasPermission("Prefix.builder")) {
		o.getScore("§eBuilder").setScore(9);
	} else if (p.hasPermission("Prefix.premium")) {
		o.getScore("§6Premium").setScore(9);
	} else if (p.hasPermission("Prefix.default")) {
		o.getScore("§7Spieler").setScore(9);

	
	}

	o.getScore("|§b ").setScore(8);
	o.getScore("|§1Online: ").setScore(7);
	o.getScore("|§4" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()).setScore(6);
	o.getScore("|§4 ").setScore(5);
	o.getScore("|§1Website").setScore(4);
	o.getScore("|§4https:/§c").setScore(3);
	
	o.getScore("").setScore(2);
	o.getScore("|§1Made by::").setScore(1);
	o.getScore("|§9Dev&7:&9Pram").setScore(0);
	
	p.setScoreboard(board);
	} 
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					setScoreboard(all);
				}
				
			}
		}.runTaskLater(this, 1);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		
		new BukkitRunnable() {	
			@Override
			public void run() {
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					setScoreboard(all);
				}
				
			}
		}.runTaskLater(this, 1);
	}

}