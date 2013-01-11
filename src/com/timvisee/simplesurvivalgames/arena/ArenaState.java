package com.timvisee.simplesurvivalgames.arena;

public enum ArenaState {
	
	WAITING(0),
	PLAYING(1),
	LOBBY(2);
	
	private int id;
	
	/**
	 * Constructor
	 * @param id state id
	 */
	ArenaState(int id) {
		this.id = id;
	}
	
	/**
	 * Get the state id
	 * @return state id
	 */
	private int getId() {
		return this.id;
	}
	
}
