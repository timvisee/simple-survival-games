package com.timvisee.simplesurvivalgames.arena.container;

import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.container.items.ArenaContainerSetManager;

public class ArenaRandomContainer extends ArenaContainer {
	
	/**
	 * Constructor
	 * @param loc random container location
	 */
	public ArenaRandomContainer(Arena arena, SSGLocation loc) {
		super(arena, loc);
	}
	
	/**
	 * Constructor
	 * @param container random container location
	 */
	public ArenaRandomContainer(Arena arena, Block container) {
		super(arena, container);
	}
	
	/**
	 * Get the content set manager
	 * @return content set manager
	 */
	public ArenaContainerSetManager getContentSetManager() {
		return this.arena.getContainerManager().getContentManager();
	}
	
	/**
	 * Refill the container with random contents
	 * @return the new container contents
	 */
	public ItemStack[] fill() {
		InventoryHolder invHolder = getInventoryHolder();
		
		// The inventory holder may not be null
		if(invHolder == null)
			return new ItemStack[]{};
		
		// Fill the inventory holder and return the new contents
		return getContentSetManager().fillInventoryHolder(invHolder);
	}
}
