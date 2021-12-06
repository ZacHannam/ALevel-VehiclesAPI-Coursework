package uk.hannam.vehiclesapi.user;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.hannam.vehiclesapi.user.exceptions.UserAlreadyExistsException;
import uk.hannam.vehiclesapi.user.exceptions.UserDoesNotExistException;

// hashmap - UUID, User
public class UserManager extends HashMap<UUID, User> {

	/**
	 * serial id
	 */
	private static final long serialVersionUID = 2021037298994875611L;
	
	/**
	 * called whenever the object is disabled
	 */
	public void halt() {
		this.haltAllUsers(); // halts all the users
	}
	
	/**
	 * Constructor:
	 */
	public UserManager() {
		this.loadAllUsers(); // loads all of the users
	}
	
	/**
	 * checks if the user is inside of the hashmap.
	 * 
	 * @param paramUUID
	 * @return
	 */
	public boolean isUser(UUID paramUUID) {
		return this.containsKey(paramUUID); // returns uuid in hashmap (is user)
	}
	
	/**
	 * returns the user from the uuid
	 * 
	 * @param paramUUID
	 * @return
	 */
	public User getUser(UUID paramUUID) {
		
		if(this.containsKey(paramUUID)) { // checks if the user exists
			return this.get(paramUUID); // returns the user
		
		}
		// user does not exist (throws exception UserDoesNotExist)
		throw new RuntimeException(new UserDoesNotExistException());
		
	}
	
	/**
	 * creates the user
	 * @param paramPlayer
	 */
	public void createUser(Player paramPlayer) {

		if(this.containsKey(paramPlayer.getUniqueId())) { // checks if the map already contains a User object with the same UUID
			throw new RuntimeException(new UserAlreadyExistsException()); // throws UserAlreadyExists exception
		}
		this.put(paramPlayer.getUniqueId(), new User(paramPlayer)); // puts the new user object in the map (only if there isn't one already there with the same key)
		
	}
	
	/**
	 * halts a user (called to stop a user)
	 * takes player input
	 * 
	 * @param paramPlayer
	 */
	public void haltUser(Player paramPlayer) {
		if(this.containsKey(paramPlayer.getUniqueId())) { // checks if the user in the map
			this.performHalt(this.get(paramPlayer.getUniqueId())); // performs the private halt user method
			return; // returns from mehtod
		}
		
		throw new RuntimeException(new UserDoesNotExistException()); // if the user not in the map, throws UserNotFoundException
	}
	

	
	/**
	 *  Used to load all users currently playing on the server
	 */
	public void loadAllUsers() {
		
		for(Player player : Bukkit.getServer().getOnlinePlayers()) { // iterates through all players on the server
			this.createUser(player); // creates a new user object for each player
		}
	}
	
	/**
	 * halts all users on the server
	 */
	public void haltAllUsers() {
		
		for(Player player : Bukkit.getServer().getOnlinePlayers()) { // run through all players on the server cannot run through all players in map otherwise ConcurrentModificationError as it removes each user from the map
			this.haltUser(player); // halts the user
		}		
	}
	
	/**
	 * private method to halt the user
	 * all methods to halt the user in here
	 * 
	 * @param paramUser
	 */
	private void performHalt(User paramUser) {
		paramUser.halt(); // halts the user class
		this.remove(paramUser.getUuid()); // removes the user from the map
	}
	
}
