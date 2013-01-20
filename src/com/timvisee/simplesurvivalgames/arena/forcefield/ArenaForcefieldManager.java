package com.timvisee.simplesurvivalgames.arena.forcefield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.timvisee.simplesurvivalgames.SSGBlockState;
import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaCuboid;
import com.timvisee.simplesurvivalgames.arena.forcefield.ArenaForcefieldBlock.ForcefieldBlockState;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;
import com.timvisee.simplesurvivalgames.util.Axis3D;

public class ArenaForcefieldManager {
	
	private Arena arena;
	private List<ArenaForcefieldBlock> blocks = new ArrayList<ArenaForcefieldBlock>();

	final double MAX_EDGE_DISTANCE = 6;
	final double MIN_CIRCLE_SIZE = 1;
	final double MAX_CIRCLE_SIZE = 4;
	final double CIRCLE_SIZE_FACTOR = 1.6;
	final List<Material> CHANGABLE_BLOCKS = new ArrayList<Material>(Arrays.asList(new Material[]{
			Material.AIR,
			Material.SAPLING,
			Material.WATER,
			Material.STATIONARY_WATER,
			Material.LAVA,
			Material.STATIONARY_LAVA,
			Material.POWERED_RAIL,
			Material.DETECTOR_RAIL,
			Material.WEB,
			Material.LONG_GRASS,
			Material.DEAD_BUSH,
			Material.YELLOW_FLOWER,
			Material.RED_ROSE,
			Material.BROWN_MUSHROOM,
			Material.RED_MUSHROOM,
			Material.TORCH,
			Material.FIRE,
			Material.REDSTONE,
			Material.LADDER,
			Material.RAILS,
			Material.LEVER,
			Material.STONE_PLATE,
			Material.WOOD_PLATE,
			Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON,
			Material.STONE_BUTTON,
			Material.SNOW,
			Material.SUGAR_CANE_BLOCK,
			Material.TRAP_DOOR,
			Material.PUMPKIN_STEM,
			Material.MELON_STEM,
			Material.VINE,
			Material.FENCE_GATE,
			Material.NETHER_WARTS,
			Material.DRAGON_EGG,
			Material.COCOA,
			Material.TRIPWIRE_HOOK,
			Material.TRIPWIRE,
			Material.CARROT,
			Material.POTATO,
			Material.WOOD_BUTTON
	}));
	
	/**
	 * Constructor
	 * @param arena the arena
	 */
	public ArenaForcefieldManager(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Get the arena
	 * @return the arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Get the list of forcefield blocks
	 * @return list of forcefield blocks
	 */
	public List<ArenaForcefieldBlock> getForcefieldBlocks() {
		return this.blocks;
	}
	
	/**
	 * Get all forcefield blocks from a player
	 * @param p the player to get the blocks from
	 * @return forcefield blocks of a player
	 */
	public List<ArenaForcefieldBlock> getForcefieldBlocks(Player p) {
		List<ArenaForcefieldBlock> playerBlocks = new ArrayList<ArenaForcefieldBlock>();
		
		for(ArenaForcefieldBlock b : this.blocks)
			if(b.getPlayer().equals(p))
				playerBlocks.add(b);
		
		return playerBlocks;
	}
	
	/**
	 * Set the list of forcefield blocks
	 * @param blocks new list of forcefield blocks
	 */
	public void setForcefieldBlocks(List<ArenaForcefieldBlock> blocks) {
		this.blocks = blocks;
	}
	
	/**
	 * Update the forcefield of a player
	 * @param ap the player to update the forcefield for
	 */
	public void updateForcefield(ArenaPlayer ap) {
		// The player has to play in the same arena,
		// the player may not be allowed to walk out of the arena,
		// the player should be playing,
		// If not remove all his forcefield blocks and cancel the update
		if(!ap.getArena().equals(this.arena) || (!ap.isPlaying() && !ap.isSpectator())) {
			removeForcefieldBlocks(ap.getPlayer());
			// TODO: Remove blocks of kicked player
			return;
		}
		
		// Make sure the cuboid is set
		if(ap.isPlaying())
			if(!ap.getArena().getArenaCuboid().isSet())
				return;
		else if(ap.isSpectator())
			if(!ap.getArena().isSpectatorsCuboidSet())
				if(!ap.getArena().getArenaCuboid().isSet())
					return;
				
		
		// Define some common used variables
		Player p = ap.getPlayer();
		Location loc = p.getEyeLocation();
		ArenaCuboid cuboid = null;
		List<Block> blocklist = new ArrayList<Block>();
		
		// Select the correct cuboid
		if(ap.isPlaying())
			cuboid = this.arena.getArenaCuboid();
		else if(ap.isSpectator())
			if(ap.getArena().isSpectatorsCuboidSet())
				cuboid = this.arena.getSpectatorsCuboid();
			else
				cuboid = this.arena.getArenaCuboid();
		else
			cuboid = this.arena.getArenaCuboid();
		
		// Generate a blocklist for all the forcefield blocks
		if(cuboid.getMinX() + MAX_EDGE_DISTANCE > loc.getX()) {
			double edgeDistance = loc.getX() - cuboid.getMinX();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					cuboid.getMinX() - 1, loc.getY(), loc.getZ())).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_X));
		}
		if(cuboid.getMaxX() - MAX_EDGE_DISTANCE < loc.getX()) {
			double edgeDistance = cuboid.getMaxX() - loc.getX();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					cuboid.getMaxX(), loc.getY(), loc.getZ())).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_X));
		}
		if(cuboid.getMinY() + MAX_EDGE_DISTANCE > loc.getY()) {
			double edgeDistance = loc.getY() - cuboid.getMinY();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					loc.getX(), cuboid.getMinY() - 1, loc.getZ())).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_Y));
		}
		if(cuboid.getMaxY() - MAX_EDGE_DISTANCE < loc.getY()) {
			double edgeDistance = cuboid.getMaxY() - loc.getY();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					loc.getX(), cuboid.getMaxY(), loc.getZ())).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_Y));
		}
		if(cuboid.getMinZ() + MAX_EDGE_DISTANCE > loc.getZ()) {
			double edgeDistance = loc.getZ() - cuboid.getMinZ();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					loc.getX(), loc.getY(), cuboid.getMinZ() - 1)).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_Z));
		}
		if(cuboid.getMaxZ() - MAX_EDGE_DISTANCE < loc.getZ()) {
			double edgeDistance = cuboid.getMaxZ() - loc.getZ();
			double circleRadius = Math.min(Math.max(((edgeDistance - MAX_EDGE_DISTANCE)*-1) / CIRCLE_SIZE_FACTOR, MIN_CIRCLE_SIZE), MAX_CIRCLE_SIZE);
			Location circleCenter = ((SSGLocation) new SSGLocation(
					p.getWorld().getName(),
					loc.getX(), loc.getY(), cuboid.getMaxZ())).toBukkitLocation();
			blocklist.addAll(makeCircleBlocklist(circleCenter, circleRadius, Axis3D.AXIS_Z));
		}
		
		// Remove some blocks to make perfect edges
		List<Block> tempBlocklist = new ArrayList<Block>();
		for(Block b : blocklist) {
			if(b.getLocation().getX() >= cuboid.getMinX() - 1 &&
					b.getLocation().getX() <= cuboid.getMaxX() &&
					b.getLocation().getY() >= cuboid.getMinY() - 1 &&
					b.getLocation().getY() <= cuboid.getMaxY() &&
					b.getLocation().getZ() >= cuboid.getMinZ() - 1 &&
					b.getLocation().getZ() <= cuboid.getMaxZ()) {
				tempBlocklist.add(b);
			}
		}
		blocklist = tempBlocklist;
		
		// Remove duplicates
		tempBlocklist = new ArrayList<Block>();
		for(Block b : blocklist)
			if(!tempBlocklist.contains(b))
				tempBlocklist.add(b);
		blocklist = tempBlocklist;

		// Remove all un-allowed blocks
		tempBlocklist = new ArrayList<Block>();
		for(Block b : blocklist) {
			if(CHANGABLE_BLOCKS.contains(b.getType()))
				tempBlocklist.add(b);
			else if(isBlockUsedAsForcefieldBlock(b))
				tempBlocklist.add(b);
		}
		blocklist = tempBlocklist;
		
		// Remove all forcefield blocks no more used by this player
		List<ArenaForcefieldBlock> remove = new ArrayList<ArenaForcefieldBlock>();
		for(ArenaForcefieldBlock b : getForcefieldBlocks(p)) {
			if(!blocklist.contains(b.getBlock())) {
				if(!isBlockUsedForOtherPlayer(b.getBlock(), p))
					b.setState(ForcefieldBlockState.NORMAL);
				remove.add(b);
			}
		}
		this.blocks.removeAll(remove);
		
		// Remove already used blocks (already used as forcefield by this player)
		tempBlocklist = new ArrayList<Block>();
		for(Block b : blocklist)
			if(!getPlayersUsingBlock(b).contains(p))
				tempBlocklist.add(b);
		blocklist = tempBlocklist;
		
		// Add all blocks in the blocklist as forcefield blocks
		for(Block b : blocklist) {
			if(isBlockUsedAsForcefieldBlock(b)) {
				// Get the original state from the block, and change the block to a forcefield block
				SSGBlockState origState = getForcefieldBlocksUsingBlock(b).get(0).getOriginalBlockState();
				ArenaForcefieldBlock afb = new ArenaForcefieldBlock(p, b);
				afb.setOriginalBlockState(origState);
				afb.setState(ForcefieldBlockState.FORCEFIELD);
				this.blocks.add(afb);
			} else {
				// Get the original state from the block, and change the block to a forcefield block
				ArenaForcefieldBlock afb = new ArenaForcefieldBlock(p, b);
				afb.setState(ForcefieldBlockState.FORCEFIELD);
				this.blocks.add(afb);
			}
		}
	}
	
	/**
	 * Get all players using this block as forcefield block
	 * @param b the block to check
	 * @return list of players using this block
	 */
	public List<Player> getPlayersUsingBlock(Block b) {
		List<Player> players = new ArrayList<Player>();
		for(ArenaForcefieldBlock entry : this.blocks)
			if(entry.getBlock().equals(b))
				players.add(entry.getPlayer());
		return players;
	}
	
	/**
	 * Get all forcefield blocks using this block
	 * @param b the block to check
	 * @return list of forcefield blocks
	 */
	public List<ArenaForcefieldBlock> getForcefieldBlocksUsingBlock(Block b) {
		List<ArenaForcefieldBlock> blocks = new ArrayList<ArenaForcefieldBlock>();
		for(ArenaForcefieldBlock entry : this.blocks)
			if(entry.getBlock().equals(b))
				blocks.add(entry);
		return blocks;
	}
	
	/**
	 * Check if a block is used as forcefield blocks
	 * @param b the block to check
	 * @return false if not
	 */
	public boolean isBlockUsedAsForcefieldBlock(Block b) {
		for(ArenaForcefieldBlock entry : this.blocks)
			if(entry.getBlock().equals(b))
				return true;
		return false;
	}
	
	/**
	 * Check if a block is also used as forcefield block for another player
	 * @param b the block to check
	 * @param p the player to check
	 * @return false if not
	 */
	public boolean isBlockUsedForOtherPlayer(Block b, Player p) {
		for(ArenaForcefieldBlock entry : this.blocks)
			if(entry.getBlock().equals(b) && !entry.getPlayer().equals(p))
				return true;
		return false;
	}
	
	/**
	 * Remove all forcefield blocks used for a specific player
	 * @param p the player to remove the blocks from
	 */
	public void removeForcefieldBlocks(Player p) {
		if(p == null)
			return;
		
		// Get a list of forcefield blocks used for this player
		List<ArenaForcefieldBlock> playerBlocks = getForcefieldBlocks(p);
		
		// Set all blocks which aren't used for other players to it's normal state
		for(ArenaForcefieldBlock b : playerBlocks) {
			if(!isBlockUsedForOtherPlayer(b.getBlock(), p))
				b.setState(ForcefieldBlockState.NORMAL);
		}
		
		// Clear the list of blocks for a player
		this.blocks.removeAll(playerBlocks);
	}
	
	/**
	 * Generate a blocklist for the blocks to change if a circle should be created
	 * @param loc circle center location
	 * @param radius circle radius
	 * @param onAxis circle rotation
	 * @return blocklist of blocks for the circle
	 */
	public List<Block> makeCircleBlocklist(Location loc, double radius, Axis3D onAxis) {
		// Add a half block to the radius to make the circle look better
		radius += .5;
		
		// Calculate the squared radius
		double radiusSq = (radius * radius);
		
		// Create a block list with a list of all blocks which should be changed
		List<Block> blocklist = new ArrayList<Block>();
		
		// Loop through every block
		for(int x = (int) -radius; x < (int) radius + 1; x++) {
			for(int y = (int) -radius; y < (int) radius + 1; y++) {
				if((x * x) + (y * y) <= radiusSq) {
					switch(onAxis) {
					case AXIS_X:
						blocklist.add(loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + x, loc.getBlockZ() + y));
						break;
						
					case AXIS_Y:
						blocklist.add(loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + y));
						break;
						
					case AXIS_Z:
						blocklist.add(loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ()));
						break;
					}
				}
			}
		}
		
		// Return the blocklist
		return blocklist;
    }
	
	/**
	 * Remove all forcefield blocks
	 */
	public void removeAllForcefieldBlocks() {
		// Set all blocks back to it's normal state
		for(ArenaForcefieldBlock b : this.blocks)
			b.setState(ForcefieldBlockState.NORMAL);
		
		// Clear the list of blocks
		this.blocks.clear();
	}
}
