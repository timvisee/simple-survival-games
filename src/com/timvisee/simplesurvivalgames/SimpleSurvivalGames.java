package com.timvisee.simplesurvivalgames;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.timvisee.simplesurvivalgames.arena.ArenaManager;
import com.timvisee.simplesurvivalgames.command.CommandHandler;
import com.timvisee.simplesurvivalgames.listener.SSGBlockListener;
import com.timvisee.simplesurvivalgames.listener.SSGEntityListener;
import com.timvisee.simplesurvivalgames.listener.SSGHangingListener;
import com.timvisee.simplesurvivalgames.listener.SSGPlayerListener;
import com.timvisee.simplesurvivalgames.listener.SSGVehicleListener;
import com.timvisee.simplesurvivalgames.manager.PermissionsManager;

public class SimpleSurvivalGames extends JavaPlugin {

	// The logger
	private Logger log;
	
	// Dragon Realms Core static instance
	public static SimpleSurvivalGames instance;
	
	// Listeners
	private final SSGBlockListener blockListener = new SSGBlockListener();
	private final SSGEntityListener entityListener = new SSGEntityListener();
	private final SSGHangingListener hangingListener = new SSGHangingListener();
	private final SSGPlayerListener playerListener = new SSGPlayerListener();
	private final SSGVehicleListener vehicleListener = new SSGVehicleListener();
	
	// Managers and Handlers
	private ArenaManager am;
	private PermissionsManager pm;
	private SSGPlayerManager playerManager = new SSGPlayerManager();
	
	public SimpleSurvivalGames() {
		// Define the Dragon Realms Core static instance variable
		instance = this;
	}
	
	public void onEnable() {
		long t = System.currentTimeMillis();
		
		// Get the Minecraft logger
		this.log = Logger.getLogger("Minecraft");
		
		// Define the plugin manager
		PluginManager pm = getServer().getPluginManager();
		
		// Setup managers and handlers
		setupArenaManager();
	    setupPermissionsManager();
		
		// Load all arenas
		getArenaManager().load();
		
		// Register event listeners
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.hangingListener, this);
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.vehicleListener, this);
		
		// Calculate the load duration
		long duration = System.currentTimeMillis() - t;
		
		// Show status message
		log.info("[SimpleSurvivalGames] Simple Survival Games v" + getVersion() + " enabled, took " + String.valueOf(duration) + " ms!");
		
		// Create a temporary arena for testing and debugin purposes
		/*getArenaManager().clear();
		Arena a = new Arena("arena1");
		a.setArenaCuboid(new ArenaCuboid(new SSGLocation("world", -20, 40, -20), new SSGLocation("world", 20, 80, 20)));
		//a.startRound();
		a.setState(ArenaState.LOBBY);
		//a.setState(ArenaState.PLAYING);
		a.getSpawnManager().addSpawn("1", new SSGLocation("world", 0, 62, 0));
		a.getSpawnManager().addSpawn("2", new SSGLocation("world", 0, 62, 5));
		a.getSpawnManager().addSpawn("3", new SSGLocation("world", 3, 62, 2));
		a.getContainerManager().addContainer(new ArenaRandomContainer(new SSGLocation("world", 0, 62, 2)));
		a.getContainerManager().addContainer(new ArenaRandomContainer(new SSGLocation("world", 0, 62, 7)));
		a.getContainerManager().addContainer(new ArenaStaticContainer(new SSGLocation("world", 3, 62, 4), new ItemStack[]{
			new ItemStack(Material.BEDROCK), null, new ItemStack(Material.STONE, 3)}));
		a.setSpectatorsSpawn(new SSGLocation("world", 0, 66, 0));
		a.setMinPlayerCount(1);
		a.setMaxPlayers(100);
		getArenaManager().addArena(a);*/
	}
	
	public void onDisable() {
		// Stop all arenas
		getArenaManager().stopAllArenas();
		
		// Save all arenas and containers
		getArenaManager().save();
		//getArenaManager().saveContainers();
		
		// Plugin disabled, show status message
		log.info("[SimpleSurvivalGames] Simple Survival Games Disabled");
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	/**
	 * Setup the arena manager
	 */
	public void setupArenaManager() {
		// Setup the arena manager
		this.am = new ArenaManager();
	}
	
	/**
	 * Get the arena manager
	 * @return
	 */
	public ArenaManager getArenaManager() {
		return this.am;
	}
	
	/**
	 * Setup the permissions manager
	 */
	public void setupPermissionsManager() {
		// Setup the permissions manager
		this.pm = new PermissionsManager(this.getServer(), this);
		this.pm.setup();
	}
	
	/**
	 * Get the permissions manager
	 * @return permissions manager
	 */
	public PermissionsManager getPermissionsManager() {
		return this.pm;
	}
	
	/**
	 * Setup the player manager
	 */
	public void setupPlayerManager() {
		// Setup the arena manager
		this.playerManager = new SSGPlayerManager();
	}
	
	public SSGPlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		// Run the command trough the command handler
		CommandHandler ch = new CommandHandler();
		return ch.onCommand(sender, cmd, commandLabel, args);
	}
}
