package com.timvisee.simplesurvivalgames.arena.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.timvisee.simplesurvivalgames.arena.Arena;

public class ArenaPlayerBlockManager {
	
	Arena arena;
	private List<ArenaPlayerBlock> blocks = new ArrayList<ArenaPlayerBlock>();
	
	/**
	 * Constructor
	 */
	public ArenaPlayerBlockManager(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Get the arena
	 * @return arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Add a new block.
	 * @param b the block to add
	 * @return true if the block already existed in the list
	 */
	public boolean addBlock(Block b) {
		return addBlock(b.getState());
	}
	
	/**
	 * Add a new block.
	 * @param b the block state to add
	 * @return true if the block already existed in the list
	 */
	public boolean addBlock(BlockState b) {
		// The block may not already exist, if so return true
		if(contains(b.getBlock()))
			return true;
		
		// Add the block to the list and return false
		this.blocks.add(new ArenaPlayerBlock(b));
		return false;
	}
	
	/**
	 * Get the ArenaPlayerBlock from a block
	 * @param b the block to get the ArenaPlayerBlock object from
	 * @return ArenaPlayerBlock or null if the block is not listed
	 */
	public ArenaPlayerBlock get(Block b) {
		// Loop through every block and check if it's equals to the parameter block
		for(ArenaPlayerBlock entry : this.blocks)
			if(entry.equals(b))
				return entry;
		
		// Block not found, return null
		return null;
	}
	
	/**
	 * Check if the block list already contains this block
	 * @param b the block to check for
	 * @return true if already exists in the list
	 */
	public boolean contains(Block b) {
		// Loop through every block and check if it's equals to the parameter block
		for(ArenaPlayerBlock entry : this.blocks)
			if(entry.equals(b))
				return true;
		
		// Block not found return false
		return false;
	}
	
	/**
	 * Get the list of blocks
	 * @return list of block
	 */
	public List<ArenaPlayerBlock> getBlocks() {
		return this.blocks;
	}
	
	/**
	 * Get the amount of blocks listed
	 * @return amount of blocks listed
	 */
	public int getBlockCount() {
		return this.blocks.size();
	}
	
	/**
	 * Remove a block from the list
	 * @param b the block to remove
	 * @return false if specified block doesn't exist in the list
	 */
	public boolean remove(Block b) {
		if(!contains(b))
			return false;
		
		// Get the blcok as ArenaPlayerBlock and remove it from the list
		return remove(get(b));
	}
	
	/**
	 * Remove an ArenaPlayerBlock from the list
	 * @param b the block to remove
	 * @return false if specified block doesn't exist in the list
	 */
	public boolean remove(ArenaPlayerBlock b) {
		// The block may not be null
		if(b == null)
			return false;
		
		return this.blocks.remove(b);
	}
	
	/**
	 * Revert a block
	 * @param b the block to revert
	 * @return false if the block wasn't found in the list
	 */
	public boolean revertBlock(Block b) {
		ArenaPlayerBlock block = get(b);
		
		// The block may not be null, if so return false
		if(block == null)
			return false;
		
		// Revert the block, and return true
		block.revert();
		return true;
	}
	
	/**
	 * Revert all the listed blocks
	 */
	public void revertAllBlocks() {
		for(ArenaPlayerBlock b : getBlocks())
			b.revert();
	}
	
	/**
	 * Clear the list of blocks
	 */
	public void clear() {
		this.blocks.clear();
	}
}
