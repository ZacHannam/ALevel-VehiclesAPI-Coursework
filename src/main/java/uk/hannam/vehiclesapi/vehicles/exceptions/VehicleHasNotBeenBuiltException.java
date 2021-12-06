package uk.hannam.vehiclesapi.vehicles.exceptions;

public class VehicleHasNotBeenBuiltException extends Exception {

	private static final long serialVersionUID = -1622254378623852474L;

	public VehicleHasNotBeenBuiltException() {
		super("Vehicle has not been built!");
	}

}
