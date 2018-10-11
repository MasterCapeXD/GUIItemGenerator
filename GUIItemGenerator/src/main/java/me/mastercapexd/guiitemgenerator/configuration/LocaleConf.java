package me.mastercapexd.guiitemgenerator.configuration;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

import me.mastercapexd.guiitemgenerator.util.BukkitText;

public final class LocaleConf extends YamlConfWrapper {

	private final Map<String, String> messages = Maps.newHashMap();

	public LocaleConf(File parent) {
		super(parent, "locale");
		load();
	}

	@Override
	public void load() {
		if (!messages.isEmpty())
			messages.clear();
		super.load();
		for (String key : asYaml().getKeys(false))
			messages.put(key, BukkitText.colorize(asYaml().getString(key)));
	}

	public String getMessage(String key) {
		return messages.get(key);
	}
}