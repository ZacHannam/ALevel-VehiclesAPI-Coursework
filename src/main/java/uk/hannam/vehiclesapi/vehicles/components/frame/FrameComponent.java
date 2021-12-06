package uk.hannam.vehiclesapi.vehicles.components.frame;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.utils.Point2D;
import uk.hannam.vehiclesapi.utils.Point4D;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.frame.entity.EntityFrame;

public abstract class FrameComponent extends Component implements Frame {
	
	//-------------------------------------------------------------------- COMPONENT METHODS ------------------------------------------------------------------------
	
	public String getComponentName() {
		return ComponentName.FRAME.getName();
	}
	
	public String[] getRequiredComponents() {
		return new String[] {
				ComponentName.WHEELBASE.getName()
		};
	}
	
	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------
	
	public abstract ItemStack HELMET_ITEM(); // Helmet item refers to the item that represents the body of the vehicle
	public abstract Vector OFFSET(); // Offset from the centre of the vehicle to the armorstand position of the frame.
	public abstract double HEIGHT(); // height of the frame for the bounding box
	public abstract double WIDTH(); // width of the frame for the bounding box
	public abstract double LENGTH(); // length of the frame for the bounding box
	public abstract double YAW_OFFSET(); // rotation offset in radians.
	
	//-------------------------------------------------------------------- ARMORSTAND METHODS ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private ItemStack helmetItem; // stores the frame item
	
	/**
	 * Used to apply an item onto the frame (Change the body of the frame)
	 */
	@SuppressWarnings("deprecation")
	public void applyHelmetItem(ItemStack paramItem) {
		this.setHelmetItem(paramItem); // sets the helmet item
		if(this.getFrameEntity() != null) { // checks if the armor stand is alive (SHOWN)
			this.getArmorStand().setHelmet(this.getHelmetItem()); // sets the new helmet on the armorstand
		}
	}
	
	//-------------------------------------------------------------------- DIMENSION METHODS ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private double width; // local variable for width
	
	@Getter
	@Setter
	private double height; // local variable for height
	
	@Getter
	@Setter
	private double length; // local variable for length
	
	/**
	 * Used to get the longest body length from width, height and length
	 */
	public double getLongestBodyLength() {
		return Collections.max(Arrays.asList(this.getWidth(), this.getLength(), this.getHeight()));
	}
	
	/**
	 * Used to get the shortest body length from width, height and length
	 */
	public double getShortestBodyLength() {
		return Collections.min(Arrays.asList(this.getWidth(), this.getLength(), this.getHeight()));
	}
	
	/**
	 * Returns 4 corners of the vehicle, where each represents an X and Z co-ordinate. Y is determined by height + vehicleLocation
	 * Therefore vehicle must be flat at all times.
	 */
	public Point2D[] getFrameCorners() {
		Point2D[] points = new Point2D[4]; // defines a list of 4 Point2Ds
		
		VehicleLocation location = super.getVehicle().getLocation(); // gets the location of the vehicle
		double yaw = this.getYawOffset() + location.getYaw(); // yaw offset of the frame + the vehicles current yaw
		
		// gets the x and z for all of the corners of the frame
		points[0] = Point2D.fromLocation(VehicleMath.getVectorYawLocation(location, new Vector(getWidth() / 2, 0, getLength() / 2), yaw));
		points[1] = Point2D.fromLocation(VehicleMath.getVectorYawLocation(location, new Vector(-getWidth() / 2, 0, getLength() / 2), yaw));
		points[2] = Point2D.fromLocation(VehicleMath.getVectorYawLocation(location, new Vector(-getWidth() / 2, 0, -getLength() / 2), yaw));
		points[3] = Point2D.fromLocation(VehicleMath.getVectorYawLocation(location, new Vector(getWidth() / 2, 0, -getLength() / 2), yaw));
		return points; // returns the 4 corners (x and z) of the Frame
	}
	
	//-------------------------------------------------------------------- POSITION METHODS ------------------------------------------------------------------------
	
	/**
	 * Called every tick to update the position of the frame
	 */
	public void updatePosition() {
		
		Location newLocation = VehicleMath.getVectorYawLocation(getVehicle().getLocation(), this.getOffset(), this.getVehicle().getLocation().getYaw()); // gets the new location of the vehicle
		
		this.getFrameEntity().setLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), 0, 0); // Sets the location of the frame entity
		
		this.getArmorStand().setHeadPose(new EulerAngle(0, super.getVehicle().getLocation().getYaw() + this.getYawOffset(), 0)); // Sets the headpose (yaw rotation of the vehicle)
	}
	
	//-------------------------------------------------------------------- SPAWNING AND DESPAWNING ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private EntityFrame frameEntity; // stores the entity used to show the body
	
	@Getter
	@Setter
	private Vector offset; // stores the added position of the frame relative to the vehicle
	
	@Getter
	@Setter
	private double yawOffset;// stores the added rotation of the frame
	
	/**
	 * Called to spawn the vehicle at location: paramLocation
	 */
	@Override
	public void spawn(VehicleLocation paramLocation) {

		Location location = VehicleMath.getVectorYawLocation(paramLocation, this.getOffset(), paramLocation.getYaw() + this.getYawOffset()); // gets the new spawning position of the frame relative to the spawning position
		
		this.setFrameEntity(new EntityFrame(super.getVehicle(), Point4D.fromLocation(location))); // creates a new entity at the starting position of the frame
		this.applyHelmetItem(this.getHelmetItem()); // sets the helmet item of the vehicle to the frame item.
	}
	
	/**
	 * Called to despawn the vehicle
	 */
	@Override
	public void despawn() {
		if(this.getFrameEntity() != null) { // checks if the frame entity still exists
			this.getArmorStand().remove(); // removes (deletes) the entity from existance
		}
		this.setFrameEntity(null); // sets the new frame entity to null
	}
	
	public ArmorStand getArmorStand() {
		return this.getFrameEntity().getArmorStand();
	}
	
	public Location getLocation() {
		return this.getArmorStand().getLocation();
	}

	//-------------------------------------------------------------------- TICK ------------------------------------------------------------------------
	
	/**
	 * Runs every tick
	 */
	@Override
	public void tick() {
		updatePosition(); // updates the position of the frame
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------
	
	public FrameComponent(Vehicle paramVehicle) {
		super(paramVehicle);
		
		this.setYawOffset(this.YAW_OFFSET());
		this.setOffset(this.OFFSET());
		this.setHelmetItem(this.HELMET_ITEM());
		this.setHeight(HEIGHT());
		this.setLength(LENGTH());
		this.setWidth(WIDTH());
	}
}
