package uk.hannam.vehiclesapi.vehicles.components.seat;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.components.seat.entity.EntitySeat;
import uk.hannam.vehiclesapi.vehicles.components.seat.exceptions.SeatOccupiedException;

public interface Seat {
	
	void setRider(Player paramPlayer) throws SeatOccupiedException;
	boolean hasRider();
	Player getRider();
	void ejectRider();
	EntitySeat getSeatEntity();
	void setSeatEntity(EntitySeat paramSeatEntity);
	Vector getOffset();
	void setOffset(Vector paramOffset);
	Location getLocation();
	ArmorStand getArmorStand();
}