package com.timvisee.simplesurvivalgames.arena.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

public class ArenaScoreboard {
	
	private Arena a;
	
	private Scoreboard s;
	
	public ArenaScoreboard(Arena a) {
		// Store the arena
		this.a = a;
		
		// TODO: Remove old scoreboard instances!
		
		// Initialize a new scoreboard
		s = Bukkit.getScoreboardManager().getNewScoreboard();
		
		// Create the objectives
		Objective os = s.registerNewObjective("killsSidebar", "kills");
		os.setDisplayName(ChatColor.DARK_RED + "Players");
		os.setDisplaySlot(DisplaySlot.SIDEBAR);

		Objective op = s.registerNewObjective("killsPlayer", "kills");
		op.setDisplayName(ChatColor.DARK_RED + "Kills");
		op.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		// Create the team to put the players in
		Team t = s.registerNewTeam("players");
		t.setDisplayName("Players");
		t.setAllowFriendlyFire(true);

		// Update the scoreboard
		update();
	}
	
	/**
	 * Get the arena instance
	 * @return Arena
	 */
	public Arena getArena() {
		return this.a;
	}
	
	/**
	 * Get the scoreboard instance
	 * @return Scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.s;
	}
	
	public void addPlayer(ArenaPlayer p) {
		addPlayer(p.getPlayer());
	}
	
	public void addPlayer(Player p) {
		// Get the team to add the player too
		Team t = this.s.getTeams().toArray(new Team[]{})[0];
		
		// Add the player to the team
		t.addPlayer(p);
		
		// Update the scoreboard
		update();
	}
	
	/**
	 * Remove a player
	 * @param p The player to remove
	 */
	public void removePlayer(ArenaPlayer p) {
		removePlayer(p.getPlayer());
	}
	
	public void removePlayer(Player p) {
		// Reset the scores of the player
		this.s.resetScores(p);
		
		// Loop through every team
		for(Team t : this.s.getTeams())
			t.removePlayer(p);
		
		// Update the scoreboard
		update();
	}
	
	public void removeViewer(ArenaPlayer p) {
		removeViewer(p.getPlayer());
	}
	
	public void removeViewer(Player p) {
		if(p.getScoreboard().equals(this.s))
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public void update() {
		// Update the score of the players
		for(Team t : this.s.getTeams()) {
			for(OfflinePlayer p : t.getPlayers()) {
				for(Objective obj : this.s.getObjectives()) {
					if(p.isOnline())
						if(this.a.getPlayerManager().isPlayer(p.getPlayer()))
							obj.getScore(p).setScore(this.a.getPlayerManager().getPlayer(p.getPlayer()).getRoundKills());
				}
			}
		}
		
		// Show the scoreboard to the players and spectators
		for(ArenaPlayer p : this.a.getPlayerManager().getPlayersAndSpectators()) {
			if(p.getPlayer() != null) {
				p.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				p.getPlayer().setScoreboard(this.s);
			}
		}
	}
}
