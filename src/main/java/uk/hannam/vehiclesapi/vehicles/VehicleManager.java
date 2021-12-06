package uk.hannam.vehiclesapi.vehicles;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.Getter;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.exceptions.CouldNotCreateVehicleException;
import uk.hannam.vehiclesapi.vehicles.models.VehicleType;

public class VehicleManager {

	/**
	 * Contains a HashMap of the Vehicle and their UUID 
	 */
	@Getter
	private final HashMap<UUID, Vehicle> vehicles = new HashMap<UUID, Vehicle>();
	
	/**
	 * Used to create a vehicle from a vehicle type, it will auto assign UUID's
	 * @param vehicleType
	 * @return
	 */
	public Vehicle createVehicle(VehicleType vehicleType) {
		
		UUID uuid = UUID.randomUUID(); // gets a new UUID from the random UUID section
		try {
			Vehicle vehicle = vehicleType.getVehicleClass().getDeclaredConstructor(UUID.class, int.class).newInstance(uuid, vehicleType.getID()); // creates a new vehicle using the uuid and the vehicle type
			vehicle.build(); // builds the vehicle
			vehicles.put(uuid, vehicle); // adds the vehicle to the vehicles hashamp alongside its generated UUID
			return vehicle; // returns the vehicle
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(new CouldNotCreateVehicleException(String.valueOf(vehicleType.getID()))); // throws a new exception if the vehicle has not been created
		}
	}
	
	/**
	 * Used to halt the class
	 */
	public void halt() {
		for(Vehicle vehicle : this.getVehicles().values()) { // goes through each vehicle on the server
			
			vehicle.halt(); // halts the vehicle
		}
		this.getVehicles().clear(); // clears the vehicles hashmap
	}

	/**
	 * Used to despawn a vehicle and halt it.
	 * @param paramVehicle
	 */
	private void destroyVehicle(Vehicle paramVehicle, boolean paramRemoveFromMap) { 
		if(paramVehicle.isSpawned()) { // checks if the vehicle is spawned
			paramVehicle.despawn();
		}
		paramVehicle.halt(); // halts the vehicle
		
		VehiclesAPI.getVehiclesDatabase().removeVehicle(paramVehicle.getUuid());
		
		if(paramRemoveFromMap) {
			this.getVehicles().remove(paramVehicle.getUuid()); // removes the vehicle from the hashmap
		}
	}
	
	/**
	 * Destroys the vehicle using the vehicle's UUID
	 * @param paramUUID
	 */
	public void destroyVehicle(UUID paramUUID) {
		if(this.isVehicle(paramUUID)) { // checks if the UUID is a vehicle
			
			Vehicle vehicle = this.getVehicleFromUUID(paramUUID); // gets the Vehicle
			
			this.destroyVehicle(vehicle, true); // destroys the vehicle
		}
	}
	
	public void destroyVehicle(Vehicle paramVehicle) {
		this.destroyVehicle(paramVehicle, true); // destroys the vehicle
	}
	
	/**
	 * Used to destroy all of the vehicles on the server permanently.
	 * Returns the number of vehicles destroyed
	 * @return
	 */
	public int destroyAllVehicles() {
		
		for(Vehicle vehicle : this.getVehicles().values()) {
			
			this.destroyVehicle(vehicle, false);
		
		}
		
		int numberOfVehicles = this.getNumberOfVehicles();
		this.getVehicles().clear();
		return numberOfVehicles;
	}

	/**
	 * Used to check if their is a vehicle at a location
	 * @param paramLocation
	 * @return
	 */
	public boolean isVehicleAtLocation(VehicleLocation paramLocation) {
		for(Vehicle vehicle : this.getVehicles().values()) { // goes through each vehicle in the vehicles hashmap
			if(VehicleMath.locationInsideVehicle(vehicle, paramLocation.toLocation())) { // checks if the point is within the vehicle
				return true; // returns true as their is a vehicle at that location
			}
		}
		return false; // returns false as there is no vehicle found at that location
	}
	
	/**
	 * Checks if there is a vehicle
	 * @param paramUUID
	 * @return
	 */
	public boolean isVehicle(UUID paramUUID) {
		return this.getVehicles().containsKey(paramUUID); // returns the value of the element being in the hashmap
	}
	
	/**
	 * 
	 * @param paramUUID
	 * @return
	 */
	public Vehicle getVehicleFromUUID(UUID paramUUID) {
		if(this.isVehicle(paramUUID)) { // checks if the UUID is a vehicle
			return this.getVehicles().get(paramUUID); // returns the vehicle found from the hashmap
		}
		return null; // if no vehicle is found, then returns null
	}

	/**
	 * returns the number of vehicles in the vehicle hashmap
	 * @return
	 */
	public int getNumberOfVehicles() {
		return this.getVehicles().size(); // returns the size of the hashmap
	}
	
	/**
	 * Used to create a vehicle from the database, using its meta to generate it.
	 * @param paramUUIDAsString
	 * @param paramType
	 * @param paramMetaAsString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vehicle createFromDatabaseQuery(String paramUUIDAsString, int paramType, String paramMetaAsString) {
		
		VehicleType vehicleType = VehicleType.getTypeFromID(paramType); // gets the vehicletype using the vehicle's type
		
		if(vehicleType == null) return null; // checks that the vehicle id is not null (this should never happen!)
		
		JSONParser parser = new JSONParser(); // defines a new json parser, necessary for turning strings into jsonobjects
		
		try {
			
			JSONObject meta = (JSONObject) parser.parse(paramMetaAsString); // turns the string meta into meta json
			
			UUID uuid = UUID.fromString(paramUUIDAsString); // loads the UUID into a String
			
			Vehicle vehicle = vehicleType.getVehicleClass().getDeclaredConstructor(UUID.class, int.class).newInstance(uuid, vehicleType.getID()); // creates a new vehicle using the uuid and the vehicle type
			
			vehicle.build(); // builds the vehicle
			
			/*
			 * OVERVIEW
			 * 			2 INVENTORIES WITH META		1 FRAME NO META	2 WHEELBASE 1 WITH META, 1 WITHOUT.
			 * components:{[INVENTORY:{[..., ...]}, FRAME:{[null]}], WHEEL_BASE:{[..., null]}]}
			 * 
			 */
			
			JSONObject componentMeta = ((JSONObject) meta.get("components")); // creates a new sub JSONObject where the primary key is components
			a: for(Object keyObj : componentMeta.keySet()) { // goes through each key (COMPONENT TYPE ATTACHED TO VEHICLE) in the vehicle
				String key = (String) keyObj; // casts it into a string
				JSONArray values = (JSONArray) componentMeta.get(keyObj); // turns [..., ....] into a JSONArray
				if((values.size() == 0) || (values.size() == 1 && values.get(0) == null)) continue a; // checks if the jsonarray is empty OR the only item in the array is null, which means that there is no saved meta for this component, continues to the next component if thats the case
				List<Component> components = vehicle.getComponentByType(key); // gets a dynamic list of all of the KEY named components in the vehicle that 
				b: for(int index = 0; index < components.size(); index++) { // let index go from 0 to len(components) -1
					if(values.get(index) == null) continue b; // checks if the data for that component is null, if it is then it will continue to the component in the jsonarray
					
					JSONObject cpMeta = (JSONObject) values.get(index); // creates another subset of JSONObject, where the primary key is the item at position index inside of the JSONArray
					components.get(index).buildFromMeta(cpMeta); // calls on the component to handle whatever else is inside of the JSON Object
				}
			}
			
			
			vehicles.put(uuid, vehicle); // adds the vehicle to the vehicles hashamp alongside its generated UUID
			

			//vehicle.setLocation(location);
			
			if((boolean) ((JSONObject) meta.get("vehicle")).get("spawned")) { // checks if the vehicle should be spawned
				VehicleLocation location = VehicleLocation.fromLocation((Location.deserialize((Map<String, Object>) ((JSONObject) meta.get("vehicle")).get("location")))); // if the vehicle is spawned then there will be a location
				
				vehicle.spawn(location); // spawns the vehicle at that location
			}
			
			return vehicle; // returns the vehicle
		} catch (ParseException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(new CouldNotCreateVehicleException(String.valueOf(vehicleType.getID()))); // throws a new exception if the vehicle has not been created
		}
	}
}
