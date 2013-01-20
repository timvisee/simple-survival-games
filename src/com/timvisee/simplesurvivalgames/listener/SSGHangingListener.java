package com.timvisee.simplesurvivalgames.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

import com.timvisee.simplesurvivalgames.SimpleSurvivalGames;
import com.timvisee.simplesurvivalgames.arena.Arena;
import com.timvisee.simplesurvivalgames.arena.ArenaManager;

public class SSGHangingListener implements Listener {
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent event) {
		Entity hanging = event.getEntity();
		
		ArenaManager am = SimpleSurvivalGames.instance.getArenaManager();
	
		for(Arena a : am.getArenas())
			if(a.isArenaCuboidSet())
				if(a.getArenaCuboid().isInsideCuboid(hanging.getLocation()))
					if(!a.isInEditMode())
						event.setCancelled(true);
	}
}
