package com.timvisee.simplesurvivalgames.arena;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;

public class ArenaPlayer {
	
	private Player p;
	
	// Assigned arena spawn
	private ArenaSpawn assignedSpawn = null;
	
	// Variables used for arena rounds
	private boolean voted = false;
	private int roundKills = 0;
	
	// Original player spawn and inventory
	private Location origLoc;
	private List<ItemStack> origInv = null;
	
	/**
	 * Constructor
	 * @param p the player
	 */
	public ArenaPlayer(Player p) {
		this.p = p;
		this.origLoc = p.getLocation();
		this.origInv = Arrays.asList(p.getInventory().getContents());
	}
	
	/**
	 * Constructor
	 * @param p the player
	 * @param origLoc original player location
	 */
	public ArenaPlayer(Player p, Location origLoc) {
		this.p = p;
		this.origLoc = origLoc;
		this.origInv = Arrays.asList(p.getInventory().getContents());
	}
	
	/**
	 * Get the player
	 * @return
	 */
	public Player getPlayer() {
		return this.p;
	}
	
	/**
	 * Is the player currently inside any arena
	 * @return false if not
	 */
	public boolean isInArena() {
		return SimpleSurvivalGames.instance.getArenaManager().isInArena(this.p);
	}
	
	public boolean isPlayer() {
		// Get the arena the player is in
		Arena a = getArena();
		
		// Make sure the arena is not null
		if(a == null)
			return false;
		
		// Is the player a player
		return a.getPlayerManager().isPlayer(this.p);
	}
	
	public boolean isPlaying() {
		// Get the arena the player is in
		Arena a = getArena();
		
		// Make sure the arena is not null
		if(a == null)
			return false;
		
		// Is the player playing
		return (a.getPlayerManager().isPlayer(this.p) &&
				a.getState().equals(ArenaState.PLAYING));
	}
	
	public boolean isInLobby() {
		// Get the arena the player is in
		Arena a = getArena();
		
		// Make sure the arena is not null
		if(a == null)
			return false;
		
		// Is the player in the lobby
		return (a.getPlayerManager().isPlayer(this.p) &&
				a.getState().equals(ArenaState.LOBBY));
	}
	
	public boolean isSpectator() {
		// Get the arena the player is in
		Arena a = getArena();
		
		// Make sure the arena is not null
		if(a == null)
			return false;
		
		// Is the player a spectator
		return a.getPlayerManager().isSpectator(this.p);
	}
	
	/**
	 * Return the arena the player is currently in
	 * @return arena the player is in, null if not in any arena
	 */
	public Arena getArena() {
		return SimpleSurvivalGames.instance.getArenaManager().getArena(this.p);
	}
	
	/**
	 * Is the player inside allowed area of the arena
	 * @return false if not in any arena, or when not in the allowed area
	 */
	public boolean isInArenaCuboid() {
		// Is the player currently in any arena
		if(!isInArena())
			return false;
		
		// Get the arena the player is in
		Arena arena = getArena();
		
		// Make sure the player has a cuboid to be in
		if(isInLobby())
			return true;
		
		// Make sure the player is not allowed to get out of the cuboid
		if(isPlaying()) {
			if(arena.getMayLeaveArenaCuboid())
				return true;
		} else if(isSpectator())
			if(arena.getMayLeaveSpectatorsCuboid())
				return true;
		
		// Make sure the arena has a cuboid set
		if(isPlaying())
			return arena.getArenaCuboid().isInsideCuboid(this);
		else if(isInLobby())
			return arena.getSpectatorsCuboid().isInsideCuboid(this);
		
		return false;
	}
	
	/**
	 * Does the player have any arena spawn assigned
	 * @return false if not
	 */
	public boolean hasAssignedAreanSpawn() {
		return (this.assignedSpawn != null);
	}
	
	/**
	 * Get the assigned arena spawn from the player
	 * @return assigned arena spawn, null if no spawn was assigned
	 */
	public ArenaSpawn getAssignedAreanSpawn() {
		return this.assignedSpawn;
	}
	
	/**
	 * Teleport the player to the assigned arena spawn<br>
	 * The player will not be teleported when no arena spawn has been assigned.
	 */
	public void teleportToAssignedArenaSpawn() {
		teleportToAssignedArenaSpawn(this.p, false);
	}
	
	/**
	 * Teleport the player to the assigned arena spawn<br>
	 * The player will not be teleported when no arena spawn has been assigned.
	 * @param playEffect play an effect at the player destination
	 */
	public void teleportToAssignedArenaSpawn(boolean playEffect) {
		teleportToAssignedArenaSpawn(this.p, playEffect);
	}
	
	/**
	 * Teleport a player to the assigned arena spawn<br>
	 * The player will not be teleported when no arena spawn has been assigned.
	 * @param p the player to teleport to the assigned arena spawn
	 * @param playEffect play an effect at the player destination
	 */
	public void teleportToAssignedArenaSpawn(Player p, boolean playEffect) {
		// Make sure the player is not null
		if(p == null)
			return;
		
		// Make sure any arena spawn has been assigned
		if(!hasAssignedAreanSpawn())
			return;
		
		Location to = this.getAssignedAreanSpawn().getLocation().toBukkitLocation().add(.5, .1, .5);
		
		// Teleport the player
		p.teleport(to);
		
		// Play effects if wanted
		if(playEffect)
			to.getBlock().getWorld().playEffect(to, Effect.MOBSPAWNER_FLAMES, 1);
	}
	
	/**
	 * Set the assigned arena spawn of the player
	 * @param spawn the new spawn to assign
	 */
	public void setAssignedArenaSpawn(ArenaSpawn spawn) {
		this.assignedSpawn = spawn;
	}
	
	/**
	 * Unassign the arena spawn from the player
	 */
	public void unassignArenaSpawn() {
		this.assignedSpawn = null;
	}
	
	/**
	 * Has the player voted already to start the round
	 * @return
	 */
	public boolean hasVoted() {
		return this.voted;
	}
	
	/**
	 * Set if the player has voted
	 * @param voted
	 */
	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	
	/**
	 * Reset the vote of the player
	 */
	public void resetVote() {
		this.voted = false;
	}
	
	/**
	 * Get the current kills in this round by this player
	 * @return round kills
	 */
	public int getRoundKills() {
		return this.roundKills;
	}
	
	/**
	 * Add a round kill to the player for the current round
	 */
	public void addRoundKill() {
		increaseRoundKills(1);
	}
	
	/**
	 * Increase the round kill amount for this player in this round
	 * @param amount
	 */
	public void increaseRoundKills(int amount) {
		// Make sure the kills wont get below 0
		this.roundKills = Math.max(this.roundKills + amount, 0);
	}
	
	/**
	 * Set the round kills for this player in this round
	 * @param roundKills
	 */
	public void setRoundKills(int roundKills) {
		this.roundKills = Math.max(roundKills, 0);
	}
	
	/**
	 * Reset the round kills for this player for a new round
	 */
	public void resetRoundKills() {
		this.roundKills = 0;
	}
	
	/**
	 * Teleport the player to the stored location
	 */
	public void teleportToStoredLocation() {
		teleportToStoredLocation(this.p);
	}
	
	/**
	 * Teleport a player to the stored location
	 * @param p the player to teleport
	 */
	public void teleportToStoredLocation(Player p) {
		// Make sure the player and the location aren't null
		if(this.p == null || this.origLoc == null)
			return;
		
		// Teleport the player
		p.teleport(this.origLoc);
	}
	
	/**
	 * Store the current location of the player as original location
	 */
	public void storeLocation() {
		storeLocation(this.p.getLocation());
	}
	
	/**
	 * Store a location as original location of the player
	 * @param loc the location to store
	 */
	public void storeLocation(Location loc) {
		this.origLoc = loc;
	}
	
	/**
	 * Is the original location of the player stored
	 * @return false if not
	 */
	public boolean isLocationStored() {
		return (this.origLoc != null);
	}
	
	/**
	 * Reset the stored location of the player
	 */
	public void resetLocation() {
		this.origLoc = null;
	}
	
	/**
	 * Get the stored inventory of the player
	 * @return stored inventory contents list, or null if not stored
	 */
	public List<ItemStack> getStoredInventory() {
		return this.origInv;
	}
	
	/**
	 * Apply the stored inventory to the player
	 */
	public void revertStoredInventory() {
		revertStoredInventory(this.p);
	}
	
	/**
	 * Apply the stored inventory to a player
	 * @param p the player to apply the stored inventory too
	 */
	public void revertStoredInventory(Player p) {
		// Make sure the player is not null!
		if(p == null)
			return;
		
		// If no inventory was stored, clear the players inventory
		if(origInv == null)
			p.getInventory().clear();
		else {
			ItemStack[] invContents = new ItemStack[]{};
			this.origInv.toArray(invContents);
			p.getInventory().setContents(invContents);
		}
	}
	
	/**
	 * Store the current player inventory<br>
	 * A previous stored inventory will be overwritten!
	 */
	public void storeInventory() {
		storeInventory(true);
	}
	
	/**
	 * Store the current player inventory
	 * @param overwrite should previous stored inventories being overwritten
	 */
	public void storeInventory(boolean overwrite) {
		storeInventory(Arrays.asList(this.p.getInventory().getContents()), overwrite);
	}

	/**
	 * Store a list of ItemStacks as player inventory
	 * @param overwrite should previous stored inventories being overwritten
	 */
	public void storeInventory(List<ItemStack> inv, boolean overwrite) {
		// Make sure the inventory should be overwritten if there's stored any. If so, store the inventory
		if(!isInventoryStored() || overwrite)
			this.origInv = inv;
	}
	
	/**
	 * Is the original inventory of the player stored
	 * @return false if not
	 */
	public boolean isInventoryStored() {
		return (this.origInv != null);
	}
	
	/**
	 * Reset the stored inventory of the player
	 */
	public void resetStoredInventory() {
		this.origInv = null;
	}
	
	/**
	 * Send a message to the player
	 * @param msg the message to send
	 */
	public void sendMessage(String msg) {
		this.p.sendMessage(msg);
	}
	
	/**
	 * Send a list of messages to the player
	 * @param msgs the messages to send
	 */
	public void sendMessages(String[] msgs) {
		this.p.sendMessage(msgs);
	}
	
	/**
	 * Get the player manager
	 * @return the player manager this player is managed by
	 */
	public ArenaPlayerManager getPlayerManager() {
		return getArena().getPlayerManager();
	}
	
	/**
	 * Compare the player to another Bukkit player
	 * @param player the player to compare to
	 * @return true if equal
	 */
	public boolean equals(Player player) {
		return this.p.equals(player);
	}
	
	/**
	 * Compare the player to another
	 * @param other the other to compare to
	 * @return true if equal
	 */
	public boolean equals(ArenaPlayer other) {
		return other.getPlayer().equals(this.p);
	}
}
