package uk.hannam.vehiclesapi.utils;

import org.bukkit.Location;

import lombok.Getter;

public class Point2D {

	@Getter
	private final double x; // x position
	@Getter
	private final double z; // z position
	
	/**
	 * Point2D is a X, Z point, it has no world.
	 * @param paramX
	 * @param paramZ
	 */
	public Point2D(double paramX, double paramZ) {
		this.x = paramX; // sets x
		this.z = paramZ; // sets z
	}

	/**
	 * turns a location's x and z into a Point2D
	 * 
	 * location.getX() -> Point2D:x
	 * location.getZ() -> Point2D:z
	 * 
	 * returns a new Point2D
	 * 
	 * @param paramLocation
	 * @return
	 */
	public static Point2D fromLocation(Location paramLocation) {
		return new Point2D(paramLocation.getX(), paramLocation.getZ()); // returns a new Point2D(x, z)
	}

	/**
	 * 
	 * dot is the total dot product of two Point2D
	 * 
	 * self acts as point1
	 * 
	 * @param paramPoint (Point 2)
	 * @return
	 */
	public double dot(Point2D paramPoint) {
		return paramPoint.getX() * this.x + paramPoint.getZ() * this.z; // returns dot product
	}

}
