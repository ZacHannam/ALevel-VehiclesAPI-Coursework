package uk.hannam.vehiclesapi.events.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;

public class InventoryClose implements Listener {
	
	/**
	 * called whenever a player's inventory is closed
	 * we want to check when the player's vehicle inventory is closed.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		
		if(VehiclesAPI.getUserManager().isUser(event.getPlayer().getUniqueId())) {
			User user = VehiclesAPI.getUserManager().getUser(event.getPlayer().getUniqueId());
			if(user.inVehicleInventory()) { // checks if the player is in a vehicles inventory
				user.closeInventory(); // remove the player from the vehicles inventory
			}
		}
	}

}
