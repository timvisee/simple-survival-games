package com.timvisee.simplesurvivalgames.command;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.ArenaState;

public class CommandHandler {
	
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
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer());
						}
					}
					
					// Join the arena and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinPlayers(p);
					
					// Get a random arena spawn and put the player on it
					ap.setAssignedArenaSpawn(ap.getArena().getSpawnManager().getRandomUnassignedSpawn());
					ap.teleportToAssignedArenaSpawn(true);
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You joined the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " vote" + ChatColor.BLUE + " to vote start the arena");
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
					
					// TODO: Enable this bellow again
					// The arena must be in lobby state (or ya know waiting)
					/*if(!arena.getState().equals(ArenaState.LOBBY) && !arena.getState().equals(ArenaState.WAITING)) {
						sender.sendMessage(ChatColor.DARK_RED + "The arena is already in progress!");
						sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec " + arena.getName() + ChatColor.YELLOW + " to spectate the arena");
						sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.YELLOW + " to list all other arenas available to join");
						return true;
					}*/
					
					// Check if there are enough slots available for this arena
					if(!arena.isPlayerSlotAvailable()) {
						sender.sendMessage(ChatColor.DARK_RED + "This arena is full!");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " spec " + arena.getName() + ChatColor.BLUE + " to spectate the arena");
						sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " list" + ChatColor.BLUE + " to list all other arenas available to join");
						return true;
					}
					
					/// Is the player already in another arena
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
						} else {
							// Kick the player out of the current arena so it can join the new one
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer());
						}
					}
					
					// Join the arena and get the ArenaPlayer object
					ArenaPlayer ap = arena.getPlayerManager().joinPlayers(p);
					
					// Get a random arena spawn and put the player on it
					ap.setAssignedArenaSpawn(ap.getArena().getSpawnManager().getRandomUnassignedSpawn());
					ap.teleportToAssignedArenaSpawn(true);
					
					// Show some info to the player
					//p.sendMessage(ChatColor.GREEN + "You joined the arena " + ChatColor.GOLD + ap.getArena().getDisplayName() + ChatColor.GREEN + "!");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " vote" + ChatColor.BLUE + " to vote start the arena");
					p.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " leave" + ChatColor.BLUE + " to leave the arena");
					return true;
				}
				
			} else if(args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("spectate") ||
					args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("s") ||
					args[0].equalsIgnoreCase("watch")) {
				
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
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer());
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
							ap.getArena().getPlayerManager().kickPlayer(ap.getPlayer());
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
				}
				
			} else if(args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("exit")) {
				
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
				if(!ap.isInLobby()) {
					sender.sendMessage(ChatColor.DARK_RED + "You can't vote to start right now!");
					return true;
				}
				
				// Has the player already voted?
				if(ap.hasVoted()) {
					sender.sendMessage(ChatColor.BLUE + "You've already voted to start the arena!");
					return true;
				}
				
				// Vote
				ap.setVoted(true);
				
				p.sendMessage(ChatColor.BLUE + "You voted to start the arena!");
				for(ArenaPlayer entry : ap.getArena().getPlayerManager().getPlayersAndSpectators())
					if(!entry.getPlayer().equals(p))
						entry.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " voted to start the arena!");
				ap.getArena().updateVotingStatus();
				return true;
				
			} else if(args[0].equalsIgnoreCase("list") ||
					args[0].equalsIgnoreCase("games") || args[0].equalsIgnoreCase("arenas")) {
				
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
				
				int i = 1;
				for(Arena arena : am.getArenas()) {
					if(arena.getState().equals(ArenaState.PLAYING))
						sender.sendMessage(ChatColor.DARK_GRAY + String.valueOf(i) + ". " + ChatColor.GOLD + arena.getName() +
								ChatColor.DARK_GRAY + " (" + String.valueOf(arena.getAssignedSpawnsCount()) + "/" +
								String.valueOf(arena.getMaxPlayerCount()) + " Players) (In Progress)");
					else
						sender.sendMessage(ChatColor.DARK_GRAY + String.valueOf(i) + ". " + ChatColor.GOLD + arena.getName() +
								ChatColor.DARK_GRAY + " (" + String.valueOf(arena.getAssignedSpawnsCount()) + "/" +
								String.valueOf(arena.getMaxPlayerCount()) + " Players)");
					i++;
				}
				return true;
			} else if(args[0].equalsIgnoreCase("force")) {
				
				// Make sure the command sender didn't filled in too much arguments
				if(args.length > 2) {
					sender.sendMessage(ChatColor.DARK_RED + fullCommand);
					sender.sendMessage(ChatColor.DARK_RED + "Too many arguments!");
					sender.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/" + commandLabel + " help " + args[0] + ChatColor.BLUE + " to view help");
					return true;
				}
				
				// Get the arena manager
				ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
				
				// TODO: Has the player any arena selected
				
				// TODO: Do the rest
				
				
				
				
				// Are there any arenas yet
				if(am.getGameCount() == 0) {
					sender.sendMessage(ChatColor.BLUE + "==========[ SURVIVAL GAMES ARENAS ]==========");
					sender.sendMessage(ChatColor.DARK_RED + "There are no arenas available yet!");
					return true;
				}

				sender.sendMessage(ChatColor.BLUE + "==========[ SURVIVAL GAMES ARENAS ]==========");
				
				List<Arena> arenas = am.getArenas();
				// Sort the list on the amount of players in the arena
				//sort student-list with fast parallel-sort, it combines many sorting algorithms and use the power of multi-core processor
				Collections.sort(arenas, new Comparator<Arena>() {
					@Override
					public int compare(Arena a1, Arena a2) {
						return a1.getAssignedSpawnsCount() - a2.getAssignedSpawnsCount();
					}
				});
				
				int i = 1;
				for(Arena arena : arenas) {
					if(arena.getState().equals(ArenaState.PLAYING))
						sender.sendMessage(ChatColor.DARK_GRAY + String.valueOf(i) + ". " + ChatColor.GOLD + arena.getName() +
								ChatColor.DARK_GRAY + " (" + String.valueOf(arena.getAssignedSpawnsCount()) + "/" +
								String.valueOf(arena.getMaxPlayerCount()) + " Players) (In Progress)");
					else
						sender.sendMessage(ChatColor.DARK_GRAY + String.valueOf(i) + ". " + ChatColor.GOLD + arena.getName() +
								ChatColor.DARK_GRAY + " (" + String.valueOf(arena.getAssignedSpawnsCount()) + "/" +
								String.valueOf(arena.getMaxPlayerCount()) + " Players)");
					i++;
				}
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
}
