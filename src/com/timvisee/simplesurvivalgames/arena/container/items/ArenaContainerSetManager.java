package com.timvisee.simplesurvivalgames.arena.container.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.timvisee.simplesurvivalgames.arena.Arena;

public class ArenaContainerSetManager {
	
	Arena arena;
	private List<ArenaContainerSet> sets = new ArrayList<ArenaContainerSet>();
	
	/**
	 * Constructor
	 */
	public ArenaContainerSetManager(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Constructor
	 * @param sets item sets
	 */
	public ArenaContainerSetManager(Arena arena, List<ArenaContainerSet> sets) {
		this.arena = arena;
		if(sets != null)
			this.sets = sets;
	}
	
	/**
	 * Get the arena
	 * @return arena
	 */
	public Arena getArena() {
		return this.arena;
	}
	
	/**
	 * Add a set
	 * @param set set to add
	 */
	public void addSet(ArenaContainerSet set) {
		if(set != null)
			this.sets.add(set);
	}

	/**
	 * Add sets
	 * @param sets sets to add
	 */
	public void addAllSets(List<ArenaContainerSet> sets) {
		for(ArenaContainerSet set : sets)
			addSet(set);
	}
	
	/**
	 * Get the sets
	 * @return sets
	 */
	public List<ArenaContainerSet> getSets() {
		return this.sets;
	}
	
	/**
	 * Does this manager contains any set
	 * @return false if not
	 */
	public boolean containsSet() {
		return (this.sets.size() > 0);
	}
	
	/**
	 * Get the sets count
	 * @return sets count
	 */
	public int getSetCount() {
		return this.sets.size();
	}
	
	/**
	 * Get the item count for this set
	 * @return item count
	 */
	public int getItemCount() {
		int count = 0;
		for(ArenaContainerSet set : this.sets)
			count += set.getItemCount();
		return count;
	}
	
	/**
	 * Clear all sets
	 */
	public void clear() {
		this.sets.clear();
	}
	
	/**
	 * Fill an inventory holder with the item sets stored
	 * @param invHolder
	 * @return new items
	 */
	public ItemStack[] fillInventoryHolder(InventoryHolder invHolder) {
		if(invHolder == null)
			return new ItemStack[]{};
		
		// Define a Random object and get the inventory
		Random rand = new Random();
		Inventory inv = invHolder.getInventory();
		int space = inv.getSize();
		
		// Clear the inventory holder contents
		inv.clear();
		
		// Make sure there are any items
		if(getItemCount() == 0)
			return new ItemStack[]{};
		
		// Pick a random set
		int randomSetNumber = rand.nextInt(getSetsChanceSum()) + 1;
		int currentChanceIndex = 0;
		ArenaContainerSet set = null;
		for(ArenaContainerSet curSet : this.sets) {
			currentChanceIndex += curSet.getChance();
			
			if(currentChanceIndex >= randomSetNumber) {
				set = curSet;
				break;
			}
		}
		
		// Return null if no set was/could be) picked
		if(set == null)
			return new ItemStack[]{};
		
		// Define the new inventory contents
		List<ItemStack> contents = new ArrayList<ItemStack>();
		
		// Add items to the contents list
		for(int i = 0; i < space; i++) {
			if(rand.nextInt(100) < set.getFillingPercentage())
				if(set.getItemCount() > 0)
					contents.add(set.getItemToAdd().getItem());
				else
					contents.add(null);
			else
				contents.add(null);
		}
		
		// Apply the contents to the inventory holder
		inv.setContents(contents.toArray(new ItemStack[]{}));
		
		// Return the new contents
		return contents.toArray(new ItemStack[]{});
	}
	
	public int getSetsChanceSum() {
		int chancesSum = 0;
		for(ArenaContainerSet set : this.sets)
			chancesSum += set.getChance();
		return chancesSum;
	}
}