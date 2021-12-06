package uk.hannam.vehiclesapi.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.hannam.vehiclesapi.enums.Messages;
import uk.hannam.vehiclesapi.enums.Permissions;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.models.VehicleType;

public class CommandSummonVehicle extends VehicleCommand {
	
	/**
	 * This command is used to spawn in any vehicle at anytime, easily allowing the user to test and for other people to spawn in vehicles.
	 */
	
	private static final String name = "summonvehicle"; // command name
	private static final String description = "Summon a vehicle"; // description
	private static final String usageMessage = "/summonvehicle <vehicletype> [<world> <x> <y> <z>]"; // usage
	private static final List<String> aliases = Arrays.asList("sv"); // aliases
	
	/**
	 * Constructor
	 * passes through the name description usage and aliases of the command, and adds then registers it.
	 */
	protected CommandSummonVehicle() {
		super(name, description, usageMessage, aliases);
	}

	/**
	 * Excuted command
	 */
	public boolean execute(CommandSender sender, String labels, String[] args) {
		
		Location spawnLocation = null; // defines spawnLocation of the vehicle
		
		// checks if the person who sent it is a player and not the console or any other command sender.
		if(sender instanceof Player) {
			
			// We have checked that the sender is a player and therefore will be no casting errors
			// assert sender instanceof Player;
			Player player = (Player) sender; // casts the sender to a player
			
			// checks if the player has the permission to summon the vehicle
			if(!Permissions.COMMAND_SUMMON_VEHICLE.hasPermission(player)) {
				// if they do not then it will send them the invalid permission message
				Messages.INVALID_PERMISSION.sendMessage(player);
				return true; // breaks (returns true)
			}
			
			/*
			 * Exepected Argument Structor (0) vehicleType [(1) world (2) x (3) y (4) z] 
			 * total = 5
			 * allow min 1 argument
			 * replace location if 5 arguments
			 */
			if(args.length == 0) { // checks if there are 0 arguments
				// if there are 0 arguments then it will not know which vehicle to spawn
				Messages.COMMAND_SUMMON_VEHICLE_USAGE.sendMessage(player); // sends the usage to the player
				return true; // breaks
			}
			
			// checks if args length is not 5, if it is not 5 then it will not have a spawn location built in 
			if(args.length != 5) {
				spawnLocation = player.getLocation(); // if there are not 5 arguments then there is not a provided location so spawn the vehicle at the feet of the player
			}
		
		} else { // command sender is not a player
			
			// as the sender will not have a location as it is not a player we cannot spawn it at the sender's feet therefore it must have a location provided
			if(args.length != 5) { // check to see if there are 5 arguments
				Messages.COMMAND_SUMMON_VEHICLE_USAGE.sendCommandSender(sender); // sends the usage to the console
				return true; // breaks
			}
		}
		
		
		assert args.length > 0; // we have already checked the args length and we know that there is atleast one argument
		
		// grabs the vehicle type from the argument 0
		// possible error: argument out of bounds: checked args.length and it is atleast 1
		VehicleType vehicleType;
		try{
			vehicleType = VehicleType.getTypeFromID(Integer.valueOf(args[0])); // gets the vehicle type from args[0]
			if(vehicleType== null) { // checks if the vehicletype is null
				throw new NumberFormatException(); // throws a new exception if the vehicle id is not valid
			}
		} catch(NumberFormatException e) {
			Messages.INVALID_VEHICLE.sendCommandSender(sender); // sends the sender a message to tell them their input was rejected
			return true;
		}
		
		if(args.length == 5) { // checks to see if we need to get a location from the arguments
			try {
				
				// NO out of bounds errors
				
				// possible error of world not found (NullPointerException)
				World world = Bukkit.getWorld(args[1]);
				if(world == null) throw new NullPointerException(); // if the world ==null then we don't want it so we throw a new exception
				
				// possible errors of NumberFormatException when converting the x, y, z coordinates from strings to doubles
				double x = Double.valueOf(args[2]);
				double y = Double.valueOf(args[3]);
				double z = Double.valueOf(args[4]);
				
				// registers a new location (no errors present)
				
				spawnLocation = new Location(world, x, y, z);
				
			} catch(NullPointerException | NumberFormatException e) {
				Messages.COMMAND_SUMMON_VEHICLE_USAGE.sendCommandSender(sender); // if there is miss-input then the user will be sent the usage message
				return true; // breaks
			}
		}
		
		// assert to make sure that spawn location is not null, it should be set by arguments or the player's location
		assert spawnLocation != null;
		
		// registers a new vehicle
		Vehicle vehicle = VehiclesAPI.getVehicleManager().createVehicle(vehicleType);
		
		// performs the spawn method on the vehicle
		vehicle.spawn(VehicleLocation.fromLocation(spawnLocation));
		
		if(sender instanceof Player) { // Checks if sender is a player
			VehiclesAPI.getUserManager().getUser(((Player) sender).getUniqueId()).quickSetDrive(vehicle); // makes the player drive the vehicle
		}
		
		// sends the sender a message confirming their action
		Messages.SUCCESSFULLY_SPAWNED_VEHICLE.sendCommandSender(sender);
		return true; // breaks
	}
}
