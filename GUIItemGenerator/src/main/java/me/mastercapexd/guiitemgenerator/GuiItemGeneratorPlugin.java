package me.mastercapexd.guiitemgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.mastercapexd.guiitemgenerator.api.GuiItemGenerator;
import me.mastercapexd.guiitemgenerator.configuration.LocaleConf;
import me.mastercapexd.guiitemgenerator.configuration.PluginConf;
import me.mastercapexd.guiitemgenerator.executor.PluginExecutor;
import me.mastercapexd.guiitemgenerator.randomable.WorldContainerGenerator;
import me.mastercapexd.guiitemgenerator.refillable.ContentType;
import me.mastercapexd.guiitemgenerator.refillable.EventListener;
import me.mastercapexd.guiitemgenerator.refillable.RefillTask;
import me.mastercapexd.guiitemgenerator.refillable.RefillableContainersData;

public final class GuiItemGeneratorPlugin extends JavaPlugin implements GuiItemGenerator {

	// Some static instances

	// The plugin API interface instance
	private static GuiItemGenerator api;
	// The plugin debug boolean value (true = debug mode is enabled, false =
	// debug mode is disabled)
	private static boolean debug;

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- //

	// WorldGuard injection. Can be null
	private WorldGuardPlugin worldGuardPlugin;

	// This class is the plugin settings manager
	private PluginConf pluginConf;
	// This class is the locale manager
	private LocaleConf localeConf;

	// This class generates containers with selected ContainerType with random
	// items
	private WorldContainerGenerator generator;

	// This class contains all data of RefillableContainers and managing them
	private RefillableContainersData refillableContainersData;

	// This class starts refill tasks with random refill time function
	private RefillTask refillTask;

	// This class is the plugin command executor
	private PluginExecutor pluginCommandExecutor;
	// This class hooks block break event to check the block
	private EventListener eventListener;

	public static GuiItemGenerator getApi() {
		return api;
	}

	public static void debug(String debugMessage) {
		if (debug)
			System.out.println("GUIItemGenerator Debug: " + debugMessage);
	}

	@Override
	public void onLoad() {
		if (api != null) {
			this.getLogger().severe("DO NOT USE THE /RELOAD COMMAND!");
			this.getLogger().severe("Server is stopping...");
			this.getServer().shutdown();
		}
		api = this;
	}

	@Override
	public void onEnable() {
		this.saveResource("cfg.yml", false);
		this.saveResource("locale.yml", false);

		loadDefaultTypes();

		worldGuardPlugin = getWorldGuard();

		pluginConf = new PluginConf(this.getDataFolder(), worldGuardPlugin);
		localeConf = new LocaleConf(this.getDataFolder());

		generator = new WorldContainerGenerator(pluginConf, localeConf, this, worldGuardPlugin);

		refillableContainersData = getData();
		refillableContainersData.checkNull();
		refillTask = new RefillTask(refillableContainersData, this);

		pluginCommandExecutor = new PluginExecutor(localeConf, refillableContainersData);
		eventListener = new EventListener(refillableContainersData);

		debug = pluginConf.isDebugModeEnabled();

		this.getCommand("guiitemgenerator").setExecutor(pluginCommandExecutor);
		this.getServer().getPluginManager().registerEvents(pluginCommandExecutor, this);
		this.getServer().getPluginManager().registerEvents(eventListener, this);

		refillTask.start();
		generator.start();
	}

	@Override
	public void onDisable() {
		saveData();
		refillTask.stop();
		generator.stop();
	}

	public void registerInventory(Location location, Inventory inventory, String type) {
		refillableContainersData.registerCustomContainer(location, inventory, type);
	}

	public void registerInventory(Location location, Inventory inventory, ContentType type) {
		registerInventory(location, inventory, type.getType());
	}

	public void registerInventory(Block block, Inventory inventory, String type) {
		registerInventory(block.getLocation(), inventory, type);
	}

	public void registerInventory(Block block, Inventory inventory, ContentType type) {
		registerInventory(block.getLocation(), inventory, type);
	}

	public void removeInventory(Location location) {
		refillableContainersData.unregisterCustomContainer(location);
	}

	public void removeInventory(Block block) {
		removeInventory(block.getLocation());
	}

	public boolean containsInventory(Location location) {
		return refillableContainersData.hasRegisteredLocation(location);
	}

	public boolean containsInventory(Block block) {
		return containsInventory(block.getLocation());
	}

	public void refill(Location location) {
		boolean result = refillableContainersData.refillContainer(location);
		if (!result)
			System.out.println("Container not found!");
	}

	public void refill(Block block) {
		refill(block.getLocation());
	}

	public void spawn(Location where, Material material, ContentType type) {
		generator.spawn(where, material, type);
	}

	private final String fileName = this.getDataFolder() + File.separator + "containers.dat";

	private RefillableContainersData getData() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(fileName)));
		} catch (IOException e) {
			System.out.println("Unable to read data from file containers.dat!");
		}
		try {
			return (RefillableContainersData) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found???");
		} catch (IOException e) {
			System.out.println("IOException while loading data from file containers.dat!");
		} catch (NullPointerException e) {
			System.out.println("File is null!");
		}
		return new RefillableContainersData();
	}

	private void saveData() {
		File dataFile = new File(fileName);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(dataFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(refillableContainersData);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to save data! File containers.dat not found!");
		} catch (IOException e) {
			System.out.println("Unable to save data!");
		}
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin))
			return null;

		return (WorldGuardPlugin) plugin;
	}

	private void loadDefaultTypes() {
		File typesFolder = new File(this.getDataFolder() + File.separator + "types");
		if (!typesFolder.exists())
			typesFolder.mkdirs();

		File exampleType = new File(typesFolder, "example-type.yml");
		File randomgen = new File(typesFolder, "randomgen.yml");

		InputStream inputStream = null;
		OutputStream outputStream = null;

		if (!exampleType.exists()) {
			inputStream = this.getResource("example-type.yml");

			try {
				outputStream = new FileOutputStream(exampleType);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			int read = 0;
			byte[] bytes = new byte[1024];

			try {
				while ((read = inputStream.read(bytes)) != -1)
					outputStream.write(bytes, 0, read);
			} catch (IOException e) {
				e.printStackTrace();
			}

			inputStream = null;
			outputStream = null;
		}

		if (!randomgen.exists()) {
			inputStream = this.getResource("randomgen.yml");

			try {
				outputStream = new FileOutputStream(randomgen);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			int read = 0;
			byte[] bytes = new byte[1024];

			try {
				while ((read = inputStream.read(bytes)) != -1)
					outputStream.write(bytes, 0, read);
			} catch (IOException e) {
				e.printStackTrace();
			}

			inputStream = null;
			outputStream = null;
		}

		ContentType.load(typesFolder);
	}
}