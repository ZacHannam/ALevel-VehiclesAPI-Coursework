package uk.hannam.vehiclesapi.user.exceptions;

public class UserAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 8532731702590770072L;

	/*
	 * This is called when the user being created already exists inside of the user's hashmap, i.e meaning that you added a player even though they were already playing.
	 */
	public UserAlreadyExistsException() {
		
	}
}
