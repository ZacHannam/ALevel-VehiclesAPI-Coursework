package uk.hannam.vehiclesapi.commands;

import java.io.Serial;
import java.util.ArrayList;

// Array to hold all the commands
public class CommandManager extends ArrayList<VehicleCommand> {
	
	// default generated serial
	@Serial
	private static final long serialVersionUID = -8834960651506437630L;

	/**
	 *  CONSTRUCTOR
	 *  
	 *  This method is used to define all commands added to the server
	 *  It handles all of the commands and registers and unregisters them
	 */
	public CommandManager() {
		this.register(new CommandSummonVehicle());
		this.register(new CommandVehicles());
	}

	/**
	 * Unregisters all of the commands from the command map, so that after reload there are not multiple commands in the map
	 */
	public void halt() {
		for(VehicleCommand command : this) { // iterates through all commands in the list
			command.unregisterCommand(); // unregisters the command
		}
	}

	/**
	 * Registers the command by using a pre built method in VehicleCommand class
	 */
	public void register(VehicleCommand paramCommand) {
		paramCommand.registerCommand(); // registers the command
		this.add(paramCommand); // adds it to this list of command
	}
	
}
