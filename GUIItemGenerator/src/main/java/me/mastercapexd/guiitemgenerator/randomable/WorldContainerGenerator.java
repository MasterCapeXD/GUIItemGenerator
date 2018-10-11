package me.mastercapexd.guiitemgenerator.randomable;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.mastercapexd.guiitemgenerator.configuration.LocaleConf;
import me.mastercapexd.guiitemgenerator.configuration.PluginConf;
import me.mastercapexd.guiitemgenerator.refillable.ContentType;
import me.mastercapexd.guiitemgenerator.util.BoundableRandom;
import me.mastercapexd.guiitemgenerator.util.BukkitChecks;

public final class WorldContainerGenerator {

	private final PluginConf pluginConf;
	private final LocaleConf locale;
	private final Plugin plugin;
	private final WorldGuardPlugin worldGuard;

	private final BoundableRandom random = new BoundableRandom();
	private final Set<World> enabledWorlds = Sets.newHashSet();

	private final ContentType contentType;
	private BukkitTask generatorTask;

	public WorldContainerGenerator(PluginConf pluginConf, LocaleConf localeConf, Plugin plugin,
			WorldGuardPlugin worldGuardPlugin) {
		this.pluginConf = pluginConf;
		this.locale = localeConf;
		this.plugin = plugin;
		this.worldGuard = worldGuardPlugin;

		for (String worldName : pluginConf.getEnabledWorlds())
			enabledWorlds.add(Bukkit.getWorld(worldName));
		contentType = pluginConf.getRandomGenType();
	}

	public void start() {
		if (!pluginConf.areRandomContainersEnabled())
			return;
		if (pluginConf.getSpawnTimer() < 0) {
			System.out.println("WARNING! Spawn timer is less than 0! Random world containers are disabled!");
			return;
		} else if (pluginConf.getSpawnTimer() < 10)
			System.out.println("WARNING! The setting `spawn-timer` in the file cfg.yml is so small and is SO LAGGY!");
		this.generatorTask = new BukkitRunnable() {

			@Override
			public void run() {
				for (World world : enabledWorlds) {
					generate(world);
				}
			}
		}.runTaskTimer(plugin, 0L, 20 * pluginConf.getSpawnTimer());
	}

	public void stop() {
		if (generatorTask != null) {
			generatorTask.cancel();
			generatorTask = null;
		}
	}

	public void generate(World world) {
		spawn(getRandomLocation(world), Material.CHEST, contentType);
	}

	public void spawn(Location location, Material material, ContentType type) {
		if (!BukkitChecks.isContainer(material))
			return;

		location.getBlock().setType(Material.CHEST);
		InventoryHolder container = (InventoryHolder) location.getBlock().getState();
		InventoryFiller inventoryFiller = new InventoryFiller(type.getItems());
		inventoryFiller.fill(container.getInventory(),
				inventoryFiller.randomize(contentType.getMinimumAmount(), contentType.getMaximumAmount()));

		for (Player player : Bukkit.getOnlinePlayers())
			if (BukkitChecks.isInWorld(location.getWorld(), player))
				player.sendMessage(locale.getMessage("random-container-generated").replace("$coords$",
						location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
	}

	private Location getRandomLocation(World world) {
		int randomX;
		int randomY;
		int randomZ;

		do {
			randomX = random.nextInt(pluginConf.getMinimumX(), pluginConf.getMaximumX());
			randomZ = random.nextInt(pluginConf.getMinimumZ(), pluginConf.getMaximumZ());
			randomY = world.getHighestBlockYAt(randomX, randomZ);
		} while (!canSpawn(new Location(world, randomX, randomY, randomZ)));

		return new Location(world, randomX, randomY, randomZ);
	}

	private boolean canSpawn(Location location) {
		if (!pluginConf.isGlobalRegionOnly())
			return true;

		RegionManager manager = worldGuard.getRegionManager(location.getWorld());
		ApplicableRegionSet set = manager.getApplicableRegions(location);
		return set.getRegions().isEmpty() || set.getRegions().iterator().next().getId().equalsIgnoreCase("__global__");
	}
}