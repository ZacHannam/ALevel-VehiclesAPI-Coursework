package uk.hannam.vehiclesapi.vehicles.components;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.VehicleLocation;
import uk.hannam.vehiclesapi.vehicles.components.frame.Frame;
import uk.hannam.vehiclesapi.vehicles.components.seat.driverseat.DriverSeat;
import uk.hannam.vehiclesapi.vehicles.components.steeringwheel.SteeringWheel;
import uk.hannam.vehiclesapi.vehicles.components.wheelbase.WheelBase;


public abstract class Component {

	//-------------------------------------------------------------------- VARIABLES ------------------------------------------------------------------------
	
	@Getter
	@Setter
	private Vehicle vehicle;
	
	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------

	
	public abstract String getComponentName(); // Gets the components type
	public abstract String[] getRequiredComponents(); // Gets a list of components that are required for this component to operate correctly
	
	//-------------------------------------------------------------------- OVERRIDABLE METHODS ------------------------------------------------------------------------

	public JSONObject getMeta() {return null;} // gets the meta associated with the component.
	public void buildFromMeta(JSONObject paramMeta) {} // builds the vehicle using the meta
	public void tick() {} // called every tick
	public void spawn(VehicleLocation paramLocation) {} // called when the vehicle is spawned
	public void despawn() {} // called when the vehicle is despawned
	public void onButtonPressW() {} // called when the W button is pressed
	public void onButtonPressA() {} // called when the A button is pressed
	public void onButtonPressS() {} // called when the S button is pressed
	public void onButtonPressD() {} // called when the D button is pressed
	public void onButtonPressSpace() {} // called when the Space button is pressed
	public void onButtonPressCtrl() {} // called when the Ctrl button is pressed
	public void noButtonPressForward() {} // called when the neither W or S are pressed
	public void noButtonPressSideways() {} // called when the neither A or D are pressed
	public void halt() {} // called when the component has to stop, in order for it to properly shut down
	public void onVehicleClick(Player paramPlayer) {} // called when a player clicks on the vehicle
	public void onDismount(OfflinePlayer paramRider) {} // called when a player dismounts from a seat (can be called if a player disconnects when riding the vehicle)
	public void onMount(Player rider) {} // called when a player starts riding a seat.
	
	//-------------------------------------------------------------------- FINAL METHODS ------------------------------------------------------------------------

	/**
	 * Called to build the component
	 * @return
	 */
	public final boolean build() {
		
		for(String componentType : getRequiredComponents()) { // iterates through all of the required components
			if(!this.getVehicle().hasComponent(componentType)) { // checks if the vehicle has the component.
				return false; // if the vehicle does not have the required components, then the vehicle cannot be built, thus returns false
			}
		}
		return true; // as all of the required components are met, it can return true.
	}
	
	//-------------------------------------------------------------------- EASE OF ACCESS METHODS ------------------------------------------------------------------------

	/**
	 * Quick access to the steering wheel
	 * @return
	 */
	public SteeringWheel getSteeringWheel() {
		return ((SteeringWheel) this.getVehicle().getFirstComponentByType(ComponentName.STEERING_WHEEL.getName()));
	}
	
	/**
	 * Quick access to the frame
	 * @return
	 */
	public Frame getFrame() {
		return ((Frame) this.getVehicle().getFirstComponentByType(ComponentName.FRAME.getName()));
	}
	
	/**
	 * Quick access to wheel base
	 * @return
	 */
	public WheelBase getWheelBase() {
		return ((WheelBase) this.getVehicle().getFirstComponentByType(ComponentName.WHEELBASE.getName()));
	}
	
	/**
	 * Quick access to the driver's seat
	 * @return
	 */
	public DriverSeat getDriverSeat() {
		return ((DriverSeat) this.getVehicle().getFirstComponentByType(ComponentName.DRIVER_SEAT.getName()));
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------

	public Component(Vehicle paramVehicle) {
		setVehicle(paramVehicle); // sets the parent vehicle (the vehicle the component is attached to)
	}
}
