package uk.hannam.vehiclesapi.utils;

import org.bukkit.Location;

import lombok.Getter;

public class Point3D {

	@Getter
	private final double x; // x position
	@Getter
	private final double y; // y position
	@Getter
	private final double z; // z position
	
	/**
	 * Point3D is a 3 dimensional place, it has an x, y and z coordinate
	 * 
	 * @param paramX
	 * @param paramY
	 * @param paramZ
	 */
	public Point3D(double paramX, double paramY, double paramZ) {
		this.x = paramX; // sets x
		this.y = paramY; // sets y
		this.z = paramZ; // sets z
	}

	/**
	 * returns a new Point3D from a location
	 * 
	 * location.getX() -> this:x
	 * location.getY() -> this:y
	 * location.getZ() -> this:z
	 * 
	 * @param paramLocation
	 * @return
	 */
	public static Point3D fromLocation(Location paramLocation) {
		return new Point3D(paramLocation.getX(), paramLocation.getY(), paramLocation.getZ());
	}
	
}
