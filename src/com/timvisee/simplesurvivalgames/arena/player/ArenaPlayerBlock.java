package com.timvisee.simplesurvivalgames.arena.player;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.timvisee.simplesurvivalgames.SSGBlockState;

public class ArenaPlayerBlock {
	
	private Block b;
	private SSGBlockState origState;
	
	/**
	 * Constructor
	 * @param b the block
	 */
	public ArenaPlayerBlock(Block b) {
		this.b = b;
		this.origState = new SSGBlockState(b.getState());
	}
	
	/**
	 * Constructor
	 * @param b the block state
	 */
	public ArenaPlayerBlock(BlockState b) {
		this.b = b.getBlock();
		this.origState = new SSGBlockState(b);
	}
	
	/**
	 * Get the block
	 * @return block
	 */
	public Block getBlock() {
		return this.b;
	}
	
	/**
	 * Get the location of the block
	 * @return
	 */
	public Location getLocation() {
		return this.b.getLocation();
	}
	
	/**
	 * Get the original block state
	 * @return original block state
	 */
	public SSGBlockState getOriginalBlockState() {
		return this.origState;
	}
	
	/**
	 * Set the original block state
	 * @param newState new block state
	 */
	public void setOriginalBlockState(BlockState newState) {
		setOriginalBlockState(new SSGBlockState(newState));
	}
	
	/**
	 * Set the original block state
	 * @param newState new block state
	 */
	public void setOriginalBlockState(SSGBlockState newState) {
		this.origState = newState;
	}
	
	/**
	 * Revert the block to it's original state
	 */
	public void revert() {
		this.origState.applyToBlock(this.b);
	}
	
	/**
	 * Check if a block is equal to the block of this one
	 * @param block the block to equal
	 * @return true if the blocks equal
	 */
	public boolean equals(Block block) {
		return (this.b.equals(block));
	}
	
	/**
	 * Check if another ArenaPlayerBlock is equal to this one
	 * @param other the other ArenaPlayerBlock
	 * @return true if equal
	 */
	public boolean equals(ArenaPlayerBlock other) {
		return (this.b.equals(other.getBlock()));
	}
}
