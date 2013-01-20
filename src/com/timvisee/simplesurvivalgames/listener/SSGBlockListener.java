package com.timvisee.simplesurvivalgames.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaState;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

public class SSGBlockListener implements Listener {
	
	// Placable and destructable blocks
	private final List<Material> breakableBlocks = Arrays.asList(new Material[]{
			Material.AIR,
			Material.LEAVES,
			Material.WEB,
			Material.LONG_GRASS,
			Material.DEAD_BUSH,
			Material.YELLOW_FLOWER,
			Material.RED_ROSE,
			Material.BROWN_MUSHROOM,
			Material.RED_MUSHROOM,
			Material.FIRE,
			Material.MELON,
			Material.MELON_BLOCK,
			Material.COCOA
	});
	private final List<Material> placableBlocks = Arrays.asList(new Material[]{
			Material.AIR,
			Material.LONG_GRASS,
			Material.DEAD_BUSH,
			Material.YELLOW_FLOWER,
			Material.RED_ROSE,
			Material.BROWN_MUSHROOM,
			Material.RED_MUSHROOM
	});
	
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
					
					// The player may not build and show a status message
					event.setCancelled(true);
					ap.sendMessage(ChatColor.DARK_RED + "You can't break the lobby of an arena!");
					ap.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg" + " vote" + ChatColor.DARK_RED + " to vote start the arena");
					
					// TODO: Current voting status message (persentage / players)
					// TODO: If already vote started, only show status message
				}
			}
			
			// Is the player playing
			if(ap.isPlaying()) {
				
				// May the user break this block
				if(!this.breakableBlocks.contains(b.getType())) {
					ap.sendMessage(ChatColor.DARK_RED + "You may not break this block!");
					event.setCancelled(true);
					return;
				}
				
				if(ap.getArena().isArenaCuboidSet()) {
					if(!ap.getArena().getArenaCuboid().isInsideCuboid(b)) {
						event.setCancelled(true);
						ap.sendMessage(ChatColor.DARK_RED + "You can't break any blocks outside the arena!");
					} else {
						// TODO: Is the player allowed to break this block
						ap.getArena().getPlayerBlockManager().addBlock(b);
					}
				}
			}
			
			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't break blocks while spectating!");
			}
		} else {
			for(Arena arena : am.getArenas()) {
				if(arena.isArenaCuboidSet()) {
					if(arena.getArenaCuboid().isInsideCuboid(b)) {
						if(!arena.isInEditMode()) {
							p.sendMessage(ChatColor.DARK_RED + "You can't break blocks in the arena while it isn't in edit mode!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg edit true" + ChatColor.BLUE + " to put an arena in edit mode!");
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		Block b = event.getBlock();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Capture the block
		for(Arena a : am.getArenas())
			if(a.isArenaCuboidSet())
				if(a.getArenaCuboid().isInsideCuboid(b))
					if(a.getState().equals(ArenaState.PLAYING) || a.getState().equals(ArenaState.STARTING))
						a.getPlayerBlockManager().addBlock(b);
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		Block b = event.getBlock();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Capture the block
		for(Arena a : am.getArenas())
			if(a.isArenaCuboidSet())
				if(a.getArenaCuboid().isInsideCuboid(b))
					if(a.getState().equals(ArenaState.PLAYING) || a.getState().equals(ArenaState.STARTING))
						a.getPlayerBlockManager().addBlock(b);
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
			
			// Is the player playing
			if(ap.isPlaying()) {
				
				// May the user place this block
				if(!this.placableBlocks.contains(b.getType())) {
					ap.sendMessage(ChatColor.DARK_RED + "You may not place this block!");
					event.setCancelled(true);
					return;
				}
				
				if(ap.getArena().isArenaCuboidSet()) {
					if(!ap.getArena().getArenaCuboid().isInsideCuboid(b)) {
						event.setCancelled(true);
						ap.sendMessage(ChatColor.DARK_RED + "You can't place any blocks outside the arena!");
					} else {
						// TODO: Is the player allowed to place down this block
						ap.getArena().getPlayerBlockManager().addBlock(event.getBlockReplacedState());
					}
				}
			}
			
			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't place blocks while spectating!");
			}
		} else {
			for(Arena arena : am.getArenas()) {
				if(arena.isArenaCuboidSet()) {
					if(arena.getArenaCuboid().isInsideCuboid(b)) {
						if(!arena.isInEditMode()) {
							p.sendMessage(ChatColor.DARK_RED + "You can't place blocks in the arena while it isn't in edit mode!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg edit true" + ChatColor.BLUE + " to put an arena in edit mode!");
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		Block source = event.getSource();
		Block to = event.getBlock();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Block spreading out and into the arena
		for(Arena arena : am.getArenas())
			if(arena.isArenaCuboidSet())
				if((arena.getArenaCuboid().isInsideCuboid(source) &&
						!arena.getArenaCuboid().isInsideCuboid(to)) ||
						(arena.getArenaCuboid().isInsideCuboid(to) &&
						!arena.getArenaCuboid().isInsideCuboid(source)))
					event.setCancelled(true);
	}
}
