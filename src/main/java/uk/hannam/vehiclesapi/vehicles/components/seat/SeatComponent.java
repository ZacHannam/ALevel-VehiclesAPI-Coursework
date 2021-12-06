package uk.hannam.vehiclesapi.vehicles.components.seat;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.utils.Point4D;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.seat.entity.EntitySeat;
import uk.hannam.vehiclesapi.vehicles.components.seat.exceptions.SeatOccupiedException;

public abstract class SeatComponent extends Component {

	//-------------------------------------------------------------------- COMPONENT METHODS ------------------------------------------------------------------------
	
	public String getComponentType() {
		return ComponentName.SEAT.getName();
	}

	public String[] getRequiredComponents() {
		return new String[] {};
	}
	
	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------
	
	public abstract Vector OFFSET();

	//-------------------------------------------------------------------- ARMORSTAND METHODS ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private EntitySeat seatEntity;
	
	public ArmorStand getArmorStand() {
		return this.getSeatEntity().getArmorStand();
	}
	
	//-------------------------------------------------------------------- RIDER METHODS ------------------------------------------------------------------------
	
	@Getter
	private Player rider;
	
	public boolean hasRider() {
		return rider != null && this.getArmorStand().getPassengers().size() > 0;
	}

	/**
	 * Used to eject the ride from the seat
	 */
	public void ejectRider() {
		if(!this.hasRider()) {
			return;
		}
		
		OfflinePlayer player = this.getRider();
		
		this.getArmorStand().removePassenger(this.getRider());
		this.removeRider();
		this.getVehicle().onDismount(player);
		
		new BukkitRunnable() {
			public void run() {
				if(player.isOnline()) {
					Location location = getLocation().clone().add(0, 1, 0);
					Player p = player.getPlayer();
					location.setPitch(p.getLocation().getPitch());
					location.setYaw(p.getLocation().getYaw());
					location.setDirection(p.getLocation().getDirection());
					
					p.teleport(location);
				}
			}
		}.runTaskLater(VehiclesAPI.getPlugin(), 1);
	}
	
	/**
	 * Used to set the rider for the vehicle
	 * @param paramPlayer
	 * @throws SeatOccupiedException
	 */
	@SuppressWarnings("deprecation")
	public void setRider(Player paramPlayer) throws SeatOccupiedException {

		if(this.getArmorStand().getPassengers().size() >= 1) {
			throw new SeatOccupiedException();
		}

		if(paramPlayer == null) {
			return;
		}

		this.getArmorStand().setPassenger(paramPlayer);

		this.rider = paramPlayer;
		this.getVehicle().onMount(paramPlayer);
	}
	
	private void removeRider() {
		this.rider = null;
	}
	
	
	//-------------------------------------------------------------------- SETUP ------------------------------------------------------------------------

	@Getter
	@Setter
	private Vector offset;
	
	//-------------------------------------------------------------------- METHODS ------------------------------------------------------------------------

	public Location getLocation() {
		return this.getArmorStand().getLocation();
	}
	
	//-------------------------------------------------------------------- MOVEMENT ------------------------------------------------------------------------

	public void move() {

		Location newLocation = VehicleMath.getVectorYawLocation(this.getVehicle().getLocation(), this.getOffset(), this.getVehicle().getLocation().getYaw());
		this.getSeatEntity().setLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), 0, 0);

	}
	
	//-------------------------------------------------------------------- TICK ------------------------------------------------------------------------

	@Override
	public void tick() {
		move();
	}	

	//-------------------------------------------------------------------- SPAWNING AND DESPAWNING ------------------------------------------------------------------------

	@Override
	public void spawn(VehicleLocation paramLocation) {
		Location location = VehicleMath.getVectorYawLocation(paramLocation, this.getOffset(), paramLocation.getYaw());
		this.setSeatEntity(new EntitySeat(super.getVehicle(), Point4D.fromLocation(location)));
	}
	
	@Override
	public void despawn() {
		if(this.getSeatEntity() != null) {
			this.getArmorStand().remove();
		}
		this.removeRider();
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------

	public SeatComponent(Vehicle paramVehicle) {
		super(paramVehicle);

		this.setOffset(OFFSET());
	}
}