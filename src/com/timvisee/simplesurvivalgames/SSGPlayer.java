package com.timvisee.simplesurvivalgames;

import org.bukkit.entity.Player;

import com.timvisee.simplesurvivalgames.arena.Arena;

public class SSGPlayer {
	
	private Player p;
	private Arena selectedArena = null;
	private PlayerMode playerMode = PlayerMode.NORMAL;
	
	public SSGPlayer(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return this.p;
	}
	
	public Arena getSelectedArena() {
		return this.selectedArena;
	}
	
	public boolean hasArenaSelected() {
		return (this.selectedArena != null);
	}
	
	public void setSelectedArena(Arena selectedArena) {
		this.selectedArena = selectedArena;
	}
	
	public void resetSelectedArena() {
		this.selectedArena = null;
	}
	
	public PlayerMode getPlayerMode() {
		return this.playerMode;
	}
	
	public void setPlayerMode(PlayerMode mode) {
		this.playerMode = mode;
	}
	
	public boolean equals(SSGPlayer other) {
		return equals(other.getPlayer());
	}
	
	public boolean equals(Player player) {
		return this.p.equals(player);
	}
	
	public enum PlayerMode {
		NORMAL,
		ADD_RANDOM_CONTAINER,
		ADD_STATIC_CONTAINER,
		REMOVE_CONTAINER;
	}
}
