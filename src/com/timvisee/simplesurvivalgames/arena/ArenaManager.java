package com.timvisee.simplesurvivalgames.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

public class ArenaManager {
	
	private List<Arena> arenas = new ArrayList<Arena>();
	
	/**
	 * Constructor
	 */
	public ArenaManager() { }
	
	/**
	 * Add an arena
	 * @param arena new arena
	 */
	public void addArena(Arena arena) {
		this.arenas.add(arena);
	}
	
	/**
	 * Get a list of arena's
	 * @return arena list
	 */
	public List<Arena> getArenas() {
		return this.arenas;
	}
	
	/**
	 * Get a list of enabled arena's
	 * @return enabled arenas list
	 */
	public List<Arena> getEnabledArenas() {
		List<Arena> enabledArenas = new ArrayList<Arena>();
		for(Arena arena : this.arenas)
			if(arena.isEnabled())
				enabledArenas.add(arena);
		return enabledArenas;
	}
	
	/**
	 * Is there any arena with this name
	 * @param name arena name to check
	 * @return false if not
	 */
	public boolean isArenaWithName(String name) {
		// First check case sensetive
		for(Arena a : this.arenas)
			if(a.getName().equals(name))
				return true;
		
		// Now check case insensetive
		for(Arena a : this.arenas)
			if(a.getName().equalsIgnoreCase(name))
				return true;
		
		// No arena found with this name
		return false;
	}
	
	/**
	 * Get the arena with this name
	 * @param name the name of the arena to get
	 * @return arena or null if no arena was found with this name
	 */
	public Arena getArenaWithName(String name) {
		// First check case sensetive
		for(Arena a : this.arenas)
			if(a.getName().equals(name))
				return a;
		
		// Now check case insensetive
		for(Arena a : this.arenas)
			if(a.getName().equalsIgnoreCase(name))
				return a;
		
		// No arena found with this name
		return null;
	}
	
	/**
	 * Get the best arena to join, a formula will be used to choose the best one.
	 * @return best arena to join, null if all arenas are in progress or when there's no arena available
	 */
	public Arena getBestArenaToJoin() {
		Arena arena = null;
		int playerCount = 0;
		
		for(Arena a : getArenas()) {
			// The arena mustn't be in progress
			if(a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// There must be a free slot in the arena
			if(a.getUnassignedSpawnsCount() == 0)
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to join
		List<Arena> arenas = getArenas();
		Collections.shuffle(arenas, new Random());
		for(Arena a : arenas) {
			// The arena mustn't be in progress
			if(a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// There must be a free slot in the arena
			if(a.getUnassignedSpawnsCount() == 0)
				continue;
			
			return a;
		}
		
		// Still no arena selected, return null
		return null;
	}
	
	/**
	 * Get the best arena to spectate, a formula will be used to choose the best one.
	 * @return best arena to spectate, null if there's no arena available
	 */
	public Arena getBestArenaToSpectate() {
		Arena arena = null;
		int playerCount = 0;
		
		for(Arena a : getArenas()) {
			// The arena should be in progress
			if(!a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// The spectators spawn has to be set
			if(!a.isSpectatorSpawnSet())
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to join
		for(Arena a : getArenas()) {
			// The spectators spawn has to be set
			if(!a.isSpectatorSpawnSet())
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to spectate
		List<Arena> arenas = getArenas();
		Collections.shuffle(arenas, new Random());
		return arenas.get(0);
	}
	
	/**
	 * Get the amount of arenas
	 * @return arena count
	 */
	public int getGameCount() {
		return this.arenas.size();
	}
	
	/**
	 * Get the player manager of an arena
	 * @param arena the arena to get the player manager from
	 * @return player manager of arena
	 */
	public ArenaPlayerManager getPlayerManager(Arena arena) {
		return arena.getPlayerManager();
	}
	
	/**
	 * Get all player managers from all arenas
	 * @return player managers from all arenas
	 */
	public List<ArenaPlayerManager> getPlayerManagers() {
		List<ArenaPlayerManager> playerManagers = new ArrayList<ArenaPlayerManager>();
		for(Arena a : this.arenas)
			playerManagers.add(a.getPlayerManager());
		return playerManagers;
	}
	
	/**
	 * Get all players and spectators
	 * @return all players and spectators
	 */
	public List<ArenaPlayer> getAllPlayersAndSpectators() {
		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();
		for(ArenaPlayerManager pm : getPlayerManagers())
			players.addAll(pm.getPlayersAndSpectators());
		return players;
	}
	
	/**
	 * Is a player in any arena
	 * @param p the player to check
	 * @return true if the player is playing
	 */
	public boolean isInArena(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return true;
		return false;
	}
	
	/**
	 * Get an arena player by a player
	 * @param p the player to get as arena player
	 * @return arena player or null when the player wasn't found
	 */
	public ArenaPlayer getPlayer(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a.getPlayerManager().getPlayer(p);
		return null;
	}
	
	/**
	 * Is a player in any arena
	 * @param p the player to check
	 * @return true if the player is playing
	 */
	public boolean isInArena(ArenaPlayer p) {
		return p.getArena().getPlayerManager().isInArena(p);
	}
	
	/**
	 * Get the arena a player is currently in
	 * @param p the player to get the arena from
	 * @return arena the player is currently in, null if not in any arena
	 */
	public Arena getArena(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a;
		return null;
	}
	
	/**
	 * Get the arena a player is currently in
	 * @param p the player to get the arena from
	 * @return arena the player is currently in, null if not in any arena
	 */
	public Arena getArena(ArenaPlayer p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a;
		return null;
	}
	
	/**
	 * Kick a player from every arena
	 * @param p the player to kick
	 */
	public void kick(Player p) {
		for(ArenaPlayerManager apm : getPlayerManagers())
			if(apm.isInArena(p)) {
				apm.kickPlayer(p);
				apm.kickSpectator(p);
			}
	}
}
