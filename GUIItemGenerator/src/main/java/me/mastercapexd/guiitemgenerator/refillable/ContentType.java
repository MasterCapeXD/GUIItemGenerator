package me.mastercapexd.guiitemgenerator.refillable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.mastercapexd.guiitemgenerator.configuration.YamlConfWrapper;
import me.mastercapexd.guiitemgenerator.randomable.InventoryItem;
import me.mastercapexd.guiitemgenerator.util.BoundableRandom;
import me.mastercapexd.guiitemgenerator.util.BukkitChecks;
import me.mastercapexd.guiitemgenerator.util.BukkitText;

public class ContentType {

	private static final Map<String, ContentType> types = Maps.newHashMap();

	public static void load(File folder) {
		File[] typeFiles = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".yml");
			}
		});

		for (File file : typeFiles) {
			System.out.println("File " + file.getName() + " is loading to ContentType...");
			types.put(file.getName().replace(".yml", ""),
					new ContentType(new YamlConfWrapper(file.getParentFile(), file.getName().replace(".yml", ""))));
		}
	}

	public static boolean isType(String type) {
		return types.containsKey(type.toLowerCase());
	}

	public static ContentType getByName(String typeName) {
		return types.get(typeName);
	}

	private final String type;
	private final boolean enableBreak;
	private final int min, max;
	private final int[] randomRespawnTime;
	private final Collection<InventoryItem> items = Sets.newHashSet();

	public ContentType(YamlConfWrapper conf) {
		this.type = conf.getName();
		this.enableBreak = conf.asYaml().getBoolean("enable-break");
		this.min = conf.asYaml().getInt("min-amount");
		this.max = conf.asYaml().getInt("max-amount");
		List<Integer> respawnTimeList = conf.asYaml().getIntegerList("random-respawn-time");
		this.randomRespawnTime = new int[respawnTimeList.size()];
		for (int i = 0; i < respawnTimeList.size(); i++) {
			randomRespawnTime[i] = respawnTimeList.get(i);
		}

		ConfigurationSection itemsSection = conf.asYaml().getConfigurationSection("items");
		for (String key : itemsSection.getKeys(false)) {
			ItemStack item = new ItemStack(Material.valueOf(itemsSection.getString(key + ".material")));
			ItemMeta meta = item.getItemMeta();

			if (itemsSection.isInt(key + ".durability"))
				item.setDurability((short) itemsSection.getInt(key + ".durability"));

			if (itemsSection.isString(key + ".name"))
				meta.setDisplayName(BukkitText.colorize(itemsSection.getString(key + ".name")));

			if (itemsSection.isList(key + ".lore"))
				meta.setLore(BukkitText.colorize(itemsSection.getStringList(key + ".lore")));

			if (itemsSection.isBoolean(key + ".random-enchants")) {
				if (BukkitChecks.isBodyArmor(item))
					randomEnchantArmor(item);
				else if (BukkitChecks.isBoots(item))
					randomEnchantBoots(item);
				else if (BukkitChecks.isHelmet(item))
					randomEnchantHelmet(item);
				else if (BukkitChecks.isSword(item))
					randomEnchantSword(item);
				else if (BukkitChecks.isPickaxe(item))
					randomEnchantPickaxe(item);
				else if (BukkitChecks.isAxe(item))
					randomEnchantAxe(item);
				else if (BukkitChecks.isShovel(item))
					randomEnchantShovel(item);
				else if (BukkitChecks.isHoe(item))
					randomEnchantHoe(item);
				else if (BukkitChecks.isBow(item))
					randomEnchantBow(item);
			} else {
				if (itemsSection.isList(key + ".enchantments")) {
					for (String enchData : itemsSection.getStringList(key + ".enchantments")) {
						String[] enchDataArray = enchData.split(":");
						meta.addEnchant(Enchantment.getByName(enchDataArray[0]), Integer.parseInt(enchDataArray[1]),
								true);
					}
				}
			}

			if (itemsSection.isInt(key + ".amount"))
				item.setAmount(itemsSection.getInt(key + ".amount"));

			item.setItemMeta(meta);
			items.add(new InventoryItem(item, itemsSection.getInt(key + ".chance")));
		}
		types.put(type, this);
	}

	public String getType() {
		return type;
	}

	public boolean isBreakEnabled() {
		return enableBreak;
	}

	public int getMinimumAmount() {
		return min;
	}

	public int getMaximumAmount() {
		return max;
	}

	public int[] getRandomRespawnTime() {
		return randomRespawnTime;
	}

	public Collection<InventoryItem> getItems() {
		return items;
	}

	private void randomEnchantArmor(ItemStack item) {
		BoundableRandom random = new BoundableRandom();
		Enchantment[] enchantments = { Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE,
				Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_PROJECTILE };
		int enchants = random.nextInt(1, enchantments.length);
		for (int i = 0; i < enchants; i++) {
			int rnd = random.nextInt(0, enchantments.length - 1);
			int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addEnchant(enchantments[rnd], level, true);
			item.setItemMeta(itemMeta);
		}
	}

	private void randomEnchantBoots(ItemStack item) {
		BoundableRandom random = new BoundableRandom();
		Enchantment[] enchantments = { Enchantment.PROTECTION_FALL };
		int enchants = random.nextInt(1, enchantments.length);
		for (int i = 0; i < enchants; i++) {
			int rnd = random.nextInt(0, enchantments.length - 1);
			int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addEnchant(enchantments[rnd], level, true);
			item.setItemMeta(itemMeta);
		}
	}

	private void randomEnchantHelmet(ItemStack item) {
		BoundableRandom random = new BoundableRandom();
		Enchantment[] enchantments = { Enchantment.OXYGEN, Enchantment.WATER_WORKER };
		int enchants = random.nextInt(1, enchantments.length);
		for (int i = 0; i < enchants; i++) {
			int rnd = random.nextInt(0, enchantments.length - 1);
			int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addEnchant(enchantments[rnd], level, true);
			item.setItemMeta(itemMeta);
		}
	}

	private void randomEnchantSword(ItemStack item) {
		BoundableRandom random = new BoundableRandom();
		Enchantment[] enchantments = { Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS,
				Enchantment.DURABILITY, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS };
		int enchants = random.nextInt(1, enchantments.length);
		for (int i = 0; i < enchants; i++) {
			int rnd = random.nextInt(0, enchantments.length - 1);
			int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addEnchant(enchantments[rnd], level, true);
			item.setItemMeta(itemMeta);
		}
	}

	private void randomEnchantPickaxe(ItemStack item) {
		BoundableRandom random = new BoundableRandom();

		if (Enchantment.getByName("MENDING") != null) {
			Enchantment[] enchantments = { Enchantment.DURABILITY, Enchantment.LOOT_BONUS_BLOCKS,
					Enchantment.SILK_TOUCH, Enchantment.getByName("MENDING"), Enchantment.DIG_SPEED };
			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		} else {
			Enchantment[] enchantments = { Enchantment.DURABILITY, Enchantment.LOOT_BONUS_BLOCKS,
					Enchantment.SILK_TOUCH, Enchantment.DIG_SPEED };
			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		}
	}

	private void randomEnchantAxe(ItemStack item) {
		BoundableRandom random = new BoundableRandom();

		if (Enchantment.getByName("MENDING") != null) {
			Enchantment[] enchantments = { Enchantment.DAMAGE_ALL, Enchantment.SILK_TOUCH, Enchantment.DURABILITY,
					Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_MOBS, Enchantment.getByName("MENDING") };

			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		} else {
			Enchantment[] enchantments = { Enchantment.DAMAGE_ALL, Enchantment.SILK_TOUCH, Enchantment.DURABILITY,
					Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_MOBS };

			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		}
	}

	private void randomEnchantHoe(ItemStack item) {
		BoundableRandom random = new BoundableRandom();

		if (Enchantment.getByName("MENDING") != null) {
			Enchantment[] enchantments = { Enchantment.DURABILITY, Enchantment.getByName("MENDING") };

			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		} else {
			Enchantment[] enchantments = { Enchantment.DURABILITY };

			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		}
	}

	private void randomEnchantShovel(ItemStack item) {
		BoundableRandom random = new BoundableRandom();

		if (Enchantment.getByName("MENDING") != null) {
			Enchantment[] enchantments = { Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY, Enchantment.DIG_SPEED,
					Enchantment.getByName("MENDING"), Enchantment.SILK_TOUCH };
			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		} else {
			Enchantment[] enchantments = { Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY, Enchantment.DIG_SPEED,
					Enchantment.SILK_TOUCH };
			int enchants = random.nextInt(1, enchantments.length);
			for (int i = 0; i < enchants; i++) {
				int rnd = random.nextInt(0, enchantments.length - 1);
				int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantments[rnd], level, true);
				item.setItemMeta(itemMeta);
			}
		}
	}

	private void randomEnchantBow(ItemStack item) {
		BoundableRandom random = new BoundableRandom();
		Enchantment[] enchantments = { Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_FIRE,
				Enchantment.ARROW_INFINITE };
		int enchants = random.nextInt(1, enchantments.length);
		for (int i = 0; i < enchants; i++) {
			int rnd = random.nextInt(0, enchantments.length - 1);
			int level = random.nextInt(enchantments[rnd].getStartLevel(), enchantments[rnd].getMaxLevel());
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addEnchant(enchantments[rnd], level, true);
			item.setItemMeta(itemMeta);
		}
	}
}