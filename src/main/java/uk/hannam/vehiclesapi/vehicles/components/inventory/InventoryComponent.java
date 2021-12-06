package uk.hannam.vehiclesapi.vehicles.components.inventory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.enums.Messages;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;
import uk.hannam.vehiclesapi.vehicles.Vehicle;
import uk.hannam.vehiclesapi.vehicles.components.Component;
import uk.hannam.vehiclesapi.vehicles.components.ComponentName;

public abstract class InventoryComponent extends Component implements Inventory {

	//-------------------------------------------------------------------- COMPONENT METHODS ------------------------------------------------------------------------
	
	
	@Override
	public String getComponentName() {
		return ComponentName.INVENTORY.getName();
	}

	@Override
	public String[] getRequiredComponents() {
		return new String[] {};
	}
	
	//-------------------------------------------------------------------- ABSTRACT METHODS ------------------------------------------------------------------------
	
	private static final int SIZE = 27;
	private static final String NAME = ChatColor.BLUE + "Vehicle";
	

	
	//-------------------------------------------------------------------- INVENTORY META ------------------------------------------------------------------------
	
	/**
	 * Recursive algorithm that turns a Map of String and Object to a JSONObject
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject toJSON(Map<String, Object> paramMap) {
		JSONObject meta = new JSONObject();
		for(Entry<String, Object> entry : paramMap.entrySet()) {
			if(entry.getValue() instanceof Map) {
				meta.put(entry.getKey(), toJSON((Map<String, Object>) entry.getValue()));
			} else if(entry.getValue() instanceof String || entry.getValue() instanceof Integer) {
				meta.put(entry.getKey(), entry.getValue());
			}
		}
		return meta;
	}
	
	/**
	 * Used to get the meta of the inventory
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getMeta() {
		if(this.getInventory() == null) return null;
		
		JSONObject inventoryMeta = new JSONObject();
		inventoryMeta.put("SIZE", SIZE);
		inventoryMeta.put("NAME", NAME);
		
		JSONObject inventorySlotMeta = new JSONObject();
		
		for(int slot = 0; slot < this.getInventory().getSize(); slot++) {
			if(this.getInventory().getItem(slot) != null) {
				
				JSONObject itemInfo = new JSONObject();
				
				itemInfo.put("ITEM", toJSON(this.getInventory().getItem(slot).serialize()));
				itemInfo.put("META", toJSON(this.getInventory().getItem(slot).getItemMeta().serialize()));
				
				inventorySlotMeta.put(slot, itemInfo);
			}
		}
		
		inventoryMeta.put("INVENTORY_SLOTS", inventorySlotMeta);
		return inventoryMeta;
	}
	
	/**
	 * Builds the inventory using the previously saved meta
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public void buildFromMeta(JSONObject paramMeta) {
		
		try {
			
			org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, SIZE, (String) paramMeta.get("NAME"));
			
			for(Object itemSlotObj : ((JSONObject) paramMeta.get("INVENTORY_SLOTS")).keySet()) {
				JSONObject itemInfoJson = (JSONObject) ((JSONObject) paramMeta.get("INVENTORY_SLOTS")).get(itemSlotObj);
				JSONObject itemJson = (JSONObject) itemInfoJson.get("ITEM");
				JSONObject metaJson = (JSONObject) itemInfoJson.get("META");
				
				Map<String, Object> seralizedItem = new HashMap<String, Object>();
				itemJson.forEach((k, v) -> seralizedItem.put((String) k, v));
				
				Map<String, Object> seralizedMeta = new HashMap<String, Object>();
				metaJson.forEach((k, v) -> seralizedMeta.put((String) k, v));
				
				ItemStack itemStack = ItemStack.deserialize(seralizedItem);
				
				ItemMeta itemMeta = null;
				
				try {
					String serverVersion = VehiclesAPI.getPlugin().getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]; // gets the version of the server
					Class[] craftMetaItemClasses = Class.forName("org.bukkit.craftbukkit."+serverVersion+".inventory.CraftMetaItem").getDeclaredClasses();
					
					for (Class craftMetaItemClass : craftMetaItemClasses) {
						if(!craftMetaItemClass.getSimpleName().equals("SerializableMeta")) continue;
						
						Method deserialize = craftMetaItemClass.getMethod("deserialize", Map.class);
						itemMeta = (ItemMeta) deserialize.invoke(null, seralizedMeta);
						
					}
					
					if(seralizedMeta.containsKey("enchants")) {
						
						for(Entry<String, Object> entry : ((Map<String, Object>) seralizedMeta.get("enchants")).entrySet()) {
							Enchantment enchantmentName = Enchantment.getByName(entry.getKey());
							long level = (long) entry.getValue();
							
							itemMeta.addEnchant(enchantmentName, (int) level, true);
						}
					}
					
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				itemStack.setItemMeta(itemMeta);
				
				
				inventory.setItem(Integer.valueOf((String) itemSlotObj), itemStack);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//-------------------------------------------------------------------- VEHICLE METHODS ------------------------------------------------------------------------
	
	public void onVehicleClick(Player paramPlayer) {
		User user = VehiclesAPI.getUserManager().getUser(paramPlayer.getUniqueId());
		user.openVehicleInventory(super.getVehicle());
	}
	
	//-------------------------------------------------------------------- INVENTORY METHODS ------------------------------------------------------------------------

	@Setter
	@Getter
	private org.bukkit.inventory.Inventory inventory;
	
	public void onInventoryClick(InventoryClickEvent event) {
		
		event.setCancelled(true);
		
		User user = VehiclesAPI.getUserManager().getUser(event.getWhoClicked().getUniqueId());
		
		if(event.getRawSlot() == 13) {
			
			if(user.inSeat()) {
				Messages.USER_IN_SEAT.sendMessage(user.getPlayer());
				return;
			}
			
			if(this.getDriverSeat().hasRider()) {
				Messages.SEAT_OCCUPIED.sendMessage(user.getPlayer());
				return;
			}
			
			user.quickSetDrive(super.getVehicle());
			user.getPlayer().closeInventory();
		}
	}
	
	@Override
	public void onMount(Player paramRider) {
		if(super.getDriverSeat().hasRider()) {
			ItemStack inSeat = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			ItemMeta inSeatMeta = inSeat.getItemMeta();
			inSeatMeta.setDisplayName(ChatColor.RED + "Seat Occupied!");
			inSeatMeta.addEnchant(Enchantment.DURABILITY, 3, true);
			inSeat.setItemMeta(inSeatMeta);
			
			this.getInventory().setItem(13, inSeat);
		}
	}
	
	@Override
	public void onDismount(OfflinePlayer paramRider) {
		if(!super.getDriverSeat().hasRider()) {
			ItemStack seatUnoccupied = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
			ItemMeta seatUnoccupiedMeta = seatUnoccupied.getItemMeta();
			seatUnoccupiedMeta.setDisplayName(ChatColor.RED + "Click to sit in vehicle");
			seatUnoccupiedMeta.addEnchant(Enchantment.DURABILITY, 3, true);
			seatUnoccupied.setItemMeta(seatUnoccupiedMeta);
			
			this.getInventory().setItem(13, seatUnoccupied);
		}
	}
	

	
	private void buildInventory() {
		
		org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, SIZE, NAME);
		
		ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta fillerItemMeta = fillerItem.getItemMeta();
		fillerItemMeta.setDisplayName(ChatColor.RED + " ");
		fillerItem.setItemMeta(fillerItemMeta);
		
		for(int i = 0; i < 3* 9; i++ ) {
			if(i == 13) {
				ItemStack seatUnoccupied = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
				ItemMeta seatUnoccupiedMeta = seatUnoccupied.getItemMeta();
				seatUnoccupiedMeta.setDisplayName(ChatColor.RED + "Click to sit in vehicle");
				seatUnoccupied.setItemMeta(seatUnoccupiedMeta);
				
				inventory.setItem(i, seatUnoccupied);
			} else {
				inventory.setItem(i, fillerItem);
			}
		}
		
		this.setInventory(inventory);
		
	}
	
	//-------------------------------------------------------------------- CONSTRUCTOR ------------------------------------------------------------------------

	public InventoryComponent(Vehicle paramVehicle) {
		super(paramVehicle);
		
		this.buildInventory();
	}


}
