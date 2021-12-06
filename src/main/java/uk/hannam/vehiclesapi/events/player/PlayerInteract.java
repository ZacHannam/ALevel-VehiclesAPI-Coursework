package uk.hannam.vehiclesapi.events.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.frame.FrameComponent;

public class PlayerInteract implements Listener   {

	// how far the player can click
	private static final double MAX_REACH_DISTANCE = 6;
	
	/**
	 * Called whenever a player clicks on a block or in the air
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR) return; // checks if the play right clicks the block
		
		if(!VehiclesAPI.getUserManager().isUser(event.getPlayer().getUniqueId())) return;
		
		User user = VehiclesAPI.getUserManager().getUser(event.getPlayer().getUniqueId());
		
		if(user.isInDebounce()) return; // checks if the user is currently in a debounce (clicked in the past 3 ticks) and returns in they are
		user.addDebounce(); // adds a debounce if they are not
		
		if(VehiclesAPI.getVehicleManager().getNumberOfVehicles() <= 0) return; // no point of doing anything else if there are no vehicles in existance, waste of CPU time.
		
		Location location = event.getPlayer().getLocation(); // gets the player location
		
		// HashMap is for potential vehicles which fall in the clicking area of the player
		HashMap<Vehicle, Double> aliveVehicles = new HashMap<Vehicle, Double>(); // creates a new hashmap
		
		// Iterates through all the vehicles
		for(Vehicle vehicle : VehiclesAPI.getVehicleManager().getVehicles().values()) {
			
			if(!vehicle.isSpawned()) continue; // firstly check if the vehicle is spawned, otherwise when trying to get the location, it will be null
			
			if(!vehicle.hasComponent(ComponentName.FRAME.getName())) continue; // checks if the vehicle has the frame component, if it doesn't then the vehicle will not have any sizes
			
			if(vehicle.getLocation().getWorld() != location.getWorld()) continue; // checks if the world is in the same world as the player
			
			FrameComponent frame = (FrameComponent) vehicle.getFirstComponentByType(ComponentName.FRAME.getName()); // gets the frame component

			// gets the distance between the frame and the player and checks if it is larger than the max click distance + the frame's biggest body length / 2
			// this is because the  location is at the centre of the frame's body. Therefore we must add a half of the longest distance to cover the entire body
			if(location.distance(frame.getLocation()) > MAX_REACH_DISTANCE + (frame.getLongestBodyLength() / 2)) continue;
			
			double shortestLength = frame.getShortestBodyLength(); // recording the shortest body length
			if(shortestLength <= 0) continue; // checks that the shortest side is bigger than 0, otherwise we will have an infinite loop
			
			double distance = -shortestLength; // initiates distance as - shortestLength, so then on the first run through the start position will be 0
			
			// name distanceloop to break out from it later.
			distanceLoop: while(distance < MAX_REACH_DISTANCE) { // keeps going while distance is less than the max reach distance, we don't worry about armorstand position as when checking the point inside of the vehicle we know it will count in the dimensions of the vehicle
				
				// possible problem occurs when shortestLength = 0, therefore check for shortestLength = 0 (Line 53)
				distance = Math.min(MAX_REACH_DISTANCE, distance+shortestLength); // distance = distance+shortest length or max reach distance, depending on which one is smaller as we don't want the player over reaching
				
				// defines the location to check by multiplying vectors, this is done by getting where the player is looking and multiplying it by the distance away
				// therefore it basically casts a ray out to the magnitude of distance
				Location checkLocation = event.getPlayer().getEyeLocation().add(event.getPlayer().getEyeLocation().getDirection().multiply(distance));
				
				if(VehicleMath.locationInsideVehicle(vehicle, checkLocation)) { // checks if the location is inside the vehicle
					
					// could use an arraylist instead and recalculate distance to the frame center but then there would be the problem of the different sizes of frames.
					aliveVehicles.put(vehicle, distance); // if it is then it will add it to the alive vehicles with a total distance away.
					
					break distanceLoop; // exits from the distance loop
					
				}
			}
		}
		
		// if there are no alive vehicles / vehicles that have been found that have been clicked then it will return 
		if(aliveVehicles.size() == 0) return;
		
		event.setCancelled(true); // as there is a clicked vehicle we can cancel the event as we know atleast one vehicle will be clicked
		// cancel because otherwise when clicking a vehicle you may also break a block
		
		if(aliveVehicles.size() == 1) { // checks if there is only one vehicle clicked, if so there is no point in trying to compare vehicles, takes more cpu time
			((Vehicle) aliveVehicles.keySet().toArray()[0]).onVehicleClick(event.getPlayer()); // triggers the vehicle click event
			return; // returns from the event
		}
		
		// minDistance bounds should be 0 -> max reach distance
		double minDistance = -1; // minDistance = -1 as it is a valid number that should never be achievable to get, therefore it can be used in place of a boolean, so we know if its the first to be set
		Vehicle minVehicle = null; // records the minVehicle (vehicle at the shortest length away from player (one that was clicked)
		
		for(Map.Entry<Vehicle, Double> entry : aliveVehicles.entrySet()) { // runs through all of the vehicles within the aliveVehicles range
			if(minDistance == -1 || entry.getValue() < minDistance) { // checks if either minDistance == -1 (which means nothing has been set) or if the distance is smaller than the smallest recorded distance
				minDistance = entry.getValue(); // sets the minDistance
				minVehicle = entry.getKey(); //sets the minVehicle
			}
		}
		
		minVehicle.onVehicleClick(event.getPlayer()); // triggers the vehicle click event with the minVehicle (closest vehicle)
		
	}
}
