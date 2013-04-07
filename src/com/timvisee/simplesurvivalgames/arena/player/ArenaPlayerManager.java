package com.timvisee.simplesurvivalgames.arena.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.arena.ArenaState;

public class ArenaPlayerManager {

	private Arena arena;
	private List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();
	private List<ArenaPlayer> spectators = new ArrayList<ArenaPlayer>();
	
	/**
	 * Constructor
	 * @param arena arena
	 */
	public ArenaPlayerManager(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Get the arena
	 * @return arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Join a player
	 * @param p the player to join
	 * @return the new arena player object
	 */
	public ArenaPlayer joinPlayers(Player p) {
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The player will be kicked from other arenas
		if(am.isInArena(p))
			am.kick(p);
		
		// Show a message to the players and spectators left in the arena
		getArena().sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " joined the arena! " +
				ChatColor.DARK_GRAY + "(" + String.valueOf(getPlayerCount() + 1) + "/" +
				String.valueOf(getArena().getMaxPlayerCount()) + " Players)");
		
		ArenaPlayer ap = new ArenaPlayer(p);
		this.players.add(ap);
		
		// Save the player state
		if(!ap.isLocationStored())
			ap.storePlayerState();
		
		// Set the state of the player
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setTotalExperience(0);
		p.setGameMode(GameMode.SURVIVAL);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setFireTicks(0);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
		p.setFallDistance(0);
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		
		// Show message to the player
		p.sendMessage(ChatColor.BLUE + "You joined the arena " + ChatColor.GOLD + getArena().getDisplayName() + ChatColor.BLUE + "!");
		
		// Broadcast a player count status message
		getArena().broadcastPlayerCount();
		
		// Broadcast the voting status if in the arena is in LOBBY state
		if(getArena().getState().equals(ArenaState.LOBBY) || getArena().getState().equals(ArenaState.STANDBY))
			getArena().broadcastVotingStatus();
		
		// TODO: Show status message
		
		// Hide all the spectators
		for(ArenaPlayer entry : getSpectators())
			p.hidePlayer(entry.getPlayer());
		
		// Update the arena state to LOBBY if the state was WAITING
		if(getArena().getState().equals(ArenaState.STANDBY))
			getArena().setState(ArenaState.LOBBY);
		
		// Start the arena if it's possible
		if(getArena().isReadyToStart())
			getArena().startRound();
		
		// Return the new ArenaPlayer object
		return ap;
	}
	
	/**
	 * Get the player as arena player
	 * @param p the player to get as arena player
	 * @return the player as arena player, null if the player doesn't exists
	 */
	public ArenaPlayer getPlayer(Player p) {
		return getPlayer(p, true, true);
	}
	
	/**
	 * Get the player as arena player
	 * @param p the player to get as arena player
	 * @param searchPlayers search all the players to compare the player
	 * @param searchSpectators search all the spectators to compare the player
	 * @return the player as arena player, null if the player doesn't exists
	 */
	public ArenaPlayer getPlayer(Player p, boolean searchPlayers, boolean searchSpectators) {
		if(searchPlayers)
			for(ArenaPlayer ap : getPlayers())
				if(ap.getPlayer().equals(p))
					return ap;
		
		if(searchSpectators)
			for(ArenaPlayer ap : getSpectators())
				if(ap.getPlayer().equals(p))
					return ap;
		
		return null;
	}
	
	/**
	 * Get all players
	 * @return players
	 */
	public List<ArenaPlayer> getPlayers() {
		return this.players;
	}
	
	/**
	 * Get all the players currently in the arena as Bukkit players
	 * @return players as Bukkit players
	 */
	public List<Player> getBukkitPlayers() {
		List<Player> players = new ArrayList<Player>();
		for(ArenaPlayer p : getPlayers())
			players.add(p.getPlayer());
		return players;
	}
	
	/**
	 * Get all players and spectators
	 * @return players and spectators
	 */
	public List<ArenaPlayer> getPlayersAndSpectators() {
		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();
		players.addAll(getPlayers());
		players.addAll(getSpectators());
		return players;
	}
	
	/**
	 * Get all players and spectators
	 * @return players and spectators
	 */
	public List<Player> getBukkitPlayersAndSpectators() {
		List<Player> players = new ArrayList<Player>();
		players.addAll(getBukkitPlayers());
		players.addAll(getBukkitSpectators());
		return players;
	}
	
	/**
	 * Is a player playing in the arena
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isPlayer(ArenaPlayer p) {
		return this.players.contains(p);
	}
	
	/**
	 * Is a player playing in the arena
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isPlayer(Player p) {
		for(ArenaPlayer ap : getPlayers())
			if(ap.getPlayer().getName().equals(p.getName()))
				return true;
		return false;
	}
	
	/**
	 * Check if a list of players is arena player
	 * @param players the players to check
	 * @return true if all players are arena player
	 */
	public boolean arePlayers(List<ArenaPlayer> players) {
		for(ArenaPlayer p : players)
			if(!isPlayer(p))
				return false;
		return true;
	}
	
	/**
	 * Check if a list of players is arena player
	 * @param players the players to check
	 * @return true if all players are arena player
	 */
	public boolean areBukkitPlayers(List<Player> players) {
		for(Player p : players)
			if(!isPlayer(p))
				return false;
		return true;
	}
	
	/**
	 * Get the player count
	 * @return player count
	 */
	public int getPlayerCount() {
		return this.players.size();
	}
	
	/**
	 * Get the vote count
	 * @return vote count
	 */
	public int getVotesCount() {
		int votes = 0;
		for(ArenaPlayer ap : getPlayers())
			if(ap.hasVoted())
				votes++;
		return votes;
	}
	
	/**
	 * Get the votes percentage
	 * @return votes percentage
	 */
	public double getVotesPercentage() {
		final int players = getPlayerCount();
		final int votes = getVotesCount();
		
		if(players == 0 || votes == 0)
			return 0;
		
		return ((players / votes) / players) * 100;
	}
	
	/**
	 * Reset all the votes of all players (and spectators)
	 */
	public void resetVotes() {
		for(ArenaPlayer ap : getPlayersAndSpectators())
			ap.resetVote();
	}
	
	/**
	 * Kick a player form the arena
	 * @param p the player to kick
	 * @param checkIfPlayerWon Should be checked if any player won
	 */
	public void kickPlayer(Player p, boolean checkIfPlayerWon) {
		// Select all the player instances to kick
		List<ArenaPlayer> kick = new ArrayList<ArenaPlayer>();
		for(ArenaPlayer ap : this.players)
			if(ap != null)
				if(ap.getPlayer().getName().equals(p.getName()))
					kick.add(ap);
		
		// Show message to the player
		if(kick.size() > 0)
			p.sendMessage(ChatColor.BLUE + "You left the arena " + ChatColor.GOLD + getArena().getDisplayName() + ChatColor.BLUE + "!");
		
		// Remove the forcefield blocks for all the players which are going to be kicked
		for(ArenaPlayer ap : kick)
			getArena().getForcefieldManager().removeForcefieldBlocks(ap.getPlayer());
		
		// Kick the selected player instances
		for(int i = 0; i < this.players.size(); i++) {
			for(int j = 0; j < kick.size(); j++) {
				if(this.players.get(i).getPlayer().getName().equals(kick.get(j).getPlayer().getName())) {
					this.players.remove(i);
					i--;
				}
			}
		}
		this.players.removeAll(kick);
		
		if(kick.size() > 0)	{
			// Show a message to the players and spectators left in the arena
			if(kick.size() == 1)
				getArena().sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " left the arena! " +
						ChatColor.GOLD + "1" + ChatColor.BLUE + " player left.");
			else
				getArena().sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " left the arena! " +
						ChatColor.GOLD + String.valueOf(getPlayerCount()) + ChatColor.BLUE + " players left.");
			
			// Broadcast the player status
			getArena().broadcastPlayerCount();
			
			// Broadcast the voting status if in the lobby
			if(getArena().getState().equals(ArenaState.LOBBY))
				getArena().broadcastVotingStatus();
		}
		
		// Restore the player states
		for(ArenaPlayer ap : kick) {
			ap.revertPlayerState();
			ap.getPlayer().setFireTicks(0);
			ap.getPlayer().setFallDistance(0);
		}
		
		// TODO: Show status message
		
		// If the arena state is WAITING and when no players are in, end the round.
		if(getPlayerCount() == 0)
			getArena().endRound();
		
		// If the player count equals to zero, the player won
		if(checkIfPlayerWon && getPlayerCount() == 1 &&
				!getArena().getState().equals(ArenaState.STANDBY) &&
				!getArena().getState().equals(ArenaState.LOBBY)) {
			
			ArenaPlayer winner = getPlayers().get(0);

			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + String.valueOf(winner.getPlayer().getName()) + ChatColor.BLUE + " won the game in " +
						ChatColor.GOLD + String.valueOf(getArena().getDisplayName()) + ChatColor.BLUE + " with " +
						ChatColor.GOLD + String.valueOf(winner.getRoundKills()) + ChatColor.BLUE + " kills!");
			Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg join " + getArena().getName() +
					ChatColor.BLUE + " to join the new round!");
			
			kickPlayer(winner.getPlayer(), true);
			
			// TODO: Give rewards
			// TODO: Change msg and do stuff for the player who won
		}
		
		// Show all the spectators again
		for(ArenaPlayer entry : getSpectators())
			p.showPlayer(entry.getPlayer());
		
		// Start the arena if it's possible
		if(getArena().isReadyToStart())
			getArena().startRound();
	}
	
	/**
	 * Kick a list of players
	 * @param players the players to kick
	 * @param checkIfPlayerWon Should be checked if any player won
	 */
	public void kickPlayers(List<ArenaPlayer> players, boolean checkIfPlayerWon) {
		if(players == null)
			return;
		
		List<Player> playersToKick = new ArrayList<Player>();
		
		for(ArenaPlayer p : players)
			if(p != null)
				playersToKick.add(p.getPlayer());
			
		for(Player p : playersToKick)
			if(p != null)
				kickPlayer(p, checkIfPlayerWon);
	}
	
	/**
	 * Kick a list of players
	 * @param players the players to kick
	 * @param checkIfPlayerWon Should be checked if any player won
	 */
	public void kickBukkitPlayers(List<Player> players, boolean checkIfPlayerWon) {
		for(Player p : players)
			kickPlayer(p, checkIfPlayerWon);
	}
	
	/**
	 * Kick all players from the arena
	 * @return amount of kicked players
	 */
	public int kickAllPlayers() {
		int playerCount = getPlayerCount();
		kickPlayers(this.players, false);
		return playerCount;
	}
	
	/**
	 * Make a player join the spectators
	 * @param p the player to join the spectators
	 * @return ArenaPlayer object
	 */
	public ArenaPlayer joinSpectators(Player p) {
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
		
		// The player will be kicked from other arenas
		if(am.isInArena(p))
			am.kick(p);
		
		// Show a message to the players left in the arena
		getArena().sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " is now spectating the arena!");
		
		ArenaPlayer ap = new ArenaPlayer(p);
		this.spectators.add(ap);
		
		// Save the player state
		if(!ap.isLocationStored())
			ap.storePlayerState();
		
		// Set the state of the player
		p.setHealth(p.getMaxHealth());
		p.setTotalExperience(0);
		p.setGameMode(GameMode.SURVIVAL);
		p.setFoodLevel(20);
		p.setTotalExperience(0);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.setFireTicks(0);
		p.getInventory().clear();
		p.setFallDistance(0);
		
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		
		// Reset the votes of this player
		ap.resetVote();
		
		// Show message to the player
		p.sendMessage(ChatColor.BLUE + "You are now spectating the arena " + ChatColor.GOLD + getArena().getDisplayName() + ChatColor.BLUE + "!");
		
		// Hide this spectator for other players
		for(ArenaPlayer entry : getPlayers())
			entry.getPlayer().hidePlayer(p);
		
		// TODO: Update arena state
		// TODO: Show status message
		
		return ap;
	}
	
	/**
	 * Get all the spectators
	 * @return spectators
	 */
	public List<ArenaPlayer> getSpectators() {
		return this.spectators;
	}
	
	/**
	 * Is a player a spectator
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isSpectator(Player p) {
		for(ArenaPlayer ap : getSpectators())
			if(ap.getPlayer().getName().equals(p.getName()))
				return true;
		return false;
	}
	
	/**
	 * Get all the spectators
	 * @return spectators
	 */
	public List<Player> getBukkitSpectators() {
		List<Player> spectators = new ArrayList<Player>();
		for(ArenaPlayer p : getSpectators())
			spectators.add(p.getPlayer());
		return spectators;
	}
	
	/**
	 * Is a player a spectator
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isSpectator(ArenaPlayer p) {
		return this.spectators.contains(p);
	}
	
	/**
	 * Check if some players are spectator
	 * @param players players to check
	 * @return true if all players are spectator
	 */
	public boolean areSpectators(List<ArenaPlayer> players) {
		for(ArenaPlayer p : players)
			if(!isSpectator(p))
				return false;
		return true;
	}
	
	/**
	 * Check if some players are spectator
	 * @param players players to check
	 * @return true if all players are spectator
	 */
	public boolean areBukkitSpectators(List<Player> players) {
		for(Player p : players)
			if(!isSpectator(p))
				return false;
		return true;
	}
	
	/**
	 * Get the spectator count
	 * @return spectator count
	 */
	public int getSpectatorCount() {
		return this.spectators.size();
	}
	
	/**
	 * Kick a spectator from the arena
	 * @param p spectator to kick
	 */
	public void kickSpectator(Player p) {
		// Select all the player instances to kick
		List<ArenaPlayer> kick = new ArrayList<ArenaPlayer>();
		for(ArenaPlayer ap : this.spectators)
			if(ap != null)
				if(ap.getPlayer().getName().equals(p.getName()))
					kick.add(ap);
		
		// Kick the selected player instances
		for(int i = 0; i < this.spectators.size(); i++) {
			for(int j = 0; j < kick.size(); j++) {
				if(this.spectators.get(i).getPlayer().getName().equals(kick.get(0).getPlayer().getName())) {
					this.spectators.remove(i);
					
					i--;
				}
			}
		}
		this.spectators.removeAll(kick);

		// Remove the forcefield blocks for all the players which are going to be kicked
		for(ArenaPlayer ap : kick)
			getArena().getForcefieldManager().removeForcefieldBlocks(ap.getPlayer());
		
		// Restore the player states
		for(ArenaPlayer ap : kick) {
			ap.revertPlayerState();
			ap.getPlayer().setFireTicks(0);
			ap.getPlayer().setFallDistance(0);
		}
		
		// Show message to the player
		if(kick.size() > 0) {
			p.sendMessage(ChatColor.BLUE + "You aren't spectating on the arena " + ChatColor.GOLD + getArena().getDisplayName() + ChatColor.BLUE + " anymore!");

			// Show a message to the players left in the arena
			getArena().sendMessage(ChatColor.GOLD + p.getName() + ChatColor.BLUE + " isn't spectating anymore!");
		}
		
		// Show the spectator for all the players again
		for(ArenaPlayer entry : getPlayers())
			entry.getPlayer().showPlayer(p);
	}
	
	/**
	 * Kick a list of spectators from the arena
	 * @param spectators spectators to kick
	 */
	public void kickSpectators(List<ArenaPlayer> spectators) {
		List<Player> playersToKick = new ArrayList<Player>();
		
		for(ArenaPlayer p : spectators)
			if(p != null)
				playersToKick.add(p.getPlayer());
		
		for(Player p : playersToKick)
			if(p != null)
				kickSpectator(p);
	}
	
	/**
	 * Kick a list of spectators from the arena
	 * @param spectators spectators to kick
	 */
	public void kickBukkitSpectators(List<Player> spectators) {
		for(Player p : spectators)
			if(isSpectator(p))
				kickSpectator(p);
	}
	
	/**
	 * Kick all spectators from the arena
	 * @return Amount of kicked spectators
	 */
	public int kickAllSpectators() {
		int spectatorCount = getSpectatorCount();
		kickSpectators(this.spectators);
		return spectatorCount;
	}
	
	/**
	 * Is a player in the arena
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isInArena(ArenaPlayer p) {
		return (isPlayer(p) || isSpectator(p));
	}
	
	/**
	 * Is a player in the arena
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isInArena(Player p) {
		return (isPlayer(p) || isSpectator(p));
	}
	
	/**
	 * Are players in the arena
	 * @param players players to check
	 * @return true if all players are in the arena
	 */
	public boolean areInArena(List<ArenaPlayer> players) {
		for(ArenaPlayer p : players)
			if(!isPlayer(p) && !isSpectator(p))
				return false;
		return true;
	}
	
	/**
	 * Are players in the arena
	 * @param players players to check
	 * @return true if all players are in the arena
	 */
	public boolean areInArenaBukkit(List<Player> players) {
		for(Player p : players)
			if(!isPlayer(p) && !isSpectator(p))
				return false;
		return true;
	}
	
	/**
	 * Get the total of round kills in this round
	 * @return total round kills this round
	 */
	public int getRoundKillsTotal() {
		int kills = 0;
		for(ArenaPlayer p : getPlayersAndSpectators())
			kills += p.getRoundKills();
		return kills;
	}
	
	/**
	 * Get the player with the most round kills
	 * @return player with most round kills, null if no player exists. A random player will be returned if no player has round kills
	 */
	public ArenaPlayer getMostRoundKillsPlayer() {
		ArenaPlayer player = null;
		int kills = 0;
		
		if(getPlayers().size() == 0 && getSpectators().size() == 0)
			return null;
		
		for(ArenaPlayer p : getPlayersAndSpectators())
			if(player == null || p.getRoundKills() > kills) {
				player = p;
				kills = p.getRoundKills();
			}
		
		return player;
	}
	
	/**
	 * Reset the round kills for every player (should be used before / after a round)
	 */
	public void resetRoundKills() {
		for(ArenaPlayer p : getPlayersAndSpectators())
			p.resetRoundKills();
	}
	
	/**
	 * Kick all players and spectators
	 * @return amount of players and specators kicked
	 */
	public int kickAll() {
		return kickAll(true, true);
	}
	
	/**
	 * Kick players and spectators
	 * @param kickPlayers should players be kicked
	 * @param kickSpectators should spectators be kicked
	 * @return amount of players and spectators kicked
	 */
	public int kickAll(boolean kickPlayers, boolean kickSpectators) {
		int amountKicked = 0;
		
		// Should players be kicked
		if(kickPlayers)
			amountKicked += kickAllPlayers();
		
		// Should spectators be kicked
		if(kickSpectators)
			amountKicked += kickAllSpectators();
		
		// Return amount of kicked players and spectators
		return amountKicked;
	}
}
