package me.mastercapexd.guiitemgenerator.executor;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;

import me.mastercapexd.guiitemgenerator.configuration.LocaleConf;
import me.mastercapexd.guiitemgenerator.refillable.ContentType;
import me.mastercapexd.guiitemgenerator.refillable.RefillableContainer;
import me.mastercapexd.guiitemgenerator.refillable.RefillableContainersData;
import me.mastercapexd.guiitemgenerator.util.BukkitChecks;
import me.mastercapexd.guiitemgenerator.util.BukkitText;

public class PluginExecutor implements CommandExecutor, Listener {

	private final LocaleConf locale;
	private final RefillableContainersData containersData;

	private final Map<Player, Location> playerSelections = Maps.newHashMap();
	private final ItemStack wand;

	public PluginExecutor(LocaleConf localeConf, RefillableContainersData refillableContainersData) {
		this.locale = localeConf;
		this.containersData = refillableContainersData;
		this.wand = getWandItem();
	}

	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
		if (args.length <= 0 || args[0].equalsIgnoreCase("help")) {
			if (!commandSender.hasPermission("guigen.help")) {
				commandSender.sendMessage(locale.getMessage("no-permission"));
				return true;
			}
			commandSender.sendMessage(BukkitText.colorize("&6&lGUI&7&lItem&e&lGenerator:"));
			commandSender.sendMessage(BukkitText.colorize("&6/guiitemgenerator [help] &7- shows this page."));
			commandSender.sendMessage(BukkitText.colorize("&6/guiitemgenerator wand &7- gives a wand."));
			commandSender.sendMessage(BukkitText.colorize(
					"&6/guiitemgenerator create <type> &7- registers a new refillable chest in selected location."));
			commandSender.sendMessage(
					BukkitText.colorize("&6/guiitemgenerator delete &7- deletes the selected refillable container."));
			commandSender.sendMessage(
					BukkitText.colorize("&6/guiitemgenerator clear &7- deletes all refillable containers."));
			return true;
		}
		if (args[0].equalsIgnoreCase("wand")) {
			if (!commandSender.hasPermission("guigen.wand")) {
				commandSender.sendMessage(locale.getMessage("no-permission"));
				return true;
			}

			if (!BukkitChecks.isPlayer(commandSender, locale.getMessage("console-command-blocked")))
				return true;

			Player player = (Player) commandSender;
			player.getInventory().addItem(wand);
			player.sendMessage(locale.getMessage("wand-got"));
		} else if (args[0].equalsIgnoreCase("create")) {
			if (!commandSender.hasPermission("guigen.create")) {
				commandSender.sendMessage(locale.getMessage("no-permission"));
				return true;
			}

			if (!BukkitChecks.isPlayer(commandSender, locale.getMessage("console-command-blocked")))
				return true;

			Player player = (Player) commandSender;
			if (args.length != 2) {
				player.sendMessage(locale.getMessage("wrong-arguments-amount"));
				return true;
			}

			if (!playerSelections.containsKey(player)) {
				player.sendMessage(locale.getMessage("wand-selection-not-found"));
				return true;
			}

			if (!ContentType.isType(args[1])) {
				player.sendMessage(locale.getMessage("type-not-found"));
				return true;
			}

			if (containersData
					.hasContainer(new RefillableContainer(playerSelections.get(player), null, containersData))) {
				player.sendMessage(locale.getMessage("container-already-exists"));
				return true;
			}

			containersData.addContainer(
					new RefillableContainer(playerSelections.get(player), args[1].toLowerCase(), containersData));
			player.sendMessage(locale.getMessage("refillable-container-created"));
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (!commandSender.hasPermission("guigen.delete")) {
				commandSender.sendMessage(locale.getMessage("no-permission"));
				return true;
			}

			if (!BukkitChecks.isPlayer(commandSender, locale.getMessage("console-command-blocked")))
				return true;

			Player player = (Player) commandSender;
			if (args.length != 1) {
				player.sendMessage(locale.getMessage("wrong-arguments-amount"));
				return true;
			}

			if (!playerSelections.containsKey(player)) {
				player.sendMessage(locale.getMessage("wand-selection-not-found"));
				return true;
			}

			if (!containersData
					.hasContainer(new RefillableContainer(playerSelections.get(player), null, containersData))) {
				player.sendMessage(locale.getMessage("container-not-found"));
				return true;
			}

			containersData.removeContainer(new RefillableContainer(playerSelections.get(player), null, containersData));
			commandSender.sendMessage(locale.getMessage("refillable-container-deleted"));
		} else if (args[0].equalsIgnoreCase("clear")) {
			if (!commandSender.hasPermission("guigen.clear")) {
				commandSender.sendMessage(locale.getMessage("no-permission"));
				return true;
			}

			containersData.removeContainers();
			commandSender.sendMessage(locale.getMessage("refillable-containers-deleted"));
		}

		return true;
	}

	@EventHandler
	private void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.getItem() == null || !(event.getItem().isSimilar(wand)))
			return;

		Player player = event.getPlayer();
		Material type = event.getClickedBlock().getType();
		if (!BukkitChecks.isContainer(type)) {
			player.sendMessage(locale.getMessage("invalid-block"));
			return;
		}

		event.setCancelled(true);
		playerSelections.put(player, event.getClickedBlock().getLocation());
		player.sendMessage(locale.getMessage("wand-location-saved"));
	}

	private ItemStack getWandItem() {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(BukkitText.colorize(locale.getMessage("wand")));
		item.setItemMeta(meta);
		return item;
	}
}