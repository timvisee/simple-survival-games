package com.timvisee.simplesurvivalgames.arena;

public enum ArenaState {
	
	STANDBY("STANDBY"),
	STARTING("STARTING"),
	PLAYING("PLAYING"),
	LOBBY("LOBBY");
	
	private String name;
	
	/**
	 * Constructor
	 * @param name state name
	 */
	ArenaState(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of the state
	 * @return name of the state
	 */
	public String getName() {
		return this.name;
	}
}
