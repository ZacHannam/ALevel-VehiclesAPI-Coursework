package uk.hannam.vehiclesapi.vehicles.components.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface Inventory {

	org.bukkit.inventory.Inventory getInventory();
	void setInventory(org.bukkit.inventory.Inventory paramInventory);
	void onInventoryClick(InventoryClickEvent event);
}
