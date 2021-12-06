package uk.hannam.vehiclesapi.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.inventory.Inventory;

public class InventoryClick implements Listener {
	
	/**
	 * Called whenever an entity clicks within an inventory
	 * Used to get when a player clicks within a vehicle's inventory
	 * @param event
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if(!(event.getWhoClicked() instanceof Player)) return; // checks that it was  a player that clicked returns if not a player
		
		if(VehiclesAPI.getUserManager().isUser(event.getWhoClicked().getUniqueId())) {
			User user = VehiclesAPI.getUserManager().getUser(event.getWhoClicked().getUniqueId());
			if(user.inVehicleInventory()) { // checks if the user is in a vehicle inventory
				Vehicle vehicle = user.getVehicleWithOpenedInventory(); // gets the current vehicle inventory
				if(vehicle == null) return; // checks the vehicle is not null
				
				if(vehicle.hasComponent(ComponentName.INVENTORY.getName())) {
					((Inventory) vehicle.getFirstComponentByType(ComponentName.INVENTORY.getName())).onInventoryClick(event); // performs the inventory click event inside of the vehicle
				}
			}
		}
	}
}
