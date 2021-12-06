package uk.hannam.vehiclesapi.events;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import lombok.Getter;
import uk.hannam.vehiclesapi.chunk.events.ChunkLoad;
import uk.hannam.vehiclesapi.chunk.events.ChunkUnload;
import uk.hannam.vehiclesapi.events.entity.EntityDeath;
import uk.hannam.vehiclesapi.events.player.EntityDismount;
import uk.hannam.vehiclesapi.events.player.InventoryClick;
import uk.hannam.vehiclesapi.events.player.InventoryClose;
import uk.hannam.vehiclesapi.events.player.PlayerArmorStandManipulate;
import uk.hannam.vehiclesapi.events.player.PlayerInteract;
import uk.hannam.vehiclesapi.events.player.PlayerInteractAtEntity;
import uk.hannam.vehiclesapi.events.player.PlayerJoin;
import uk.hannam.vehiclesapi.events.player.PlayerQuit;
import uk.hannam.vehiclesapi.events.protocollib.PlayClientSteerVehicle;
import uk.hannam.vehiclesapi.main.VehiclesAPI;

class ProtocolLibEventManager {
	
	public ProtocolLibEventManager() {
		new PlayClientSteerVehicle();
	}
}

public class EventManager {

	@Getter
	private final ProtocolLibEventManager protocolLibEventManager;
	
	public EventManager() {
		
		this.protocolLibEventManager = new ProtocolLibEventManager();
		
		Plugin plugin = VehiclesAPI.getPlugin();
		PluginManager pluginManager = Bukkit.getPluginManager();
		
		pluginManager.registerEvents(new ChunkLoad(), plugin);
		pluginManager.registerEvents(new ChunkUnload(), plugin);
		pluginManager.registerEvents(new PlayerJoin(), plugin);
		pluginManager.registerEvents(new PlayerQuit(), plugin);
		pluginManager.registerEvents(new EntityDismount(), plugin);
		pluginManager.registerEvents(new PlayerInteract(), plugin);
		pluginManager.registerEvents(new PlayerArmorStandManipulate(), plugin);
		pluginManager.registerEvents(new InventoryClick(), plugin);
		pluginManager.registerEvents(new InventoryClose(), plugin);
		pluginManager.registerEvents(new PlayerInteractAtEntity(), plugin);
		pluginManager.registerEvents(new EntityDeath(), plugin);
	}
}
