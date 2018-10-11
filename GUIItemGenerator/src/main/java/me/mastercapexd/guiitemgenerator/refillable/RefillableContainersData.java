package me.mastercapexd.guiitemgenerator.refillable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.mastercapexd.guiitemgenerator.randomable.InventoryFiller;

public final class RefillableContainersData implements Serializable {

	private static final long serialVersionUID = 6077605120616491073L;

	private transient Map<Location, Inventory> customRegisteredInventories = Maps.newHashMap();
	private transient Map<Location, RefillableContainer> customRefillableContainers = Maps.newHashMap();
	private Map<String, RefillableContainer> refillableContainers = Maps.newHashMap();

	public void registerCustomContainer(Location location, Inventory inventory, String type) {
		customRegisteredInventories.put(location, inventory);
		customRefillableContainers.put(location, new RefillableContainer(location, type, this));
	}

	public boolean hasRegisteredLocation(Location location) {
		return customRegisteredInventories.containsKey(location);
	}

	public void unregisterCustomContainer(Location location) {
		if (!hasRegisteredLocation(location)) {
			System.out.println("WARN | Unregister failure: Container not registered!");
			return;
		}
		customRefillableContainers.remove(location);
		customRegisteredInventories.remove(location);
	}

	public void addContainer(RefillableContainer container) {
		refillableContainers.put(container.toString(), container);
	}

	public boolean hasContainer(RefillableContainer container) {
		return refillableContainers.containsKey(container.toString());
	}

	public boolean hasContainer(Location location) {
		return refillableContainers.containsKey(location.getWorld().getName() + ";" + location.getBlockX() + ";"
				+ location.getBlockY() + ";" + location.getBlockZ());
	}

	public void removeContainer(RefillableContainer container) {
		refillableContainers.remove(container.toString());
	}

	public void removeContainers() {
		this.refillableContainers.clear();
	}

	public boolean refillContainer(Location location) {
		boolean result = false;

		if (hasRegisteredLocation(location)) {
			RefillableContainer container = customRefillableContainers.get(location);
			container.refill(new InventoryFiller(container.getContentType().getItems()));
			result = true;
		} else {
			if (hasContainer(location)) {
				RefillableContainer container = getContainer(location);
				container.refill(new InventoryFiller(container.getContentType().getItems()));
				result = true;
			}
		}
		return result;
	}

	public void checkNull() {
		if (customRegisteredInventories == null)
			customRegisteredInventories = Maps.newHashMap();
		if (customRefillableContainers == null)
			customRefillableContainers = Maps.newHashMap();
	}

	RefillableContainer getContainer(Location location) {
		return refillableContainers.get(location.getWorld().getName() + ";" + location.getBlockX() + ";"
				+ location.getBlockY() + ";" + location.getBlockZ());
	}

	Set<RefillableContainer> getAllContainers() {
		Set<RefillableContainer> containers = Sets.newHashSet(refillableContainers.values());
		if (customRefillableContainers.isEmpty())
			return containers;
		else
			containers.addAll(customRefillableContainers.values());
		return containers;
	}

	Inventory getCustomContainerInventory(Location location) {
		return customRegisteredInventories.get(location);
	}
}