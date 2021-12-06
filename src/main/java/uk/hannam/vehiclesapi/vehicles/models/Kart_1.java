package uk.hannam.vehiclesapi.vehicles.models;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.frame.FrameComponent;
import uk.hannam.vehiclesapi.vehicles.components.inventory.InventoryComponent;
import uk.hannam.vehiclesapi.vehicles.components.seat.driverseat.DriverSeatComponent;
import uk.hannam.vehiclesapi.vehicles.components.steeringwheel.SteeringWheelComponent;
import uk.hannam.vehiclesapi.vehicles.components.wheelbase.WheelBaseComponent;

class Frame_Kart_1 extends FrameComponent {

	public Frame_Kart_1(Vehicle paramVehicle, ItemStack paramHelmetItem) {
		super(paramVehicle);
		
		this.applyHelmetItem(paramHelmetItem);
	}
	
	public Frame_Kart_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public ItemStack HELMET_ITEM() {
		
		ItemStack item = new ItemStack(Material.FIREWORK_STAR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
		itemMeta.setCustomModelData(8);

        item.setItemMeta(itemMeta);

		/* OUTDATED CODE
        CraftItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        NBTTagCompound nmsData = nmsItem.getTag();

        nmsData.setInt("CustomModelData", 8); //8

        nmsItem.setTag(nmsData);

        ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
        item.setItemMeta(meta);

		*/
        
        return item;
	}
	
	@Override
	public Vector OFFSET() {
		return new Vector(0, -1.25, 0);
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

class DriverSeat_Kart_1 extends DriverSeatComponent {

	public DriverSeat_Kart_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public boolean SHOW_STEERING_DISPLAY() {
		return true;
	}

	@Override
	public int NUMBER_OF_CHARACTERS_ON_DISPLAY() {
		return 51;
	}

	@Override
	public Vector OFFSET() {
		return new Vector(0, 0, -0.2);
	}
}

class SteeringWheel_Kart_1 extends SteeringWheelComponent {

	public SteeringWheel_Kart_1(Vehicle paramVehicle, ItemStack paramHelmetItem) {
		super(paramVehicle);
		
		this.applyHelmetItem(paramHelmetItem);
	}
	
	public SteeringWheel_Kart_1(Vehicle paramVehicle) {
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
        item.setItemMeta(meta);

		 */
		return item;
        
		//return new ItemStack(Material.BLACK_CARPET);
	}
	
	@Override
	public Vector OFFSET() {
		return new Vector (0, -0.57, -0.5);
	}
	
	@Override
	public double YAW_OFFSET() {
		return Math.PI / 2;
	}

	@Override
	public int STEERINGWHEEL_LOCKS() {
		return 1;
	}

	@Override
	public double STEERINGWHEEL_ANGLE() {
		return Math.toRadians(42);
	}

}

class WheelBase_Kart_1 extends WheelBaseComponent {

	public WheelBase_Kart_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}

	@Override
	public Vector[] WHEEL_OFFSETS() {
		return new Vector[] {
				new Vector(0.5, 0, 1),
				new Vector(-0.5, 0, 1),
				new Vector(0.5, 0, -1),
				new Vector(-0.5, 0, -1)
			};
	}

	@Override
	public double MAX_SPEED() {
		return 0.8;
	}

	@Override
	public float NORMALISE_STEERING_AMOUNT() {
		return (float) (Math.PI / 360);
	}

	@Override
	public float MAX_STEERING_ROTATION() {
		return (float) (Math.PI / 6);
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
		return 0.004;
	}

	@Override
	public double IDLE_ACCELERATION() {
		return 0.001;
	}

	@Override
	public double BRAKE_ACCELERATION() {
		return 0.014;
	}
}

class Inventory_Kart_1 extends InventoryComponent {

	public Inventory_Kart_1(Vehicle paramVehicle) {
		super(paramVehicle);
	}
}


public class Kart_1 extends Vehicle {

	public Kart_1(UUID paramUUID, int paramTypeID) {
		super(paramUUID, paramTypeID);
		
		super.addComponent(new WheelBase_Kart_1(this));
		super.addComponent(new DriverSeat_Kart_1(this));
		super.addComponent(new Frame_Kart_1(this));
		super.addComponent(new SteeringWheel_Kart_1(this));
		super.addComponent(new Inventory_Kart_1(this));
	}
	
}
