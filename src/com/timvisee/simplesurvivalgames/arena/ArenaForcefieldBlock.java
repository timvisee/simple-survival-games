package com.timvisee.simplesurvivalgames.arena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.timvisee.simplesurvivalgames.SSGBlockState;

public class ArenaForcefieldBlock {
	
	private Player p;
	private Block b;
	private SSGBlockState origState;
	private ForcefieldBlockState state = ForcefieldBlockState.NORMAL;
	
	/**
	 * Constructor
	 * @param b the block
	 */
	public ArenaForcefieldBlock(Player p, Block b) {
		this.p = p;
		this.b = b;
		this.origState = new SSGBlockState(b.getState());
	}
	
	/**
	 * Get the current player
	 * @return current player
	 */
	public Player getPlayer() {
		return this.p;
	}
	
	/**
	 * Set the current player
	 * @param p new player
	 */
	public void setPlayer(Player p) {
		if(p != null)
			this.p = p;
	}
	
	/**
	 * Get the block
	 * @return the block
	 */
	public Block getBlock() {
		return this.b;
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
	 * @param origState new original block state 
	 */
	public void setOriginalBlockState(SSGBlockState origState) {
		this.origState = origState;
	}
	
	/**
	 * Get the current state of the block
	 * @return current block state
	 */
	public ForcefieldBlockState getState() {
		return this.state;
	}

	/**
	 * Set the current state of the block
	 * @param newState the state the block should be changed too
	 */
	public void setState(ForcefieldBlockState newState) {
		setState(newState, true);
	}
	
	/**
	 * Set the current state of the block
	 * @param newState the state the block should be changed too
	 * @param updateBlock should the block be updated if the state changes
	 */
	public void setState(ForcefieldBlockState newState, boolean updateBlock) {
		// The state has to be different than before
		if(!this.state.equals(newState)) {
			
			this.state = newState;
			
			switch(newState) {
			case NORMAL:
				if(updateBlock)
					origState.applyToBlock(b);
				break;
				
			case FORCEFIELD:
				if(updateBlock)
					b.setType(Material.GLASS);
				break;
			}
		}
	}
	
	/**
	 * Does this object equals to another
	 * @param other the other object
	 * @return true if equal
	 */
	public boolean equals(ArenaForcefieldBlock other) {
		if(other == null)
			return false;
		
		return this.b.equals(other.getBlock());
	}
	
	public enum ForcefieldBlockState {
		NORMAL,
		FORCEFIELD;
	}
}
