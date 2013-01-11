package com.timvisee.simplesurvivalgames.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.timvisee.simplesurvivalgames.SSGLocation;

public class Arena {
	
	private boolean enabled = true;
	
	private String name = "Arena";
	private String dispName = "Arena";
	
	private ArenaState state = ArenaState.WAITING;
	
	private String world = "world";
	private ArenaCuboid arenaCuboid = null;
	private boolean mayLeaveArenaCuboid = false;
	
	private SSGLocation spectatorsSpawn = null;
	private ArenaCuboid spectatorsCuboid = null;
	private boolean mayLeaveSpectatorsCuboid = false;
	
	private ArenaSpawnManager spawnManager = new ArenaSpawnManager(this);
	private ArenaContainerManager containerManager = new ArenaContainerManager(this);
	
	private ArenaPlayerManager pm = new ArenaPlayerManager(this);
	
	private double minVotesPercentage = 50;
	private int gracePeriodLength = 0;
	private int minPlayers = 0;
	private int maxPlayers = -1;
	
	private boolean editMode = false;
	
	private List<String> allowedCommands = new ArrayList<String>();
	
	private ArenaForcefieldManager fm = new ArenaForcefieldManager(this);
	
	// TODO variables:
	// Rewards
	// Arena lobby spawn
	// Destructable Blocks
	
	/**
	 * Constructor
	 * @param name arena name
	 */
	public Arena(String name) {
		this.name = name;
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
	}
	
	/**
	 * Is the arena ready (in the lobby) to start a round with the current players. Calculcated by current player count and votes.
	 * @return true if ready
	 */
	public boolean isReady() {
		// There should be enough players in the lobby
		if(!hasEnoughtPlayersToStart())
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
		// Reset the player votes
		getPlayerManager().resetVotes();
		
		// Reset the kill count of every player
		getPlayerManager().resetRoundKills();
		
		// Set the arena state to PLAYING
		setState(ArenaState.PLAYING);
		
		// Broadcast a start message
		sendMessage(ChatColor.BLUE + "Arena started!");
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
		setState(ArenaState.WAITING);
		
		// TODO: Broadcast end message
		
		// Return the amount of kicked players and spectators
		return kickedPlayers;
	}
	
	/**
	 * Get the name of the current arena world
	 * @return arena world name
	 */
	public String getWorldName() {
		return this.world;
	}
	
	/**
	 * Is the arena cuboid set
	 * @return true if set
	 */
	public boolean isArenaCuboidSet() {
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
		return (getVotesPercentage() >= getMinVotesPercentage());
	}
	
	/**
	 * Get the minimum required votes percentage to start a round
	 * @return min required votes percentage
	 */
	public double getMinVotesPercentage() {
		return this.minVotesPercentage;
	}
	
	/**
	 * Get the required remaining player votes count to start the arena
	 * @return required remaining player votes count
	 */
	public int getRemainingPlayerVotesCount() {
		if(getMinVotesPercentage() <= 0)
			return 0;
		
		if(Math.max(getPlayerManager().getPlayerCount(), getMinPlayerCount()) == 0)
			return 0;
		
		double minVotesPercentage = getMinVotesPercentage();
		double votesPercentage = getVotesPercentage();
		double votesPercentageEach = 100 / Math.max(getPlayerManager().getPlayerCount(), getMinPlayerCount());
		
		if(minVotesPercentage <= 0 || minVotesPercentage <= votesPercentage)
			return 0;
		
		double votesPercentageDelta = minVotesPercentage - votesPercentage;
		
		return (int) roundUp(votesPercentageDelta / votesPercentageEach);
	}
	
	private static int roundUp(double d) {  
        return (d > (int) d) ? (int) d + 1 : (int) d;  
    }  
	
	public void updateVotingStatus() {
		int remainingPlayersCount = getRemainingPlayerVotesCount();
		if(remainingPlayersCount == 1)
			sendMessage(ChatColor.GOLD + String.valueOf(getVotesCount()) + ChatColor.BLUE + " players voted to start! " +
					ChatColor.GOLD + "1" + ChatColor.BLUE + " more player has to vote!");
		else
			sendMessage(ChatColor.GOLD + String.valueOf(getVotesCount()) + ChatColor.BLUE + " players voted to start! " +
					ChatColor.GOLD + String.valueOf(remainingPlayersCount) + ChatColor.BLUE + " more players have to vote!");
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
	 * Get the grace period length
	 * @return grace period length
	 */
	public int getGracePeriodLength() {
		return this.gracePeriodLength;
	}
	
	/**
	 * Set the grace period length
	 * @param time grace period length in seconds
	 */
	public void setGracePeriodLength(int time) {
		// Make sure the time is not below zero
		if(time < 0)
			time = 0;
			
		this.gracePeriodLength = time;
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
		// Make sure the amount won't bet bellow 0
		return Math.max(Math.min(this.minPlayers, getMaxPlayerCount()), 0);
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
	public boolean hasEnoughtPlayersToStart() {
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
		int maxSlots = this.maxPlayers;
		
		if(maxSlots > getSpawnManager().getSpawnCount() || maxSlots <= 0)
			maxSlots = getSpawnManager().getSpawnCount();
		
		return maxSlots;
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
