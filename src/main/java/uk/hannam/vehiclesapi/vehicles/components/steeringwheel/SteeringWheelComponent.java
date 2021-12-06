package uk.hannam.vehiclesapi.vehicles.components.steeringwheel;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.utils.Point4D;
import uk.hannam.vehiclesapi.utils.VehicleMath;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;
import uk.hannam.vehiclesapi.vehicles.components.steeringwheel.entity.EntitySteeringWheel;
import uk.hannam.vehiclesapi.vehicles.components.wheelbase.WheelBase;

public abstract class SteeringWheelComponent extends Component implements SteeringWheel {
	
	//-------------------------------------------------------------------- COMPONENT METHODS ------------------------------------------------------------------------
	
	public String getComponentName() {
		return ComponentName.STEERING_WHEEL.getName();
	}
		
	public String[] getRequiredComponents() {
		return new String[] {
				ComponentName.FRAME.getName(), ComponentName.DRIVER_SEAT.getName(), ComponentName.WHEELBASE.getName(),
		};
	}
	
	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------
	
	public abstract ItemStack HELMET_ITEM();
	public abstract int STEERINGWHEEL_LOCKS();
	public abstract double STEERINGWHEEL_ANGLE();
	public abstract Vector OFFSET();
	public abstract double YAW_OFFSET();
	
	//-------------------------------------------------------------------- ARMORSTAND METHODS ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private EntitySteeringWheel steeringWheelEntity;
	
	@Getter
	@Setter
	private ItemStack helmetItem;
	
	public ArmorStand getArmorStand() {
		return this.getSteeringWheelEntity().getArmorStand();
	}
	
	@SuppressWarnings("deprecation")
	public void applyHelmetItem(ItemStack paramItem) {
		this.setHelmetItem(paramItem);
		if(this.getSteeringWheelEntity() != null) {
			this.getArmorStand().setHelmet(this.getHelmetItem());
		}
	}
	
	private void move() {
		
		Location newLocation = VehicleMath.getVectorYawLocation(this.getVehicle().getLocation(), this.getOffset(), this.getVehicle().getLocation().getYaw());
		
		Location seatLocation = super.getDriverSeat().getLocation();
		
		double deltaX = seatLocation.getX() - newLocation.getX();
		double deltaZ = seatLocation.getZ() - newLocation.getZ();
		
		double theta = Math.atan2(deltaZ, deltaX);
		
		newLocation.setYaw((float) Math.toDegrees(theta));
		
		this.getArmorStand().teleport(newLocation);
		this.getArmorStand().setHeadPose(new EulerAngle(0, this.getMappedSteeringAngle() + this.getYawOffset(), this.getSteeringWheelAngle()));
	}
	
	//-------------------------------------------------------------------- STEERINGWHEEL METHODS ------------------------------------------------------------------------

	@Getter
	@Setter
	private double steeringWheelAngle;
	
	@Getter
	@Setter
	private int steeringWheelLocks;
	

	
	private double getMappedSteeringAngle() {
		
		WheelBase wheelBase = (WheelBase) getVehicle().getFirstComponentByType(ComponentName.WHEELBASE.getName());
		
		return (wheelBase.getSteeringRotation() / wheelBase.getMaxSteeringRotation()) * (this.getSteeringWheelLocks() * Math.PI);
		
	}
	
	//-------------------------------------------------------------------- SPAWNING AND DESPAWNING ------------------------------------------------------------------------

	@Getter
	@Setter
	private Vector offset;
	
	@Getter
	@Setter
	private double yawOffset;
	
	@Override
	public void spawn(VehicleLocation paramLocation) {
		
		Location spawnLocation = VehicleMath.getVectorYawLocation(paramLocation, this.getOffset(), paramLocation.getYaw());
		
		this.setSteeringWheelEntity(new EntitySteeringWheel(this.getVehicle(), Point4D.fromLocation(spawnLocation)));
		this.applyHelmetItem(this.getHelmetItem());
	}
	
	@Override
	public void despawn() {
		
		if(this.getSteeringWheelEntity() != null) {
			this.getArmorStand().remove();
		}
		this.setSteeringWheelEntity(null);
		
	}
	
	//-------------------------------------------------------------------- TICK ------------------------------------------------------------------------

	@Override
	public void tick() {
		this.move();
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------
	
	public SteeringWheelComponent(Vehicle paramVehicle) {
		super(paramVehicle);
		
		this.setYawOffset(YAW_OFFSET());
		this.setHelmetItem(HELMET_ITEM());
		this.setOffset(OFFSET());
		this.setSteeringWheelAngle(STEERINGWHEEL_ANGLE());
		this.setSteeringWheelLocks(STEERINGWHEEL_LOCKS());
	}
}
