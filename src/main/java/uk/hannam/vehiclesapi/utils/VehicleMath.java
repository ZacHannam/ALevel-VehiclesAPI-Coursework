package uk.hannam.vehiclesapi.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.frame.Frame;


public final class VehicleMath {
	
	/**
	 * This method returns the location of an object when it has been rotated around a point with a displacement (x, z)
	 * Document: 
	 * 
	 * @param paramBaseLocation
	 * @param paramDisplacement (Vector)
	 * @param paramYaw (double [IN RADIANS NOT DEGREES])
	 * @return
	 */
	public static Location getVectorYawLocation(VehicleLocation paramBaseLocation, Vector paramDisplacement, double paramYaw)  {

		double x = paramBaseLocation.getX(); // sets X equal to the baseLocation X so that it is not null
		double z = paramBaseLocation.getZ(); // sets Z equal to the baseLocation Z so that it is not null
		
		x += Math.cos(paramYaw) * paramDisplacement.getX();
		z += Math.sin(paramYaw) * paramDisplacement.getX();

		x += Math.cos(paramYaw + 0.5 * Math.PI) * paramDisplacement.getZ();
		z += Math.sin(paramYaw +  0.5 * Math.PI) * paramDisplacement.getZ();
		
		/*
		if(paramDisplacement.getX() < 0) {
			x += Math.cos(paramYaw + 0.5 * Math.PI) * paramDisplacement.getX();
			z += Math.sin(paramYaw +  0.5 * Math.PI) * paramDisplacement.getX();
		} else {
			
		}
		*/
		
		Location location = new Location(paramBaseLocation.getWorld(), x, paramBaseLocation.getY() + paramDisplacement.getY(), z);
		
		return location;
	}
	
	/**
	 * Simplified version of ackerman's principle which uses only one front wheel instead of two as wheels do not move, therefore there is no differential.
	 * Returns the next location of the vehicle using this principle
	 * 
	 * @param paramBaseLocation (VehicleLocation)
	 * @param paramSteeringAngle (double RADIANS)
	 * @param paramMeterPerTick (double SPEED)
	 * @param paramVehicleLength (double)
	 * @param paramInReverse (boolean)
	 * @return
	 */
	public static VehicleLocation getNextLocation(VehicleLocation paramBaseLocation, double paramSteeringAngle, double paramMeterPerTick, double paramVehicleLength, boolean paramInReverse) {

		double radius = 0; // defining radius as zero
		
		// angle travelled = the base location yaw - 90 degrees
		double angleTravelled = (paramBaseLocation.getYaw() - (0.5 * Math.PI));
		
		// steering angle is changed so that it matches the correct motion when in reverse, therefore making it so that if the vehicle is going backwards then it will correctly follow the steering direction
		// if(inReverse()){
		// paramSteeringAngle = -paramSteeringAngle
		// }
		paramSteeringAngle = paramInReverse ? -paramSteeringAngle : paramSteeringAngle;
		
		
		if(paramSteeringAngle % Math.PI != 0) {
			// radius = (length / sin(sA)) * cos(sA)
			// possible error is division by zero, we must check that the sin(paramSteeringAngle) == 0 therefore (paramSteeringAngle % pi)
			// as at every 180 degrees sin(x) = 0
			radius = (paramVehicleLength / Math.sin(paramSteeringAngle)) * Math.cos(paramSteeringAngle);
			
			// angle travelled increases by meter per tick / radius
			angleTravelled += (paramMeterPerTick / radius);
		}

		// defines x and z
		double x, z;
		
		// checks if vehicle is in reverse, if it is in reverse then it will travel in the opposite direction.
		// all timesed by the paramMeterPerTick, this is the speed at which the vehicle is travelling
		if(paramInReverse) {
			
			x = paramBaseLocation.getX() - Math.cos(angleTravelled) * paramMeterPerTick;
			z = paramBaseLocation.getZ() - Math.sin(angleTravelled) * paramMeterPerTick;

		} else {
			
			x = Math.cos(angleTravelled) * paramMeterPerTick + paramBaseLocation.getX();
			z = Math.sin(angleTravelled) * paramMeterPerTick + paramBaseLocation.getZ();
		}
		double yaw = angleTravelled + (0.5 * Math.PI); // sets the yaw variable to angleTravelled + 90 degrees.
		
		return new VehicleLocation(paramBaseLocation.getWorld(), x, paramBaseLocation.getY(), z, yaw); // returns the new vehicle location.
		
	}
	
	/**
	 * Converts radians to yaw.
	 * 
	 * @param paramRadians
	 * @return
	 */
	public static float radianToYaw(double paramRadians) {
		return (float) (Math.toDegrees(paramRadians) - 180);
	}
	
	/**
	 * Used to fix the spawning height of a vehicle.
	 * 
	 * @param paramLocation
	 * @return
	 */
	public static VehicleLocation fixSpawnY(VehicleLocation paramLocation) {
		
		Location location = paramLocation.toLocation(); // sets location the location of the vehicle#
		
		location.setY(Math.round(location.getY())); // rounds the value of y in the location.
		while(location.getBlock().getType().isSolid()) { // checks the block that the vehicle is in, if it is solid (a block) then it will perform the operation
			location.add(0, 1, 0); // adds 1 to the y of the vehicle
		}
		location.add(0, -1, 0); // takes one away from the vehicle's y value, to fix it.
		while(!location.getBlock().getType().isSolid()) { // used for if the vehicle is spawned in the air.
			location.add(0, -1, 0);
		}
		
		double addedHeight = location.getBlock().isPassable() ? 0 : location.getBlock().getBoundingBox().getHeight(); // fixes slabs and snow layers. if the block is not a full block then it will correct for that, by adding the height of the current block 
		
		location.add(0, addedHeight, 0); // adds the height variable to the location
		
		return VehicleLocation.fromLocation(location); // returns a new vehicle's location of where the vehicle should spawn
		
	}

	/**
	 * Vector method, used by the location inside of vehicle method
	 * 
	 * 
	 * @param paramPoint1
	 * @param paramPoint2
	 * @return
	 */
	private static Point2D vector(Point2D paramPoint1, Point2D paramPoint2) {
		return new Point2D(paramPoint2.getX() - paramPoint1.getX(), paramPoint2.getZ() - paramPoint1.getZ()); // calculates a new Point2D using the difference between two points
	}
	
	/**
	 * Used to check if the location is inside of the vehicle
	 * 
	 * @param paramVehicle
	 * @param paramLocation
	 * @return
	 */
	public static boolean locationInsideVehicle(Vehicle paramVehicle, Location paramLocation) {
		
		if(paramLocation.getWorld() != paramVehicle.getLocation().getWorld()) { // checks if the vehicle is in the same location we are checking.
			return false; // returns false as the location is in not in the same world as the vehicle
		}
		
		if(!paramVehicle.hasComponent(ComponentName.FRAME.getName())) { // check if the vehicle has a frame component
			return false; // if the vehicle has no frame component return false
		}
		
		Frame frame = (Frame) paramVehicle.getFirstComponentByType(ComponentName.FRAME.getName());
		
		if(paramLocation.getY() > paramVehicle.getLocation().getY() + frame.getHeight() 
				|| paramLocation.getY() < paramVehicle.getLocation().getY()) { // checks if the y value is between the floor of the vehicle to the height of the vehicle.
			return false; // returns false as location is not on the same y level as the vehicle
		}
		
		Point2D[] frameCorners = frame.getFrameCorners(); // gets the list of frame corners
			
		
		Point2D location = Point2D.fromLocation(paramLocation); // converts the location provided to a Point2D

		/*
		 * Alogithm on Stack Overflow by Eric Bainville. https://stackoverflow.com/a/2763387.
		 * Modified use to use corners of vehicles as the points of the rectangle
		 */
		
		Point2D AB = vector(frameCorners[0], frameCorners[1]);
		Point2D AM = vector(frameCorners[0] , location);
		Point2D BC = vector(frameCorners[1], frameCorners[2]);
		Point2D BM = vector(frameCorners[1], location);

		double dotABAM = AB.dot(AM);
		double dotABAB = AB.dot(AB);
		double dotBCBM = BC.dot(BM);
		double dotBCBC = BC.dot(BC);
		
		return 0 <= dotABAM && dotABAM <= dotABAB && 0 <= dotBCBM && dotBCBM <= dotBCBC;
	}
	
}
