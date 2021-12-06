package uk.hannam.vehiclesapi.events.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;

public class PlayerQuit implements Listener{
	
	/**
	 * called whenever a player leaves the server
	 * 
	 * used to remove the player's user object
	 * @param event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		VehiclesAPI.getUserManager().haltUser(event.getPlayer()); // halts the user
	}
}
