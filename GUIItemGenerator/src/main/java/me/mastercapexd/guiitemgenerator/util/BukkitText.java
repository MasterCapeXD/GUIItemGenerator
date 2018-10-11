package me.mastercapexd.guiitemgenerator.util;

import java.util.List;

import org.bukkit.ChatColor;

import com.google.common.collect.Lists;

public class BukkitText {

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static List<String> colorize(List<String> listOfStrings) {
		List<String> list = Lists.newArrayList();
		for (String str : listOfStrings) {
			list.add(colorize(str));
		}
		return list;
	}

	public static String decolorize(String input) {
		return ChatColor.stripColor(input);
	}

	public static List<String> decolorize(List<String> listOfStrings) {
		List<String> list = Lists.newArrayList();
		for (String str : listOfStrings) {
			list.add(decolorize(str));
		}
		return list;
	}
}