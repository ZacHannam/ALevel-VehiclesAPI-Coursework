package uk.hannam.vehiclesapi.vehicles.exceptions;

public class TooManyDriverSeatsException extends Exception {

	private static final long serialVersionUID = 670365403969722106L;

	public TooManyDriverSeatsException() {
		super("Too many driver seats found when building vehicle.");
	}
}