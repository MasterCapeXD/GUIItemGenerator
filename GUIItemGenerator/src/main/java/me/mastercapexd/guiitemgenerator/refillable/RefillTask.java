package me.mastercapexd.guiitemgenerator.refillable;

import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Maps;

import me.mastercapexd.guiitemgenerator.GuiItemGeneratorPlugin;
import me.mastercapexd.guiitemgenerator.randomable.InventoryFiller;
import me.mastercapexd.guiitemgenerator.util.BoundableRandom;
import me.mastercapexd.guiitemgenerator.util.CooldownChecker;

public final class RefillTask {

	private final RefillableContainersData containersData;
	private final Plugin plugin;

	private final BoundableRandom random = new BoundableRandom();
	private final Map<RefillableContainer, Long> randomizedTimes = Maps.newHashMap();
	private final Map<RefillableContainer, CooldownChecker> millisContainer = Maps.newHashMap();
	private BukkitTask refillTask;

	public RefillTask(RefillableContainersData refillableContainersData, Plugin plugin) {
		this.containersData = refillableContainersData;
		this.plugin = plugin;
	}

	public void start() {
		this.refillTask = new BukkitRunnable() {

			@Override
			public void run() {
				GuiItemGeneratorPlugin
						.debug(containersData.getAllContainers().size() + " refillable containers was found!");
				if (containersData.getAllContainers().isEmpty())
					return;

				for (RefillableContainer container : containersData.getAllContainers()) {
					if (!randomizedTimes.containsKey(container)) {
						millisContainer.put(container, new CooldownChecker());
						int randomInt = random.nextInt(0, container.getContentType().getRandomRespawnTime().length - 1);
						randomizedTimes.put(container,
								new Long(container.getContentType().getRandomRespawnTime()[randomInt] * 1000));
					}

					if (!millisContainer.get(container).isPassed(randomizedTimes.get(container))) {
						randomizedTimes.replace(container, randomizedTimes.get(container) - 1000);
						continue;
					}

					new BukkitRunnable() {

						@Override
						public void run() {
							container.refill(new InventoryFiller(container.getContentType().getItems()));
						}
					}.runTask(plugin);

					randomizedTimes.remove(container);
					millisContainer.get(container).reset();
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 20L);
	}

	public void stop() {
		refillTask.cancel();
	}
}