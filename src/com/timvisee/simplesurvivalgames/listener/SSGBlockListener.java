package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.ArenaPlayerManager;

public class SSGBlockListener implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The player may not break forcefield blocks
		for(Arena a : am.getArenas()) {
			if(a.getForcefieldManager().isBlockUsedAsForcefieldBlock(b)) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.DARK_RED + "You can't break the forcefield!");
			}
		}
		
		// TODO: Is the player allowed to break blocks in the arena
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Update the players forcefield
			ap.getArena().getForcefieldManager().updateForcefield(ap);
			
			// Is the arena in lobby state
			if(ap.isInLobby()) {
				
				// Make sure the player is linked to a spawn point
				if(ap.hasAssignedAreanSpawn()) {
					
					// The player may not build and hsow a status message
					event.setCancelled(true);
					ap.sendMessage(ChatColor.DARK_RED + "You can't break the lobby of an arena!");
					ap.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg" + " vote" + ChatColor.DARK_RED + " to vote start the arena");
					
					// TODO: Current voting status message (persentage / players)
					// TODO: If already vote started, only show status message
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The player may not break forcefield blocks
		for(Arena a : am.getArenas())
			if(a.getForcefieldManager().isBlockUsedAsForcefieldBlock(b))
				event.setCancelled(true);
		
		// TODO Is the player allowed to place any blocks in the arena
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the arena in lobby state
			if(ap.isInLobby()) {
				
				// Make sure the player is linked to a spawn point
				if(ap.hasAssignedAreanSpawn()) {
					
					// The player may not build and hsow a status message
					event.setCancelled(true);
					ap.sendMessage(ChatColor.DARK_RED + "You can't build in the lobby of an arena!");
					ap.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/sg" + " vote" + ChatColor.DARK_RED + " to vote start the arean");
					
					// TODO: Current voting status message (persentage / players)
					// TODO: If already vote started, only show status message
				}
			}
		}
	}
}
