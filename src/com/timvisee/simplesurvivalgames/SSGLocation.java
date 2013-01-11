package com.timvisee.simplesurvivalgames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SSGLocation {
	
	// Variables
	private String world = "world";
	private double x, y, z = 0;
	private float yaw, pitch = 0;
	
	// Constructors
	public SSGLocation() { }
	
	public SSGLocation(SSGLocation loc) {
		setLocation(loc);
	}
	
	public SSGLocation(Location loc) {
		setLocation(loc);
	}
	
	public SSGLocation(World world) {
		this.world = world.getName();
	}
	
	public SSGLocation(String worldName) {
		this.world = worldName;
	}
	
	public SSGLocation(World world, double x, double y, double z) {
		this.world = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SSGLocation(String worldName, double x, double y, double z) {
		this.world = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SSGLocation(World world, double x, double y, double z, float yaw, float pitch) {
		this.world = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public SSGLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
		this.world = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	// Methods
	public String getWorldName() {
		return this.world;
	}
	
	public World getWorld() {
		// Loop through each world to check if it's the current world, if so return the world
		for(World w : Bukkit.getServer().getWorlds())
			if(w.getName().equals(this.world))
				return w;
		return null;
	}
	
	public void setWorld(World world) {
		this.world = world.getName();
	}
	
	public void setWorld(String worldName) {
		if(worldName.trim().equals(""))
			return;
		this.world = worldName;
	}
	
	public double getX() {
		return this.x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw % 360;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = ((pitch + 90) % 180) - 90;
	}
	
	public Block getBlock() {
		Location loc = toBukkitLocation();
		if(loc == null)
			return null;
		return loc.getBlock();
	}
	
	public Location toBukkitLocation() {
		// Get the world, the world may not be null
		World w = getWorld();
		if(w == null)
			return null;
		
		// Create a Bukkit location class, define and return it
		return new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
	}
	
	public void setLocation(SSGLocation loc) {
		this.world = loc.getWorldName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
	}
	
	public void setLocation(Location loc) {
		this.world = loc.getWorld().getName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
	}
	
	public SSGLocation clone() {
		return new SSGLocation(getWorldName(), getX(), getY(), getZ(), getYaw(), getPitch());
	}
}
