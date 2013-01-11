package com.timvisee.simplesurvivalgames.arena;

import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;

public class ArenaContainer {
	
	private SSGLocation loc;
	
	/**
	 * Constructor
	 * @param loc chest location
	 */
	public ArenaContainer(SSGLocation loc) {
		this.loc = loc;
	}
	
	/**
	 * Constructor
	 * @param container container location
	 */
	public ArenaContainer(Block container) {
		this.loc = new SSGLocation(container.getLocation());
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
	@SuppressWarnings("deprecation")
	public ContainerBlock getContainerBlock() {
		// Get the container block, the block may not be null
		Block b = getBlock();
		if(b == null)
			return null;
		
		// Convert the block to a control and return
		return (ContainerBlock) b.getState();
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
		InventoryHolder invHolder = getContainerBlock();
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
	public ItemStack[] refill() {
		// TODO REFILL FUNCTION
		
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
