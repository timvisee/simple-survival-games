package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaCuboid;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaPlayer;

public class SSGPlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "You aren't tired enough to enter the bed inside the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "You may not flood the lobby in the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "You may not dehumidify the lobby in the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String fullCommand = event.getMessage();
		String commandLabel = fullCommand.substring(1).split(" ")[0];
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			Arena a = am.getArena(p);
			
			// Is the player in the lobby of the arena
			if(!a.isAllowedCommand(commandLabel)) {
				
				// Make sure the player is allowed to use the command
				if(!a.isAllowedCommand(commandLabel) &&
						!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.bypassblockedcommands")) {
					p.sendMessage(ChatColor.DARK_RED + fullCommand);
					p.sendMessage(ChatColor.DARK_RED + "You can't use this command in an arena!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "Drop parties are not allowed inside the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerExpPickup(PlayerExpChangeEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);

			// The player may not pickup any Exp as spectator
			if(ap.isSpectator())
				event.setAmount(0);
		}
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "You can't fish inside the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					if(!event.isCancelled()) {
						event.setCancelled(true);
						p.sendMessage(ChatColor.DARK_RED + "You can't interact with anything in the lobby of an arena!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			Arena arena = ap.getArena();
			
			// Is the player in the lobby of the arena
			if(ap.isPlaying()) {
				// Is the player allowed to get out of the arena cuboid
				if(!arena.getMayLeaveArenaCuboid() && arena.getArenaCuboid().isSet()) {
					// The player has to be inside the arena
					if(!ap.isInArenaCuboid()) {
						// The the new location of the player
						Location newLoc = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(p.getLocation()), 1).toBukkitLocation();
						
						// Store the entity to teleport back into the arena
						Entity toTeleport = p;
						
						// Make the vehicle a player is in teleport if he is inside one
						if(p.isInsideVehicle())
							toTeleport = p.getVehicle();
						
						// Set the players velocity to zero
						toTeleport.setVelocity(new Vector(0, 0, 0));
						
						// Teleport the player
						toTeleport.teleport(newLoc);
						
						// Send a notification message
						p.sendMessage(ChatColor.DARK_RED + "You may not get out of the arena!");
						
						// Damage the player to take measures!
						p.damage(4);
					}
				}
			}
			
			// Update the forcefield of the player
			ap.getArena().getForcefieldManager().updateForcefield(ap);
			
			// Is the arena still in lobby state
			if(ap.isInLobby()) {
				
				// Make sure the player is linked to a spawn point
				if(ap.hasAssignedAreanSpawn()) {
					
					// Is the player at the spawn
					if(!ap.getAssignedAreanSpawn().isAtSpawn(ap)) {
						// Teleport the player to the spawn and show a status message
						ap.teleportToAssignedArenaSpawn();
						
						// Has the player already voted? If not show a message to vote
						if(!ap.hasVoted())
							ap.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg" + " vote" + ChatColor.BLUE + " to vote start the arena!");
						// TODO: Show voting status message if the player already voted
						
						// TODO: Current voting status message (persentage / players)
						// TODO: If already vote started, only show status message
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn())
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Make sure the player is not null
		if(p == null)
			return;
		
		// Is the current in any arena
		if(am.isInArena(p)) {
			Arena arena = am.getPlayer(p).getArena();
			
			// Kick the player out of the arena
			SimpleSurvivalGames.instance.getArenaManager().kick(p);
			
			// Show a message to all players this player died
			arena.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.DARK_RED + " lost connection!");
			
			// TODO: Check if any player won the match
			// TODO: Update player count in chat etc
		}
	}
	
	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			// Is the player in the lobby of the arena
			if(ap.isInLobby()) {
				
				// Has the player a arena spawn assigned
				if(ap.hasAssignedAreanSpawn()) {
					
					// Cancel the event and send a status message
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + "It's a shame to make this sheep naked inside the lobby of an arena!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		TeleportCause cause = event.getCause();
		Location from = event.getFrom();
		Location to = event.getTo();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		if(to == null)
			return;
		
		// Is the current player playing
		if(am.isInArena(p)) {
			ArenaPlayer ap = am.getPlayer(p);
			
			if(ap.isPlaying()) {
				// Is the player allowed to get out of the arena cuboid
				if(!ap.getArena().getMayLeaveArenaCuboid()) {
					// The player may not teleport out of the arena
					if(!ap.getArena().getArenaCuboid().isInsideCuboid(to)) {
						
						switch (cause) {
						case ENDER_PEARL:
							// Get the nearest location inside the arena cuboid to teleport the player to
							Location newLoc = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(to), 1).toBukkitLocation();
							
							p.teleport(newLoc);
							break;
							
						default:
							// Cancel the event and show a message
							event.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED + "You may not teleport out of the arena!");
						}
					}
					
				} else {
					// The player may not be teleported out of the game world
					if(!from.getWorld().equals(to.getWorld())) {
						// Cancel the event and show a status message
						event.setCancelled(true);
						p.sendMessage(ChatColor.DARK_RED + "You may not teleport away from an arena!");
					}
				}
				
			} else if(ap.isSpectator()) {
				// Is the player allowed to get out of the arena cuboid
				if(!ap.getArena().getMayLeaveSpectatorsCuboid()) {
					
					ArenaCuboid specCuboid = null;
					if(ap.getArena().isSpectatorsCuboidSet())
						specCuboid = ap.getArena().getSpectatorsCuboid();
					else
						specCuboid = ap.getArena().getArenaCuboid();
					
					// The player may not teleport out of the arena
					if(!specCuboid.isInsideCuboid(to)) {
						
						switch (cause) {
						case ENDER_PEARL:
							// Get the nearest location inside the arena cuboid to teleport the player to
							Location newLoc = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(to), 1).toBukkitLocation();
							p.teleport(newLoc);
							break;
							
						default:
							// Cancel the event and show a message
							event.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED + "You may not teleport away from the game while spectating!");
						}
					}
						
					
				} else {
					// The player may not be teleported out of the game world
					if(!from.getWorld().equals(to.getWorld())) {
						// Cancel the event and show a status message
						event.setCancelled(true);
						p.sendMessage(ChatColor.DARK_RED + "You may not teleport away while spectating!");
					}
				}
				
			} else if(ap.isInLobby()) {
				/*// Make sure the player is linked to a spawn point
				if(ap.hasAssignedAreanSpawn()) {
					
					// If the player wants to teleport out, cancel the event
					if(!ap.getAssignedAreanSpawn().isAtSpawn(to))
						
						// Cancel the teleportation and show a message
						event.setCancelled(true);
						p.sendMessage(ChatColor.DARK_RED + "You can't teleport away when inside the lobby of a game!");
				}*/
			}
		}
	}
}
