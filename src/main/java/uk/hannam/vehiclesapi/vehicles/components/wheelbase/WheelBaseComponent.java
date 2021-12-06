package uk.hannam.vehiclesapi.vehicles.components.wheelbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPosition;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.utils.Point3D;
import uk.hannam.vehiclesapi.utils.Point4D;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;

enum Direction {
	
	PRESS_POWER,
	PRESS_BRAKE,
	NO_PRESS
}

class Force {
	
	private final double theta;
	private final double magnitude;
	
	public Force(double paramTheta, double paramMagnitude) {
		this.theta = paramTheta;
		this.magnitude = paramMagnitude;
	}
	
	public double getTheta() {
		return this.theta;
	}
	
	public double getMagnitude() {
		return this.magnitude;
	}
}

class ForceManager extends HashMap<Double, Double> {

	private static final long serialVersionUID = 3376711873046792627L;
	
	public void addEntry(double paramTheta, double d) {
		paramTheta %= 2*Math.PI;
		if(!this.containsKey(paramTheta) || d > this.get(paramTheta)) {
			this.put(paramTheta, d);
		}
	}
	
	public void degrade(double paramDegradeSize, List<Double> paramIgnore) {
		HashMap<Double, Double> newMap = new HashMap<Double, Double>();
		for(Map.Entry<Double, Double> entry : this.entrySet()) {
			if(paramIgnore.contains(entry.getKey()%2)) continue;
			Double newValue = (entry.getValue() * paramDegradeSize);
			if(Math.round(newValue) != 0.0) {
				newMap.put(entry.getKey(), newValue);
			}
		}
		this.clear();
		for(Map.Entry<Double, Double> entry : newMap.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	public Force calculatePositions() {
		double positionX = 0;
		double positionZ = 0;
		for(Map.Entry<Double, Double> entry : this.entrySet()) {
			double theta = (Math.PI / 2) - entry.getKey();
			positionX += (Math.cos(theta) * entry.getValue());
			positionZ += (Math.sin(theta) * entry.getValue());
		}
		positionX /= this.size();
		positionZ /= this.size();
		double magnitude = Math.sqrt(Math.pow(positionX, 2) + Math.pow(positionZ, 2));
		double theta = positionX == 0.0 ? 0.0 : (Math.PI / 2) - Math.atan(positionZ/positionX);
		return (new Force(theta, magnitude));
	}
}

class Wheel {
	
	//-------------------------------------------------------------------- CONSTANTS ------------------------------------------------------------------------

	public static final double JUMP_HEIGHT = 1;
	
	//-------------------------------------------------------------------- VARIABLES ------------------------------------------------------------------------

	@Getter
	@Setter
	private Vector offset;
	
	@Getter
	@Setter
	private Point4D lastLocation;
	
	@Getter
	@Setter
	World world;
	
	@Getter
	@Setter
	double x;
	
	@Getter
	@Setter
	double y;
	
	@Getter
	@Setter
	double z;
	
	//-------------------------------------------------------------------- LOCATION METHODS ------------------------------------------------------------------------

	/**
	 * Sets the new location using a Point4D
	 * @param paramLocation
	 */
	public void setLocation(Point4D paramLocation) {
		this.setLocation(paramLocation.getWorld(), paramLocation.getX(), paramLocation.getY(), paramLocation.getZ());
	}

	/**
	 * Sets the new location using a Point3D
	 * @param paramLocation
	 */
	public void setLocation(Point3D paramLocation) {
		this.setLocation(paramLocation.getX(), paramLocation.getY(), paramLocation.getZ());
	}
	
	/**
	 * Sets the new location of the wheel
	 * @param paramWorld
	 * @param paramX
	 * @param paramY
	 * @param paramZ
	 */
	public void setLocation(World paramWorld, double paramX, double paramY, double paramZ) {
		
		this.setLastLocation(new Point4D(this.getWorld(), this.getX(), this.getY(), this.getZ())); // stores the last location into a new Point4D variable.
		
		this.setWorld(paramWorld);
		this.setX(paramX);
		this.setY(paramY);
		this.setZ(paramZ);
	}
	
	/**
	 * Used to set the location from a point 3D
	 * @param paramX
	 * @param paramY
	 * @param paramZ
	 */
	public void setLocation(double paramX, double paramY, double paramZ) {
		
		this.setLastLocation(new Point4D(this.getWorld(), this.getX(), this.getY(), this.getZ()));
		
		this.setX(paramX);
		this.setY(paramY);
		this.setZ(paramZ);
	}
	
	/**
	 * Returns the location of the wheel
	 * @return
	 */
	public Location getLocation() {
		return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
	}
	
	/**
	 * Gets the next location of the wheel.
	 * @param paramLocation
	 * @return
	 */
	public Point3D nextLocation(Location paramLocation) {
		
		Location location = paramLocation.clone();
		
		location.setY(Math.round(this.getY()));
		
		while(location.getBlock().getType().isSolid() && location.getY() <= 255) {
			location.setY(location.getY() + 1);
		}
		
		location.setY(location.getY() - 1);
		while(!location.getBlock().getType().isSolid() && location.getY() >= 0) {
			location.setY(location.getY() - 1);
		}
		
		if((location.getY() - this.y) > (JUMP_HEIGHT-1)) return null;
		
		double y = location.getY() + (location.getBlock().isPassable() ? 0 : location.getBlock().getBoundingBox().getHeight());
		
		return new Point3D(location.getX(), y, location.getZ());
	}

	/**
	 * Sets the location to the last location
	 */
	public void teleportToLastLocation() {
		this.setLocation(this.lastLocation);
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------

	//Constructor
	public Wheel(Vector paramOffset, Location paramLocation) {
		this.setLocation(paramLocation.getWorld(), paramLocation.getX(), paramLocation.getY(), paramLocation.getZ()); // Sets the location
		this.setOffset(paramOffset);
	}

}

public abstract class WheelBaseComponent extends Component implements WheelBase {

	//-------------------------------------------------------------------- COMPONENT METHODS ------------------------------------------------------------------------

	
	@Override
	public String getComponentName() {
		return ComponentName.WHEELBASE.getName();
	}
	
	public String[] getRequiredComponents() {
		return new String[] {
		};
	}

	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------

	
	public abstract Vector[] WHEEL_OFFSETS();
	public abstract float MAX_STEERING_ROTATION();
	public abstract float STEERING_SPEED();
	public abstract double MAX_SPEED();
	public abstract float NORMALISE_STEERING_AMOUNT();
	public abstract double REVERSE_SPEED_MULTIPLIER();
	public abstract double POWER_ACCELERATION();
	public abstract double IDLE_ACCELERATION();
	public abstract double BRAKE_ACCELERATION();

	
	//-------------------------------------------------------------------- METHODS ------------------------------------------------------------------------
	
	public void addWheel(Wheel paramWheel) {
		this.getWheels().add(paramWheel);
	}
	
	
	public void crash() {
		this.setLinearAcceleration(0);
	}
	
	public Location[] getWheelLocations() {
		Location[] locations = new Location[this.getWheels().size()];
		for(int index = 0; index < this.getWheels().size(); index++) {
			locations[index] = this.getWheels().get(index).getLocation();
		}
		
		return locations;
	}

	
	//-------------------------------------------------------------------- SPAWNING AND DESPAWNING ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private Vector[] wheelOffsets;
	
	
	// Spawn
	@Override
	public void spawn(VehicleLocation paramLocation) {
		
		for(Vector displacement : this.getWheelOffsets()) { // runs through the wheel displacements
			
			this.createWheel(displacement, VehicleMath.getVectorYawLocation(paramLocation, displacement, paramLocation.getYaw())); // creates a wheel at the displacement
		}
	}
	
	// Despawn
	@Override
	public void despawn() {
		this.getWheels().clear(); // clears all of the wheels
	}
	
	//-------------------------------------------------------------------- STEERING ------------------------------------------------------------------------

	
	@Getter
	@Setter
	private float steeringRotation;
	
	@Getter
	@Setter
	private float maxSteeringRotation;
	
	@Getter
	@Setter
	private float steeringSpeed;
	
	@Getter
	@Setter
	private float normaliseSteeringAmount;
	
	// A- Click
	@Override
	public void onButtonPressA() {
		
		// turns steering wheel to the right
		this.setSteeringRotation(Math.max(-this.getMaxSteeringRotation(), this.getSteeringRotation() - this.getSteeringSpeed()));
		
	}
	
	// D- Click
	@Override
	public void onButtonPressD() {
		
		// turns steering wheel to the left
		this.setSteeringRotation(Math.min(this.getMaxSteeringRotation(), this.getSteeringRotation() + this.getSteeringSpeed()));
		
	}
	
	// Space- Click
	@Override
	public void onButtonPressSpace() {
		this.setSteeringRotation(0); // centres the steering wheel
	}
	
	
	public void normaliseSteering() {
		if(this.getSteeringRotation() > 0) {
			this.setSteeringRotation((float) Math.max(0, this.getSteeringRotation() - this.getNormaliseSteeringAmount() * (this.getSpeed() / this.getMaxSpeed())));
		} else if(this.getSteeringRotation() < 0) {
			this.setSteeringRotation((float) Math.min(0,  this.getSteeringRotation() + this.getNormaliseSteeringAmount() * (this.getSpeed() / this.getMaxSpeed())));
		}
	}
	
	//-------------------------------------------------------------------- SPEED CONTROLS ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private double powerAcceleration;
	
	@Getter
	@Setter
	private double idleAcceleration;
	
	@Getter
	@Setter
	private double brakeAcceleration;
	
	@Getter
	@Setter
	private double linearAcceleration;
	
	@Getter
	@Setter
	private double maxSpeed;
	
	@Getter
	@Setter
	private Direction direction;
	
	@Getter
	@Setter
	private boolean canSwitchReverse;
	
	@Getter
	@Setter
	private boolean inReverse;
	
	@Override
	public void onButtonPressW() {
		
		if(this.isCanSwitchReverse() && this.getLinearAcceleration() <= 0) {
			this.setInReverse(false);
		}
		
		if(this.isInReverse()) {
			this.setDirection(Direction.PRESS_BRAKE);
			this.setLinearAcceleration(Math.max(this.getLinearAcceleration() - this.getBrakeAcceleration(), 0));
			if(this.getLinearAcceleration() != 0) {
				this.playEffectAtWheels(Effect.SMOKE);
			}
		} else {
			this.setDirection(Direction.PRESS_POWER);
			this.setLinearAcceleration(Math.min(this.getLinearAcceleration() + this.getPowerAcceleration(), maxSpeed));
		}
		this.setCanSwitchReverse(false);
	}
	
	@Override
	public void onButtonPressS() {
		
		if(this.isCanSwitchReverse() && this.getLinearAcceleration() <= 0) {
			this.setInReverse(true);
		}
		
		if(this.isInReverse()) {
			this.setDirection(Direction.PRESS_POWER);
			this.setLinearAcceleration(Math.min(this.getLinearAcceleration() + this.getPowerAcceleration(), maxSpeed));
		} else {
			this.setDirection(Direction.PRESS_BRAKE);
			this.setLinearAcceleration(Math.max(this.getLinearAcceleration() - this.getBrakeAcceleration(), 0)); // used when braking
			if(this.getLinearAcceleration() != 0) {
				this.playEffectAtWheels(Effect.SMOKE);
			}
		}
		this.setCanSwitchReverse(false);
	}
	
	@Override
	public void noButtonPressForward() {
		if(this.getDirection() == Direction.PRESS_BRAKE) {
			
		}
		this.setDirection(Direction.NO_PRESS);
		this.setLinearAcceleration(Math.max(this.getLinearAcceleration() - this.getIdleAcceleration(), 0)); // used to get certain number of dp.
		
		this.setCanSwitchReverse(true);
	}
	
	public double getSpeed() {
		switch(this.getDirection())	{
		case PRESS_POWER:
			return Math.sqrt(Math.pow(this.getMaxSpeed(), 2) - Math.pow((this.getLinearAcceleration()-this.getMaxSpeed()), 2));
		case PRESS_BRAKE:
			return Math.sqrt(Math.pow(this.getMaxSpeed(), 2) - Math.pow((this.getLinearAcceleration()-this.getMaxSpeed()), 2));
		case NO_PRESS:
			return Math.sqrt(Math.pow(this.getMaxSpeed(), 2) - Math.pow((this.getLinearAcceleration()-this.getMaxSpeed()), 2));
		default:
			return 0;
		}
	}
	
	//-------------------------------------------------------------------- TICK ------------------------------------------------------------------------
	
	@Override
	public void tick() {
		
		updatePosition();
		
		normaliseSteering();
	}
	
	//-------------------------------------------------------------------- POSITION CONTROL ------------------------------------------------------------------------
	
	
	@Getter
	@Setter
	private ForceManager forceManager;
	
	
	@Getter
	@Setter
	private VehicleLocation lastLocation;
	
	@Getter
	@Setter
	private double reversePower;
	
	private void updatePosition() {
		
		this.getForceManager().addEntry(this.getVehicle().getLocation().getYaw() + this.getSteeringRotation(), this.getSpeed());
		
		Force force = this.getForceManager().calculatePositions(); // calculates the next position the vehicle will go to
		
		double nextSpeed = this.isInReverse() ? force.getMagnitude() * this.getReversePower(): force.getMagnitude();
	
		VehicleLocation location = VehicleMath.getNextLocation(super.getVehicle().getLocation(), force.getTheta() - this.getVehicle().getLocation().getYaw(), nextSpeed, this.getLength(), this.isInReverse());
	
		if(super.getVehicle().isOtherVehicle(location)) {
			this.crash();
			return;
		}
		
		Point3D[] nextWheelLocations = new Point3D[4];
		int completedWheels = 0;
		for(int index = 0; index < wheels.size(); index++) {
			Wheel wheel = this.getWheels().get(index);
			Location wheelLocation = VehicleMath.getVectorYawLocation(location, wheel.getOffset(), location.getYaw());
			Point3D nextLocation = wheel.nextLocation(wheelLocation);
			if(nextLocation == null) {
				break;
			}
			nextWheelLocations[index] = nextLocation;
			completedWheels++;
		}
		
		if(completedWheels == this.getWheels().size()) {
			
			double y = 0;
			for(Wheel wheel : this.getWheels()) {
				y += wheel.getLocation().getY();
			}
			y /= wheels.size();
			
			location.setY(y);
			
			this.getVehicle().setLocation(location);
			
			this.setLastLocation(location);
			for(int index = 0; index < wheels.size(); index++) {
				Wheel wheel = this.getWheels().get(index);
				wheel.setLocation(nextWheelLocations[index]);
			}
		} else {
			this.crash();
			
		}
		
		this.getForceManager().degrade(this.getSurfaceFriction(), Arrays.asList(this.getVehicle().getLocation().getYaw()));
	}
	
	public double getSurfaceFriction() {
		
		double totalFriction = 0;
		
		for(Wheel wheel : wheels) {
			Block block = wheel.getLocation().clone().add(0, -1, 0).getBlock();
			CraftBlock craftBlock = CraftBlock.at(((CraftWorld)block.getWorld()).getHandle(),
					new BlockPosition(block.getX(), block.getY(), block.getZ()));
			
			totalFriction += craftBlock.getNMS().getBlock().getFrictionFactor();
		}
		
		
		return totalFriction/(wheels.size());
	}


	//-------------------------------------------------------------------- VEHICLE METHODS ------------------------------------------------------------------------
	
	public void playEffectAtWheels(Effect paramEffect) {
		
		Location[] wheelLocations = this.getWheelLocations();
		
		if(wheelLocations.length <= 0) {
			return;
		}
		
		World world = wheelLocations[0].getWorld();
		
		for(Location location : wheelLocations) {
			world.playEffect(location.clone().add(0, -0.5, 0), paramEffect, 1);
		}
	}
	

	//-------------------------------------------------------------------- SETUP ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private double length;
	
	@Getter
	@Setter
	private double width;
	
	@Getter
	@Setter
	private ArrayList<Wheel> wheels = new ArrayList<Wheel>();
	
	private Wheel createWheel(Vector paramOffset, Location paramLocation) {
		
		Wheel wheel = new Wheel(paramOffset, paramLocation);
	
		this.addWheel(wheel);
		return wheel;
		
	}
	
	private void calculateWidthAndLength() {
		
		double max_width = 0;
		double min_width = 0;
		
		double max_length = 0;
		double min_length = 0;
		
		for(Vector displacement : wheelOffsets) {
			if(displacement.getX() < min_width) {
				min_width = displacement.getX();
			}
			if(displacement.getX() > max_width) {
				max_width = displacement.getX();
			}
			if(displacement.getZ() > max_length) {
				max_length = displacement.getZ();
			}
			if(displacement.getZ() < min_length) {
				min_length = displacement.getZ();
			}
		}
		
		
		this.setWidth(max_length - min_length);
		this.setLength(max_width - min_width);
		
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------
	
	public WheelBaseComponent(Vehicle paramVehicle) {	
		super(paramVehicle);
		
		this.setWheelOffsets(this.WHEEL_OFFSETS());
		this.setMaxSteeringRotation(this.MAX_STEERING_ROTATION());
		this.setSteeringSpeed(this.STEERING_SPEED());
		this.setMaxSpeed(this.MAX_SPEED());
		this.setNormaliseSteeringAmount(this.NORMALISE_STEERING_AMOUNT());
		this.setReversePower(this.REVERSE_SPEED_MULTIPLIER());
		this.setBrakeAcceleration(this.BRAKE_ACCELERATION());
		this.setPowerAcceleration(this.POWER_ACCELERATION());
		this.setIdleAcceleration(this.IDLE_ACCELERATION());
		
		this.setForceManager(new ForceManager());
		this.setDirection(Direction.NO_PRESS);
		this.setLinearAcceleration(0);
		
		this.calculateWidthAndLength();
	}
}