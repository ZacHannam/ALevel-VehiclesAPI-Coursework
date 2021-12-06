package uk.hannam.vehiclesapi.vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.exceptions.ComponentDoesNotExistException;
import uk.hannam.vehiclesapi.vehicles.components.seat.driverseat.DriverSeat;
import uk.hannam.vehiclesapi.vehicles.exceptions.ComponentCouldNotBeBuiltException;
import uk.hannam.vehiclesapi.vehicles.exceptions.TooManyDriverSeatsException;
import uk.hannam.vehiclesapi.vehicles.exceptions.VehicleHasNotBeenBuiltException;

public abstract class Vehicle implements ComponentBasedVehicle {
	
	//-------------------------------------------------------------------- VARIABLES ------------------------------------------------------------------------

	@Getter
	@Setter
	private int taskID; // stores the taskid (repeating task every tick)
	
	@Getter
	@Setter
	private boolean spawned; // stores the value of if the vehicle is spawned or not
	
	@Getter
	@Setter
	private VehicleLocation location; // stores the location of the vehicle

	@Getter
	private final ArrayList<Component> components = new ArrayList<Component>(); //  stores an arraylist of components that are in the vehicle
	
	@Getter
	@Setter
	private boolean built;
	
	@Getter
	@Setter
	private UUID uuid; // stores the UUID of the vehicle
	
	@Getter
	@Setter
	public int typeID; // stores the vehicles type id.
	
	@Getter
	@Setter
	private boolean hidden; // when chunk is loaded it is false and when chunk is unloaded it is false
	
	//-------------------------------------------------------------------- COMPONENTS IN VEHICLE ------------------------------------------------------------------------
	/**
	 * 
	 * Returns a list of components that the vehicle has that are of a certain component type
	 * 
	 * @param paramComponentType
	 * @return
	 */
	public List<Component> getComponentByType(String paramComponentType) {
		
		List<Component>foundComponents = new ArrayList<Component>(); // creates the found list arraylist
		
		for(Component component : components) { // runs through each component in the vehicle
			if(component.getComponentName().equals(paramComponentType)) { // checks if it has the right component type
				foundComponents.add(component); // adds the component to foundComponents
			}
		}
		return foundComponents; // returns the found components
	}
	
	/**
	 * Used to get the first component in the componets arraylist. This is for when we are certain that there is only one of the components.
	 * 
	 * @param paramComponentName
	 * @return
	 */
	public Component getFirstComponentByType(String paramComponentName) {
		
		for(Component component : components) { // runs through each component
			if(component.getComponentName().equals(paramComponentName)) { // checks if the component type is the same as the paramComponentType
				return component; // returns that component
			}
		}
		throw new RuntimeException(new ComponentDoesNotExistException(paramComponentName)); //no component was found with that type, so it throws an exception
	}
	
	/**
	 * Used to add a component to the vehicle
	 * @param paramComponent
	 */
	public void addComponent(Component paramComponent) {
		components.add(paramComponent); // adds the component
		
		if(this.isSpawned()) { // checks if the vehicle is spawned
			this.despawn(); // despawns the vehicle if the vehicle is spawned
		}
		this.setBuilt(false); // sets built to flase
	}
	
	/**
	 * Used to check if the vehicle has a component
	 * @param paramComponentName
	 * @return
	 */
	public boolean hasComponent(String paramComponentName) {
		for(Component component : this.components) { // iterates through each component in the vehicle
			if(component.getComponentName().equals(paramComponentName)) { // checks if the componentType is the same as the component's type
				return true; // returns true if it is the same
			}
		}
		return false; // after iteration, no components found to match the componentType then return false
	}
	
	//-------------------------------------------------------------------- SPAWNING, DESPAWNING AND, BUILDING ------------------------------------------------------------------------
	
	/** 
	 * Used to spawn in the vehicle at the location paramLocation
	 * @param paramLocation
	 */
	public void spawn(VehicleLocation paramLocation) {
		
		if(!this.isBuilt()) { // checks if the vehicle is built
			throw new RuntimeException(new VehicleHasNotBeenBuiltException()); // throws a VehicleHasNotBeenBuiltException if the vehicle has not been built
		}
		
		VehicleLocation spawnLocation = VehicleMath.fixSpawnY(paramLocation); // fixes the y spawning position, so it doesnt spawn in the air or in a block
		
		setLocation(spawnLocation); // sets the location of the vehicle
		
		setSpawned(true); // sets spawned to true
		
		if(VehiclesAPI.getChunkManager().isChunkLoaded(spawnLocation.toLocation())) { // checks if the chunk the vehicle is spawning in is loaded
			this.show(); // shows the vehicle as the chunk is loaded
		} else {
			VehiclesAPI.getChunkManager().addVehicleToBuffer(this); // adds the vehicle to the chunk buffer
		}
		
		
	}
	
	/**
	 * Used to despawn the vehicle
	 */
	public void despawn() {
		
		if(this.isHidden()) { // checks if the vehicle is hidden
			VehiclesAPI.getChunkManager().removeVehicleFromBuffer(this); // if the vehicle is already hidden then it will remove it from the chunk buffer
		} else {
			this.hide(); // hides the vehicle
		}
		
		setSpawned(false); // sets spawned to false
	}
	
	/**
	 * Used to build the vehicle
	 * 
	 * Building does not spawn the vehicle, it just checks if the vehicle is spawnable.
	 */
	public void build() {
		int numberOfDriverSeats = 0; // stores a count for number of driver seats / only one is allowed
		for(Component component : this.components) { // runs through each component in the vehicle
			if(!component.build()) { // attempts to build the component, if it cannot be built then throws an exception
				throw new RuntimeException(new ComponentCouldNotBeBuiltException(component.getComponentName()));
			}
			if(component.getComponentName().equals("DRIVER_SEAT")) { // checks if the component is a driver's seat
				++numberOfDriverSeats; // adds 1 to the number of driver seats
				if(numberOfDriverSeats >= 2) { // checks if the number of driver seats > 1
					throw new RuntimeException(new TooManyDriverSeatsException()); // throws the too many driver seats execption
				}
			}
		}
		
		setBuilt(true); // sets built to true.
	}
	
	//-------------------------------------------------------------------- VEHICLE METHODS ------------------------------------------------------------------------

	/**
	 * Checks if there is a driver in the vehicle
	 * @return
	 */
	public boolean isDriver() {
		for(Component seat :  this.getComponentByType("DRIVER_SEAT")) { // runs throughe each seat in the vehicle
			if(((DriverSeat) seat).hasRider()) return true; // returns true if there is a rider in the driver seat
		}
		return false; // otherwise returns false
	}
	
	/**
	 * Checks if there is another vehicle at the location
	 * 
	 * @param paramLocation
	 * @return
	 */
	public boolean isOtherVehicle(VehicleLocation paramLocation) {
		for(Vehicle vehicle : VehiclesAPI.getVehicleManager().getVehicles().values()) { // runs through each vehicle in the VehicleManager
			if(vehicle != this) { // checks vehicle is the not this vehicle
				if(VehicleMath.locationInsideVehicle(vehicle, paramLocation.toLocation())) { // checks if they are inside of eachother
					return true; // returns true if there is a vehicle at that location
				}
			}
		}
		return false; // after completed iteration, no vehicles found, return false
	}

	//-------------------------------------------------------------------- HANDLES VEHICLE META ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private JSONObject getVehicleMeta() {
		
		JSONObject meta = new JSONObject(); // creates a new meta jsonobject
		meta.put("spawned", isSpawned()); // spawned to isspawned
		if(getLocation() != null) {
			meta.put("location", getLocation().toLocation().serialize()); // sets location to getLocation
		}
		
		return meta; // returns the meta object
	}
	
	/**
	 * Gets all of the meta for the vehicle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getMeta() {
		JSONObject meta = new JSONObject(); // creates a new JSONObject that will store all of the vehicles meta
		JSONObject componentsJSON = new JSONObject(); // creates a new JSONObject that will store all of the components meta
		for(Component component : this.getComponents()) { // iterates through each component in the vehicle
			if(componentsJSON.containsKey(component.getComponentName())) continue; // checks if another component with the same type, has already been added to component meta, if so that component can be skipped, as components are stored in lists
			List<Component> components = this.getComponentByType(component.getComponentName()); // gets all of the components in the vehicle, that are of the same component type as our current iteration, including iteself.
			List<JSONObject> componentsMeta = new ArrayList<JSONObject>(); // creates a new JSONObject that will store all of the components type meta
			for(int componentIndex = 0; componentIndex < components.size(); componentIndex++) { // index is from 0 to the number of the components in the vehicle that are of component type
				componentsMeta.add(components.get(componentIndex).getMeta()); // adds the meta of that component into components type JSONObject
			}
			
			componentsJSON.put(component.getComponentName(), componentsMeta); // after going through each component of component type, adds the JSONArray to the JSONObject with the key of the component type
		}
		// FINAL componentsJSON {INVENTORY:{[null]}, WHEELS:{[..., ..., ..., ...]}}
		meta.put("vehicle", getVehicleMeta()); // Retrieves the vehicles own meta and puts it into the meta JSONObject
		meta.put("components", componentsJSON); // puts the component meta into the meta JSONObject.
		
		return meta; // returns all of the vehicles meta
	}
	
	//-------------------------------------------------------------------- SHOW AND HIDE ------------------------------------------------------------------------

	/**
	 * Called the show the vehicle only when the chunk the vehicle is in is active, if it is called and the chunk is not active, then the vehicle will appear broken (STUPID MINECRAFT!)
	 */
	public void show() {

		if(!this.isSpawned()) return; // if the vehicle is not spawned then it will return, as the vehicle should not be shown. Also checks if the vehicle is shown, if it is shown then if shown again, will cause problems.
		
		for(Component component : components) { // runs through each of the components in the vehicle
			component.spawn(this.getLocation()); // spawns the component
		}
		
		setTaskID(new BukkitRunnable() { // creates a new Bukkit Runnable and sets the taskID to the taskID of the Runnable

			@Override
			public void run() {
				
				if(!isSpawned()) { // checks if the vehicle is spawned
					this.cancel();// if the vehicle is not spawned then it end the runnable
					return; // returns from the function
				}
				
				for(Component component : components) { // runs through each of the components
					
					if(!isDriver()) { // if there is no driver then it will call the noButtonPressForward and noButtonPressSideways
						noButtonPressForward();
						noButtonPressSideways();
					}
					
					
					component.tick(); // calls the tick function for each component
				}
				
			}
			
		}.runTaskTimer(VehiclesAPI.getPlugin(), 1, 1).getTaskId()); // runs the task every tick
		
		this.setHidden(false); // sets the vehicle to have the shown flag.
	}
	
	/**
	 * Called to hide the vehicle when the Vehicle is nolonger in an active chunk.
	 * Stops lag and duplicate vehicles from spawning
	 */
	public void hide() {
		
		if(!this.isSpawned()) return; // if the vehicle is not spawned then it cannot be hidden. Also checks if the vehicle is already hidden, if the vehicle is already hidden, then errors may occur.

		Bukkit.getScheduler().cancelTask(taskID); // cancels the repeating tick task
		
		for(Component component : components) {	// runs through each component in the vehicle
			component.despawn(); // despawns the component
		}
		
		this.setHidden(true); // sets the vehicle to have the hidden flag.
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR AND HALT ------------------------------------------------------------------------
	
	// CONSTRUCTOR
	public Vehicle(UUID paramUUID, int paramTypeID) {
		this.setUuid(paramUUID); // sets UUID to paramUUID
		this.setTypeID(paramTypeID);
		
		this.setBuilt(false); // sets built to false
		this.setSpawned(false); // sets spawned to false
	}
	
	/**
	 * Halts the vehicle
	 */
	public void halt() {
		this.haltComponents();
		VehiclesAPI.getVehiclesDatabase().saveVehicle(this); // saves the vehicle's data
		if(this.isSpawned()) { // checks if the vehicle is spawned
			this.despawn(); // despawns the vehicle
		}
	}
}
