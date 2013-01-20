package com.timvisee.simplesurvivalgames.arena.spawn;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

public class ArenaSpawn {
	
	private Arena arena;
	private SSGLocation loc;
	
	/**
	 * Constructor
	 * @param arena the arena
	 * @param name spawn name
	 * @param loc spawn location
	 */
	public ArenaSpawn(Arena arena, SSGLocation loc) {
		this.arena = arena;
		this.loc = loc;
	}
	
	/**
	 * Get the arena
	 * @return spawn arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Get the spawn's spawn location
	 * @return spawn location
	 */
	public SSGLocation getLocation() {
		return this.loc;
	}
	
	/**
	 * Set the spawn's spawn location
	 * @param loc new spawn location
	 */
	public void setSpawn(SSGLocation loc) {
		this.loc = loc;
	}
	
	/**
	 * Is the player at spawn
	 * @param p the player to check
	 * @return true if at spawn
	 */
	public boolean isAtSpawn(ArenaPlayer p) {
		return isAtSpawn(p.getPlayer().getLocation());
	}

	/**
	 * Is a location at the spawn
	 * @param loc the location to check
	 * @return true if at spawn
	 */
	public boolean isAtSpawn(SSGLocation loc) {
		return isAtSpawn(loc.toBukkitLocation());
	}
	
	/**
	 * Is a location at the spawn
	 * @param loc the location to check
	 * @return true if at spawn
	 */
	public boolean isAtSpawn(Location loc) {
		// Compare worlds
		if(!loc.getWorld().getName().equals(getLocation().getWorldName()))
			return false;

		Block player = loc.getBlock();
		Block spawn = getLocation().toBukkitLocation().getBlock();
		Block spawn2 = getLocation().toBukkitLocation().add(0, 1, 0).getBlock();
		
		// Is the player at spawn or a block above (jumping)
		return player.equals(spawn) || player.equals(spawn2);
	}
	
	/**
	 * Compare two arena spawns with each other
	 * @param other the other spawn to compare to
	 * @return true if equals
	 */
	public boolean equals(ArenaSpawn other) {
		return (other.getLocation().equals(this.getLocation()) &&
				other.getArena().equals(this.getArena()));
	}
}
