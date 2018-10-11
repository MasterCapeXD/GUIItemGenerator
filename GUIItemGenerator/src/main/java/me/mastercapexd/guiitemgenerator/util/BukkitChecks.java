package me.mastercapexd.guiitemgenerator.util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitChecks {

	public static boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	public static boolean isPlayer(CommandSender sender, String message) {
		boolean value = isPlayer(sender);
		if (!value)
			sender.sendMessage(message);
		return value;
	}

	public static boolean isInWorld(World world, Player player) {
		return player.getWorld().equals(world);
	}

	public static boolean isBodyArmor(ItemStack stack) {
		return stack.getType().name().endsWith("CHESTPLATE") || stack.getType().name().endsWith("LEGGINGS");
	}

	public static boolean isBoots(ItemStack stack) {
		return stack.getType().name().endsWith("BOOTS");
	}

	public static boolean isHelmet(ItemStack stack) {
		return stack.getType().name().endsWith("HELMET");
	}

	public static boolean isSword(ItemStack stack) {
		return stack.getType().name().endsWith("SWORD");
	}

	public static boolean isAxe(ItemStack stack) {
		return stack.getType().name().endsWith("AXE");
	}

	public static boolean isPickaxe(ItemStack stack) {
		return stack.getType().name().endsWith("PICKAXE");
	}

	public static boolean isHoe(ItemStack stack) {
		return stack.getType().name().endsWith("HOE");
	}

	public static boolean isShovel(ItemStack stack) {
		return stack.getType().name().endsWith("SHOVEL") || stack.getType().name().endsWith("SPADE");
	}

	public static boolean isBow(ItemStack stack) {
		return stack.getType().name().endsWith("BOW");
	}

	public static boolean isContainer(Material material) {
		return material.name().contains("CHEST") || material.name().contains("SHULKER_BOX")
				|| material.name().contains("FURANCE") || material.name().contains("DROPPER")
				|| material.name().contains("DISPENSER") || material.name().contains("HOPPER");
	}
}