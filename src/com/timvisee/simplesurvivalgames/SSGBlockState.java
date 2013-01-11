package com.timvisee.simplesurvivalgames;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class SSGBlockState {
	
	private SSGLocation loc;
	private int typeId = 0;
	private byte data = 0;
	private String[] lines = new String[]{};

	/**
	 * Constructor
	 * @param typeId
	 */
	public SSGBlockState(SSGLocation loc, int typeId) {
		this.typeId = typeId;
	}

	/**
	 * Constructor
	 * @param typeId
	 * @param data
	 */
	public SSGBlockState(SSGLocation loc, int typeId, byte data) {
		this.typeId = typeId;
		this.data = data;
	}
	
	/**
	 * Constructor
	 * @param state
	 */
	public SSGBlockState(BlockState state) {
		this.loc = new SSGLocation(state.getLocation());
		this.typeId = state.getTypeId();
		this.data = state.getData().getData();
		
		// Sign text
		if(state instanceof Sign) {
			Sign sign = (Sign) state;
			this.lines = sign.getLines();
		}
	}
	
	/**
	 * Get the location of the block state
	 * @param loc the location
	 */
	public SSGLocation getLocation() {
		return this.loc;
	}
	
	/**
	 * Set the location of the block state
	 * @param loc the new location
	 */
	public void setLocation(SSGLocation loc) {
		this.loc = loc;
	}
	
	/**
	 * Get the block of the block state
	 * @return block of the block state, null if block not loaded
	 */
	public Block getBlock() {
		return this.loc.getBlock();
	}
	
	/**
	 * Get the block ID of the block state
	 * @return block ID
	 */
	public int getTypeId() {
		return this.typeId;
	}
	
	/**
	 * Set the type ID of the block state 
	 * @param typeId the new block state type id
	 */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	/**
	 * Get the data from the block state
	 * @return block state data
	 */
	public byte getData() {
		return this.data;
	}
	
	/**
	 * Set the state data
	 * @param data new state data
	 */
	public void setData(byte data) {
		this.data = data;
	}
	
	/**
	 * Apply the current block state to the block
	 */
	public void applyToBlock() {
		Block b = this.loc.getBlock();
		
		if(b == null)
			return;
		
		applyToBlock(b);
	}
	
	/**
	 * Apply the current block state to a block
	 * @param block the block to apply to
	 */
	public void applyToBlock(Block block) {
		block.setTypeId(this.typeId);
		block.setData(this.data);
		
		if(block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			sign.setLine(1, this.lines[0]);
			sign.setLine(2, this.lines[1]);
			sign.setLine(3, this.lines[2]);
			sign.setLine(4, this.lines[3]);
			sign.update();
		}
	}
}

