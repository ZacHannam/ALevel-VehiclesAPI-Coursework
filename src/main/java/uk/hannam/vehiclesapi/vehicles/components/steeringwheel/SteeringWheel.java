package uk.hannam.vehiclesapi.vehicles.components.steeringwheel;

import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.components.steeringwheel.entity.EntitySteeringWheel;

public interface SteeringWheel {
	
	EntitySteeringWheel getSteeringWheelEntity();
	void setSteeringWheelEntity(EntitySteeringWheel paramSteeringWheelEntity);
	ArmorStand getArmorStand();
	void applyHelmetItem(ItemStack paramItem);
	ItemStack getHelmetItem();
	void setOffset(Vector paramOffset);
	Vector getOffset();
	void setSteeringWheelAngle(double paramAngle);
	double getSteeringWheelAngle();
	void setSteeringWheelLocks(int paramSteeringWheelLocks);
	int getSteeringWheelLocks();
	double getYawOffset();
	void setYawOffset(double paramYawOffset);
	

}
