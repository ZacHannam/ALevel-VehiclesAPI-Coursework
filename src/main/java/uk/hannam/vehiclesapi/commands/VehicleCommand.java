package uk.hannam.vehiclesapi.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import uk.hannam.vehiclesapi.main.VehiclesAPI;

public abstract class VehicleCommand extends BukkitCommand { // abstract cannot be instantiated.

	/**
	 * CONSTRUCTOR methods
	 * passed in by the command, and is then used by the BukkitCommand parent class.
	 * 
	 * @param name
	 * @param description
	 * @param usageMessage
	 * @param aliases
	 */
	protected VehicleCommand(String name, String description, String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}
	
	private SimpleCommandMap getCommandMap() {
		// By grabbing the class this way, it allows for multiversion support from Minecraft, as their package names change for every different version, however methods mostly stay the same
		String serverVersion = VehiclesAPI.getPlugin().getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]; // gets the version of the server
		String classPath = "org.bukkit.craftbukkit."+serverVersion+".CraftServer"; // gets the class name of the CraftServer (not from the API)
		Class<?> craftserver = null; // defines craftserver class object
		try {
			craftserver = Class.forName(classPath);// tries to get the class from the package location
		} catch (ClassNotFoundException e) { // there should be no errors
			e.printStackTrace(); // prints the error if there is one.
		}
		
		// craft server should not be null and if it is then there was an error above.
		assert craftserver != null;
		try {
			return (SimpleCommandMap) craftserver.cast(VehiclesAPI.getPlugin().getServer()).getClass().getMethod("getCommandMap")
					.invoke(VehiclesAPI.getPlugin().getServer()); // gets the command map in use by the server
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace(); // should be absolutely no errors, however exceptions are not thrown in runtime so we have to check for them.
		}
		throw new NullPointerException();
	}

	/**
	 * Registers the command into the command map for the server.
	 * 
	 */
	protected void registerCommand() {
		
		this.getCommandMap().register(VehiclesAPI.getPlugin().getName(), this); // registers command
    }
	
	/**
	 * Unregisters the command from the command map for the server.
	 */
	protected void unregisterCommand() {
		this.unregister(this.getCommandMap()); //unregisters command
    }
	
}
