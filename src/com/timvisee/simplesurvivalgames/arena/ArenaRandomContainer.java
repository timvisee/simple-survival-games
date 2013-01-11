package com.timvisee.simplesurvivalgames.arena;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;

public class ArenaRandomContainer extends ArenaContainer {
	
	/**
	 * Constructor
	 * @param loc random container location
	 */
	public ArenaRandomContainer(SSGLocation loc) {
		super(loc);
	}
	
	/**
	 * Constructor
	 * @param container random container location
	 */
	public ArenaRandomContainer(Block container) {
		super(container);
	}
	
	/**
	 * Refill the container with random contents
	 * @return the new container contents
	 */
	public ItemStack[] refill() {
		// TODO RANDOM REFILL SYSTEM HERE
		
		return new ItemStack[]{};
	}
}
