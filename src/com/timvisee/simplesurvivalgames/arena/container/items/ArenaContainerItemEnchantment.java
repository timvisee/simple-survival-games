package com.timvisee.simplesurvivalgames.arena.container.items;

import java.util.Random;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArenaContainerItemEnchantment {
	
	private int chance = 100;
	private Enchantment ench = null;
	private int minLvl = 1;
	private int maxLvl = 1;
	
	/**
	 * Constructor
	 * @param chance enchantment chance
	 */
	public ArenaContainerItemEnchantment(int chance, Enchantment ench) {
		this.chance = chance;
		this.ench = ench;
	}
	
	/**
	 * Get the enchantment chance
	 * @return
	 */
	public int getChance() {
		return this.chance;
	}
	
	/**
	 * Set the enchantment chance
	 * @param chance new chance
	 */
	public void setChance(int chance) {
		// Make sure the chance won't be less than 0
		this.chance = Math.max(chance, 0);
	}
	
	/**
	 * Get the enchantment
	 * @return enchantment
	 */
	public Enchantment getEnchantment() {
		return this.ench;
	}
	
	/**
	 * Set the enchantment
	 * @param ench enchantment
	 */
	public void setEnchantment(Enchantment ench) {
		this.ench = ench;
	}
	
	/**
	 * Get the min level
	 * @return min level
	 */
	public int getMinLevel() {
		return this.minLvl;
	}
	
	/**
	 * Set the min level
	 * @param minLvl min level
	 */
	public void setMinLevel(int minLvl) {
		this.minLvl = minLvl;
		if(this.maxLvl < this.minLvl)
			this.maxLvl = this.minLvl;
	}
	
	/**
	 * Get the max level
	 * @return max level
	 */
	public int getMaxLevel() {
		return this.maxLvl;
	}
	
	/**
	 * Set max level
	 * @param maxLvl max level
	 */
	public void setMaxLevel(int maxLvl) {
		this.maxLvl = maxLvl;
		if(this.minLvl > this.maxLvl)
			this.minLvl = this.maxLvl;
	}
	
	/**
	 * Get the level
	 * @return level
	 */
	public int getLevel() {
		// If the min/max level equal to each other, return the min
		if(this.minLvl == this.maxLvl)
			return this.minLvl;
		
		// If the min/max level are different choose a random number between
		Random rand = new Random();
		int delta = this.maxLvl - this.minLvl;
		return this.minLvl + rand.nextInt(delta + 1);
	}
	
	/**
	 * Set the level
	 * @param lvl level
	 */
	public void setLevel(int lvl) {
		this.minLvl = lvl;
		this.maxLvl = lvl;
	}
	
	/**
	 * Add the enchantment to an item
	 * @param item item to apply the enchantment to
	 */
	public void addEnchantment(ItemStack item) {
		addEnchantment(item, true);
	}
	
	/**
	 * Add the enchantment to an item
	 * @param item item to apply the enchantment to
	 * @param unsafe true to force the enchantment to be added to the item
	 */
	public void addEnchantment(ItemStack item, boolean unsafe) {
		if(unsafe)
			item.addUnsafeEnchantment(ench, getLevel());
		else
			item.addEnchantment(ench, getLevel());
	}
}
