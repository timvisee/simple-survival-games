package com.timvisee.simplesurvivalgames.arena.player;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaState;
import com.timvisee.simplesurvivalgames.arena.spawn.ArenaSpawn;

public class ArenaPlayer {
	
	private Player p;
	
	// Assigned arena spawn
	private ArenaSpawn assignedSpawn = null;
	
	// Variables used for arena rounds
	private boolean voted = false;
	private int roundKills = 0;
	
	// Original player state
	private Location origLoc;
	private int origHealth = -1;
	private int origFood = -1;
	private int origExp = -1;
	private GameMode origGamemode = GameMode.SURVIVAL;
	private boolean origAllowFlying = false;
	private boolean origFlying = false;
	private List<ItemStack> origInv = null;
	private List<ItemStack> origInvArmor = null;
	
	/**
	 * Constructor
	 * @param p the player
	 */
	public ArenaPlayer(Player p) {
		this.p = p;
		storePlayerState();
	}
	
	/**
	 * Constructor
	 * @param p the player
	 * @param origLoc original player location
	 */
	public ArenaPlayer(Player p, Location origLoc) {
		this.p = p;
		storePlayerState();
		this.origLoc = origLoc;
	}
	
	/**
	 * Get the player
	 * @return
	 */
	public Player getPlayer() {
		return this.p;
	}
	
	/**
	 * Store the player state
	 */
	public void storePlayerState() {
		storePlayerState(true, true, true, true, true, true, true);
	}
	
	/**
	 * Store the player state
	 * @param loc store the location
	 * @param health store the health
	 * @param food store the food
	 * @param exp store the Exp
	 * @param gamemode store the gamemode
	 * @param fly store the fly
	 * @param inv store the inventory
	 */
	public void storePlayerState(boolean loc, boolean health, boolean food, boolean exp, boolean gamemode, boolean fly, boolean inv) {
		// Store the location
		if(loc)
			storeLocation();

		// Store the health
		if(health)
			storeHealth();
		
		// Store the food
		if(food)
			storeFood();
		
		// Store the Exp
		if(exp)
			storeExp();
		
		// Store the gamemode
		if(gamemode)
			storeGamemode();
		
		// Store the FlyMode
		if(fly) {
			storeAllowFlying();
			storeFlying();
		}
		
		// Store the inventory
		if(inv)
			storeInventory();
	}
	
	/**
	 * Revert the player state
	 */
	public void revertPlayerState() {
		revertPlayerState(true, true, true, true, true, true, true);
	}
	
	/**
	 * Revert the player state
	 * @param loc refert it's location
	 * @param health revert it's health
	 * @param exp revert it's Exp
	 * @param gamemode revert it's gamemode
	 * @param flymode revert it's flymode
	 * @param inv revert it's inventory
	 */
	public void revertPlayerState(boolean loc, boolean health, boolean food, boolean exp, boolean gamemode, boolean flymode, boolean inv) {
		// Revert the location
		if(loc && isLocationStored()) {
			teleportToStoredLocation();
			this.p.setVelocity(new Vector(0, 0, 0));
		}

		// Revert the health
		if(health && isOriginalHealthStored())
			this.p.setHealth(getOriginalHealth());
		
		// Revert the food
		if(food && isOriginalFoodStored())
			this.p.setFoodLevel(getOriginalFood());
		
		// Revert the Exp
		if(exp && isOriginalExpStored())
			this.p.setTotalExperience(getOriginalExp());
		
		// Revert the gamemode
		if(gamemode)
			this.p.setGameMode(getOriginalGamemode());
		
		// Revert the FlyMode
		if(flymode) {
			this.p.setAllowFlight(getOriginalAllowFlying());
			this.p.setFlying(getOriginalFlying());
		}
		
		// Revert the inventory
		if(inv && isInventoryStored())
			revertStoredInventory();
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
		else if(isSpectator()) {
			if(getArena().isSpectatorsCuboidSet()) {
				return getArena().getSpectatorsCuboid().isInsideCuboid(this);
			} else if(getArena().isArenaCuboidSet()) {
				return getArena().getArenaCuboid().isInsideCuboid(this);
			} else
				return true;
		}
		
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
		setVoted(voted, true);
	}
	
	/**
	 * Set if the player has voted
	 * @param voted
	 * @param startArenaIfReady check if the arena is ready, if so start the arena
	 */
	public void setVoted(boolean voted, boolean startArenaIfReady) {
		// Update if the player voted
		this.voted = voted;
		
		// Start the arena if it's ready
		if(startArenaIfReady)
			if(getArena().isReadyToStart())
				getArena().startRound();
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
	 * Store the health of the player
	 */
	public void storeHealth() {
		setOriginalHealth(this.p.getHealth());
	}
	
	/**
	 * Is the original life of the player stored
	 * @return false if not
	 */
	public boolean isOriginalHealthStored() {
		return (this.origHealth >= 0);
	}
	
	/**
	 * Get the original life of the player
	 * @return original life of the player
	 */
	public int getOriginalHealth() {
		return this.origHealth;
	}
	
	/**
	 * Set the original life of the player
	 * @param origLife original life of the player
	 */
	public void setOriginalHealth(int origLife) {
		this.origHealth = Math.max(origLife, -1);
	}
	
	/**
	 * Reset the original life of the player
	 */
	public void resetOriginalHealth() {
		this.origHealth = -1;
	}
	
	/**
	 * Store the food level of the player
	 */
	public void storeFood() {
		setOriginalFood(this.p.getFoodLevel());
	}
	
	/**
	 * Is the original food level of the player stored
	 * @return false if not
	 */
	public boolean isOriginalFoodStored() {
		return (this.origFood >= 0);
	}
	
	/**
	 * Get the original food level of the player
	 * @return get original food level
	 */
	public int getOriginalFood() {
		return this.origFood;
	}
	
	/**
	 * Set the original food level of the player
	 * @param origFood new original food level
	 */
	public void setOriginalFood(int origFood) {
		this.origFood = Math.max(origFood, 0);
	}
	
	/**
	 * Reset the original food level of the player
	 */
	public void resetOriginalFood() {
		this.origFood = -1;
	}
	
	/**
	 * Store the Exp of the player
	 */
	public void storeExp() {
		setOriginalExp(this.p.getTotalExperience());
	}
	
	/**
	 * Is the original Exp of the player stored
	 * @return false if not
	 */
	public boolean isOriginalExpStored() {
		return (this.origExp >= 0);
	}
	
	/**
	 * Get the original exp of the player
	 * @return original exp
	 */
	public int getOriginalExp() {
		return this.origExp;
	}
	
	/**
	 * Set the original exp of the player
	 * @param origExp original exp
	 */
	public void setOriginalExp(int origExp) {
		this.origExp = Math.max(origExp, -1);
	}
	
	/**
	 * Reset the original exp of the player
	 */
	public void resetOriginalExp() {
		this.origExp = -1;
	}
	
	/**
	 * Store the original gamemode of the player
	 */
	public void storeGamemode() {
		setOriginalGamemode(this.p.getGameMode());
	}
	
	/**
	 * Get the original gamemode of the player
	 * @return original gamemode
	 */
	public GameMode getOriginalGamemode() {
		return this.origGamemode;
	}
	
	/**
	 * Set the original gamemode of the player
	 * @param origGamemode original gamemode
	 */
	public void setOriginalGamemode(GameMode origGamemode) {
		this.origGamemode = origGamemode;
	}
	
	/**
	 * Reset the original gamemode of the player
	 */
	public void resetOriginalGamemode() {
		this.origGamemode = GameMode.SURVIVAL;
	}
	
	/**
	 * Store the allow flying
	 */
	public void storeAllowFlying() {
		this.origAllowFlying = this.p.getAllowFlight();
	}
	
	/**
	 * Get the original allow flying
	 * @return original allow flying
	 */
	public boolean getOriginalAllowFlying() {
		return this.origAllowFlying;
	}
	
	/**
	 * Set the original allow flying
	 * @param origAllowFlying original allow flying
	 */
	public void setOriginalAllowFlying(boolean origAllowFlying) {
		this.origAllowFlying = origAllowFlying;
	}
	
	/**
	 * Reset the original allow flying
	 */
	public void resetOriginalAllowFlying() {
		this.origAllowFlying = false;
	}
	
	/**
	 * Store the fly mode of the player
	 */
	public void storeFlying() {
		setOriginalFlying(this.p.isFlying());
	}
	
	/**
	 * Get the original fly mode of the player
	 * @return original fly mode
	 */
	public boolean getOriginalFlying() {
		return this.origFlying;
	}
	
	/**
	 * Set the original fly mode of the player
	 * @param origFlying original fly mode
	 */
	public void setOriginalFlying(boolean origFlying) {
		this.origFlying = origFlying;
	}
	
	/**
	 * Reset the original fly mode
	 */
	public void resetOriginalFlying() {
		this.origFlying = true;
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
		storeInventory(Arrays.asList(this.p.getInventory().getContents()), Arrays.asList(this.p.getInventory().getArmorContents()), overwrite);
	}

	/**
	 * Store a list of ItemStacks as player inventory
	 * @param overwrite should previous stored inventories being overwritten
	 */
	public void storeInventory(List<ItemStack> inv, List<ItemStack> invArmor, boolean overwrite) {
		// Make sure the inventory should be overwritten if there's stored any. If so, store the inventory
		if(!isInventoryStored() || overwrite) {
			this.origInv = inv;
			this.origInvArmor = invArmor;
		}
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
		if(this.origInv == null)
			p.getInventory().clear();
		else {
			ItemStack[] invContents = this.origInv.toArray(new ItemStack[this.origInv.size()]);
			p.getInventory().setContents(invContents);
			ItemStack[] invArmorContents = this.origInvArmor.toArray(new ItemStack[this.origInvArmor.size()]);
			p.getInventory().setArmorContents(invArmorContents);
		}
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
