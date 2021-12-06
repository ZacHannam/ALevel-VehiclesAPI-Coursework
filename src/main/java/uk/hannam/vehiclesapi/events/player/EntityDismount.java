package uk.hannam.vehiclesapi.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;

public class EntityDismount implements Listener {

	/**
	 * Called whenever a player dismounts from a vehicle
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDismount(EntityDismountEvent event) {
		
		if(event.isCancelled()) return; // checks if the event is cancelled
		if(event.getEntity() instanceof Player) { // checks if entity is a player
			if(VehiclesAPI.getUserManager().isUser(event.getEntity().getUniqueId())) { // must check as player may have left
				User user = VehiclesAPI.getUserManager().getUser(event.getEntity().getUniqueId()); // gets the user from the UserManager
				if(user.getSeat() != null) { // checks if the user is in a seat, however also checks that the seat is not null
					user.dismount(); // dismounts  the user from the seat
				}
			}
		}
	}
}
