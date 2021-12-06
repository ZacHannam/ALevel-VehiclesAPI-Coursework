package uk.hannam.vehiclesapi.chunk.exceptions;

public class VehicleHasNoLocationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VehicleHasNoLocationException() {
		super("The vehicle has no location and therefore cannot be added to the buffer");
	}
}
