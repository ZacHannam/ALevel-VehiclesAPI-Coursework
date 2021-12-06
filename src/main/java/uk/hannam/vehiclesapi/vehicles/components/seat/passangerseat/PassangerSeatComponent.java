package uk.hannam.vehiclesapi.vehicles.components.seat.passangerseat;

import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.seat.SeatComponent;

public abstract class PassangerSeatComponent extends SeatComponent {
	
	//-------------------------------------------------------------------- COMPOONENT METHODS ------------------------------------------------------------------------
	
	
	@Override
	public String getComponentName() {
		return ComponentName.PASSANGER_SEAT.getName();
	}

	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------
	
	public PassangerSeatComponent(Vehicle paramVehicle) {
		super(paramVehicle);
	}
}