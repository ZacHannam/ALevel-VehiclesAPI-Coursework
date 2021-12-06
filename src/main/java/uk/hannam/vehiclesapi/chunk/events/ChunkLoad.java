package uk.hannam.vehiclesapi.chunk.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;

public class ChunkLoad implements Listener {

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		VehiclesAPI.getChunkManager().loadChunk(event.getChunk());
	}
	
}
