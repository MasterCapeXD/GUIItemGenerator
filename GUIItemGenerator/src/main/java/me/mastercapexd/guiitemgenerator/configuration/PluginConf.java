package me.mastercapexd.guiitemgenerator.configuration;

import java.io.File;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.mastercapexd.guiitemgenerator.refillable.ContentType;

public final class PluginConf extends YamlConfWrapper {

	private final boolean randomContainersEnabled;
	private final boolean globalRegionOnly;
	private final int xmin;
	private final int xmax;
	private final int zmin;
	private final int zmax;
	private final int spawnTimer;
	private final Set<String> enabledWorlds;
	private final ContentType randomGenType;

	private final boolean debugMode;

	public PluginConf(File parent, WorldGuardPlugin worldGuardPlugin) {
		super(parent, "cfg");
		ConfigurationSection randomContainersSection = this.asYaml().getConfigurationSection("random-world-containers");
		this.randomContainersEnabled = randomContainersSection.getBoolean("enabled");

		if (worldGuardPlugin != null)
			this.globalRegionOnly = randomContainersSection.getBoolean("global-region-only");
		else {
			System.out.println("WorldGuard plugin not found. WG support disabling...");
			globalRegionOnly = false;
		}

		this.xmin = randomContainersSection.getInt("x-min");
		this.xmax = randomContainersSection.getInt("x-max");
		this.zmin = randomContainersSection.getInt("z-min");
		this.zmax = randomContainersSection.getInt("z-max");
		this.spawnTimer = randomContainersSection.getInt("spawn-timer");
		this.enabledWorlds = Sets.newHashSet(randomContainersSection.getStringList("enabled-worlds"));
		this.randomGenType = ContentType.getByName(randomContainersSection.getString("content-type"));
		this.debugMode = this.asYaml().getBoolean("debug-mode");
	}

	public boolean areRandomContainersEnabled() {
		return randomContainersEnabled;
	}

	public boolean isGlobalRegionOnly() {
		return globalRegionOnly;
	}

	public int getMinimumX() {
		return xmin;
	}

	public int getMaximumX() {
		return xmax;
	}

	public int getMinimumZ() {
		return zmin;
	}

	public int getMaximumZ() {
		return zmax;
	}

	public int getSpawnTimer() {
		return spawnTimer;
	}

	public Set<String> getEnabledWorlds() {
		return enabledWorlds;
	}

	/**
	 * @return the randomGenType
	 */
	public ContentType getRandomGenType() {
		return randomGenType;
	}

	public boolean isDebugModeEnabled() {
		return debugMode;
	}
}