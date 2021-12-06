package uk.hannam.vehiclesapi.user.exceptions;

public class UserDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1613728861348538503L;
	
	/*
	 * Called whenever the you try and get a user that does not exist, i.e using an offline player
	 */
	public UserDoesNotExistException() {
		
	}

}
