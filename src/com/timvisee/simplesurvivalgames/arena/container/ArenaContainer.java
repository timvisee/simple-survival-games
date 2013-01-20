package com.timvisee.simplesurvivalgames.arena.container;

import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.Arena;

@SuppressWarnings("deprecation")
public class ArenaContainer {
	
	Arena arena;
	private SSGLocation loc;
	
	/**
	 * Constructor
	 * @param loc chest location
	 */
	public ArenaContainer(Arena arena, SSGLocation loc) {
		this.arena = arena;
		this.loc = loc;
	}
	
	/**
	 * Constructor
	 * @param container container location
	 */
	public ArenaContainer(Arena arena, Block container) {
		this.arena = arena;
		this.loc = new SSGLocation(container.getLocation());
	}
	
	/**
	 * Get the arena
	 * @return
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Get the location of the container
	 * @return container location
	 */
	public SSGLocation getLocation() {
		return this.loc;
	}
	
	/**
	 * Get the block (location) of the chests
	 * @return container (block) location
	 */
	public Block getBlock() {
		return loc.getBlock();
	}
	
	/**
	 * Get the current container
	 * @return the container
	 */
	public ContainerBlock getContainerBlock() {
		// Get the container block, the block may not be null
		Block b = getBlock();
		if(b == null)
			return null;
		
		// Convert the block to a control and return
		if(b.getState() instanceof ContainerBlock)
			return (ContainerBlock) b.getState();
		return null;
	}

	/**
	 * Get the inventory holder
	 * @return inventory holder
	 */
	public InventoryHolder getInventoryHolder() {
		// Get the inventory holder of the block, the block may not be null
		Block b = getBlock();
		if(b == null)
			return null;

		// Convert the block to an inventory holder
		if(b.getState() instanceof InventoryHolder)
			return (InventoryHolder) b.getState();
		return null;
	}
	
	/**
	 * Get the container contents
	 * @return container contents
	 */
	public ItemStack[] getContents() {
		// Get the container and cast it to an inventory holder, make sure it's not null
		InventoryHolder invHolder = getContainerBlock();
		if(invHolder == null)
			return new ItemStack[]{};
		
		// Return the container contents
		return invHolder.getInventory().getContents();
	}
	
	/**
	 * Set the container contents
	 * @param newContents new container contents
	 * @return true if succeed
	 */
	public boolean setContents(ItemStack[] newContents) {
		// Get the container and cast it to an inventory holder, make sure it's not null
		InventoryHolder invHolder = getInventoryHolder();
		if(invHolder == null)
			return false;
		
		// Set the contents of the container
		invHolder.getInventory().setContents(newContents);
		return true;
	}
	
	/**
	 * Refill the container
	 * @return the new container contents
	 */
	public ItemStack[] fill() {
		return new ItemStack[]{};
	}
	
	/**
	 * Is the chest empty
	 * @return true if the chest is empty, or when the chest inventory could not be loaded
	 */
	public boolean isEmpty() {
		// Get the container, and cast it to an InventoryHolder, make sure it's not null
		InventoryHolder invHolder = getContainerBlock();
		if(invHolder == null)
			return true;
		
		// Get the container contents
		ItemStack[] contents = invHolder.getInventory().getContents();
		
		// Are the contents empty?
		for(ItemStack item : contents) {
			// If the item stack equals null, continue in the for loop
			if(item == null)
				continue;
			
			// If the item stack isn't air, the contents aren't empty
			if(item.getTypeId() != 0)
				return false;
		}
		
		// Return true as default (inventory = empty)
		return true;
	}
}
