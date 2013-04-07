package com.timvisee.simplesurvivalgames.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.container.ArenaContainerManager;
import com.timvisee.simplesurvivalgames.arena.forcefield.ArenaForcefieldManager;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayerBlockManager;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayerManager;
import com.timvisee.simplesurvivalgames.arena.scoreboard.ArenaScoreboard;
import com.timvisee.simplesurvivalgames.arena.spawn.ArenaSpawnManager;

public class Arena {
	
	private boolean enabled = true;
	
	private String name = "Arena";
	private String dispName = "Arena";
	
	private ArenaState state = ArenaState.STANDBY;
	
	private ArenaCuboid arenaCuboid = null;
	private boolean mayLeaveArenaCuboid = false;
	
	private SSGLocation spectatorsSpawn = null;
	private ArenaCuboid spectatorsCuboid = null;
	private boolean mayLeaveSpectatorsCuboid = false;
	
	private ArenaSpawnManager spawnManager = new ArenaSpawnManager(this);
	private ArenaContainerManager containerManager = new ArenaContainerManager(this);
	
	private ArenaPlayerBlockManager apb = new ArenaPlayerBlockManager(this);
	private ArenaPlayerManager pm = new ArenaPlayerManager(this);
	
	private double minVotesPercentage = 50;
	private int minPlayers = 0;
	private int maxPlayers = -1;
	
	// Grace time
	private long gracePeriodLength = 6000;
	private long gracePeriodUntil = -1;
	
	private boolean editMode = false;
	
	private List<String> allowedCommands = new ArrayList<String>();
	
	private ArenaForcefieldManager fm = new ArenaForcefieldManager(this);
	
	private ArenaScoreboard as;
	
	// TODO: variables:
	// TODO: Rewards
	// TODO: Arena lobby spawn
	// TODO: Destructable Blocks
	
	/**
	 * Constructor
	 * @param name arena name
	 */
	public Arena(String name) {
		this.name = name;
		
		// Initialize / construct the arena scoreboard
		this.as = new ArenaScoreboard(this);
	}
	
	/**
	 * Is the arena enabled
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
	
	/**
	 * Set if the arena is enabled
	 * @param enabled true to enable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Get the name of the arena
	 * @return arena name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the name of the arena
	 * @param name arena name
	 */
	public void setName(String name) {
		if(name.trim().equals(""))
			return;
		this.name = name.trim();
	}
	
	/**
	 * Get the display name of the arena
	 * @return arena display name
	 */
	public String getDisplayName() {
		return this.dispName;
	}
	
	/**
	 * Set the display name of the arena
	 * @param dispName arena display name
	 */
	public void setDisplayName(String dispName) {
		if(dispName.trim().equals(""))
			return;
		this.dispName = dispName.trim();
	}
	
	/**
	 * Get the arena state
	 * @return arena state
	 */
	public ArenaState getState() {
		return this.state;
	}
	
	/**
	 * Set the arena state
	 * @param state new arena state
	 */
	public void setState(ArenaState state) {
		this.state = state;
		
		// Preform any tasks on state changes
		switch(state) {
		case LOBBY:
		case STANDBY:
			getForcefieldManager().removeAllForcefieldBlocks();
			break;
			
		default:
		}
	}
	
	/**
	 * Is the arena ready (in the lobby) to start a round with the current players. Calculcated by current player count and votes.
	 * @return true if ready
	 */
	public boolean isReady() {
		// There should be enough players in the lobby
		if(!hasEnoughPlayers())
			return false;
		
		// There should be enough votes
		if(!hasEnoughtVotes())
			return false;
		
		// The arena seems to be ready to start!
		return true;
	}
	
	/**
	 * Start a new arena round
	 */
	public void startRound() {
		// TODO: Reset health
		// TODO: Reset Exp
		// TODO: Reset creative & fly
		// TODO: Reset inventory
		
		// Remove all entities from the arena
		removeEntitiesFromArena();
		
		// Reset the player votes
		getPlayerManager().resetVotes();
		
		// Reset the kill count of every player
		getPlayerManager().resetRoundKills();
		
		// Refill the containers
		getContainerManager().fillConatiners(false);
		
		// Set the arena state to PLAYING
		setState(ArenaState.PLAYING);
		
		// Broadcast a start message
		sendMessage(ChatColor.GOLD + "The arena has been started!");
		
		// Make players visible for everybody
		for(ArenaPlayer p : getPlayerManager().getPlayers())
			for(ArenaPlayer entry : getPlayerManager().getPlayers())
				p.getPlayer().showPlayer(entry.getPlayer());
		
		// Activate the grace time
		if(getGracePeriodLength() > 0)
			activateGracePeriod(true);
		
		// Update the arena scoreboard
		getArenaScoreboard().update();
	}
	
	/**
	 * Check if the arena has enough players, votes, etc to start the new round.
	 * @return false if not ready to start round, or when not in LOBBY state
	 */
	public boolean isReadyToStart() {
		// Is the arena in lobby mode
		if(!getState().equals(ArenaState.LOBBY))
			return false;
		
		// At least 2 players has to join, this is already checked by the 'hasEnoughPlayers()' method
		// Has the arena enough players
		if(!hasEnoughPlayers())
			return false;
		
		// Has the arena enough votes
		if(!hasEnoughtVotes())
			return false;
		
		return true;
	}
	
	/**
	 * Stop the current round in the arena
	 * @return amount of kicked players and spectators (caused by the shop)
	 */
	public int endRound() {
		return endRound(false);
	}
	
	/**
	 * Stop the current round in the arena
	 * @param kickSpectators should spectators be kicked
	 * @return amount of kicked players and spectators (caused by the shop)
	 */
	public int endRound(boolean kickSpectators) {
		
		// Reset the kill count of every player
		getPlayerManager().resetRoundKills();
		
		// Kick players and spectators (if set)
		int kickedPlayers = kickAll(true, kickSpectators);
		
		// Set the arena state to WAITING
		setState(ArenaState.STANDBY);
		
		// Remove all the changed blocks (by players)
		getPlayerBlockManager().revertAllBlocks();
		getPlayerBlockManager().clear();
		
		// Refill the static container
		getContainerManager().fillStaticContainers(false);
		
		// Remove all entities from the arena
		removeEntitiesFromArena();
		
		// Update the arena scoreboard
		getArenaScoreboard().update();
		
		// Return the amount of kicked players and spectators
		return kickedPlayers;
	}
	
	/**
	 * Is the arena cuboid set
	 * @return true if set
	 */
	public boolean isArenaCuboidSet() {
		if(arenaCuboid == null)
			return false;
		return this.arenaCuboid.isSet();
	}
	
	/**
	 * Get the arena cuboid
	 * @return arena cuboid
	 */
	public ArenaCuboid getArenaCuboid() {
		return this.arenaCuboid;
	}
	
	/**
	 * Set the arena cuboid
	 * @param arenaCuboid new arena cuboid
	 */
	public void setArenaCuboid(ArenaCuboid arenaCuboid) {
		this.arenaCuboid = arenaCuboid;
	}
	
	/**
	 * Is a player allowed to leave the arean cuboid
	 * @return false if not
	 */
	public boolean getMayLeaveArenaCuboid() {
		return this.mayLeaveArenaCuboid;
	}
	
	/**
	 * Set if players are allowed to leave the arena cuboid
	 * @param mayLeaveArenaCuboid false if not
	 */
	public void setMayLeaveArenaCuboid(boolean mayLeaveArenaCuboid) {
		this.mayLeaveArenaCuboid = mayLeaveArenaCuboid;
	}
	
	/**
	 * Is the spectators spawn set
	 * @return true if set
	 */
	public boolean isSpectatorSpawnSet() {
		return (this.spectatorsSpawn != null);
	}
	
	/**
	 * Get the spectators spawn
	 * @return spectators spawn, null if not set
	 */
	public SSGLocation getSpectatorsSpawn() {
		return this.spectatorsSpawn;
	}

	/**
	 * Set the spectators spawn
	 * @param newSpectatorsSpawn new spectators spawn
	 */
	public void setSpectatorsSpawn(SSGLocation newSpectatorsSpawn) {
		this.spectatorsSpawn = newSpectatorsSpawn;
	}
	
	/**
	 * Is the spectators cuboid set
	 * @return true if set
	 */
	public boolean isSpectatorsCuboidSet() {
		if(this.spectatorsCuboid == null)
			return false;
		return this.spectatorsCuboid.isSet();
	}
	
	/**
	 * Get the spectators cuboid
	 * @return spectatord cuboid
	 */
	public ArenaCuboid getSpectatorsCuboid() {
		return this.spectatorsCuboid;
	}
	
	/**
	 * Set the spectators cuboid
	 * @param newSpectatorsCuboid new spectators cuboid
	 */
	public void setSpectatorsCuboid(ArenaCuboid newSpectatorsCuboid) {
		this.spectatorsCuboid = newSpectatorsCuboid;
	}
	
	/**
	 * May spectators leave the spectators cuboid
	 * @return false if not
	 */
	public boolean getMayLeaveSpectatorsCuboid() {
		return this.mayLeaveSpectatorsCuboid;
	}
	
	/**
	 * Set if spectators can leave the spectators cuboid
	 * @param canLeaveSpectatorsCuboid false if not
	 */
	public void setMayLeaveSpectatorsCuboid(boolean canLeaveSpectatorsCuboid) {
		this.mayLeaveSpectatorsCuboid = canLeaveSpectatorsCuboid;
	}
	
	/**
	 * Get the spawn manager of the arena
	 * @return spawn manager
	 */
	public ArenaSpawnManager getSpawnManager() {
		return this.spawnManager;
	}
	
	/**
	 * Get the container manager of the arena
	 * @return container manager
	 */
	public ArenaContainerManager getContainerManager() {
		return this.containerManager;
	}
	
	/**
	 * Get the vote count
	 * @return vote count
	 */
	public int getVotesCount() {
		return getPlayerManager().getVotesCount();
	}
	
	/**
	 * Get the votes percentage
	 * @return votes percentage
	 */
	public double getVotesPercentage() {
		return getPlayerManager().getVotesPercentage();
	}
	
	/**
	 * Are there enough votes for starting a round
	 * @return false if not
	 */
	public boolean hasEnoughtVotes() {
		return (getRemainingPlayerVotesCount() <= 0);
	}
	
	/**
	 * Get the minimum required votes percentage to start a round
	 * @return min required votes percentage
	 */
	public double getMinVotesPercentage() {
		return this.minVotesPercentage;
	}
	
	public void setMinVotesPercentage(double minVotesPercentage) {
		this.minVotesPercentage = minVotesPercentage;
	}
	
	/**
	 * Get the required remaining player votes count to start the arena
	 * @return required remaining player votes count
	 */
	public int getRemainingPlayerVotesCount() {
		if(getMinVotesPercentage() <= 0)
			return 0;
		
		int players = Math.max(getPlayerManager().getPlayerCount(), getMinPlayerCount());
		
		if(players == 0)
			return 0;
		
		double minVotesPercentage = getMinVotesPercentage();
		double votesCount = getVotesCount();
		
		int votesNeeded = (int) (roundUp(players * (minVotesPercentage/100)) - votesCount);
		
		return Math.max(votesNeeded, 0);
	}
	
	/**
	 * Round up (ceil) doubles
	 * @param d the double to round up (ceil)
	 * @return rounded up number (ceiled)
	 */
	private static int roundUp(double d) {  
        return (d > (int) d) ? (int) d + 1 : (int) d;  
    }
	
	/**
	 * Broadcast the player count to the players and spectators inside the arena
	 */
	public void broadcastPlayerCount() {
		final int minPlayers = getMinPlayerCount();
		final int players = getPlayerManager().getPlayerCount();
		final int maxPlayers = getMaxPlayerCount();
		
		// Do a different thing for each arena state
		switch(getState()) {
		case LOBBY:
		case STANDBY:
			if(minPlayers > 0)
				if(minPlayers > players)
					sendMessage(ChatColor.BLUE + "There " + (players==1 ? "is " + ChatColor.GOLD + "1" + ChatColor.BLUE + " player" : "are " + ChatColor.GOLD + String.valueOf(players) + ChatColor.BLUE + " players") + " in the arena. " +
							ChatColor.GOLD + (minPlayers-players==1 ? "1" + ChatColor.BLUE + " more player" : String.valueOf(minPlayers-players) + ChatColor.BLUE + " more players") + " needed to join!");
				else
					sendMessage(ChatColor.BLUE + "There " + (players==1 ? "is " + ChatColor.GOLD + "1" + ChatColor.BLUE + " player" : "are " + ChatColor.GOLD + String.valueOf(players) + ChatColor.BLUE + " players") + " in the arena. " +
							ChatColor.GOLD + (maxPlayers-players==1 ? "1" + ChatColor.BLUE + " more player" : String.valueOf(maxPlayers-players) + ChatColor.BLUE + " more players") + " can join.");
			else
				if(maxPlayers > 0)
					sendMessage(ChatColor.BLUE + "There " + (players==1 ? "is " + ChatColor.GOLD + "1" + ChatColor.BLUE + " player" : "are " + ChatColor.GOLD + String.valueOf(players) + ChatColor.BLUE + " players") + " in the arena. " +
						ChatColor.GOLD + (maxPlayers-players==1 ? "1" + ChatColor.BLUE + " more player" : String.valueOf(maxPlayers-players) + ChatColor.BLUE + " more players") + " can join.");
				else
					sendMessage(ChatColor.BLUE + "There " + (players==1 ? "is " + ChatColor.GOLD + "1" + ChatColor.BLUE + " player" : "are " + ChatColor.GOLD + String.valueOf(players) + ChatColor.BLUE + " players") + " in the arena.");
			return;
		
		case PLAYING:
		case STARTING:
			sendMessage(ChatColor.BLUE + "There " + (players==1 ? "is " + ChatColor.GOLD + "1" + ChatColor.BLUE + " player" : "are " + ChatColor.GOLD + String.valueOf(players) + ChatColor.BLUE + " players") + " in the arena.");
			return;
		}
	}
	
	/**
	 * Broadcast the voting status to the players and spectators inside the arena
	 */
	public void broadcastVotingStatus() {
		// Get some common used values and put them in variables
		final int votesCount = getVotesCount();
		final int remainingPlayersCount = getRemainingPlayerVotesCount();
		
		// Send a message to every player with the current voting status
		sendMessage(ChatColor.GOLD + (votesCount==1 ? "1" + ChatColor.BLUE + " player" : String.valueOf(votesCount) + ChatColor.BLUE + " players") + " voted to start! " +
					ChatColor.GOLD + (remainingPlayersCount==1 ? "1" + ChatColor.BLUE + " more player has" : String.valueOf(remainingPlayersCount) + ChatColor.BLUE + " more players have") + " to vote!");
		
		// Show a vote instruction message to
		for(ArenaPlayer entry : getPlayerManager().getPlayers())
			if(!entry.hasVoted())
				entry.sendMessage(ChatColor.BLUE + "Use " + ChatColor.GOLD + "/sg vote" + ChatColor.BLUE + " to vote start the arena");
	}
	
	/**
	 * Reset all the votes of all players (and spectators)
	 */
	public void resetVotes() {
		getPlayerManager().resetVotes();
	}
	
	/**
	 * Get the ArenaPlayerBlockManager
	 * @return the ArenaPlayerBlockManager
	 */
	public ArenaPlayerBlockManager getPlayerBlockManager() {
		return this.apb;
	}
	
	/**
	 * Kick all players from the arena
	 * @return amount of kicked players
	 */
	public int kickAllPlayers() {
		return this.pm.kickAllPlayers();
	}
	
	/**
	 * Kick all spectators from the arena
	 * @return amount of kicked spectators
	 */
	public int kickAllSpectators() {
		return this.pm.kickAllSpectators();
	}
	
	/**
	 * Kick all spectators and players from the arena
	 * @return amount of kicked players and spectators
	 */
	public int kickAll() {
		return this.pm.kickAll();
	}
	
	/**
	 * Kick players and spectators from the arena (selective)
	 * @param kickPlayers should players be kicked
	 * @param kickSpectators should spectators be kicked
	 * @return amount of kicked players and spectators
	 */
	public int kickAll(boolean kickPlayers, boolean kickSpectators) {
		return this.pm.kickAll(kickPlayers, kickSpectators);
	}
	
	/**
	 * Get the arena player manager
	 * @return player manager
	 */
	public ArenaPlayerManager getPlayerManager() {
		return this.pm;
	}
	
	/**
	 * Get the minimum amount of used slots in a round
	 * @return min amount of used slots
	 */
	public int getMinPlayerCount() {
		// Make sure the amount won't bet bellow 2
		return Math.max(Math.min(this.minPlayers, getMaxPlayerCount()), 2);
	}
	
	/**
	 * Get the minimum amount of used slots in a round
	 * @return min amount of used slots
	 */
	public int getMinPlayerCountConfig() {
		// Return the minPlayers from the config
		return this.minPlayers;
	}
	
	/**
	 * Set the minimum allowed amount of players in the arena
	 * @param minPlayers max allowed amount of players
	 */
	public void setMinPlayerCount(int minPlayers) {
		if(minPlayers < -1)
			minPlayers = 0;
		this.minPlayers = minPlayers;
	}
	
	/**
	 * Are there enough players inside the arena to start the round
	 * @return false if not
	 */
	public boolean hasEnoughPlayers() {
		return (getAssignedSpawnsCount() >= getMinPlayerCount());
	}
	
	/**
	 * Get the used amount of player slots
	 * @return used amount of player slots
	 */
	public int getAssignedSpawnsCount() {
		return getSpawnManager().getAssignedSpawnCount();
	}
	
	/**
	 * Get the amount of free slots in the arena
	 * @return
	 */
	public int getUnassignedSpawnsCount() {
		return getSpawnManager().getUnassignedSpawnCount();
	}
	
	/**
	 * Is there any slot free for use
	 * @return true if any free plot is availble
	 */
	public boolean isPlayerSlotAvailable() {
		return (getUnassignedSpawnsCount() > 0);
	}
	
	/**
	 * Get the maximum allowed amount of players in the arena
	 * @return max allowed amount of players, negative for infinite
	 */
	public int getMaxPlayerCount() {
		return Math.max(Math.min(this.maxPlayers, getSpawnManager().getSpawnCount()), -1);
	}
	
	/**
	 * Get the maximum allowed amount of players in the arena
	 * @return max allowed amount of players, negative for infinite
	 */
	public int getMaxPlayerCountConfig() {
		// Return the max players from the config
		return this.maxPlayers;
	}
	
	/**
	 * Set the maximum allowed amount of players in the arena
	 * @param maxPlayers max allowed amount of players, negative for infinite
	 */
	public void setMaxPlayers(int maxPlayers) {
		if(maxPlayers <= 0)
			maxPlayers = -1;
		this.maxPlayers = maxPlayers;
	}
	
	/**
	 * Get the grace period length
	 * @return grace period length
	 */
	public long getGracePeriodLength() {
		return this.gracePeriodLength;
	}
	
	/**
	 * Set the grace period length
	 * @param time grace period length in seconds
	 */
	public void setGracePeriodLength(int time) {
		// Make sure the time is not below zero
		this.gracePeriodLength = Math.max(time, 0);
	}
	
	/**
	 * Is the grace period currently activated
	 * @return false if not
	 */
	public boolean isGracePeriodActive() {
		return (this.gracePeriodUntil != -1 && this.gracePeriodUntil > System.currentTimeMillis());
	}
	
	/**
	 * Get the grace period time left
	 * @return time left, 0 when grace time was never activated. Time can be negative.
	 */
	public long getGracePeriodTimeLeft() {
		if(this.gracePeriodUntil == -1)
			return 0;
		
		return this.gracePeriodUntil - System.currentTimeMillis();
	}

	/**
	 * Activate the grace period
	 */
	public void activateGracePeriod() {
		activateGracePeriod(true);
	}
	
	/**
	 * Activate the grace period
	 * @param showMessages show messages
	 */
	public void activateGracePeriod(boolean showMessages) {
		if(showMessages) {
			sendMessage(ChatColor.GOLD + "Grace time started! " + String.valueOf((int) (getGracePeriodLength() / 1000)) + " seconds left!");
			
			for(int i = 90; i <= 300; i+=30) {
				if((this.getGracePeriodLength() / 1000) - 1 < i)
					break;
				
				final int time = i;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SimpleSurvivalGames.instance, new Runnable() {
					public void run() {
						sendMessage(ChatColor.GOLD + "Grace time active! " + String.valueOf(time) + " seconds left!");
					}
				}, (this.gracePeriodLength / 1000 - i) * 20);
			}
			
			for(int i = 30; i <= 75; i+=15) {
				if((this.getGracePeriodLength() / 1000) - 1 < i)
					break;
				
				final int time = i;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SimpleSurvivalGames.instance, new Runnable() {
					public void run() {
						sendMessage(ChatColor.GOLD + "Grace time active! " + String.valueOf(time) + " seconds left!");
					}
				}, (this.gracePeriodLength / 1000 - i) * 20);
			}
			
			for(int i = 10; i <= 25; i+=5) {
				if((this.getGracePeriodLength() / 1000) - 1 < i)
					break;
				
				final int time = i;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SimpleSurvivalGames.instance, new Runnable() {
					public void run() {
						sendMessage(ChatColor.GOLD + "Grace time active! " + String.valueOf(time) + " seconds left!");
					}
				}, (this.gracePeriodLength / 1000 - i) * 20);
			}
			
			for(int i = 1; i <= 5; i++) {
				if((this.getGracePeriodLength() / 1000) - 1 < i)
					break;
				
				final int time = i;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SimpleSurvivalGames.instance, new Runnable() {
					public void run() {
						sendMessage(ChatColor.GOLD + "Grace time active! " + String.valueOf(time) + " seconds left!");
					}
				}, (this.gracePeriodLength / 1000 - i) * 20);
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SimpleSurvivalGames.instance, new Runnable() {
				public void run() {
					sendMessage(ChatColor.GOLD + "The grace time ended! Hack and Slash time!");
				}
			}, (this.gracePeriodLength / 1000) * 20);
		}
		
		// Activate the grace time
		this.gracePeriodUntil = System.currentTimeMillis() + this.gracePeriodLength;
	}
	
	/**
	 * Disable the grace period
	 */
	public void disableGracePeriod() {
		resetGracePeriod();
	}
	
	/**
	 * Reset the grace period
	 */
	public void resetGracePeriod() {
		this.gracePeriodUntil = -1;
	}
	
	/**
	 * Is the arena in edit mode
	 * @return true if enabled
	 */
	public boolean isInEditMode() {
		return this.editMode;
	}
	
	/**
	 * Set the arenas edit mode
	 * @param enabled true to enable
	 */
	public void setEditMode(boolean enabled) {
		this.editMode = enabled;
	}
	
	/**
	 * Remove all entities from the arena
	 */
	public void removeEntitiesFromArena() {
		removeEntitiesFromArena(true, true, true);
	}
	
	/**
	 * Remove entities from the arena
	 * @param items remove items
	 * @param creatures remove mobs
	 * @param projectiles remove projectiles
	 */
	public void removeEntitiesFromArena(boolean items, boolean creatures, boolean projectiles) {
		// The arena cuboid has to be set
		if(!isArenaCuboidSet())
			return;
		
		// Loop through all the entities, if they are inside the arena, remove them
		World w = getArenaCuboid().getWorld();
		ArenaCuboid cuboid = getArenaCuboid();
		for(Entity e : w.getEntities()) {
			if(items && e instanceof Item)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
			
			if(creatures && e instanceof Creature)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
			
			if(projectiles && e instanceof Projectile)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
		}
	}
	
	/**
	 * Remove all the items from the arena
	 */
	public void removeItemsFromArena() {
		// The arena cuboid has to be set
		if(!isArenaCuboidSet())
			return;
		
		// Loop through all the entities, if they are inside the arena, remove them
		World w = getArenaCuboid().getWorld();
		ArenaCuboid cuboid = getArenaCuboid();
		for(Entity e : w.getEntities())
			if(e instanceof Item)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
	}
	
	/**
	 * Remove all the creature from the arena
	 */
	public void removeCreaturesFromArena() {
		// The arena cuboid has to be set
		if(!isArenaCuboidSet())
			return;
		
		// Loop through all the entities, if they are inside the arena, remove them
		World w = getArenaCuboid().getWorld();
		ArenaCuboid cuboid = getArenaCuboid();
		for(Entity e : w.getEntities())
			if(e instanceof LivingEntity)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
	}
	
	/**
	 * Remove all the projectiles from the arena
	 */
	public void removeProjectilesFromArena() {
		// The arena cuboid has to be set
		if(!isArenaCuboidSet())
			return;
		
		// Loop through all the entities, if they are inside the arena, remove them
		World w = getArenaCuboid().getWorld();
		ArenaCuboid cuboid = getArenaCuboid();
		for(Entity e : w.getEntities())
			if(e instanceof Projectile)
				if(cuboid.isInsideCuboid(e.getLocation()))
					e.remove();
	}
	
	/**
	 * Get all allowed commands
	 * @return
	 */
	public List<String> getAllowedCommands() {
		return this.allowedCommands;
	}
	
	/**
	 * Is a command allowed in this arena?
	 * @return true if allowed
	 */
	public boolean isAllowedCommand(String command) {
		// Get the command label from the command
		command = command.split(" ")[0];
		
		// Remove any slashes from the command
		if(command.startsWith("/"))
			command = command.substring(1);
		
		// Simple Survival Games commands are allowed
		if(command.equalsIgnoreCase("sg") || command.equalsIgnoreCase("ssg") ||
				command.equalsIgnoreCase("hg") || command.equalsIgnoreCase("shg") ||
				command.equalsIgnoreCase("simplesurvivalgames") || command.equalsIgnoreCase("simplehungergames"))
			return true;
		
		// Return if the command is allowed
		return this.allowedCommands.contains(command);
	}
	
	/**
	 * Add an allowed command
	 * @param command allowed command
	 */
	public void addAllowedCommand(String command) {
		this.allowedCommands.add(command);
	}
	
	/**
	 * Add a list of allowed commands
	 * @param commands list of allowed commands
	 */
	public void addAllowedCommands(List<String> commands) {
		this.allowedCommands.addAll(commands);
	}
	
	/**
	 * Set all allowed commands
	 * @param commands allowed commands
	 */
	public void setAllowedCommands(List<String> commands) {
		this.allowedCommands = commands;
	}
	
	/**
	 * Remove an allowed command from the list
	 * @param command command to remove
	 * @return false if this command wasn't in the list
	 */
	public boolean removeAllowedCommand(String command) {
		return this.allowedCommands.remove(command);
	}
	
	/**
	 * Clear the list with all allowed commands
	 */
	public void clearAllowedCommands() {
		this.allowedCommands.clear();
	}
	
	/**
	 * Get the arena scoreboard
	 * @return Arena scoreboard
	 */
	public ArenaScoreboard getArenaScoreboard() {
		return this.as;
	}
	
	/**
	 * Send a message to all players and spectators in the arena
	 * @param msg message to send
	 */
	public void sendMessage(String msg) {
		for(ArenaPlayer p : getPlayerManager().getPlayersAndSpectators())
			p.sendMessage(msg);
	}
	
	/**
	 * Send some messages to all players and spectators in the arena
	 * @param msgs messages to send
	 */
	public void sendMessage(String[] msgs) {
		for(ArenaPlayer p : getPlayerManager().getPlayersAndSpectators())
			p.sendMessages(msgs);
	}
	
	/**
	 * Send a message to all players in the arena
	 * @param msg message to send
	 */
	public void sendMessageToPlayers(String msg) {
		for(ArenaPlayer p : getPlayerManager().getPlayers())
			p.sendMessage(msg);
	}
	
	/**
	 * Send some messages to all players in the arena
	 * @param msgs messages to send
	 */
	public void sendMessageToPlayers(String[] msgs) {
		for(ArenaPlayer p : getPlayerManager().getPlayers())
			p.sendMessages(msgs);
	}
	
	/**
	 * Send a message to all spectators in the arena
	 * @param msg message to send
	 */
	public void sendMessageToSpectators(String msg) {
		for(ArenaPlayer p : getPlayerManager().getSpectators())
			p.sendMessage(msg);
	}
	
	/**
	 * Send some messages to all spectators in the arena
	 * @param msgs messages to send
	 */
	public void sendMessageToSpectators(String[] msgs) {
		for(ArenaPlayer p : getPlayerManager().getSpectators())
			p.sendMessages(msgs);
	}
	
	/**
	 * Is the arena set and ready to be used
	 * @return false if not
	 */
	public boolean isSet() {
		return !(arenaCuboid == null ||
				spawnManager == null ||
				containerManager == null);
	}
	
	/**
	 * Get the forcefield manager for this arena
	 * @return forcefield manager
	 */
	public ArenaForcefieldManager getForcefieldManager() {
		return this.fm;
	}
	
	/**
	 * Compare two arena's
	 * @param other the arena to compare to
	 * @return true if the arena's are equal to each other
	 */
	public boolean equals(Arena other) {
		return other.getName().equals(this.name);
	}
}
