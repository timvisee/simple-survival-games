package com.timvisee.simplesurvivalgames.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaState;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayerManager;

public class SSGEntityListener implements Listener {
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity e = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Loop through every arena and check if the entity was spawned inside any arena
		for(Arena a : am.getArenas())
			if(a.isArenaCuboidSet())
				if(!a.getState().equals(ArenaState.STARTING) && !a.getState().equals(ArenaState.PLAYING))
					if(a.getArenaCuboid().isInsideCuboid(e.getLocation()))
						event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the entity a player
		if(e instanceof Player) {
			Player p = (Player) e;
			
			// Is the current in any arena
			if(am.isInArena(p)) {
				ArenaPlayer ap = am.getPlayer(p);
				
				// Is the player playing
				if(ap.isPlaying()) {
					// Is the grace time active
					if(ap.getArena().isGracePeriodActive())
						event.setCancelled(true);
				}
				
				// The player may not get damaged in the lobby or as spectator
				if(ap.isInLobby() || ap.isSpectator())
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity e = event.getEntity();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		Entity damager = event.getDamager();
		
		// Is the entity a player
		if(damager instanceof Player) {
			Player dmg = (Player) damager;
			
			// Is the current in any arena
			if(am.isInArena(dmg)) {
				ArenaPlayer apDmg = am.getPlayer(dmg);
				
				// Is the damager spectating
				if(apDmg.isSpectator()) {
					event.setCancelled(true);
					
					if(e instanceof Player)
						apDmg.sendMessage(ChatColor.DARK_RED + "Do not damage the players!");
					else
						apDmg.sendMessage(ChatColor.DARK_RED + "Do not damage the mobs!");
				}
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		Entity e = event.getEntity();
		Entity target = event.getTarget();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The entity or the target may not be null
		if(e == null || target == null)
			return;
		
		// If the target is a player, he may not be targeted if he's in the lobby or when he's a spectator
		if(target instanceof Player) {
			Player p = (Player) target;
			
			// Is the target player currently in any arena
			if(am.isInArena(p)) {
				ArenaPlayer ap = am.getPlayer(p);
				
				// The player may not be targeted
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
			
			// Store the original loaction of the player
			Location origLoc = arena.getPlayerManager().getPlayer(p).getOriginalLocation().clone();
			
			// Kick the player out of the arena
			SimpleSurvivalGames.instance.getArenaManager().kick(p);
			
			// Show a message to all players this player died
			arena.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " died!");
			// TODO: Show died cause
			
			// Hide the death message
			event.setDeathMessage("");
			
			// Make the player an spectator if he's not the only one left
			arena.getPlayerManager().joinSpectators(p);
			
			// Reset the original location of the player
			if(origLoc != null)
				arena.getPlayerManager().getPlayer(p).storeLocation(origLoc);
		}
		
		// TODO count player kills
	}
}
