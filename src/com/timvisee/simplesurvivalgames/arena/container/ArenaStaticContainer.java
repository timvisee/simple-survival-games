package com.timvisee.simplesurvivalgames.arena.container;

import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.Arena;

public class ArenaStaticContainer extends ArenaContainer{
	
	private ItemStack[] defContents = new ItemStack[]{};
	
	/**
	 * Constructor
	 * @param loc chest location
	 * @param defaultContensts default container contents
	 */
	public ArenaStaticContainer(Arena arena, SSGLocation loc, ItemStack[] defaultContents) {
		super(arena, loc);
		this.defContents = defaultContents;
	}
	
	/**
	 * Constructor
	 * @param chest chest location
	 * @param defaultContents default container contents
	 */
	public ArenaStaticContainer(Arena arena, Block chest, ItemStack[] defaultContents) {
		super(arena, new SSGLocation(chest.getLocation()));
		this.defContents = defaultContents;
	}
	
	/**
	 * Get the default contents
	 * @return
	 */
	public ItemStack[] getDefaultContents() {
		return this.defContents;
	}
	
	/**
	 * Set the default contents of the container
	 * @param defContents
	 */
	public void setDefaultContents(ItemStack[] defContents) {
		this.defContents = defContents;
	}
	
	/**
	 * Store the current state of the chest
	 */
	public void storeCurrentContents() {
		this.defContents = getContents();
	}
	
	/**
	 * Refill the container with it's default contents
	 * @return the new container contents
	 */
	public ItemStack[] fill() {
		// Set the container contents to it's default
		setContents(defContents);
		return getContents();
	}
}
