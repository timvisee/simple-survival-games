package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaPlayer;

public class SSGEntityListener implements Listener {
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the entity a player
		if(e instanceof Player) {
			Player p = (Player) e;
			
			// Is the current in any arean
			if(am.isInArena(p)) {
				ArenaPlayer ap = am.getPlayer(p);
				
				// The player may not get damaged in the lobby or as spectator
				if(ap.isInLobby() || ap.isSpectator())
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Entity e = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the entity a player
		if(e instanceof Player) {
			Player p = (Player) e;
			
			// Is the current in any arean
			if(am.isInArena(p)) {
				ArenaPlayer ap = am.getPlayer(p);
				
				// The food level of the player may not be changed when he's in the lobby or when he's a spectator
				if(ap.isInLobby() || ap.isSpectator())
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Make sure the player is not null
		if(p == null)
			return;
		
		// Is the current in any arena
		if(am.isInArena(p)) {
			Arena arena = am.getPlayer(p).getArena();
			
			// Show a message to the player he died
			p.sendMessage(ChatColor.DARK_RED + "You died!");
			// TODO: Show died cause
			
			// Kick the player out of the arena
			SimpleSurvivalGames.instance.getArenaManager().kick(p);
			
			// Show a message to all players this player died
			arena.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " died!");
			// TODO: Show died cause
			
			// Hide the death message
			event.setDeathMessage("");
			
			// TODO: Check if any player won the match
		}
		
		// TODO count player kills
	}
}
