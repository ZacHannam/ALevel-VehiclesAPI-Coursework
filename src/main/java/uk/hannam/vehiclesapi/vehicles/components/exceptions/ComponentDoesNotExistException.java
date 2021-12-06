package uk.hannam.vehiclesapi.vehicles.components.exceptions;

public class ComponentDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7029259790265887332L;

	public ComponentDoesNotExistException(String paramComponentName) {
		super("Component does not exist: " + paramComponentName);
	}
	
}
