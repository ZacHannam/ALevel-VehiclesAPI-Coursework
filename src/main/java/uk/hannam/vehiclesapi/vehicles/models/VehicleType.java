package uk.hannam.vehiclesapi.vehicles.models;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.models.exceptions.NameOrIdAlreadyInUseException;

enum RegisteredVehicle{
	
	KART_1(Kart_1.class, 1),
	RACECAR_1( RaceCar_1.class, 2),
	LOWRIDER_1( Lowrider_1.class, 3);
	
	/**
	 * Loads all of the registered vehicles (these are the vehicles provided with the API!)
	 */
	public static void loadAllVehicles() {
		for(RegisteredVehicle type : values()) { // iterates through each vehicle model
			new VehicleType(type.getVehicleClass(), type.getID()); // creates a new vehicle type to register it into the actual API
		}
	}
	
	@Getter
	@Setter
	private int ID; // ID of the vehicle (MOST COMMONLY USED) (MUST BE UNIQUE)
	
	@Getter
	@Setter
	private Class<? extends Vehicle> vehicleClass; // Vehicle model class (Must have the Vehicle class as a parent)
	
	/**
	 * Constructor for enum item
	 * @param paramVehicleClass
	 * @param paramID
	 */
	RegisteredVehicle(Class<? extends Vehicle> paramVehicleClass, int paramID){
		this.setID(paramID);
		this.setVehicleClass(paramVehicleClass);
	}
}

public class VehicleType {
	
	@Getter
	private static final HashMap<Integer, VehicleType> ID_MAP = new HashMap<Integer, VehicleType>(); // stores a list of ids and their VehicleType
	
	/**
	 * Used to get the VehicleType from its ID
	 * @param paramID
	 * @return
	 */
	public static VehicleType getTypeFromID(int paramID) {
		if(ID_MAP.containsKey(paramID)) { // checks if the ID is valid
			return ID_MAP.get(paramID); // returns the corresponding VehicleType
		}
		return null; // if the ID is not valid null will be returned instead
		
	}
	
	@Getter
	@Setter
	private int ID; // ID of the vehicle (MOST COMMONLY USED) (MUST BE UNIQUE)
	
	@Getter
	@Setter
	private Class<? extends Vehicle> vehicleClass; // Vehicle model class (Must have the Vehicle class as a parent)
	
	/**
	 * Constructor which setups the object, and puts it in the static ID_MAP and NAME_MAP
	 * @param paramVehicleClass
	 * @param paramID
	 */
	public VehicleType(Class<? extends Vehicle> paramVehicleClass, int paramID) {
		this.setID(paramID);
		this.setVehicleClass(paramVehicleClass);
		
		if(getID_MAP().containsKey(this.getID())) { // checks if the key and name are already in use
			throw new RuntimeException(new NameOrIdAlreadyInUseException()); // raises a new exception as atleast one is already used
		}
		
		if(this.getID() > 0) { // checks if the ID is greater than 0, IDS must be bigger than 0. Otherwise the VehicleType cannot be found using its ID
			getID_MAP().put(this.getID(), this); // adds the ID and VehicleType into ID_MAP
			VehiclesAPI.getVehiclesDatabase().loadAllWithID(paramID); // Now that the VehicleType has been 'discovered' it is safe to load in all vehicles that use the id.
		}
	}

	/**
	 * Called to load in all API defined vehicles. 
	 */
	public static void reload() {
		for(int ID : ID_MAP.keySet()) { // goes through each vehicle in the id map
			VehiclesAPI.getVehiclesDatabase().loadAllWithID(ID); // loads that vehicle
		}
		
	}

	public static void loadAll() {
		RegisteredVehicle.loadAllVehicles(); // loads all of the vehicles.
	}
}
