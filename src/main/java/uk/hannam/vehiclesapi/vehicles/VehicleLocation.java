package uk.hannam.vehiclesapi.vehicles;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import lombok.Setter;

public class VehicleLocation {
	
	//-------------------------------------------------------------------- VARIABLES ------------------------------------------------------------------------

	@Getter
	@Setter
	public World world;
	
	@Getter
	@Setter
	public double x;
	
	@Getter
	@Setter
	public double y;
	
	@Getter
	@Setter
	public double z;
	
	@Getter
	@Setter
	public double yaw;
	
	//-------------------------------------------------------------------- METHODS ------------------------------------------------------------------------

	/**
	 * Clones the locaition to a new VehicleLocation
	 */
	public VehicleLocation clone() {
		return new VehicleLocation(this.world, this.x, this.y, this.z, this.yaw); // generates a new Vehicle Location with identical new variables and returns it.
	}
	
	/**
	 * Creates a new VehicleLocation from a location provided
	 * @param paramLocation
	 * @return
	 */
	public static VehicleLocation fromLocation(Location paramLocation) {
		
		return new VehicleLocation(paramLocation.getWorld(),
				paramLocation.getX(), paramLocation.getY(), paramLocation.getZ(), Math.toRadians(paramLocation.getYaw() + 180)); //creates a new VehicleLocation using the arguments given.
	}
	
	/**
	 * Converts a VehicleLocation to a Bukkit Location
	 * @return
	 */
	public Location toLocation() {
		Location location = new Location(this.getWorld(), this.getX(), this.getY(), this.getZ()); // defines a new location
		location.setYaw((float) Math.toDegrees(this.yaw) - 180); // corrects the yaw
		return location;// returns the location
	}
	
	/*
	 * Equals
	 */
	public boolean equals(VehicleLocation paramLocation) {
		return (this.getWorld() == paramLocation.getWorld() && this.getX() == paramLocation.getX() && this.getY() == paramLocation.getY() && this.getZ() == paramLocation.getZ()); // checks if all variables between the two VehicleLocations have the same values
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------
	
	//Constructor
	public VehicleLocation(World paramWorld, double paramX, double paramY, double paramZ, double paramYaw) {
		setWorld(paramWorld);
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setYaw(paramYaw);
	}
}
