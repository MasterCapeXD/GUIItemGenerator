package me.mastercapexd.guiitemgenerator.refillable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {

	private final RefillableContainersData containersData;

	public EventListener(RefillableContainersData refillableContainersData) {
		this.containersData = refillableContainersData;
	}

	@EventHandler
	private void on(BlockBreakEvent event) {
		if (containersData.hasContainer(event.getBlock().getLocation())) {
			RefillableContainer container = containersData.getContainer(event.getBlock().getLocation());
			event.setCancelled(!container.getContentType().isBreakEnabled());
		}
	}
}