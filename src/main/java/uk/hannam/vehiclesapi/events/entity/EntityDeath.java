package uk.hannam.vehiclesapi.events.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import uk.hannam.vehiclesapi.vehicles.VehicleEntity;

public class EntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(VehicleEntity.isVehicle(event.getEntity().getUniqueId())) {
            VehicleEntity.removeVehicleEntity(event.getEntity().getUniqueId());
        }
        return;
    }

}
