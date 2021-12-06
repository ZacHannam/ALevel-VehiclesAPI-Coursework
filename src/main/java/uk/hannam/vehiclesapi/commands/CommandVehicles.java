package uk.hannam.vehiclesapi.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import uk.hannam.vehiclesapi.enums.Messages;
import uk.hannam.vehiclesapi.enums.Permissions;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;

public class CommandVehicles extends VehicleCommand {

	private static final String name = "vehicles"; // command name
	private static final String description = "Main vehicles command"; // description
	private static final String usageMessage = "/vehicles <reload | destroynear | destroyall> [range]"; // usage
	private static final List<String> aliases = Arrays.asList("ve"); // aliases
	
	protected CommandVehicles() {
		super(name, description, usageMessage, aliases);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		
		if(args.length < 1) {
			Messages.VEHICLES_USAGE.sendCommandSender(sender);
			return true;
		}
		
		if(args[0].matches("(?i)reload")) {
			if(!sender.hasPermission(Permissions.COMMAND_VEHICLES_RELOAD.getPermission())) {
				Messages.INVALID_PERMISSION.sendCommandSender(sender);
				return true;
			}
			
			VehiclesAPI.reload();
			Messages.RELOADED_PLUGIN.sendCommandSender(sender);
			
		} else if(args[0].matches("(?i)destroyall")) {
			
			if(!sender.hasPermission(Permissions.COMMAND_VEHICLES_DESTROYALL.getPermission())) {
				Messages.INVALID_PERMISSION.sendCommandSender(sender);
				return true;
			}
			
			int numberDestroyed = VehiclesAPI.getVehicleManager().destroyAllVehicles();
			
			Messages.VEHICLES_DESTROYED.sendCommandSender(sender, ImmutableMap.of("%amount%", String.valueOf(numberDestroyed)));
			
		} else if(args[0].matches("(?i)destroynear")) {
			
			if(!sender.hasPermission(Permissions.COMMAND_VEHICLES_DESTROYNEAR.getPermission())) {
				Messages.INVALID_PERMISSION.sendCommandSender(sender);
				return true;
			}
			
			if(args.length < 2) {
				Messages.VEHICLES_USAGE.sendCommandSender(sender);
				return true;
			}
			
			if(!(sender instanceof Player)) {
				Messages.MUST_BE_PLAYER.sendCommandSender(sender);
				return true;
			}
			
			Player player = (Player) sender;
			
			try {
				double distance = Double.valueOf(args[1]);
				
				if(distance <= 0) {
					throw new NumberFormatException();
				}
				
				Collection<Vehicle> vehiclesNoAccess = VehiclesAPI.getVehicleManager().getVehicles().values();
				List<Vehicle> vehicles = new ArrayList<Vehicle>(vehiclesNoAccess);
				
				int numberDestroyed = 0;
				for(Vehicle vehicle : vehicles) {
					if(player.getLocation().distance(vehicle.getLocation().toLocation()) <= distance) {
						VehiclesAPI.getVehicleManager().destroyVehicle(vehicle);
						numberDestroyed++;
					}
				}
				
				Messages.VEHICLES_DESTROYED.sendCommandSender(sender, ImmutableMap.of("%amount%", String.valueOf(numberDestroyed)));
			} catch(NumberFormatException e) {
				Messages.VEHICLES_USAGE.sendCommandSender(sender);
			}
		} else {
			Messages.VEHICLES_USAGE.sendCommandSender(sender);
		}
		
		return false;
	}
	
	

}
