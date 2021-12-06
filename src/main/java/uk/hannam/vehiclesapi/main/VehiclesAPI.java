package uk.hannam.vehiclesapi.main;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import uk.hannam.vehiclesapi.chunk.ChunkManager;
import uk.hannam.vehiclesapi.commands.CommandManager;
import uk.hannam.vehiclesapi.database.VehiclesDatabase;
import uk.hannam.vehiclesapi.enums.Messages;
import uk.hannam.vehiclesapi.events.EventManager;
import uk.hannam.vehiclesapi.user.UserManager;
import uk.hannam.vehiclesapi.vehicles.VehicleManager;
import uk.hannam.vehiclesapi.vehicles.models.VehicleType;

public class VehiclesAPI extends JavaPlugin {
	
	@Getter
	public static Plugin plugin;
	
	@Getter
	public static VehicleManager vehicleManager;
	
	@Getter
	public static UserManager userManager;
	
	@Getter
	public static EventManager eventManager;
	
	@Getter
	public static CommandManager commandManager;
	
	@Getter
	public static VehiclesDatabase vehiclesDatabase;
	
	@Getter
	public static ChunkManager chunkManager;

	public void onEnable() {
	
		plugin = this;
		Messages.reload();
		eventManager = new EventManager();
		chunkManager = new ChunkManager();
		vehicleManager = new VehicleManager();
		userManager = new UserManager();
		commandManager = new CommandManager();	
		vehiclesDatabase = new VehiclesDatabase();
		
		VehicleType.loadAll();
		
	}
	
	public void onDisable() {
		
		vehicleManager.halt();
		vehiclesDatabase.halt();
		userManager.halt();
		commandManager.halt();	
		
	}

	public static void reload() {
		vehicleManager.halt();
		vehiclesDatabase.halt();
		userManager.halt();
		
		Messages.reload();
		chunkManager = new ChunkManager();
		vehicleManager = new VehicleManager();
		userManager = new UserManager();
		vehiclesDatabase = new VehiclesDatabase();
		
		VehicleType.reload();
	}
}
