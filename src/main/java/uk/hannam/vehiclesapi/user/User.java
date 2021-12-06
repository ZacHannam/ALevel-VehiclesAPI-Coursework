package uk.hannam.vehiclesapi.user;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.enums.Messages;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.inventory.InventoryComponent;
import uk.hannam.vehiclesapi.vehicles.components.seat.Seat;
import uk.hannam.vehiclesapi.vehicles.components.seat.SeatComponent;
import uk.hannam.vehiclesapi.vehicles.components.seat.driverseat.DriverSeat;
import uk.hannam.vehiclesapi.vehicles.components.seat.exceptions.SeatOccupiedException;

// no overriding methods 
public final class User {

	// DEBOUNCE +
	/*
	 * Debounce is to stop the user from accidently clicking multiple times when they mean to only click once
	 */

	private static final long DEBOUNCE_TIME = 3; // debounce time in ticks (20 ticks / second)

	@Getter
	private boolean inDebounce; // in debounce

	/**
	 * adds a debounce to the user so that they cannot perform any more interactions for the set debounce times
	 */
	public void addDebounce() {

		this.inDebounce = true; // sets debounce to true

		// bukkit runnable (performs everything a set time later)
		new BukkitRunnable() { 

			@Override 
			public void run() { // will perform DEBOUNCE_TIME later
				inDebounce = false; // sets debounce to false
			}

		}.runTaskLater(VehiclesAPI.getPlugin(), DEBOUNCE_TIME);

	}


	// DEBOUNCE -

	// VEHICLE +

	@Getter
	@Setter
	private Seat seat; // seat the user is sat on

	/**
	 * returns the vehicle that the user is in, from the seat
	 * 
	 * NO SEAT -> returns null
	 * SEAT -> returns vehicle
	 * 
	 * @return
	 */
	public Vehicle getVehicle() {
		if(this.getSeat() == null) { // checks if the seat is null
			return null; // if so returns null
		}
		return ((SeatComponent) this.getSeat()).getVehicle(); // returns the vehicle the seat is attached to
	}

	/**
	 * dismounts the user from the seat / vehicle object
	 */
	public void dismount() {
		if(this.getSeat() != null) { // checks if the seat is not null
			this.getSeat().ejectRider(); // ejects the ride from the seat
			this.setSeat(null); // sets the seat to be null		
		}
	}

	/**
	 * returns the user being sat on a seat
	 * @return
	 */
	public boolean inSeat() {
		return seat != null && this.getPlayer().getVehicle() != null;
	}

	/**
	 * returns the user being sat on a driver seat
	 * @return
	 */
	public boolean inDriverSeat() {
		return inSeat() && seat instanceof DriverSeat;
	}

	/*
	 * Instantly put them in the drivers seat of a vehicle
	 */
	public void quickSetDrive(Vehicle paramVehicle) {

		if(this.inSeat()) { // checks if player is in seat
			Messages.USER_IN_SEAT.sendMessage(this.getPlayer()); // sends them a message if they are already in a seat
			return;
		}

		try {

			SeatComponent seatComponent = (SeatComponent) paramVehicle.getFirstComponentByType(ComponentName.DRIVER_SEAT.getName());
			seatComponent.setRider(this.getPlayer());

			this.setSeat((DriverSeat) seatComponent); // sets the player in the seat whilst returning the seat they are in then sets the seat to be equal to the driver's seat

		} catch (SeatOccupiedException e) {
			Messages.SEAT_OCCUPIED.sendMessage(this.getPlayer()); // if there is already a driver then it will stop and send them a message
		}

	}

	// VEHICLE -

	// PLAYER + 

	@Getter
	private final Player player; // stores the player object
	@Getter
	private final UUID uuid; // stores the uuid of the player

	/*
	 * Called to stop the user, so removing the player from the seat
	 * 
	 * protected - stops the user being halted without being halted in the UserManager class
	 */
	protected void halt() {
		this.dismount(); // dismounts the user from the seat

	}

	// PLAYER - 

	// VEHICLE INVENTORY +

	@Getter
	@Setter
	private Vehicle vehicleWithOpenedInventory; // stores the vehicles open inventory

	/**
	 * Opens the vehicles inventory for the player
	 * @param paramVehicle
	 */
	public void openVehicleInventory(Vehicle paramVehicle) {
		if(this.getPlayer().getOpenInventory() != null) { // checks if the player currently has an inventory open
			this.getPlayer().closeInventory(); // closes live inventory
		}

		if(paramVehicle.hasComponent(ComponentName.INVENTORY.getName())) {
			this.getPlayer().openInventory(((InventoryComponent) paramVehicle.getFirstComponentByType(ComponentName.INVENTORY.getName())).getInventory()); // Opens the inventory to the player
			this.setVehicleWithOpenedInventory(paramVehicle); // sets the new inventory
		}
	}

	/**
	 * returns whether the player is looking at a vehicles inventory
	 * @return
	 */
	public boolean inVehicleInventory() {
		return(this.getPlayer().getOpenInventory() != null && this.getVehicleWithOpenedInventory() != null); // checks if both the player is in an inventory and that the inventory is from a vehicle
	}

	/**
	 * Called when the user either closes their inventory or plugin owner wants to close it for them
	 */
	public void closeInventory() {
		if(this.getVehicleWithOpenedInventory() != null ) {
			this.setVehicleWithOpenedInventory(null); // sets the live inventory to null
		}
	}

	// VEHICLE INVENTORY -

	/*
	 * User object, handles everything to do with the player
	 */
	public User(Player paramPlayer) {
		this.player = paramPlayer; // sets player
		this.uuid = paramPlayer.getUniqueId(); // sets uniqueID
		this.vehicleWithOpenedInventory = null; // sets the open inventory to null
	}


}