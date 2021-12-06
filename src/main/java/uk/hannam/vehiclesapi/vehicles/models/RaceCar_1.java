package uk.hannam.vehiclesapi.vehicles.models;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.frame.FrameComponent;
import uk.hannam.vehiclesapi.vehicles.components.seat.driverseat.DriverSeatComponent;
import uk.hannam.vehiclesapi.vehicles.components.steeringwheel.SteeringWheelComponent;
import uk.hannam.vehiclesapi.vehicles.components.wheelbase.WheelBaseComponent;

class Frame_RaceCar_1 extends FrameComponent {

	public Frame_RaceCar_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public ItemStack HELMET_ITEM() {
	
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
		itemMeta.setCustomModelData(80);

        item.setItemMeta(itemMeta);
	/* OUDATED CODE
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound nmsData = nmsItem.getTag();

        nmsData.setInt("CustomModelData", 80);

        nmsItem.setTag(nmsData);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);
        */
        return item;
		
	}

	@Override
	public Vector OFFSET() {
		return new Vector(0, -1.5, 0);
	}

	@Override
	public double HEIGHT() {
		return 2;
	}

	@Override
	public double WIDTH() {
		return 1;
	}

	@Override
	public double LENGTH() {
		return 2;
	}

	@Override
	public double YAW_OFFSET() {
		return Math.PI;
	}
}

class Seat_RaceCar_1 extends DriverSeatComponent {

	public Seat_RaceCar_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public boolean SHOW_STEERING_DISPLAY() {
		return true;
	}

	@Override
	public int NUMBER_OF_CHARACTERS_ON_DISPLAY() {
		return 71;
	}

	@Override
	public Vector OFFSET() {
		return new Vector(0, -0.25, -0.1);
	}
	
}

class SteeringWheel_RaceCar_1 extends SteeringWheelComponent {

	public SteeringWheel_RaceCar_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public ItemStack HELMET_ITEM() {
		
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
		itemMeta.setCustomModelData(129);

        item.setItemMeta(itemMeta);
		/* OUDATED CODE
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound nmsData = nmsItem.getTag();

        nmsData.setInt("CustomModelData", 129);

        nmsItem.setTag(nmsData);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);

		 */
		return item;
	}

	@Override
	public int STEERINGWHEEL_LOCKS() {
		return 1;
	}

	@Override
	public double STEERINGWHEEL_ANGLE() {
		return 0.5 * Math.PI;
	}

	@Override
	public Vector OFFSET() {
		return new Vector (0, -1.0, -0.3);
	}

	@Override
	public double YAW_OFFSET() {
		return Math.PI / 2;
	}
	
}

class WheelBase_RaceCar_1 extends WheelBaseComponent {

	public WheelBase_RaceCar_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public Vector[] WHEEL_OFFSETS() {
		return new Vector[] {
				new Vector(0.5, 0, 1.5),
				new Vector(-0.5, 0, 1.5),
				new Vector(0.5, 0, -1.5),
				new Vector(-0.5, 0, -1.5)
			};
	}
	@Override
	public double MAX_SPEED() {
		return 3;
	}

	@Override
	public float NORMALISE_STEERING_AMOUNT() {
		return (float) (Math.PI / 180);
	}

	@Override
	public float MAX_STEERING_ROTATION() {
		return (float) (Math.PI / 12);
	}

	@Override
	public float STEERING_SPEED() {
		return (float) (Math.PI / 180);
	}
	
	@Override
	public double REVERSE_SPEED_MULTIPLIER() {
		return 0.2;
	}

	@Override
	public double POWER_ACCELERATION() {
		return 0.008;
	}

	@Override
	public double IDLE_ACCELERATION() {
		return 0.001;
	}

	@Override
	public double BRAKE_ACCELERATION() {
		return 0.05;
	}
}

public class RaceCar_1 extends Vehicle {

	public RaceCar_1(UUID paramUUID, int paramTypeID) {
		super(paramUUID, paramTypeID);
			
		super.addComponent(new WheelBase_RaceCar_1(this));
		super.addComponent(new Frame_RaceCar_1(this));
		super.addComponent(new Seat_RaceCar_1(this));
		super.addComponent(new SteeringWheel_RaceCar_1(this));
		
	}
}

