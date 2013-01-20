package com.timvisee.simplesurvivalgames.arena.container;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.container.items.ArenaContainerSetManager;

public class ArenaContainerManager {
	
	Arena arena;
	List<ArenaContainer> containers = new ArrayList<ArenaContainer>();
	private ArenaContainerSetManager contentManager;
	
	/**
	 * Constructor
	 * @param arena the arena
	 */
	public ArenaContainerManager(Arena arena) {
		this.arena = arena;
		this.contentManager = new ArenaContainerSetManager(arena);
	}
	
	/**
	 * Get the current arena
	 * @return the current arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Add a container
	 * @param loc the chest location
	 * @return the new chest
	 */
	public ArenaContainer addContainer(SSGLocation loc) {
		ArenaContainer newContainer = new ArenaContainer(this.arena, loc);
		addContainer(newContainer);
		return newContainer;
	}
	
	/**
	 * Add a container
	 * @param container thew new container
	 */
	public void addContainer(ArenaContainer container) {
		this.containers.add(container);
	}
	
	/**
	 * Is a block an arena container block
	 * @param b the block to check
	 * @return false if not
	 */
	public boolean isContainer(Block b) {
		for(ArenaContainer container : this.containers)
			if(container.getBlock().equals(b))
				return true;
		return false;
	}
	
	/**
	 * Get a list of all the containers in this arena
	 * @return list of containers
	 */
	public List<ArenaContainer> getContainers() {
		return this.containers;
	}
	
	/**
	 * Get a list of all empty containers
	 * @return list of empty containers
	 */
	public List<ArenaContainer> getEmptyContainers() {
		// Loop through all containers to check if they're empty, on the end return all empty containers
		List<ArenaContainer> emptyContainers = new ArrayList<ArenaContainer>();
		for(ArenaContainer container : this.containers)
			if(container.isEmpty())
				emptyContainers.add(container);
		return emptyContainers;
	}
	
	/**
	 * Get a list of all random containers
	 * @return list of random containers
	 */
	public List<ArenaRandomContainer> getRandomContainers() {
		// Loop through all containers to check if they're a random container, on the end return all empty containers
		List<ArenaRandomContainer> emptyRandomContainers = new ArrayList<ArenaRandomContainer>();
		for(ArenaContainer container : this.containers)
			if(container instanceof ArenaRandomContainer)
				if(container.isEmpty())
					emptyRandomContainers.add((ArenaRandomContainer) container);
		return emptyRandomContainers;
	}
	
	/**
	 * Get a list of all random containers
	 * @return list of random containers
	 */
	public List<ArenaStaticContainer> getStaticContainers() {
		// Loop through all containers to check if they're a static container, on the end return all empty containers
		List<ArenaStaticContainer> emptyStaticContainers = new ArrayList<ArenaStaticContainer>();
		for(ArenaContainer container : this.containers)
			if(container instanceof ArenaStaticContainer)
				if(container.isEmpty())
					emptyStaticContainers.add((ArenaStaticContainer) container);
		return emptyStaticContainers;
	}
	
	/**
	 * Remove a container
	 * @param container the container to remove
	 * @return false if the container wasn't found in the list
	 */
	public boolean removeContainer(ArenaContainer container) {
		return this.containers.remove(container);
	}
	
	/**
	 * Remove a container
	 * @param container the container to remove
	 * @return false if the container wasn't found in the list
	 */
	public boolean removeContainer(Block container) {
		List<ArenaContainer> remove = new ArrayList<ArenaContainer>();
		for(ArenaContainer entry : this.containers)
			if(entry.getBlock() != null)
				if(entry.getBlock().equals(container))
					remove.add(entry);
		this.containers.removeAll(remove);
		return (remove.size() != 0);
	}
	
	/**
	 * Refill all, or only empty container in the arena
	 * @param onlyEmpty true to only fill empty container
	 */
	public void fillConatiners(boolean onlyEmpty) {
		for(ArenaContainer container : this.containers)
			if(!onlyEmpty || container.isEmpty())
				container.fill();
	}
	/**
	 * 
	 * Refill all static containers, or only empty container in the arena
	 * @param onlyEmpty true to only fill empty container
	 */
	public void fillStaticContainers(boolean onlyEmpty) {
		for(ArenaContainer container : this.containers)
			if(container instanceof ArenaStaticContainer)
				if(!onlyEmpty || container.isEmpty())
					container.fill();
	}
	
	/**
	 * Get the amount of containers in this arena
	 * @return the amount of containers
	 */
	public int getContainerCount() {
		return this.containers.size();
	}
	
	/**
	 * Get the amount of empty containers in this arena
	 * @return empty containers amount
	 */
	public int getEmptyContainerCount() {
		return this.getEmptyContainers().size();
	}
	
	/**
	 * Get the amount of random containers in this arena
	 * @return random containers amount
	 */
	public int getRandomContainerCount() {
		return this.getRandomContainers().size();
	}
	
	/**
	 * Get the amount of static containers in this arena
	 * @return static containers amount
	 */
	public int getStaticContainerCount() {
		return this.getStaticContainers().size();
	}
	
	/**
	 * Clear the list with containers
	 */
	public void clear() {
		this.containers.clear();
	}
	
	/**
	 * Get the content manager
	 * @return content manager
	 */
	public ArenaContainerSetManager getContentManager() {
		return this.contentManager;
	}
}
