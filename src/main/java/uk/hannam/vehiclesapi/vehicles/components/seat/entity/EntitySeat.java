package uk.hannam.vehiclesapi.vehicles.components.seat.entity;

import uk.hannam.vehiclesapi.utils.Point4D;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleEntity;

public class EntitySeat extends VehicleEntity {

	public EntitySeat(Vehicle paramVehicle, Point4D paramLocation) {
		super(paramVehicle, paramLocation);
		
		this.setMarker(true);
	}
}
