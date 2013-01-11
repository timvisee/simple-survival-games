package com.timvisee.simplesurvivalgames.arena;

import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;

public class ArenaStaticContainer extends ArenaContainer{
	
	private ItemStack[] defaultContents = new ItemStack[]{};
	
	/**
	 * Constructor
	 * @param loc chest location
	 * @param defaultContensts default container contents
	 */
	public ArenaStaticContainer(SSGLocation loc, ItemStack[] defaultContents) {
		super(loc);
		this.defaultContents = defaultContents;
	}
	
	/**
	 * Constructor
	 * @param chest chest location
	 * @param defaultContents default container contents
	 */
	public ArenaStaticContainer(Block chest, ItemStack[] defaultContents) {
		super(new SSGLocation(chest.getLocation()));
		this.defaultContents = defaultContents;
	}
	
	/**
	 * Refill the container with it's default contents
	 * @return the new container contents
	 */
	public ItemStack[] refill() {
		// Set the container contents to it's default
		setContents(defaultContents);
		return getContents();
	}
}
