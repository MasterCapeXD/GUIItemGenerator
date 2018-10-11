package me.mastercapexd.guiitemgenerator.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfWrapper {

	private final File file;
	private final String fileName;
	private final FileConfiguration yaml;

	public YamlConfWrapper(File parent, String fileName) {
		this.file = new File(parent + File.separator + fileName + ".yml");

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Unable to create this file! Details: " + e.getMessage());
			}

		this.fileName = fileName;
		this.yaml = YamlConfiguration.loadConfiguration(file);
	}

	public String getName() {
		return fileName;
	}

	public FileConfiguration asYaml() {
		return yaml;
	}

	public void load() {
		try {
			this.yaml.load(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found! Details: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error while loading data from file! Details: " + e.getMessage());
		} catch (InvalidConfigurationException e) {
			System.out.println("Unable to load data from file! Details: " + e.getMessage());
		}
	}

	public void save() {
		try {
			this.yaml.save(file);
		} catch (IOException e) {
			System.out.println("Unable to save data to file! Details: " + e.getMessage());
		}
	}
}