package com.timvisee.simplesurvivalgames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class SSGPlayerManager {
	
	private List<SSGPlayer> players = new ArrayList<SSGPlayer>();
	
	public SSGPlayerManager() { }
	
	public SSGPlayer addPlayer(Player p) {
		if(containsPlayer(p))
			return getPlayer(p);
		
		SSGPlayer player = new SSGPlayer(p);
		this.players.add(player);
		return player;
	}
	
	public List<SSGPlayer> getPlayers() {
		return this.players;
	}
	
	public int getPlayerCount() {
		return this.players.size();
	}
	
	public boolean containsPlayer(SSGPlayer p) {
		return containsPlayer(p.getPlayer());
	}
	
	public boolean containsPlayer(Player p) {
		for(SSGPlayer entry : this.players)
			if(entry.getPlayer().equals(p))
				return true;
		return false;
	}
	
	public SSGPlayer getPlayer(Player p) {
		for(SSGPlayer entry : this.players)
			if(entry.getPlayer().equals(p))
				return entry;
		return null;
	}
	
	public boolean hasArenaSelected(Player p) {
		if(!containsPlayer(p))
			return false;
		
		return getPlayer(p).hasArenaSelected();
	}
	
	public void clear() {
		this.players.clear();
	}
}
