package me.mastercapexd.guiitemgenerator.randomable;

import org.bukkit.inventory.ItemStack;

public final class InventoryItem {

	private final ItemStack stack;
	private final int chance;

	public InventoryItem(ItemStack stack, int chance) {
		this.stack = stack;
		this.chance = chance;
	}

	public ItemStack getItemStack() {
		return stack;
	}

	public int getChance() {
		return chance;
	}
}