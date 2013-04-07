package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SSGPlayer;
import com.timvisee.simplesurvivalgames.SSGPlayer.PlayerMode;
import com.timvisee.simplesurvivalgames.SSGPlayerManager;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaCuboid;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.container.ArenaContainerManager;
import com.timvisee.simplesurvivalgames.arena.container.ArenaRandomContainer;
import com.timvisee.simplesurvivalgames.arena.container.ArenaStaticContainer;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;

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
			
			// The player may not sleep
			if(ap.isPlaying()) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.DARK_RED + "You can't sleep while in the arena!");
			}
			
			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't sleep while spectating!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// TODO: Is the player allowed to place down water
		
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't use buckets while spectating!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// TODO: Is the player allowed to get water
		
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't use buckets while spectating!");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "Drop parties are not allowed while spectating!");
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't fish while spectating!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Disable player modes when right clicking
		SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
		if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if(pm.containsPlayer(p)) {
				SSGPlayer ssgPlayer = pm.getPlayer(p);
				if(ssgPlayer.getPlayerMode().equals(PlayerMode.ADD_RANDOM_CONTAINER) ||
						ssgPlayer.getPlayerMode().equals(PlayerMode.ADD_STATIC_CONTAINER) ||
						ssgPlayer.getPlayerMode().equals(PlayerMode.REMOVE_CONTAINER)) {
					ssgPlayer.setPlayerMode(PlayerMode.NORMAL);
					p.sendMessage(ChatColor.BLUE + "Container mode disabled!");
					event.setCancelled(true);
					return;
				}
			}
		}
		
		// The player may not place down tall grass using bonemeal
		if(b != null) {
			if(p.getItemInHand().getType().equals(Material.INK_SACK) &&
					p.getItemInHand().getData().getData() == 15) {
				for(Arena arena : am.getArenas()) {
					if(arena.isArenaCuboidSet()) {
						if(arena.getArenaCuboid().isInsideCuboid(b)) {
							if(!arena.isInEditMode()) {
								if(am.isInArena(p))
									p.sendMessage(ChatColor.DARK_RED + "You can not grow tall grass inside the arena!");
								else {
									p.sendMessage(ChatColor.DARK_RED + "You can not grow tall grass while the arena isn't in edit mode!");
									p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg edit true" + ChatColor.BLUE + " to enable the edit mode!");
								}
								event.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
			
		
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't interact with anything while spectating!");
			}
			
			// Is the player playing in the arena
			if(ap.isPlaying()) {// The player may not interact with serval blocks like repeater times
				// Make sure the block is not null
				if(b != null) {
					if(b.getType().equals(Material.DIODE_BLOCK_OFF) ||
							b.getType().equals(Material.DIODE_BLOCK_ON)) {
						event.setCancelled(true);
						ap.sendMessage(ChatColor.DARK_RED + "You may not change the repeater times while in the arena!");
					}
				}
			}
		} else {
			if(pm.containsPlayer(p)) {
				if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
					SSGPlayer ssgPlayer = pm.getPlayer(p);
					
					// Make sure the player does have any arena selected
					if(!ssgPlayer.hasArenaSelected()) {
						p.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg arena <arena>" + ChatColor.BLUE + " to select an arena!");
						return;
					}
					
					// Get the selected arena
					Arena arena = ssgPlayer.getSelectedArena();
					ArenaContainerManager cm = arena.getContainerManager();
					
					switch(ssgPlayer.getPlayerMode()) {
					case ADD_RANDOM_CONTAINER:
						// The block has to be an container
						if(b.getState() instanceof ContainerBlock) { } else {
							p.sendMessage(ChatColor.DARK_RED + "This is not a container block!");
							event.setCancelled(true);
							return;
						}
						
						// The container may not be used yet
						if(cm.isContainer(b)) {
							p.sendMessage(ChatColor.DARK_RED + "This conainer is already used!");
							event.setCancelled(true);
							return;
						}
						
						// Add the container
						cm.addContainer(new ArenaRandomContainer(arena, b));
						am.save();
						p.sendMessage(ChatColor.BLUE + "This block is now a random container!");
						event.setCancelled(true);
						return;
					
					case ADD_STATIC_CONTAINER:
						// The block has to be an container
						if(b.getState() instanceof ContainerBlock) { } else {
							p.sendMessage(ChatColor.DARK_RED + "This is not a container block!");
							event.setCancelled(true);
							return;
						}
						
						// The container may not be used yet
						if(cm.isContainer(b)) {
							p.sendMessage(ChatColor.DARK_RED + "This conainer is already used!");
							event.setCancelled(true);
							return;
						}
						
						// Add the container
						ArenaStaticContainer staticContainer = new ArenaStaticContainer(arena, b, new ItemStack[]{});
						staticContainer.storeCurrentContents();
						cm.addContainer(staticContainer);
						am.save();
						p.sendMessage(ChatColor.BLUE + "This block is now a static container!");
						event.setCancelled(true);
						return;
						
					case REMOVE_CONTAINER:
						// The container may not be used yet
						if(!cm.isContainer(b)) {
							p.sendMessage(ChatColor.DARK_RED + "This block is not a container used by the arena!");
							event.setCancelled(true);
							return;
						}
						
						// Add the container
						cm.removeContainer(b);
						am.save();
						p.sendMessage(ChatColor.BLUE + "This block is not a container anymore!");
						event.setCancelled(true);
						return;
						
					default:
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The player may not be null
		if(p == null)
			return;
		
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
						
						// Get out of a vehicle
						if(p.isInsideVehicle())
							p.getVehicle().eject();
						
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
			
			// TODO: use spectator AND arena cuboid
			// Is the player in the lobby of the arena
			if(ap.isSpectator()) {
				// Is the player allowed to get out of the arena cuboid
				if(!arena.getMayLeaveArenaCuboid() && arena.getArenaCuboid().isSet()) {
					// The player has to be inside the arena
					if(!ap.isInArenaCuboid()) {
						// The the new location of the player
						Location newLoc = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(p.getLocation()), 1).toBukkitLocation();
						
						// Get out of a vehicle
						if(p.isInsideVehicle())
							p.getVehicle().eject();
						
						// Teleport the player
						p.teleport(newLoc);
						
						// Set the players velocity to zero
						p.setVelocity(new Vector(0, 0, 0));
						
						// Put the player back in fly mode
						p.setAllowFlight(true);
						p.setFlying(true);
						
						// Reset the players fall distance
						p.setFallDistance(0);
						
						// Send a notification message
						p.sendMessage(ChatColor.DARK_RED + "You may not get out of the arena!");
					}
				}
				
				// The player has to be 5 above the ground so he can't block other players
				Location loc = p.getLocation();
				Block blockBellow = null;
				for(int y = loc.getBlockY(); y > 0; y--) {
					Block b = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
					if(!b.getType().equals(Material.AIR) && blockBellow == null) {
						blockBellow = b;
						break;
					}
				}
				if(blockBellow != null) {
					double curY = loc.getY();
					double surfaceY = blockBellow.getY();
					
					if((curY - surfaceY) < 5) {
						p.sendMessage(ChatColor.DARK_RED + "You may not get this close to the ground!");
						p.setVelocity(p.getVelocity().setY(0));
						
						Location toLoc = null;
						for(int y = (int) (surfaceY + 6); y < 258; y++) {
							Block b = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
							if(b == null) {
								toLoc = new Location(loc.getWorld(), loc.getBlockX(), y, loc.getBlockZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
								break;
							}
							
							if(b.getType().equals(Material.AIR) && toLoc == null) {
								toLoc = new Location(loc.getWorld(), loc.getBlockX(), y, loc.getBlockZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
								break;
							}
						}
						if(toLoc != null) {
							p.teleport(toLoc);
							
							p.setAllowFlight(true);
							p.setFlying(true);
							
							// Reset the players fall distance
							p.setFallDistance(0);
						}
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

			// Is the player an spectators
			if(ap.isSpectator())
				event.setCancelled(true);
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
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// Make sure the player is not null
		if(p == null)
			return;
		
		// Make sure the event was not cancelled
		if(event.isCancelled())
			return;
		
		// Is the current in any arena
		if(am.isInArena(p)) {
			Arena arena = am.getPlayer(p).getArena();
			
			// Kick the player out of the arena
			SimpleSurvivalGames.instance.getArenaManager().kick(p);
			
			// Show a message to all players this player died
			arena.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.DARK_RED + " lost connection!");
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

			// Is the player an spectators
			if(ap.isSpectator()) {
				event.setCancelled(true);
				ap.sendMessage(ChatColor.DARK_RED + "You can't make this sheep naked while spectating!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		TeleportCause cause = event.getCause();
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
							
							// Get the nearest location inside the arena cuboid to teleport the player to
							Location newLoc2 = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(to), 1).toBukkitLocation();
							
							p.teleport(newLoc2);
							
							p.sendMessage(ChatColor.DARK_RED + "You may not teleport out of the arena!");
						}
					}
					
				} else {
					// The player may not be teleported out of the game world
					if(ap.getArena().getArenaCuboid().getWorldName().equals(to.getWorld().getName())) {
						// Cancel the event and show a status message
						event.setCancelled(true);
						
						// Get the nearest location inside the arena cuboid to teleport the player to
						Location newLoc2 = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(to), 1).toBukkitLocation();
						
						p.teleport(newLoc2);
						
						p.sendMessage(ChatColor.DARK_RED + "You may not teleport away from an arena!");
					}
				}
				
			} else if(ap.isSpectator()) {
				// Is the player allowed to get out of the arena cuboid
				if(!ap.getArena().getMayLeaveSpectatorsCuboid()) {
					
					ArenaCuboid specCuboid = ap.getArena().getArenaCuboid();
					// TODO: Finish this
					/*if(ap.getArena().isSpectatorsCuboidSet())
						specCuboid = ap.getArena().getSpectatorsCuboid();
					else
						specCuboid = ap.getArena().getArenaCuboid();*/
					
					// The player may not teleport out of the arena
					if(!specCuboid.isInsideCuboid(to)) {
						
						switch (cause) {
						case ENDER_PEARL:
							// Get the nearest location inside the arena cuboid to teleport the player to
							Location newLoc = ap.getArena().getArenaCuboid().getNearestLocationInsideCuboid(new SSGLocation(to), 1).toBukkitLocation();
							p.teleport(newLoc);
							p.setAllowFlight(true);
							p.setFlying(true);
							break;
							
						default:
							// Cancel the event and show a message
							event.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED + "You may not teleport away from the game while spectating!");
							p.teleport(ap.getArena().getSpectatorsSpawn().toBukkitLocation());
							p.setAllowFlight(true);
							p.setFlying(true);
						}
					}
					
				} else {
					// The player may not be teleported out of the game world
					if(!ap.getArena().getArenaCuboid().getWorldName().equals(to.getWorld().getName())) {
						// Cancel the event and show a status message
						p.sendMessage(ChatColor.DARK_RED + "You may not teleport away while spectating!");
						event.setCancelled(true);
						
						p.teleport(ap.getArena().getSpectatorsSpawn().toBukkitLocation());
						p.setAllowFlight(true);
						p.setFlying(true);
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
