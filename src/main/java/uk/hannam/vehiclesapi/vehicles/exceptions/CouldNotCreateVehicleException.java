package uk.hannam.vehiclesapi.vehicles.exceptions;

public class CouldNotCreateVehicleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4121947868615663278L;
	
	public CouldNotCreateVehicleException(String paramName) {
		super("Vehicle could not be created: " + paramName);
	}

}
