package uk.hannam.vehiclesapi.vehicles.components.wheelbase;

import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.VehicleLocation;

public interface WheelBase {
	Vector[] getWheelOffsets();
	double getReversePower();
	VehicleLocation getLastLocation();
	float getSteeringRotation();
	float getMaxSteeringRotation();
	float getSteeringSpeed();
	float getNormaliseSteeringAmount();
	double getPowerAcceleration();
	double getBrakeAcceleration();
	double getIdleAcceleration();
	double getLinearAcceleration();
	double getMaxSpeed();
	Direction getDirection();
	boolean isInReverse();
	ForceManager getForceManager();
	double getSurfaceFriction();
	double getWidth();
	double getLength();
}
