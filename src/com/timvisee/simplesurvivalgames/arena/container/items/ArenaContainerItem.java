package com.timvisee.simplesurvivalgames.arena.container.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ArenaContainerItem {
	
	private int chance = 0;
	private int typeId = 0;
	private byte data = 0;
	private short minDur = 0;
	private short maxDur = 0;
	private String itemName = "";
	private int minAmount = 1;
	private int maxAmount = 1;
	private List<ArenaContainerItemEnchantment> enchs = new ArrayList<ArenaContainerItemEnchantment>();
	
	/**
	 * Constructor
	 * @param typeId type id
	 */
	public ArenaContainerItem(int typeId) {
		setTypeId(typeId);
	}
	
	/**
	 * Get the chance
	 * @return chance
	 */
	public int getChance() {
		return this.chance;
	}
	
	/**
	 * Set the chance
	 * @param chance new chance
	 */
	public void setChance(int chance) {
		// The chance has to be 0 or higher
		this.chance = Math.max(chance, 0);
	}
	
	/**
	 * Get the type id
	 * @return
	 */
	public int getTypeId() {
		return this.typeId;
	}
	
	/**
	 * Set the type id
	 * @param typeId
	 */
	public void setTypeId(int typeId) {
		// The item id may not be negative
		this.typeId = Math.max(typeId, 0);
	}
	
	/**
	 * Get the item data
	 * @return item data
	 */
	public byte getData() {
		return this.data;
	}
	
	/**
	 * Set the item data
	 * @param data new item data
	 */
	public void setData(byte data) {
		this.data = data;
	}
	
	/**
	 * Get the min item durability
	 * @return min item durability
	 */
	public int getMinDurability() {
		return this.minDur;
	}
	
	/**
	 * Set the min item durability
	 * @param minDurability min item durability
	 */
	public void setMinDurability(short minDurability) {
		this.minDur = minDurability;
		if(this.maxDur < this.minDur)
			this.maxDur = this.minDur;
	}
	
	/**
	 * Get the max item durability
	 * @return max item durability
	 */
	public int getMaxDurability() {
		return this.maxDur;
	}
	
	/**
	 * Set the max item durability
	 * @param maxDurability max item durability
	 */
	public void setMaxDurability(short maxDurability) {
		this.maxDur = maxDurability;
		if(this.minDur > this.maxDur)
			this.minDur = this.maxDur;
	}
	
	/**
	 * Get the item durability
	 * @return item durability
	 */
	public short getDurability() {
		// If the min/max durability equal to each other, return the min
		if(this.minDur == this.maxDur)
			return this.minDur;
		
		// If the min/max durability are different choose a random number between
		Random rand = new Random();
		short delta = (short) (this.maxDur - this.minDur);
		return (short) (this.minDur + rand.nextInt(delta) + 1);
	}
	
	/**
	 * Set the item durability
	 * @param durability item durability
	 */
	public void setDurability(short durability) {
		this.minDur = durability;
		this.maxDur = durability;
	}
	
	/**
	 * Has this item an custom item name
	 * @return false if not
	 */
	public boolean hasCustomItemName() {
		return (!this.itemName.trim().equals(""));
	}
	
	/**
	 * Get the custom item name
	 * @return
	 */
	public String getItemName() {
		// TODO: Return regular name if no custom item name has been set
		return this.itemName;
	}
	
	/**
	 * Set the item name of this item
	 * @param customItemName new custom item name
	 */
	public void setItemName(String customItemName) {
		this.itemName = customItemName;
	}
	
	/**
	 * Reset the item name to it's default
	 */
	public void resetItemName() {
		this.itemName = "";
	}
	
	/**
	 * Get the min amount
	 * @return min amount
	 */
	public int getMinAmount() {
		return this.minAmount;
	}
	
	/**
	 * Set the min amount
	 * @param minAmount min amount
	 */
	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
		if(this.maxAmount < this.minAmount)
			this.maxAmount = this.minAmount;
	}
	
	/**
	 * get the max item amount
	 * @return max item amount
	 */
	public int getMaxAmount() {
		return this.maxAmount;
	}
	
	/**
	 * Set the max item amount
	 * @param maxAmount max item amount
	 */
	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
		if(this.minAmount > this.maxAmount)
			this.minAmount = this.maxAmount;
	}
	
	/**
	 * Get the item amount
	 * @return item amount
	 */
	public int getAmount() {
		// If the min/max amount equal to each other, return the min
		if(this.minAmount == this.maxAmount)
			return this.minAmount;
		
		// If the min/max amount are different choose a random number between
		Random rand = new Random();
		int delta = this.maxAmount - this.minAmount;
		return this.minAmount + rand.nextInt(delta + 1);
	}
	
	/**
	 * Set the item amount
	 * @param amount item amount
	 */
	public void setAmount(int amount) {
		this.minAmount = amount;
		this.maxAmount = amount;
	}
	
	public void addEnchantment(ArenaContainerItemEnchantment ench) {
		this.enchs.add(ench);
	}
	
	public void addAllEnchantments(List<ArenaContainerItemEnchantment> enchs) {
		this.enchs.addAll(enchs);
	}
	
	/**
	 * Add an enchantment to the possible enchantment list
	 * @param chance chance
	 * @param enchType enchantment
	 * @param minLvl min level
	 * @param maxLvl max level
	 * @return new enchantent as ArenaContainerItemEnchantment object
	 */
	public ArenaContainerItemEnchantment addEnchantment(int chance, Enchantment enchType, int minLvl, int maxLvl) {
		ArenaContainerItemEnchantment ench = new ArenaContainerItemEnchantment(chance, enchType);
		ench.setMinLevel(minLvl);
		ench.setMaxLevel(maxLvl);
		this.enchs.add(ench);
		return ench;
	}
	
	/**
	 * Get the enchantments
	 * @return enchantments
	 */
	public List<ArenaContainerItemEnchantment> getEnchantments() {
		return this.enchs;
	}
	
	/**
	 * Get a list of enchantments to apply
	 * @return enchantments to apply
	 */
	public List<ArenaContainerItemEnchantment> getEnchantmentsToApply() {
		if(this.enchs.size() == 0)
			return new ArrayList<ArenaContainerItemEnchantment>();
		
		// Define a random object and save all the enchantments to apply
		Random rand = new Random();
		List<ArenaContainerItemEnchantment> enchToApply = new ArrayList<ArenaContainerItemEnchantment>();
		
		// Loop through every enchantment and check if it should be added
		for(ArenaContainerItemEnchantment ench : this.enchs) {
			if(ench.getChance() == 0)
				continue;
			
			// Calculate the chance
			if(rand.nextInt(100) < ench.getChance())
				enchToApply.add(ench);
		}
		
		// Return the enchantments to apply
		return enchToApply;
	}
	
	/**
	 * Get the enchantment count
	 * @return enchantment count
	 */
	public int getEnchantmentCount() {
		return this.enchs.size();
	}
	
	/**
	 * Remove enchantments of a specific type
	 * @param ench enchantment type to remove
	 */
	public void removeEnchantment(Enchantment ench) {
		List<ArenaContainerItemEnchantment> remove = new ArrayList<ArenaContainerItemEnchantment>();
		for(ArenaContainerItemEnchantment entry : this.enchs)
			if(entry.getEnchantment().equals(ench))
				remove.add(entry);
		this.enchs.removeAll(remove);
	}
	
	/**
	 * Remove an enchantment
	 * @param ench enchantment to remove
	 */
	public void removeEnchantment(ArenaContainerItemEnchantment ench) {
		this.enchs.remove(ench);
	}
	
	/**
	 * Clear the list of possible enchantments
	 */
	public void clearEnchantments() {
		this.enchs.clear();
	}
	
	/**
	 * Get the item as ItemStack
	 * @return the item as ItemStack
	 */
	public ItemStack getItem() {
		return getItem(true, true, true, true, true);
	}
	
	/**
	 * Get the item as ItemStack
	 * @param applyData apply the item data
	 * @param applyDurability apply the item durability
	 * @param applyItemName apply the item name
	 * @param applyAmount apply the item amount
	 * @return the item as ItemStack
	 */
	public ItemStack getItem(boolean applyData, boolean applyDurability, boolean applyItemName, boolean applyAmount, boolean applyEnchantments) {
		ItemStack item = new ItemStack(this.typeId);
		
		// Make sure the item stack is valid
		if(item == null)
			return null;
		
		// Set the item durability
		if(applyDurability)
			item.setDurability(getDurability());
		
		// Set the item name
		// TODO: Apply item names
		if(applyItemName && hasCustomItemName()) { }
		
		// Set the item amount
		if(applyAmount)
			item.setAmount(Math.min(getAmount(), item.getMaxStackSize()));
		
		// Set the item enchantments
		if(applyEnchantments) {
			List<ArenaContainerItemEnchantment> enchToApply = getEnchantmentsToApply();
			for(ArenaContainerItemEnchantment ench : enchToApply)
				ench.addEnchantment(item, true);
		}
		
		
		// TODO: Move this up
		// Set the item data
		if(applyData)
			item.setData(new MaterialData(item.getType(), this.data));
		
		// Return the item
		return item;
	}
}
