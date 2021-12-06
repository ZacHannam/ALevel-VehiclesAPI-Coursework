package uk.hannam.vehiclesapi.utils;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;

public class Point4D {
	
	@Getter
	private final World world; // world
	@Getter
	private final double x; // x position
	@Getter
	private final double y; // y position
	@Getter
	private final double z; // z position
	
	/**
	 * 4 dimensional position with x,y,z and world value
	 * 
	 * @param paramWorld
	 * @param paramX
	 * @param paramY
	 * @param paramZ
	 */
	public Point4D(World paramWorld, double paramX, double paramY, double paramZ) {
		this.world = paramWorld; // sets world
		this.x = paramX; // sets x
		this.y = paramY; // sets y
		this.z = paramZ; // sets z
	}

	/**
	 * Turns a location into a Point4D
	 *
	 * location.getWorld() -> this:world
	 * location.getX() -> this:x
	 * location.getY() -> this:y
	 * location.getZ() -> this:z
	 * 
	 * @param paramLocation
	 * @return
	 */
	public static Point4D fromLocation(Location paramLocation) {
		return new Point4D(paramLocation.getWorld(), paramLocation.getX(), paramLocation.getY(), paramLocation.getZ());
	}
}
