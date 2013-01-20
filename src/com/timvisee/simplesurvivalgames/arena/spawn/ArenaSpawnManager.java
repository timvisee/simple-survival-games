package com.timvisee.simplesurvivalgames.arena.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

public class ArenaSpawnManager {
	
	Arena arena;
	List<ArenaSpawn> spawns = new ArrayList<ArenaSpawn>();
	
	/**
	 * Constructor
	 * @param arena the current arena
	 */
	public ArenaSpawnManager(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Get the current arena
	 * @return the current arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Get a list of all the spawns in this arena
	 * @return the list of spawns in this arena
	 */
	public List<ArenaSpawn> getSpawns() {
		return this.spawns;
	}
	
	/**
	 * Add a new spawn to this arena
	 * @param name the name of the spawn
	 * @param loc the location of the spawn
	 * @return the new ArenaSpawn
	 */
	public ArenaSpawn addSpawn(SSGLocation loc) {
		ArenaSpawn newSpawn = new ArenaSpawn(this.arena, loc);
		this.spawns.add(newSpawn);
		return newSpawn;
	}
	
	/**
	 * Get all assigned spawns
	 * @return list of assigned spawns
	 */
	public List<ArenaSpawn> getAssignedSpawns() {
		List<ArenaSpawn> assignedSpawns = new ArrayList<ArenaSpawn>();
		
		// Loop through all spawns to check if it's a assigned spawn
		for(ArenaSpawn s : this.spawns)
			if(isSpawnAssigned(s))
				assignedSpawns.add(s);
		
		// Return all assigned spawns
		return assignedSpawns;
	}
	
	/**
	 * Get all unassiged spawns
	 * @return list of unassiged spawns
	 */
	public List<ArenaSpawn> getUnassignedSpawns() {
		List<ArenaSpawn> unassignedSpawns = new ArrayList<ArenaSpawn>();
		
		// Loop through all spawns to check if it's a unassigned spawn
		for(ArenaSpawn s : this.spawns)
			if(!isSpawnAssigned(s))
				unassignedSpawns.add(s);
		
		// Return all unassigned spawns
		return unassignedSpawns;
	}
	
	/**
	 * Get a random spawn
	 * @return random spawn, null if no spawn is availalbe
	 */
	public ArenaSpawn getRandomSpawn() {
		return getRandomSpawn(new Random());
	}
	
	/**
	 * Get a random spawn
	 * @param rand random
	 * @return random spawn, null if no spawn is availalbe
	 */
	public ArenaSpawn getRandomSpawn(Random rand) {
		if(getSpawns().size() == 0)
			return null;
		
		int i = rand.nextInt(getSpawns().size());
		return getSpawns().get(i);
	}
	
	/**
	 * Get a random unassigned spawn
	 * @return random spawn, null if no spawn is availalbe
	 */
	public ArenaSpawn getRandomUnassignedSpawn() {
		return getRandomUnassignedSpawn(new Random());
	}
	
	/**
	 * Get a random unassigned spawn
	 * @param rand random
	 * @return random spawn, null if no spawn is availalbe
	 */
	public ArenaSpawn getRandomUnassignedSpawn(Random rand) {
		if(getUnassignedSpawns().size() == 0)
			return null;
		
		int i = rand.nextInt(getUnassignedSpawns().size());
		return getUnassignedSpawns().get(i);
	}
	
	/**
	 * Is a arena spawn free
	 * @param spawn the spawn to check
	 * @return true if free
	 */
	public boolean isSpawnAssigned(ArenaSpawn spawn) {
		for(ArenaPlayer p : spawn.getArena().getPlayerManager().getPlayers())
			if(p.hasAssignedAreanSpawn())
				if(p.getAssignedAreanSpawn().equals(spawn))
					return true;
		return false;
	}
	
	/**
	 * Get the spawn count
	 * @return spawn count
	 */
	public int getSpawnCount() {
		return this.spawns.size();
	}
	
	/**
	 * Get the assigned spawns count
	 * @return assigned spawns count
	 */
	public int getAssignedSpawnCount() {
		return getSpawnCount() - getUnassignedSpawnCount();
	}
	
	/**
	 * Get the unassigned spawns count
	 * @return unassigned spawns count
	 */
	public int getUnassignedSpawnCount() {
		return getUnassignedSpawns().size();
	}
	
	/**
	 * Force an arena spawn to be unassigned from a player
	 * @param spawn the spawn to be force unassigned
	 */
	public void unassignSpawn(ArenaSpawn spawn) {
		for(ArenaPlayer p : SimpleSurvivalGames.instance.getArenaManager().getAllPlayersAndSpectators())
			if(p.hasAssignedAreanSpawn())
				if(p.getAssignedAreanSpawn().equals(spawn))
					p.unassignArenaSpawn();
	}
	
	/**
	 * Force all arena spawns to be unassigned from players
	 */
	public void unassignSpawns() {
		for(ArenaSpawn spawn : this.spawns)
			unassignSpawn(spawn);
	}
	
	/**
	 * Remove a spawn from this arena
	 * @param spawn the spawn
	 * @return false if this spawn wasn't found
	 */
	public boolean removeSpawn(ArenaSpawn spawn) {
		return this.spawns.remove(spawn);
	}
	
	/**
	 * Remove a spawn from this arena
	 * @param index the index
	 */
	public void removeSpawn(int index) {
		this.spawns.remove(index);
	}
	
	/**
	 * Clear the list with spawns
	 */
	public void clear() {
		this.spawns.clear();
	}
}
