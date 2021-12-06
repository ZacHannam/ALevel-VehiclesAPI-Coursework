package uk.hannam.vehiclesapi.vehicles.models.exceptions;

public class NameOrIdAlreadyInUseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2433445303089082097L;
	
	public NameOrIdAlreadyInUseException() {
		super("Name or ID of VehicleType is already in use.");
	}
}
