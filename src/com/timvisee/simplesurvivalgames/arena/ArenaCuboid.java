package com.timvisee.simplesurvivalgames.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

public class ArenaCuboid {
	
	private SSGLocation corner1, corner2 = null;
	
	/**
	 * Constructor
	 * @param corner1 first corner location
	 * @param corner2 second corner locatoin
	 */
	public ArenaCuboid(SSGLocation corner1, SSGLocation corner2) {
		this.corner1 = corner1;
		this.corner2 = corner2;
	}
	
	/**
	 * Get the first corner location
	 * @return first corner location
	 */
	public SSGLocation getFirstCorner() {
		return this.corner1;
	}
	
	/**
	 * Is the first corner set
	 * @return true if set
	 */
	public boolean isFirstCornerSet() {
		return this.corner1 != null;
	}
	
	/**
	 * Set the first corner location
	 * @param cornerLoc first corner location
	 */
	public void setFirstCorner(SSGLocation cornerLoc) {
		this.corner1 = cornerLoc;
	}
	
	/**
	 * Get the second corner location
	 * @return second corner location
	 */
	public SSGLocation getSecondCorner() {
		return this.corner2;
	}
	
	/**
	 * Is the second corner set
	 * @return true if set
	 */
	public boolean isSecondCornerSet() {
		return this.corner2 != null;
	}
	
	/**
	 * Set the second corner location
	 * @param cornerLoc second corner location
	 */
	public void setSecondCorner(SSGLocation cornerLoc) {
		this.corner2 = cornerLoc;
	}
	
	/**
	 * Get cuboid world name
	 * @return world name
	 */
	public String getWorldName() {
		return this.corner1.getWorldName();
	}
	
	/**
	 * Get cuboid world
	 * @return world
	 */
	public World getWorld() {
		return this.corner1.getWorld();
	}
	
	/**
	 * Get the min x coord
	 * @return min x coord
	 */
	public int getMinX() {
		return (int) Math.min(this.corner1.getX(), this.corner2.getX());
	}
	
	/**
	 * Get the max x coord
	 * @return max x coord
	 */
	public int getMaxX() {
		return (int) Math.max(this.corner1.getX(), this.corner2.getX());
	}
	
	/**
	 * Get the min y coord
	 * @return min y coord
	 */
	public int getMinY() {
		return (int) Math.min(this.corner1.getY(), this.corner2.getY());
	}
	
	/**
	 * Get the max y coord
	 * @return max y coord
	 */
	public int getMaxY() {
		return (int) Math.max(this.corner1.getY(), this.corner2.getY());
	}
	
	/**
	 * Get the min z coord
	 * @return min z coord
	 */
	public int getMinZ() {
		return (int) Math.min(this.corner1.getZ(), this.corner2.getZ());
 	}
	
	/**
	 * Get the max z coord
	 * @return max z coord
	 */
	public int getMaxZ() {
		return (int) Math.max(this.corner1.getZ(), this.corner2.getZ());
	}
	
	/**
	 * Check if a player is inside the cuboid
	 * @param player the player to check
	 * @return true if inside cuboid
	 */
	public boolean isInsideCuboid(ArenaPlayer player) {
		// Get the player
		Player p = player.getPlayer();
		
		// The player may not be null
		if(p == null)
			return false;
		
		// Is the player inside the cuboid
		return isInsideCuboid(new SSGLocation(p.getLocation()));
	}
	
	/**
	 * Check if a location is inside the cuboid
	 * @param loc the location to check
	 * @return true if inside cuboid
	 */
	public boolean isInsideCuboid(Location loc) {
		// The location has to be in the same world
		if(!loc.getWorld().getName().equals(getWorldName()))
			return false;
		
		// Check the coords
		return isInsideCuboid(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Check if a block is inside the cuboid
	 * @param block the block to check
	 * @return true if inside cuboid
	 */
	public boolean isInsideCuboid(Block block) {
		return isInsideCuboid(block.getLocation());
	}
	
	/**
	 * Check if a location is inside thye cuboid
	 * @param loc the location to check
	 * @return true if inside cuboid
	 */
	public boolean isInsideCuboid(SSGLocation loc) {
		// The location has to be in the same world
		if(!loc.getWorldName().equals(getWorldName()))
			return false;
		
		// Check the coords
		return isInsideCuboid(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Is a 3d point inside the cuboid
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 * @return true if inside cuboid
	 */
	public boolean isInsideCuboid(double x, double y, double z) {
		return (getMinX() <= x && getMaxX() >= x &&
				getMinY() <= y && getMaxY() >= y &&
				getMinZ() <= z && getMaxZ() >= z);
	}
	
	/**
	 * Get the nearest location inside the cuboid from any other location
	 * @param loc current loc
	 * @return nearest location inside cuboid
	 */
	public SSGLocation getNearestLocationInsideCuboid(SSGLocation loc) {
		return getNearestLocationInsideCuboid(loc, 1);
	}
	
	/**
	 * Get the nearest location inside the cuboid from any other location
	 * @param loc current loc
	 * @param offset offset
	 * @return nearest location inside cuboid
	 */
	public SSGLocation getNearestLocationInsideCuboid(SSGLocation loc, double offset) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		
		if(x < getMinX() + offset)
			x = getMinX() + offset;
		else if(x > getMaxX() - offset)
			x = getMaxX() - offset;
		
		if(y < getMinY() + offset)
			y = getMinY() + offset;
		else if(y > getMaxY() - offset)
			y = getMaxY() - offset;
		
		if(z < getMinZ() + offset)
			z = getMinZ() + offset;
		else if (z > getMaxZ() - offset)
			z = getMaxZ() - offset;
		
		SSGLocation newLoc = loc.clone();
		newLoc.setX(x);
		newLoc.setY(y);
		newLoc.setZ(z);
		return newLoc;
	}
	
	/**
	 * Get the cuboid volume
	 * @return cuboid volume
	 */
	public int getVolume() {
		return (getMaxX() - getMinX()) * (getMaxY() - getMinY()) * (getMaxZ() - getMinZ());
	}
	
	/**
	 * Get the cuboid height
	 * @return cuboid height
	 */
	public int getHeight() {
		return getMaxY() - getMinY();
	}
	
	/**
	 * Is the cuboid set
	 * @return true if set
	 */
	public boolean isSet() {
		return (this.corner1 != null && this.corner2 != null);
	}
}
