package com.timvisee.simplesurvivalgames.arena.container.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaContainerSet {
	
	private int chance = 0;
	private int fillingPercentage = 0;
	private List<ArenaContainerItem> items = new ArrayList<ArenaContainerItem>();
	
	/**
	 * Constructor
	 */
	public ArenaContainerSet(int chance) {
		setChance(chance);
	}
	
	/**
	 * Constructor
	 * @param items items
	 */
	public ArenaContainerSet(List<ArenaContainerItem> items) {
		if(items != null)
			this.items = items;
	}
	
	/**
	 * Get the set chance
	 * @return set chance
	 */
	public int getChance() {
		return this.chance;
	}
	
	/**
	 * Set the set chance
	 * @param chance new set chance, must be 0 or higher
	 */
	public void setChance(int chance) {
		// Make sure the chance wont get bellow 0
		this.chance = Math.max(chance, 0);
	}
	
	/**
	 * Get the filling percentage
	 * @return filling percentage
	 */
	public int getFillingPercentage() {
		return this.fillingPercentage;
	}
	
	/**
	 * Set the filling percentage
	 * @param fillingPercentage filling percentage
	 */
	public void setFillingPercentage(int fillingPercentage) {
		this.fillingPercentage = Math.max(fillingPercentage, 0);
	}
	
	/**
	 * Add an item
	 * @param item item to add
	 */
	public void addItem(ArenaContainerItem item) {
		this.items.add(item);
	}
	
	/**
	 * Add an item
	 * @param item item to add
	 */
	public void addAllItems(List<ArenaContainerItem> items) {
		for(ArenaContainerItem item : items)
			addItem(item);
	}
	
	/**
	 * Get all the items from this set
	 * @return items from this set
	 */
	public List<ArenaContainerItem> getItems() {
		return this.items;
	}
	
	/**
	 * Get a random item from the list
	 * @return any random item from the list
	 */
	public ArenaContainerItem getRandomItem() {
		// The list must contain any item
		if(this.items.size() < 1)
			return null;
		
		// Return a random item from the list
		Random rand = new Random();
		return this.items.get(rand.nextInt(this.items.size()));
	}
	
	/**
	 * Get random items from the list
	 * @param amount the amount to get
	 * @return random items from the list
	 */
	public List<ArenaContainerItem> getRandomItems(int amount) {
		List<ArenaContainerItem> randItems = new ArrayList<ArenaContainerItem>();
		
		// Make sure the amount is higher than 0
		if(amount < 1)
			return randItems;
		
		// Define a random object
		Random rand = new Random();
		
		// Get random items
		while(randItems.size() < amount)
			randItems.add(this.items.get(rand.nextInt(this.items.size())));
		
		// Return the random items
		return randItems;
	}
	
	/**
	 * Does this set contain any items
	 * @return true if this set contains any item
	 */
	public boolean containsItems() {
		return (this.items.size() > 0);
	}
	
	/**
	 * Get the items count
	 * @return items count
	 */
	public int getItemCount() {
		return this.items.size();
	}
	
	/**
	 * Get the item to add to a chest (calculated from item chances)
	 * @return item to add (calculated from item chances)
	 */
	public ArenaContainerItem getItemToAdd() {
		int chancesSum = getChancesSum();
		
		// Make sure there's any item with a chance larger than 0, if not return null
		if(chancesSum == 0)
			return null;
		
		// Define a random object
		Random rand = new Random();
		
		// Pick a random item
		int randomItemNumber = rand.nextInt(chancesSum) + 1;
		int currentChanceIndex = 0;
		ArenaContainerItem curItem = null;
		for(ArenaContainerItem item : this.items) {
			currentChanceIndex += Math.max(item.getChance(), 0);
			
			if(currentChanceIndex >= randomItemNumber) {
				curItem = item;
				break;
			}
		}
		
		// Any item must be picked, if not return null
		if(curItem == null)
			return null;
		
		// Return the picked item
		return curItem;
	}
	
	/**
	 * Get the chances sum
	 * @return sum of all chances of all items together
	 */
	public int getChancesSum() {
		int chancesSum = 0;
		for(ArenaContainerItem item : this.items)
			chancesSum += item.getChance();
		return chancesSum;
	}
}
