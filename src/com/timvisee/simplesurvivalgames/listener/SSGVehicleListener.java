package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;

public class SSGVehicleListener implements Listener {
	
	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		Vehicle v = event.getVehicle();
		Entity attacker = event.getAttacker();
		
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
	
		for(Arena a : am.getArenas()) {
			if(a.isArenaCuboidSet()) {
				if(a.getArenaCuboid().isInsideCuboid(v.getLocation())) {
					if(!a.isInEditMode()) {
						event.setCancelled(true);
						if(attacker instanceof Player) {
							Player p = (Player) attacker;
							p.sendMessage(ChatColor.DARK_RED + "You may not destroy this vehicle!");
						}
					}
				}
			}
		}
	}
}
