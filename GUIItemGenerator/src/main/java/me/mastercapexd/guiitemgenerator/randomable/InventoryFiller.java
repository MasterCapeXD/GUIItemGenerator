package me.mastercapexd.guiitemgenerator.randomable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.mastercapexd.guiitemgenerator.GuiItemGeneratorPlugin;
import me.mastercapexd.guiitemgenerator.util.BoundableRandom;

public final class InventoryFiller {

	private final BoundableRandom random = new BoundableRandom();
	private final List<InventoryItem> itemList;

	public InventoryFiller(Collection<InventoryItem> items) {
		itemList = Lists.newArrayList(items);
	}

	public Collection<ItemStack> randomize(int minAmount, int maxAmount) {
		Collection<ItemStack> items = Sets.newHashSet();
		if (minAmount <= 0)
			minAmount = 0;

		if (itemList.size() < maxAmount)
			maxAmount = itemList.size();

		int randomAmount = random.nextInt(minAmount, maxAmount);

		for (int i = 1; i <= randomAmount; i++) {
			int maxChance = 0;
			int from = 0;

			for (InventoryItem item : itemList) {
				maxChance += item.getChance();
			}

			int randomInt = random.nextInt(0, maxChance);
			for (InventoryItem item : itemList) {
				if ((from <= randomInt) && (randomInt < from + item.getChance())) {
					items.add(item.getItemStack());
				}
				from += item.getChance();
			}
		}

		GuiItemGeneratorPlugin.debug("Randomized items:");
		items.forEach(item -> GuiItemGeneratorPlugin.debug(item.getType() + ", amount: " + item.getAmount()));

		return items;
	}

	public void fill(Inventory inventory, Collection<ItemStack> items) {
		inventory.clear();
		int size = inventory.getSize();
		List<ItemStack> itemsList = Lists.newArrayList(items);

		for (int i = 0; i < size; i++) {
			if (i >= itemsList.size())
				break;
			inventory.setItem(random.nextInt(0, size - 1), itemsList.get(i));
		}
	}

	public void fill(Inventory inventory, ItemStack... items) {
		fill(inventory, Arrays.asList(items));
	}
}