package me.mastercapexd.guiitemgenerator.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import me.mastercapexd.guiitemgenerator.refillable.ContentType;

public interface GuiItemGenerator {

	void registerInventory(Location location, Inventory inventory, String type);

	void registerInventory(Location location, Inventory inventory, ContentType type);

	void registerInventory(Block block, Inventory inventory, String type);

	void registerInventory(Block block, Inventory inventory, ContentType type);

	void removeInventory(Location location);

	void removeInventory(Block block);

	boolean containsInventory(Location location);

	boolean containsInventory(Block block);

	void refill(Location location);

	void refill(Block block);

	void spawn(Location where, Material material, ContentType type);
}