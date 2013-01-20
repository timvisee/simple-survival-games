package com.timvisee.simplesurvivalgames.arena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.SSGLocation;
import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.container.ArenaContainer;
import com.timvisee.simplesurvivalgames.arena.container.ArenaRandomContainer;
import com.timvisee.simplesurvivalgames.arena.container.ArenaStaticContainer;
import com.timvisee.simplesurvivalgames.arena.container.items.ArenaContainerItem;
import com.timvisee.simplesurvivalgames.arena.container.items.ArenaContainerItemEnchantment;
import com.timvisee.simplesurvivalgames.arena.container.items.ArenaContainerSet;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayer;
import com.timvisee.simplesurvivalgames.arena.player.ArenaPlayerManager;
import com.timvisee.simplesurvivalgames.arena.spawn.ArenaSpawn;

public class ArenaManager {
	
	private List<Arena> arenas = new ArrayList<Arena>();
	
	/**
	 * Constructor
	 */
	public ArenaManager() { }
	
	/**
	 * Add an arena
	 * @param arena new arena
	 */
	public void addArena(Arena arena) {
		this.arenas.add(arena);
	}
	
	/**
	 * Get a list of arena's
	 * @return arena list
	 */
	public List<Arena> getArenas() {
		return this.arenas;
	}
	
	/**
	 * Get a list of enabled arena's
	 * @return enabled arenas list
	 */
	public List<Arena> getEnabledArenas() {
		List<Arena> enabledArenas = new ArrayList<Arena>();
		for(Arena arena : this.arenas)
			if(arena.isEnabled())
				enabledArenas.add(arena);
		return enabledArenas;
	}
	
	/**
	 * Is there any arena with this name
	 * @param name arena name to check
	 * @return false if not
	 */
	public boolean isArenaWithName(String name) {
		// First check case sensetive
		for(Arena a : this.arenas)
			if(a.getName().equals(name))
				return true;
		
		// Now check case insensetive
		for(Arena a : this.arenas)
			if(a.getName().equalsIgnoreCase(name))
				return true;
		
		// No arena found with this name
		return false;
	}
	
	/**
	 * Get the arena with this name
	 * @param name the name of the arena to get
	 * @return arena or null if no arena was found with this name
	 */
	public Arena getArenaWithName(String name) {
		// First check case sensetive
		for(Arena a : this.arenas)
			if(a.getName().equals(name))
				return a;
		
		// Now check case insensetive
		for(Arena a : this.arenas)
			if(a.getName().equalsIgnoreCase(name))
				return a;
		
		// No arena found with this name
		return null;
	}
	
	/**
	 * Get the best arena to join, a formula will be used to choose the best one.
	 * @return best arena to join, null if all arenas are in progress or when there's no arena available
	 */
	public Arena getBestArenaToJoin() {
		Arena arena = null;
		int playerCount = 0;
		
		for(Arena a : getArenas()) {
			// The arena mustn't be in progress
			if(a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// There must be a free slot in the arena
			if(a.getUnassignedSpawnsCount() == 0)
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to join
		List<Arena> arenas = getArenas();
		Collections.shuffle(arenas, new Random());
		for(Arena a : arenas) {
			// The arena mustn't be in progress
			if(a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// There must be a free slot in the arena
			if(a.getUnassignedSpawnsCount() == 0)
				continue;
			
			return a;
		}
		
		// Still no arena selected, return null
		return null;
	}
	
	/**
	 * Get the best arena to spectate, a formula will be used to choose the best one.
	 * @return best arena to spectate, null if there's no arena available
	 */
	public Arena getBestArenaToSpectate() {
		Arena arena = null;
		int playerCount = 0;
		
		for(Arena a : getArenas()) {
			// The arena should be in progress
			if(!a.getState().equals(ArenaState.PLAYING))
				continue;
			
			// The spectators spawn has to be set
			if(!a.isSpectatorSpawnSet())
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to join
		for(Arena a : getArenas()) {
			// The spectators spawn has to be set
			if(!a.isSpectatorSpawnSet())
				continue;
			
			// If this arena is better to use, select this one
			if(playerCount < a.getAssignedSpawnsCount() && 1 <= a.getAssignedSpawnsCount()) {
				arena = a;
				playerCount = a.getAssignedSpawnsCount();
			}
		}
		
		// Is any arena selected yet
		if(arena != null)
			return arena;
		
		// No arena has been selected yet, select a random arena to spectate
		List<Arena> arenas = getArenas();
		Collections.shuffle(arenas, new Random());
		return arenas.get(0);
	}
	
	/**
	 * Get the amount of arenas
	 * @return arena count
	 */
	public int getGameCount() {
		return this.arenas.size();
	}
	
	/**
	 * Get the player manager of an arena
	 * @param arena the arena to get the player manager from
	 * @return player manager of arena
	 */
	public ArenaPlayerManager getPlayerManager(Arena arena) {
		return arena.getPlayerManager();
	}
	
	/**
	 * Get all player managers from all arenas
	 * @return player managers from all arenas
	 */
	public List<ArenaPlayerManager> getPlayerManagers() {
		List<ArenaPlayerManager> playerManagers = new ArrayList<ArenaPlayerManager>();
		for(Arena a : this.arenas)
			playerManagers.add(a.getPlayerManager());
		return playerManagers;
	}
	
	/**
	 * Get all players and spectators
	 * @return all players and spectators
	 */
	public List<ArenaPlayer> getAllPlayersAndSpectators() {
		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();
		for(ArenaPlayerManager pm : getPlayerManagers())
			players.addAll(pm.getPlayersAndSpectators());
		return players;
	}
	
	/**
	 * Is a player in any arena
	 * @param p the player to check
	 * @return true if the player is playing
	 */
	public boolean isInArena(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return true;
		return false;
	}
	
	/**
	 * Get an arena player by a player
	 * @param p the player to get as arena player
	 * @return arena player or null when the player wasn't found
	 */
	public ArenaPlayer getPlayer(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a.getPlayerManager().getPlayer(p);
		return null;
	}
	
	/**
	 * Is a player in any arena
	 * @param p the player to check
	 * @return true if the player is playing
	 */
	public boolean isInArena(ArenaPlayer p) {
		return p.getArena().getPlayerManager().isInArena(p);
	}
	
	/**
	 * Get the arena a player is currently in
	 * @param p the player to get the arena from
	 * @return arena the player is currently in, null if not in any arena
	 */
	public Arena getArena(Player p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a;
		return null;
	}
	
	/**
	 * Get the arena a player is currently in
	 * @param p the player to get the arena from
	 * @return arena the player is currently in, null if not in any arena
	 */
	public Arena getArena(ArenaPlayer p) {
		for(Arena a : this.arenas)
			if(a.getPlayerManager().isInArena(p))
				return a;
		return null;
	}
	
	/**
	 * Kick a player from every arena
	 * @param p the player to kick
	 */
	public void kick(Player p) {
		for(ArenaPlayerManager apm : getPlayerManagers())
			if(apm.isInArena(p)) {
				if(apm.isPlayer(p))
					apm.kickPlayer(p, true);
				if(apm.isSpectator(p))
					apm.kickSpectator(p);
			}
	}
	
	/**
	 * Stop all arenas
	 */
	public void stopAllArenas() {
		for(Arena a : getArenas())
			a.endRound(true);
	}
	
	/**
	 * Clear the list of arenas
	 */
	public void clear() {
		this.arenas.clear();
	}
	
	/**
	 * Save all arenas in an external file
	 * @return saving duration in miliseconds
	 */
	public long save() {
		// Define the default file path and save the shops
		return this.save(new File(SimpleSurvivalGames.instance.getDataFolder(), "data/arenas.yml"));
	}
	
	/**
	 * Save all arenas in an external file
	 * @param f the file to save the arenas in
	 * @return saving duration in miliseconds
	 */
	public long save(File f) {
		// Check if the arenas file already exists
		if(!f.exists())
			System.out.println("[SimpleSurvivalGames] Arenas file not found. Creating new file...");
		
		long t = System.currentTimeMillis();
		
		// Show a status message in the config
		System.out.println("[SimpleSurvivalGames] Saving arenas...");
		
		// Define the new config file holder to put all the arenas in
		YamlConfiguration c = new YamlConfiguration();
		
		// Add some header information to the config file
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		c.options().header("File generated by Simple Survival Games v" + SimpleSurvivalGames.instance.getVersion() + " at " + sdf.format(cal.getTime()));
		
		// Create a section for the arenas
		c.createSection("arenas");
		
		// Loop through every arena and save it into the file
		for(Arena a : this.arenas) {
			String arenaName = a.getName();
			c.set("arenas." + arenaName + ".enabled", a.isEnabled());
			c.set("arenas." + arenaName + ".displayName", a.getDisplayName());
			c.set("arenas." + arenaName + ".editMode", a.isInEditMode());
			c.set("arenas." + arenaName + ".minPlayers", a.getMinPlayerCountConfig());
			c.set("arenas." + arenaName + ".maxPlayers", a.getMaxPlayerCountConfig());
			c.set("arenas." + arenaName + ".cuboids.arena.c1", a.getArenaCuboid().getFirstCorner().toString());
			c.set("arenas." + arenaName + ".cuboids.arena.c2", a.getArenaCuboid().getSecondCorner().toString());
			if(a.isSpectatorsCuboidSet()) {
				c.set("arenas." + arenaName + ".cuboids.spectator.c1", a.getSpectatorsCuboid().getFirstCorner().toString());
				c.set("arenas." + arenaName + ".cuboids.spectator.c2", a.getSpectatorsCuboid().getSecondCorner().toString());
			}
			
			c.createSection("arenas." + arenaName + ".spawns.arena");
			int spawnIndex = 0;
			for(ArenaSpawn as : a.getSpawnManager().getSpawns()) {
				c.set("arenas." + arenaName + ".spawns.arena." + String.valueOf(spawnIndex) + ".loc", as.getLocation().toString());
				spawnIndex++;
			}
			
			if(a.isSpectatorSpawnSet())
				c.set("arenas." + arenaName + ".spawns.spectator.loc", a.getSpectatorsSpawn().toString());
			
			c.set("arenas." + arenaName + ".mayLeaveArenaCuboid", a.getMayLeaveArenaCuboid());
			c.set("arenas." + arenaName + ".mayLeaveSpectatorsCuboid", a.getMayLeaveSpectatorsCuboid());
			c.set("arenas." + arenaName + ".minVotingPercentage", a.getMinVotesPercentage());
			c.set("arenas." + arenaName + ".gracePeriodLength", a.getGracePeriodLength());
			c.set("arenas." + arenaName + ".commands.allowed", a.getAllowedCommands());
			
			// Create a section for the arenas
			c.createSection("arenas." + arenaName + ".containers");
			
			// Loop through every arena and save it into the file
			int i = 0;
			for(ArenaContainer container : a.getContainerManager().getContainers()) {
				if(container instanceof ArenaRandomContainer) {
					ArenaRandomContainer randomContainer = (ArenaRandomContainer) container;
					c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".type", "RANDOM");
					c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".loation", randomContainer.getLocation().toString());
				} else if(container instanceof ArenaStaticContainer) {
					ArenaStaticContainer staticContainer = (ArenaStaticContainer) container;
					c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".type", "STATIC");
					c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".location", staticContainer.getLocation().toString());
					c.createSection("arenas." + arenaName + ".containers." + String.valueOf(i) + ".defContents");
					
					int itemStackIndex = 0;
					for(ItemStack entry : staticContainer.getDefaultContents()) {
						if(entry != null)
							c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".defContents." + String.valueOf(itemStackIndex), entry);
						else
							c.set("arenas." + arenaName + ".containers." + String.valueOf(i) + ".defContents." + String.valueOf(itemStackIndex), "null");
						itemStackIndex++;
					}
				}
				i++;
			}
		}
		
		// Add the plugin version into the config file
		c.set("version", SimpleSurvivalGames.instance.getVersion());
		
		// Save the config
		FileConfiguration fileConfig = c;
		try {
			fileConfig.save(f);
		} catch (IOException e) {
			System.out.println("[SimpleSurvivalGames] Error while saving the arenas!");
			e.printStackTrace();
			return 0;
		}
		
		// Calculate the save duration
		long duration = System.currentTimeMillis() - t;

		// Show an message in the console
		System.out.println("[SimpleSurvivalGames] " + (this.arenas.size()==1 ? "1 arena" : String.valueOf(this.arenas.size()) + " arenas") + " saved, took " + String.valueOf(duration) + "ms!");
	
		// Return the saving duration
		return duration;
	}
	
	/**
	 * Load all arenas from an external file
	 * @return loading duration in miliseconds
	 */
	public long load() {
		// Define the default file path and save the shops
		return this.load(new File(SimpleSurvivalGames.instance.getDataFolder(), "data/arenas.yml"));
	}
	
	/**
	 * Load all arenas from an external file
	 * @param f the file to load the arenas from
	 * @return loading duration in miliseconds
	 */
	public long load(File f) {
		// Check if the shops file exists
		if(!f.exists()) {
			System.out.println("[SimpleSurvivalGames] Arenas data file doesn't exist!");
			return 0;
		}
		
		long t = System.currentTimeMillis();
		
		// Show a status message
		System.out.println("[SimpleSurvivalGames] Loading arenas...");
		
		// Load the arenas data file
		YamlConfiguration c = new YamlConfiguration();
		try {
			c.load(f);
		} catch (FileNotFoundException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading arenas data file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading arenas data file!");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading arenas data file!");
			e.printStackTrace();
		}

		// Create a list for the new anenas
		List<Arena> newArenas = new ArrayList<Arena>();
		
		// Get all the arenas from the config file
		Set<String> arenas = c.getConfigurationSection("arenas").getKeys(false);
		for(String arenaName : arenas) {
			
			Arena arena = new Arena(arenaName);
			
			arena.setEnabled(c.getBoolean("arenas." + arenaName + ".enabled", true));
			arena.setDisplayName(c.getString("arenas." + arenaName + ".displayName", ""));
			arena.setEditMode(c.getBoolean("arenas." + arenaName + ".editMode", false));
			arena.setMinPlayerCount(c.getInt("arenas." + arenaName + ".minPlayers", 2));
			arena.setMaxPlayers(c.getInt("arenas." + arenaName + ".maxPlayers", -1));
			
			// Get arena cuboid locations
			SSGLocation arenaCorner1 = new SSGLocation(c.getString("arenas." + arenaName + ".cuboids.arena.c1", ""));
			SSGLocation arenaCorner2 = new SSGLocation(c.getString("arenas." + arenaName + ".cuboids.arena.c2", ""));
			arena.setArenaCuboid(new ArenaCuboid(arenaCorner1, arenaCorner2));;
			
			// Get the spectators cuboid
			if(c.contains("arenas." + arenaName + ".cuboids.spectator")) {
				SSGLocation specCorner1 = new SSGLocation(c.getString("arenas." + arenaName + ".cuboids.spectator.c1", ""));
				SSGLocation specCorner2 = new SSGLocation(c.getString("arenas." + arenaName + ".cuboids.arena.c2", ""));
				arena.setSpectatorsCuboid(new ArenaCuboid(specCorner1, specCorner2));
			}
			
			// Add the arena spawns
			if(c.contains("arenas." + arenaName + ".spawns.arena")) {
				Set<String> arenaSpawns = c.getConfigurationSection("arenas." + arenaName + ".spawns.arena").getKeys(false);
				for(String spawnIndex : arenaSpawns)
					arena.getSpawnManager().addSpawn(new SSGLocation(
							 c.getString("arenas." + arenaName + ".spawns.arena." + spawnIndex + ".loc", "")
							 ));
			}
			
			// Set the spectator spawn
			if(c.contains("arenas." + arenaName + ".spawns.spectator"))
				arena.setSpectatorsSpawn(new SSGLocation(c.getString("arenas." + arenaName + ".spawns.spectator.loc", "")));

			arena.setMayLeaveArenaCuboid(c.getBoolean("arenas." + arenaName + ".mayLeaveArenaCuboid", false));
			arena.setMayLeaveSpectatorsCuboid(c.getBoolean("arenas." + arenaName + ".mayLeaveSpectatorsCuboid", false));
			arena.setMinVotesPercentage(c.getDouble("arenas." + arenaName + ".minVotingPercentage", 50));
			arena.setGracePeriodLength(c.getInt("arenas." + arenaName + ".gracePeriodLength", 0));
			arena.setAllowedCommands((List<String>) c.getList("arenas." + arenaName + ".commands.allowed", new ArrayList<String>()));
			
			// Containers
			if(c.contains("arenas." + arenaName + ".containers")) {
				arena.getContainerManager().clear();
				Set<String> containers = c.getConfigurationSection("arenas." + arenaName + ".containers").getKeys(false);
				for(String containerIndex : containers) {
					String containerType = c.getString("arenas." + arenaName + ".containers." + containerIndex + ".type", "");
					if(containerType.equalsIgnoreCase("RANDOM")) {
						String locString = c.getString("arenas." + arenaName + ".containers." + containerIndex + ".loation", "");
						SSGLocation loc = new SSGLocation(locString);
						arena.getContainerManager().addContainer(new ArenaRandomContainer(arena, loc));
						
					} else if(containerType.equalsIgnoreCase("STATIC")) {
						String locString = c.getString("arenas." + arenaName + ".containers." + containerIndex + ".loation", "");
						SSGLocation loc = new SSGLocation(locString);
						
						List<ItemStack> defContents = new ArrayList<ItemStack>();
						Set<String> containerContents = c.getConfigurationSection("arenas." + arenaName + ".containers." + containerIndex + ".defContents").getKeys(false);
						for(String itemStackIndex : containerContents) {
							int index = Integer.parseInt(itemStackIndex);
							if(c.getString("arenas." + arenaName + ".containers." + containerIndex + ".defContents." + String.valueOf(index), "").equalsIgnoreCase("null"))
								defContents.add(null);
							else
								defContents.add(c.getItemStack("arenas." + arenaName + ".containers." + containerIndex + ".defContents." + String.valueOf(index), null));
						}
						arena.getContainerManager().addContainer(new ArenaStaticContainer(arena, loc, defContents.toArray(new ItemStack[]{})));
					}
				}
			}
			
			// Load the container sets
			loadContainerSets(arena);
			
			// Add the arena to the list
			newArenas.add(arena);
		}
		
		// Replace the arenas list with the new loaded arenas list
		this.arenas = newArenas;
		
		// Calculate the load duration
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message
		System.out.println("[SimpleSurvivalGames] " + (this.arenas.size()==1 ? "1 arena" : String.valueOf(this.arenas.size()) + " arenas") +  " loaded, took " + String.valueOf(duration) + "ms!");
	
		// Return the loading duration
		return duration;
	}
	
	public long loadContainerSets(Arena arena) {
		return loadContainerSets(arena, new File(SimpleSurvivalGames.instance.getDataFolder(), "container_sets.yml"));
	}
	
	public long loadContainerSets(Arena arena, File f) {
		// Check if the shops file exists
		if(!f.exists()) {
			System.out.println("[SimpleSurvivalGames] Container sets file doesn't exist!");
			return 0;
		}
		
		long t = System.currentTimeMillis();
		
		// Show a status message
		System.out.println("[SimpleSurvivalGames] Loading container sets...");
		
		// Load the arenas data file
		YamlConfiguration c = new YamlConfiguration();
		try {
			c.load(f);
		} catch (FileNotFoundException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading container sets file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading container sets file!");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			System.out.println("[SimpleSurvivalGames] Error while loading container sets file!");
			e.printStackTrace();
		}
		
		// Make sure there's anything setup for this arena
		if(!c.contains("ContainerSets.Arenas." + arena.getName()))
			return 0;
		
		// Create a list for the new anenas
		List<ArenaContainerSet> sets = new ArrayList<ArenaContainerSet>();
		
		// Get all the arenas from the config file
		Set<String> setNames = c.getConfigurationSection("ContainerSets.Arenas." + arena.getName() + ".Sets").getKeys(false);
		for(String setName : setNames) {
			
			// Get the set chance
			int setChance = c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Chance", 0);
			int setFillingPercentage = c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".FillingPercentage", 0);

			List<ArenaContainerItem> setItems = new ArrayList<ArenaContainerItem>();
			Set<String> itemNames = c.getConfigurationSection("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items").getKeys(false);
			for(String itemName : itemNames) {
				int itemChance = c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Chance", 0);
				int itemTypeId = c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".TypeId", 0);
				
				ArenaContainerItem item = new ArenaContainerItem(itemTypeId);
				item.setChance(itemChance);
				
				if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemData"))
					item.setData((byte) c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemData", 0));
				
				if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemDurability"))
					item.setDurability((short) c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemDurability", 0));
				else {
					if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMinDurability"))
						item.setMinDurability((short) c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMinDurability", 0));
					if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMaxDurability"))
						item.setMaxDurability((short) c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMaxDurability", 0));
				}
				
				if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemName"))
					item.setItemName(c.getString("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemName", ""));
				
				if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemAmount"))
					item.setAmount(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemAmount", 1));
				else {
					if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMinAmount"))
						item.setMinAmount(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMinAmount", 0));
					if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMaxAmount"))
						item.setMaxAmount(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".ItemMaxAmount", 0));
				}
				
				// Enchantments
				if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments")) {
					List<ArenaContainerItemEnchantment> enchs = new ArrayList<ArenaContainerItemEnchantment>();
					Set<String> enchantmentNames = c.getConfigurationSection("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments").getKeys(false);
					for(String enchantmentName : enchantmentNames) {
						int enchChance = c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".Chance", 0);
						String enchName = c.getString("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".EnchantmentName", "");
						
						if(enchName == "") {
							System.out.println("[SimpleSurvivalGames] [ERROR] Unknown enchantment: " + enchName);
			    			continue;
			    		}
						
						Enchantment enchType = Enchantment.getByName(enchName);
			    		
			    		// The enchantment may not be null
			    		if(enchName == null) {
							System.out.println("[SimpleSurvivalGames] [ERROR] Unknown enchantment: " + enchName);
			    			continue;
			    		}
						
						ArenaContainerItemEnchantment ench = new ArenaContainerItemEnchantment(enchChance, enchType);
						
						if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".Level"))
							ench.setLevel(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".Level", 1));
						else {
							if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".MinLevel"))
								ench.setMinLevel(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".MinLevel", 1));
							if(c.contains("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".MaxLevel"))
								ench.setMaxLevel(c.getInt("ContainerSets.Arenas." + arena.getName() + ".Sets." + setName + ".Items." + itemName + ".Enchantments." + enchantmentName + ".MaxLevel", 1));
						}
						enchs.add(ench);
					}
					item.addAllEnchantments(enchs);
				}
				
				// Add the item to the list
				setItems.add(item);
			}
			
			ArenaContainerSet set = new ArenaContainerSet(setChance);
			set.addAllItems(setItems);
			set.setFillingPercentage(setFillingPercentage);
			
			sets.add(set);
		}

		// Add the sets
		arena.getContainerManager().getContentManager().clear();
		arena.getContainerManager().getContentManager().addAllSets(sets);
	
		// Calculate the load duration
		long duration = System.currentTimeMillis() - t;
		
		// Show a status message
		// TODO: Do this for sets amount, not for arenas amount
		System.out.println("[SimpleSurvivalGames] " + (sets.size()==1 ? "1 container set" : String.valueOf(sets.size()) + " container sets") +  " loaded, took " + String.valueOf(duration) + "ms!");
	
		// Return the loading duration
		return duration;
	}
}
