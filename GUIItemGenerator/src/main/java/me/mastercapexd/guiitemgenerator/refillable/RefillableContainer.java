package me.mastercapexd.guiitemgenerator.refillable;

import java.io.Serializable;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.mastercapexd.guiitemgenerator.GuiItemGeneratorPlugin;
import me.mastercapexd.guiitemgenerator.randomable.InventoryFiller;

public final class RefillableContainer implements Serializable {

	private static final long serialVersionUID = -3366568088309332586L;

	private final transient RefillableContainersData refillableContainersData;

	private final String contentType;
	private final String world;
	private final int x;
	private final int y;
	private final int z;

	public RefillableContainer(Location location, String contentType, RefillableContainersData data) {
		this.refillableContainersData = data;

		this.contentType = contentType;
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}

	public ContentType getContentType() {
		return ContentType.getByName(contentType);
	}

	public void refill(InventoryFiller inventoryFiller) {
		Collection<ItemStack> items = inventoryFiller.randomize(getContentType().getMinimumAmount(),
				getContentType().getMaximumAmount());
		BlockState blockState = toLocation().getBlock().getState();

		if (GuiItemGeneratorPlugin.getApi().containsInventory(toLocation())) {
			inventoryFiller.fill(refillableContainersData.getCustomContainerInventory(toLocation()), items);
		} else {
			if (!(blockState instanceof InventoryHolder))
				return;

			InventoryHolder container = (InventoryHolder) blockState;
			inventoryFiller.fill(container.getInventory(), items);
			GuiItemGeneratorPlugin.debug("The container at location " + x + " " + y + " " + z + " has been refilled!");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof RefillableContainer))
			return false;
		RefillableContainer refillableContainer = (RefillableContainer) obj;
		return this.hashCode() == refillableContainer.hashCode();
	}

	@Override
	public int hashCode() {
		return toLocation().hashCode();
	}

	Location toLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	@Override
	public String toString() {
		return world + ";" + x + ";" + y + ";" + z;
	}
}
