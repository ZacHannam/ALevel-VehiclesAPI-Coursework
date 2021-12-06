package uk.hannam.vehiclesapi.chunk.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import uk.hannam.vehiclesapi.main.VehiclesAPI;

public class ChunkUnload implements Listener {
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		VehiclesAPI.getChunkManager().unloadChunk(event.getChunk());
	}
}
