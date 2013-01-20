package com.timvisee.simplesurvivalgames.command;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SSGPlayer;
import com.timvisee.simplesurvivalgames.SSGPlayerManager;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.SSGPlayer.PlayerMode;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaState;
import com.timvisee.simplesurvivalgames.arena.container.ArenaContainer;
import com.timvisee.simplesurvivalgames.arena.container.ArenaStaticContainer;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.spawn.ArenaSpawn;

public class CommandHandler {
	
	public CommandHandler() { }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		String fullCommand = "/" + commandLabel + " " + implodeArray(args, " ");
		
		// Make sure the command sender used one of the Simple Survival Games commands
		if(commandLabel.equalsIgnoreCase("simplesurvivalgames") || commandLabel.equalsIgnoreCase("simplehungergames") ||
				commandLabel.equalsIgnoreCase("ssg") || commandLabel.equalsIgnoreCase("shg") ||
				commandLabel.equalsIgnoreCase("sg") || commandLabel.equalsIgnoreCase("hg")) {
			
			// Make sure the command sender filled in any arguments
			if(args.length == 0) {
				sender.sendMessage(ChatColor.DARK_RED + "Unknown command!");
				sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help" + ChatColor.BLUE + " to view help");
				return true;
			}
			
			// Arena join command
			if(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("j") || args[0].equalsIgnoreCase("play")) {
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.join", true)) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only join an arena in-game!");
					return true;
				}
				
				// Get the player who executed this command and the arena manager
				Player p = (Player) sender;
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// Is there any arena to join?
				if(am.getGameCount() == 0) {
					sender.sendMessage(ChatColor.DARK_RED + "There are no arenas available to join!");
					return true;
				}
				
				// Is any arena argumented?
				if(args.length == 1) {
					// Arena not argumented, join random arena
					// Get the best arena to join
					Arena arena = am.getBestArenaToJoin();
					
					// Make sure any arena is selected
					if(arena == null) {
						sender.sendMessage(ChatColor.DARK_RED + "All arenas are already full or in progress!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate an arena");
						return true;
					}
					
					// The player does need to have permission to join this specific arena
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.arena.join." + arena.getName(), p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to join this arnea!");
						return true;
					}
					
					// The arena may not be disabled
					if(!arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is disabled, you can't join it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// The arena may not be in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is in edit mode, you can't join it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// Is the player already in another arena
					if(am.isInArena(p)) {
						ArenaPlayer ap = am.getPlayer(p);
						if(ap.getArena().equals(arena) && ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.BLUE + "You already are in this arena!");
							return true;
						} else if(ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't join another arena while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg leave" + ChatColor.BLUE + " to leave the current arena");
							return true;
						} else if(ap.isInLobby()) {
							// The player is already in the arena
							p.sendMessage(ChatColor.BLUE + "You already are in that arena!");
							return true;
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer(), true);
						}
					}
					
					// Join the arena and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinPlayers(p);
					
					// Get a random arena spawn and put the player on it
					ap.setAssignedArenaSpawn(ap.getArena().getSpawnManager().getRandomUnassignedSpawn());
					ap.teleportToAssignedArenaSpawn(true);

					// Start the arena if it's ready
					if(ap.getArena().isReadyToStart())
						ap.getArena().startRound();
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You joined the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					//p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " vote" + ChatColor.BLUE + " to vote start the arena");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
					return true;
					
				} else {
					
					// Arena argumented
					String arenaName = args[1];
					
					// Is there any arena with this name
					if(!am.isArenaWithName(arenaName)) {
						sender.sendMessage(ChatColor.DARK_RED + arenaName);
						sender.sendMessage(ChatColor.DARK_RED + "There's no arena available with this name!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all arenas");
						return true;
					}
					
					// Get the arena with this name
					Arena arena = am.getArenaWithName(arenaName);
					
					// The player does need to have permission to join this specific arena
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.arena.join." + arena.getName(), p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to join this arnea!");
						return true;
					}
					
					// The arena must be in lobby state (or ya know waiting)
					if(!arena.getState().equals(ArenaState.LOBBY) && !arena.getState().equals(ArenaState.STANDBY)) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is already in progress!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec " + arena.getName() + ChatColor.BLUE + " to spectate the arena");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// The arena may not be disabled
					if(!arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is disabled, you can't join it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// The arena may not be in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is in edit mode, you can't join it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// Check if there are enough slots available for this arena
					if(!arena.isPlayerSlotAvailable()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is full!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec " + arena.getName() + ChatColor.BLUE + " to spectate the arena");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// Is the player already in another arena
					if(am.isInArena(p)) {
						ArenaPlayer ap = am.getPlayer(p);
						if(ap.getArena().equals(arena) && ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.BLUE + "You already are in this arena!");
							return true;
						} else if(ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't join another arena while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg leave" + ChatColor.BLUE + " to leave the current arena");
							return true;
						} else if(ap.isInLobby()) {
							// The player is already in the arena
							p.sendMessage(ChatColor.BLUE + "You already are in that arena!");
							return true;
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer(), true);
						}
					}
					
					// Join the arena and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinPlayers(p);
					
					// Get a random arena spawn and put the player on it
					ap.setAssignedArenaSpawn(ap.getArena().getSpawnManager().getRandomUnassignedSpawn());
					ap.teleportToAssignedArenaSpawn(true);
					
					// Start the arena if it's ready
					if(ap.getArena().isReadyToStart())
						ap.getArena().startRound();
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You joined the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					//p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " vote" + ChatColor.BLUE + " to vote start the arena");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("spectate") ||
					args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("s") ||
					args[0].equalsIgnoreCase("watch")) {

				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.spectator", true)) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only spectate an arena in-game!");
					return true;
				}
				
				// Get the player who executed this command and the arena manager
				Player p = (Player) sender;
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// Is there any arena to spectate?
				if(am.getGameCount() == 0) {
					sender.sendMessage(ChatColor.DARK_RED + "There are no arenas available to spectate!");
					return true;
				}
				
				// Is any arena argumented?
				if(args.length == 1) {
					// Game not argumented, spectate random arena
					// Get the best arena to spectate
					Arena arena = am.getBestArenaToSpectate();
					
					// Make sure any arena is selected
					if(arena == null) {
						sender.sendMessage(ChatColor.DARK_RED + "There's no arena available to spectate on!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate an arena");
						return true;
					}
					
					// The player does need to have permission to join this specific arena
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.arena.spectator." + arena.getName(), p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to spectate on this arena!");
						return true;
					}
					
					// The arena may not be disabled
					if(!arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is disabled, you can't spectate it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// The arena may not be in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is in edit mode, you can't spectate it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// Is the player already in another arena
					if(am.isInArena(p)) {
						ArenaPlayer ap = am.getPlayer(p);
						if(ap.getArena().equals(arena) && ap.isSpectator()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.BLUE + "You already are spectating on this arena!");
							return true;
						} else if(ap.getArena().equals(arena) && ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't switch to spectator mode while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
							return true;
						} else if(ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't join another arena while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg leave" + ChatColor.BLUE + " to leave the current arena");
							return true;
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer(), true);
						}
					}
					
					// Is the spectators spawn set
					if(!arena.isSpectatorSpawnSet()) {
						p.sendMessage(ChatColor.DARK_RED + "Spectators spawn not set, can't spectate the arena!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate another arena");
						return true;
					}
					
					// Join the arena as spectator and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinSpectators(p);
					
					// Get a random arena spawn and put the player on it
					Location specSpawn = ap.getArena().getSpectatorsSpawn().toBukkitLocation();
					
					if(specSpawn == null) {
						p.sendMessage(ChatColor.DARK_RED + "Spectators spawn not set, can't spectate the arena!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate another arena");
						am.kick(p);
						return true;
					}
					
					// Teleport the player to the arena spawn
					p.teleport(specSpawn);
					p.playEffect(specSpawn, Effect.MOBSPAWNER_FLAMES, 1);
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You are now spectating the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " join " + arena.getName() + ChatColor.BLUE + " to join the arena");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
					return true;
					
				} else {
					
					// Arena argumented
					String arenaName = args[1];
					
					// Is there any arena with this name
					if(!am.isArenaWithName(arenaName)) {
						sender.sendMessage(ChatColor.DARK_RED + arenaName);
						sender.sendMessage(ChatColor.DARK_RED + "There's no arena available with this name!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all arenas");
						return true;
					}
					
					// Get the arena with this name
					Arena arena = am.getArenaWithName(arenaName);
					
					// The player does need to have permission to join this specific arena
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.arena.spectator." + arena.getName(), p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to spectate on this arena!");
						return true;
					}
					
					// Is the player already in another arena
					if(am.isInArena(p)) {
						ArenaPlayer ap = am.getPlayer(p);
						if(ap.getArena().equals(arena) && ap.isSpectator()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.BLUE + "You already are spectating on this arena!");
							return true;
						} else if(ap.getArena().equals(arena) && ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't switch to spectator mode while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
							return true;
						} else if(ap.isPlaying()) {
							// The player is already in this arena, show a message
							p.sendMessage(ChatColor.DARK_RED + "You can't join another arena while playing!");
							p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg leave" + ChatColor.BLUE + " to leave the current arena");
							return true;
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer(), true);
						}
					}
					
					// The arena may not be disabled
					if(!arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is disabled, you can't spectate it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// The arena may not be in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is in edit mode, you can't spectate it right now!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					// Is the spectators spawn set
					if(!arena.isSpectatorSpawnSet()) {
						p.sendMessage(ChatColor.DARK_RED + "Spectators spawn not set, can't spectate the arena!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate another arena");
						return true;
					}
					
					// Join the arena as spectator and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinSpectators(p);
					
					// Get a random arena spawn and put the player on it
					Location specSpawn = ap.getArena().getSpectatorsSpawn().toBukkitLocation();
					
					if(specSpawn == null) {
						p.sendMessage(ChatColor.DARK_RED + "Spectators spawn not set, can't spectate the arena!");
						p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec <arena>" + ChatColor.BLUE + " to spectate another arena");
						am.kick(p);
						return true;
					}
					
					// Teleport the player to the arena spawn
					p.teleport(specSpawn);
					p.playEffect(specSpawn, Effect.MOBSPAWNER_FLAMES, 1);
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You are now spectating the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " join " + arena.getName() + ChatColor.BLUE + " to join the arena");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("exit")) {

				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.leave", true)) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// Get the player who executed this command and the arena manager
				Player p = (Player) sender;
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// The player has to be in any arena
				if(!am.isInArena(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You aren't in any arena right now!");
					return true;
				}
				
				// Kick the player out of the game / spectator mode
				am.kick(p);
				return true;
				
			} else if(args[0].equalsIgnoreCase("vote") || args[0].equalsIgnoreCase("v")) {

				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.vote", true)) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// Get the player who executed this command and the arena manager
				Player p = (Player) sender;
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// The player has to be in any arena
				if(!am.isInArena(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You aren't in any arena right now!");
					return true;
				}
				
				ArenaPlayer ap = am.getPlayer(p);
				
				// The arena has to be in lobby modus
				if(ap.isPlaying()) {
					sender.sendMessage(ChatColor.BLUE + "You are already playing!");
					return true;
				} else if(!ap.isInLobby()) {
					sender.sendMessage(ChatColor.DARK_RED + "You can't vote to start right now!");
					return true;
				}
				
				// Has the player already voted?
				if(ap.hasVoted()) {
					sender.sendMessage(ChatColor.BLUE + "You've already voted to start the arena!");
					return true;
				}
				
				// Vote
				ap.setVoted(true, false);
				
				// Show a message to the voter, and all other players and spectators about the vote of this player
				p.sendMessage(ChatColor.BLUE + "You voted to start the arena!");
				for(ArenaPlayer entry : ap.getArena().getPlayerManager().getPlayersAndSpectators())
					if(!entry.getPlayer().equals(p))
						entry.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " voted to start the arena!");
				
				// Show the current voting status
				ap.getArena().broadcastVotingStatus();
				
				// If there are no votes needed anymore, and there are more players needed, print the players status.
				if(ap.getArena().getRemainingPlayerVotesCount() == 0 && !ap.getArena().hasEnoughPlayers())
					ap.getArena().broadcastPlayerCount();
				
				// Start the arena if it's ready
				if(ap.getArena().isReadyToStart())
					ap.getArena().startRound();
				
				return true;
				
			} else if(args[0].equalsIgnoreCase("list") ||
					args[0].equalsIgnoreCase("games") || args[0].equalsIgnoreCase("arenas")) {
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.list", true)) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// Get the arena manager
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// Are there any arenas yet
				if(am.getGameCount() == 0) {
					sender.sendMessage(ChatColor.BLUE + "==========[ SURVIVAL GAMES ARENAS ]==========");
					sender.sendMessage(ChatColor.DARK_RED + "There are no arenas available yet!");
					return true;
				}

				sender.sendMessage(ChatColor.BLUE + "==========[ SURVIVAL GAMES ARENAS ]==========");
				
				Arena bestToJoin = am.getBestArenaToJoin();
				
				int i = 1;
				for(Arena arena : am.getArenas()) {
					String line = ChatColor.DARK_GRAY + String.valueOf(i) + ". " + ChatColor.GOLD + arena.getName() +
							ChatColor.DARK_GRAY + " (" + String.valueOf(arena.getAssignedSpawnsCount()) + "/" +
							String.valueOf(arena.getMaxPlayerCount()) + " Players)";
					
					if(arena.getState().equals(ArenaState.PLAYING))
						line += " (In Progress)";
					
					if(sender instanceof Player)
						if(arena.getPlayerManager().isInArena((Player) sender))
							line += " (Joined)";
					
					if(arena.getState().equals(ArenaState.LOBBY)) {
						int minPlayers = arena.getMinPlayerCount();
						int curPlayers = arena.getPlayerManager().getPlayerCount();
						int neededPlayers = Math.max(minPlayers - curPlayers, 0);
						if(neededPlayers > 0)
							line += " (" + (neededPlayers==1 ? "1 Player" : String.valueOf(neededPlayers) + " Players") + " Needed)";
					}
					
					if(bestToJoin != null)
						if(bestToJoin.equals(arena))
							line += " (Best To Join)";
					
					sender.sendMessage(line);
					
					i++;
				}
				return true;
				
			} else if(args[0].equalsIgnoreCase("force")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length != 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.list", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected((Player) sender)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				Arena arena = pm.getPlayer((Player) sender).getSelectedArena();
				String action = args[1];
				
				if(action.equalsIgnoreCase("start") || action.equalsIgnoreCase("begin")) {
					
					// If the command sender is a player, he does need to have permission to use the command
					if(sender instanceof Player) {
						Player p = (Player) sender;
						if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.force.start", p.isOp())) {
							sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
							return true;
						}
					}
					
					// The arena can not start if it's disabled
					if(!arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is not enabled!");
						return true;
					}
					
					// The arena can not start while it's in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is in edit mode!");
						return true;
					}
					
					// The arena needs to be in the lobby / waiting state
					if(arena.getState().equals(ArenaState.STARTING) ||
							arena.getState().equals(ArenaState.PLAYING)) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is already running!");
						return true;
					}
					
					final int playerCount = arena.getPlayerManager().getPlayerCount();
					
					// At least 2 players are needed in the arena
					if(playerCount < 2) {
						sender.sendMessage(ChatColor.DARK_RED + "There need to be at least 2 players in the arena!");
						return true;
					}
					
					// Start the arena round
					arena.startRound();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The arena " + ChatColor.GOLD + arena.getDisplayName() + ChatColor.BLUE + " has been started!");
					return true;
					
				} else if(action.equalsIgnoreCase("stop") || action.equalsIgnoreCase("end")) {
					
					// If the command sender is a player, he does need to have permission to use the command
					if(sender instanceof Player) {
						Player p = (Player) sender;
						if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.force.stop", p.isOp())) {
							sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
							return true;
						}
					}
					
					// The arena has to be in progress
					if(!arena.getState().equals(ArenaState.STARTING) &&
							!arena.getState().equals(ArenaState.PLAYING)) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is not running!");
						return true;
					}
					
					// Stop the arena
					arena.endRound();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The arena " + ChatColor.GOLD + arena.getDisplayName() + ChatColor.BLUE + " has been stopped!");
					return true;
					
				} else {
					
					// The action argument has to be valid right
					sender.sendMessage(ChatColor.DARK_RED + action);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid argument!");
					sender.sendMessage(ChatColor.BLUE + "Choose from:");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " start");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " stop");
					return true;
					
				}
				
			} else if(args[0].equalsIgnoreCase("arena") || args[0].equalsIgnoreCase("a") ||
					args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel") ||
					args[0].equalsIgnoreCase("selectarena")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length == 1) {
					sender.sendMessage(ChatColor.DARK_RED + "Arena name missing!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
					
				} else if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				Player p = (Player) sender;
				
				if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.arena", p.isOp())) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
					return true;
				}
				
				// Get the arena and player manager
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				String arenaName = args[1];
				
				if(!am.isArenaWithName(arenaName)) {
					sender.sendMessage(ChatColor.DARK_RED + arenaName);
					sender.sendMessage(ChatColor.DARK_RED + "There's no arena with this name!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all arenas");
					return true;
				}
				
				SSGPlayer ssgPlayer = null;
				if(pm.containsPlayer(p)) {
					ssgPlayer = pm.getPlayer(p);
				} else {
					ssgPlayer = pm.addPlayer(p);
				}
				
				// Get the arena and select it
				Arena arena = am.getArenaWithName(arenaName);
				ssgPlayer.setSelectedArena(arena);
				
				// Show a status message
				p.sendMessage(ChatColor.BLUE + "The arena " + ChatColor.GOLD + arena.getDisplayName() + ChatColor.BLUE + " has been selected!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i") || args[0].equalsIgnoreCase("information")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.info", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected((Player) sender)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer((Player) sender).getSelectedArena();
				
				// Show the arena info
				sender.sendMessage(ChatColor.BLUE + "==========[ ARENA INFO - " + arena.getDisplayName().trim().toUpperCase() + " ]==========");
				sender.sendMessage(ChatColor.BLUE + "Enabled: " + (arena.isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.DARK_RED + "No"));
				sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + arena.getName() + ChatColor.GRAY + " (" + arena.getDisplayName() + ")");
				sender.sendMessage(ChatColor.BLUE + "EditMode: " + (arena.isInEditMode() ? ChatColor.DARK_RED + "Enabled" : ChatColor.GOLD + "Disabled"));
				sender.sendMessage(ChatColor.BLUE + "Players: " + (arena.getPlayerManager().getPlayerCount() == arena.getMaxPlayerCount() ? ChatColor.DARK_RED  : ChatColor.GOLD) + String.valueOf(arena.getPlayerManager().getPlayerCount()));
				sender.sendMessage(ChatColor.BLUE + "Spectators: " + ChatColor.GOLD + String.valueOf(arena.getPlayerManager().getSpectatorCount()));
				sender.sendMessage(ChatColor.BLUE + "Min Players: " + ChatColor.GOLD + String.valueOf(arena.getMinPlayerCount()));
				sender.sendMessage(ChatColor.BLUE + "Max Players: " + ChatColor.GOLD + String.valueOf(arena.getMaxPlayerCount()));
				sender.sendMessage(ChatColor.BLUE + "Arean State: " + ChatColor.GOLD + arena.getState().getName());
				sender.sendMessage(ChatColor.BLUE + "Arena Cuboid: " + (arena.isArenaCuboidSet() ? ChatColor.GREEN + "Set" : ChatColor.DARK_RED + "Not set!"));
				sender.sendMessage(ChatColor.BLUE + "Spectator Spawn: " + (arena.isSpectatorSpawnSet() ? ChatColor.GREEN + "Set" : ChatColor.DARK_RED + "Not set!"));
				sender.sendMessage(ChatColor.BLUE + "Spectator Cuboid: " + (arena.isSpectatorsCuboidSet() ? ChatColor.GREEN + "Set" : ChatColor.GOLD + "Not set"));
				sender.sendMessage(ChatColor.BLUE + "Arena Spawns: " + (arena.getMinPlayerCount() > arena.getSpawnManager().getSpawnCount() ? ChatColor.DARK_RED : ChatColor.GOLD) + String.valueOf(arena.getSpawnManager().getSpawnCount()));
				sender.sendMessage(ChatColor.BLUE + "Containers: " + ChatColor.GOLD + String.valueOf(arena.getContainerManager().getContainerCount()));
				sender.sendMessage(ChatColor.BLUE + "Min Voting Percentage: " + ChatColor.GOLD + String.valueOf(arena.getMinVotesPercentage()) + "%");
				sender.sendMessage(ChatColor.BLUE + "Grace Period Length: " + ChatColor.GOLD + String.valueOf((int) (arena.getGracePeriodLength() / 1000)) + " seconds");
				sender.sendMessage(ChatColor.BLUE + "Forcefield Blocks: " + ChatColor.GOLD + String.valueOf(arena.getForcefieldManager().getForcefieldBlocks().size()));
				return true;
				
			} else if(args[0].equalsIgnoreCase("enable")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.enable", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected((Player) sender)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer((Player) sender).getSelectedArena();
				
				// The arena may not be in edit mode
				if(arena.isInEditMode()) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is in edit mode, you can't enable it right now!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " edit false" + ChatColor.BLUE + " to disable the edit mode.");
					return true;
				}
				
				// The arena has to be disabled
				if(arena.isEnabled()) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is already enabled!");
					return true;
				}
				
				// Enable the arena
				arena.setEnabled(true);
				
				// Save the arena(s)
				SimpleSurvivalGames.instance.getArenaManager().save();
				
				// Show a status message and return true
				sender.sendMessage(ChatColor.BLUE + "The arena has been " + ChatColor.GREEN + "Enabled" + ChatColor.BLUE + "!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("disable")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.disable", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected((Player) sender)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer((Player) sender).getSelectedArena();
				
				// The arena has to be enabled
				if(!arena.isEnabled()) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is already disabled!");
					return true;
				}
				
				// The arena should NOT be running
				if(arena.getState().equals(ArenaState.PLAYING) || arena.getState().equals(ArenaState.STARTING)) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is currently running and can't be disabled!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " force stop" + ChatColor.BLUE + " to force stop the arena.");
					return true;
				}
				
				// Kick all players from the arena
				arena.kickAll(true, true);
				
				// Disable the arena
				arena.setEnabled(false);
				
				// Save the arena(s)
				SimpleSurvivalGames.instance.getArenaManager().save();
				
				// Show a status message and return true
				sender.sendMessage(ChatColor.BLUE + "The arena has been " + ChatColor.DARK_RED + "Disabled" + ChatColor.BLUE + "!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("editmode")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.edit", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected((Player) sender)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer((Player) sender).getSelectedArena();
				
				String action = "";
				if(args.length == 2)
					action = args[1];
				else
					action = (arena.isInEditMode() ? "false" : "true");
				
				if(action.equalsIgnoreCase("true") || action.equalsIgnoreCase("t") ||
						action.equalsIgnoreCase("yes") || action.equalsIgnoreCase("y") ||
						action.equalsIgnoreCase("enable") || action.equalsIgnoreCase("e")) {
					
					// The arena has to be disabled
					if(arena.isEnabled()) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is enabled, you can't enable it's edit mode!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " disable" + ChatColor.BLUE + " to disable the arena!");
						return true;
					}
					
					// Make sure the arena isn't already in edit mode
					if(arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is already in edit mode!");
						return true;
					}
					
					// Enable the edit mode of the arena
					arena.setEditMode(true);
					
					// Save the arena(s)
					SimpleSurvivalGames.instance.getArenaManager().save();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The edit mode of the arena has been " + ChatColor.GREEN + "Enabled" + ChatColor.BLUE + "!");
					return true;
					
				} else if(action.equalsIgnoreCase("false") || action.equalsIgnoreCase("f") ||
						action.equalsIgnoreCase("no") || action.equalsIgnoreCase("n") ||
						action.equalsIgnoreCase("disable") || action.equalsIgnoreCase("d")) {
					
					// The arena should be in edit mode
					if(!arena.isInEditMode()) {
						sender.sendMessage(ChatColor.DARK_RED + "The edit mode of this arena is already disabled!");
						return true;
					}
					
					// Save all static containers
					for(ArenaContainer container : arena.getContainerManager().getContainers()) {
						if(container instanceof ArenaStaticContainer) {
							ArenaStaticContainer staticContainer = (ArenaStaticContainer) container;
							staticContainer.storeCurrentContents();
						}
					}
					
					// Disable the edit mode of the arena
					arena.setEditMode(false);
					
					// Save the arena(s)
					SimpleSurvivalGames.instance.getArenaManager().save();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The edit mode of the arena has been " + ChatColor.DARK_RED + "Disabled" + ChatColor.BLUE + "!");
					return true;
					
				} else {
					
					// The action argument has to be valid right
					sender.sendMessage(ChatColor.DARK_RED + action);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid argument!");
					sender.sendMessage(ChatColor.BLUE + "Choose from:");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " true");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " false");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("ss") || args[0].equalsIgnoreCase("spawn")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				} else if(args.length == 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Missing spawn type!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.setspawn", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer(p).getSelectedArena();
				
				String spawnType = args[1];
				
				if(spawnType.equalsIgnoreCase("spectator") || spawnType.equalsIgnoreCase("spectators") ||
						spawnType.equalsIgnoreCase("spec") || spawnType.equalsIgnoreCase("s")) {
					
					// Set the spawn location for spectators
					arena.setSpectatorsSpawn(new SSGLocation(p.getLocation()));
					
					// Save the arena(s)
					SimpleSurvivalGames.instance.getArenaManager().save();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The spectators spawn has been set to your current location!");
					return true;
					
				} else {
					
					// The action argument has to be valid right
					sender.sendMessage(ChatColor.DARK_RED + spawnType);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid spawn type!");
					sender.sendMessage(ChatColor.BLUE + "Choose from:");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " spectator");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("setcuboid") || args[0].equalsIgnoreCase("sc") || args[0].equalsIgnoreCase("cuboid")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 3) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				} else if(args.length < 3) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Missing arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.setcuboid", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer(p).getSelectedArena();
				
				String cuboidType = args[1];
				String cuboidCornerNumber = args[2];
				int cuboidCorner = 0;
				
				if(cuboidCornerNumber.equalsIgnoreCase("1"))
					cuboidCorner = 1;
				else if(cuboidCornerNumber.equalsIgnoreCase("2"))
					cuboidCorner = 2;
				else {
					sender.sendMessage(ChatColor.DARK_RED + "Invalid corner number!");
					return true;
				}
				
				if(cuboidType.equalsIgnoreCase("arena") || cuboidType.equalsIgnoreCase("a") ||
						cuboidType.equalsIgnoreCase("game") || cuboidType.equalsIgnoreCase("battlefield")) {
					
					// Get the location to set the cuboid corner too
					SSGLocation corner = new SSGLocation(p.getLocation());
					
					if(cuboidCorner == 1)
						arena.getArenaCuboid().setFirstCorner(corner);
					else
						arena.getArenaCuboid().setSecondCorner(corner);
					
					// Save the arena(s)
					SimpleSurvivalGames.instance.getArenaManager().save();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The arena cuboid has been updated!");
					return true;
					
				} else if(cuboidType.equalsIgnoreCase("spectator") || cuboidType.equalsIgnoreCase("spectators") ||
						cuboidType.equalsIgnoreCase("spec") || cuboidType.equalsIgnoreCase("s")) {
					
					// Get the location to set the cuboid corner too
					SSGLocation corner = new SSGLocation(p.getLocation());
					
					if(cuboidCorner == 1)
						arena.getSpectatorsCuboid().setFirstCorner(corner);
					else
						arena.getSpectatorsCuboid().setSecondCorner(corner);
					
					// Save the arena(s)
					SimpleSurvivalGames.instance.getArenaManager().save();
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "The spectators cuboid has been updated!");
					return true;
					
				} else {
					
					// The action argument has to be valid right
					sender.sendMessage(ChatColor.DARK_RED + cuboidType);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid spawn type!");
					sender.sendMessage(ChatColor.BLUE + "Choose from:");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " spectator");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("addspawn") || args[0].equalsIgnoreCase("as")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.addspawn", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer(p).getSelectedArena();
				
				// The arena has to be disabled
				if(arena.isEnabled()) {
					p.sendMessage(ChatColor.DARK_RED + "The arena has to be disabled before you an add a spawn!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg disable" + ChatColor.BLUE + " to disable the arena!");
					return true;
				}
				
				// Add the spawn
				Location loc = p.getLocation().getBlock().getLocation();
				loc.setYaw(p.getLocation().getYaw());
				loc.setPitch(p.getLocation().getPitch());
				arena.getSpawnManager().addSpawn(new SSGLocation(loc));
				
				// Save the arena
				SimpleSurvivalGames.instance.getArenaManager().save();
				
				// Show a status message and return true
				p.sendMessage(ChatColor.BLUE + "A new arena spawn has been added!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("listspawns") || args[0].equalsIgnoreCase("listspawn") || args[0].equalsIgnoreCase("ls")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.listspawns", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer(p).getSelectedArena();
				
				p.sendMessage(ChatColor.BLUE + "==========[ ARENA SPAWNS ]==========");
				
				if(arena.getSpawnManager().getSpawnCount() == 0) {
					p.sendMessage(ChatColor.DARK_RED + "No arena spawns available yet!");
					return true;
				}
				
				int index = 1;
				for(ArenaSpawn spawn : arena.getSpawnManager().getSpawns()) {
					p.sendMessage(ChatColor.DARK_GRAY + "Index: " + ChatColor.BLUE + String.valueOf(index) +
							ChatColor.DARK_GRAY + "  Location: " + ChatColor.BLUE +
							String.valueOf((int) spawn.getLocation().getX()) + ", " +
							String.valueOf((int) spawn.getLocation().getY()) + ", " +
							String.valueOf((int) spawn.getLocation().getZ()));
					index++;
				}
				return true;
				
			} else if(args[0].equalsIgnoreCase("removespawn") || args[0].equalsIgnoreCase("rs") ||
					 args[0].equalsIgnoreCase("deletespawn") || args[0].equalsIgnoreCase("delspawn")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				} else if(args.length < 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Missing arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.removespawn", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				Arena arena = pm.getPlayer(p).getSelectedArena();
				
				// The arena has to be disabled
				if(arena.isEnabled()) {
					p.sendMessage(ChatColor.DARK_RED + "The arena has to be disabled before you an remove a spawn!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg disable" + ChatColor.BLUE + " to disable the arena!");
					return true;
				}
				
				// Check if any spawn is available
				if(arena.getSpawnManager().getSpawnCount() == 0) {
					p.sendMessage(ChatColor.DARK_RED + "There's no spawn available to remove!");
					return true;
				}
				
				String spawnIndexString = args[1];
				
				if(!isInt(spawnIndexString)) {
					p.sendMessage(ChatColor.DARK_RED + spawnIndexString);
					p.sendMessage(ChatColor.DARK_RED + "This is not a valid spawn index!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg listspawns" + ChatColor.BLUE + " to list all spawns!");
					return true;
				}
				
				int spawnIndex = Integer.parseInt(spawnIndexString);
				
				// Make sure the user entered a valid spawn
				if(spawnIndex < 1) {
					p.sendMessage(ChatColor.DARK_RED + spawnIndexString);
					p.sendMessage(ChatColor.DARK_RED + "Invalid spawn index!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg listspawns" + ChatColor.BLUE + " to list all spawns!");
					return true;
				} else if(spawnIndex > arena.getSpawnManager().getSpawnCount()) {
					p.sendMessage(ChatColor.DARK_RED + spawnIndexString);
					p.sendMessage(ChatColor.DARK_RED + "Spawn index too large!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg listspawns" + ChatColor.BLUE + " to list all spawns!");
					return true;
				}
				
				// Add the spawn
				arena.getSpawnManager().removeSpawn(spawnIndex - 1);
				
				// Save the arena
				SimpleSurvivalGames.instance.getArenaManager().save();
				
				// Show a status message and return true
				p.sendMessage(ChatColor.BLUE + "The spawn has been removed!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("addcontainer") || args[0].equalsIgnoreCase("ac") || args[0].equalsIgnoreCase("addchest")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				} else if(args.length < 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Missing arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.addcontainer", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				SSGPlayer ssgPlayer = pm.getPlayer(p);
				Arena arena = ssgPlayer.getSelectedArena();
				
				// The arena must be in edit mode
				if(!arena.isInEditMode()) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is not in edit mode!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " edit true" + ChatColor.BLUE + " to enable the edit mode.");
					return true;
				}
				
				String containerType = args[1];
				
				if(containerType.equalsIgnoreCase("random") || containerType.equalsIgnoreCase("rand") ||
						containerType.equalsIgnoreCase("r")) {
					
					// Set the players mode
					ssgPlayer.setPlayerMode(PlayerMode.ADD_RANDOM_CONTAINER);
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "Left click on a container to add it as random container!");
					sender.sendMessage(ChatColor.BLUE + "Right click on any block to disable this mode!");
					return true;
					
				} else if(containerType.equalsIgnoreCase("static") || containerType.equalsIgnoreCase("stat") ||
						containerType.equalsIgnoreCase("s")) {
					
					// Set the players mode
					ssgPlayer.setPlayerMode(PlayerMode.ADD_STATIC_CONTAINER);
					
					// Show a status message and return true
					sender.sendMessage(ChatColor.BLUE + "Left click on a container to add it as static container!");
					sender.sendMessage(ChatColor.BLUE + "Right click on any block to disable this mode!");
					return true;
					
				} else {
					
					// The action argument has to be valid right
					sender.sendMessage(ChatColor.DARK_RED + containerType);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid container type!");
					sender.sendMessage(ChatColor.BLUE + "Choose from:");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " random");
					sender.sendMessage(ChatColor.GOLD + "  /" + commandLabel + " " + args[0] + " static");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("removecontainer") || args[0].equalsIgnoreCase("rc") || args[0].equalsIgnoreCase("removechest")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// The sender must be a player
				if(sender instanceof Player) { } else {
					sender.sendMessage(ChatColor.DARK_RED + "You can only use this command in-game!");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.removecontainer", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				Player p = (Player) sender;
				SSGPlayerManager pm = SimpleSurvivalGames.instance.getPlayerManager();
				
				// The player does need to have any arena selected
				if(!pm.hasArenaSelected(p)) {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have any arena selected!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " arena <arena>" + ChatColor.BLUE + " to select an arena.");
					return true;
				}
				
				// Get the arena
				SSGPlayer ssgPlayer = pm.getPlayer(p);
				Arena arena = ssgPlayer.getSelectedArena();
				
				// The arena must be in edit mode
				if(!arena.isInEditMode()) {
					sender.sendMessage(ChatColor.DARK_RED + "The arena is not in edit mode!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " edit true" + ChatColor.BLUE + " to enable the edit mode.");
					return true;
				}
				
				// Set the players mode
				ssgPlayer.setPlayerMode(PlayerMode.REMOVE_CONTAINER);
				
				// Show a status message and return true
				sender.sendMessage(ChatColor.BLUE + "Left click on any container to remove it!");
				sender.sendMessage(ChatColor.BLUE + "Right click on any block to disable this mode!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("reload")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length != 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.reload", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// Show a status message
				sender.sendMessage(ChatColor.BLUE + "Loading arenas...");
				
				// Stop all arenas
				am.stopAllArenas();
				
				// Load the arena's and track the duration
				long duration = am.load();
				
				// Get the current arena count
				int arenaCount = am.getArenas().size();
				
				// Show a status message and return true
				sender.sendMessage(ChatColor.BLUE + (arenaCount==1 ? "1 " + "arena has" : String.valueOf(arenaCount) + " arenas have") +
						" been loaded, took " + String.valueOf(duration) + " ms!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("save")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length != 1) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// If the command sender is a player, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.save", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				// Get the arena manager
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				int arenaCount = am.getArenas().size();
				
				// Show a status message
				sender.sendMessage(ChatColor.BLUE + "Saving " + (arenaCount==1 ? "1 " + "arena" : String.valueOf(arenaCount) + " arenas") + "...");
				
				// Save the arena's and save the duration
				long duration = am.save();
				
				// Show a status message and return true
				sender.sendMessage(ChatColor.BLUE + (arenaCount==1 ? "1 " + "arena has" : String.valueOf(arenaCount) + " arenas have") +
						" been saved, took " + String.valueOf(duration) + " ms!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
				
				// If the command sender is a plyer, he does need to have permission to use the command
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!SimpleSurvivalGames.instance.getPermissionsManager().hasPermission(p, "simplesurvivalgames.command.help", p.isOp())) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use the command!");
						return true;
					}
				}
				
				sender.sendMessage(ChatColor.BLUE + "The help files will become available in upcomming versions of Simple Survival Games!");
				return true;
				
			} else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("v") ||
					args[0].equalsIgnoreCase("about")) {
				
				// Send some information about Simple Survival Games to the command sender
				PluginDescriptionFile pdfFile = SimpleSurvivalGames.instance.getDescription();
				sender.sendMessage(ChatColor.BLUE + "This server is running Simple Survival Games v" + pdfFile.getVersion());
				sender.sendMessage(ChatColor.BLUE + "Simple Survival Games is made by Tim Visee - timvisee.com");
				return true;
				
			} else {
				
				sender.sendMessage(ChatColor.DARK_RED + "Unknown command!");
				sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help" + ChatColor.BLUE + " for help");
				return true;
				
			}
			
			// TODO: Show unknown command
		}
		
		return false;
	}
	
	/**
	 * Implode a string array to a string
	 * @param input the input string array
	 * @param glue the glue
	 * @return imploded string array
	 */
	public String implodeArray(String[] input, String glue) {
		String output = "";
		
		if (input.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(input[0]);
			
			for (int i = 1; i < input.length; i++) {
				sb.append(glue);
				sb.append(input[i]);
			}
			
			output = sb.toString();
		}
	
		return output;
	}
	
	public boolean isInt(String input) {
		 try {
		    int x = Integer.parseInt(input);
		    return true;
		} catch(NumberFormatException nFE) {
		   	return false;
		}
	}
}
