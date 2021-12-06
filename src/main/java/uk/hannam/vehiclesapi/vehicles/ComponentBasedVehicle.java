package uk.hannam.vehiclesapi.vehicles;

import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import uk.hannam.vehiclesapi.vehicles.components.Component;

public interface ComponentBasedVehicle {

	// Returns a list of components
	ArrayList<Component> getComponents();
	
	/**
	 * Calls onButtonPressW on all of the components
	 */
	default void buttonPressW() {
		
		for(Component component : getComponents()) {
			component.onButtonPressW();
		}
		
	}
	
	/**
	 * Calls onButtonPressA on all of the components
	 */
	default void buttonPressA() {
		
		for(Component component : getComponents()) {
			component.onButtonPressA();
		}
		
	}
	
	/**
	 * Calls onButtonPressS on all of the components
	 */
	default void buttonPressS() {
		
		for(Component component : getComponents()) {
			component.onButtonPressS();
		}
		
	}
	
	/**
	 * Calls onButtonPressD on all of the components
	 */
	default void buttonPressD() {
		
		for(Component component : getComponents()) {
			component.onButtonPressD();
		}
		
	}
	
	/**
	 * Calls onButtonPressSpace on all of the components
	 */
	default void buttonPressSpace() {
		
		for(Component component : getComponents()) {
			component.onButtonPressSpace();
		}
		
	}
	
	/**
	 * Calls onButtonPressCtrl on all of the components
	 */
	default void buttonPressCtrl() {
		
		for(Component component : getComponents()) {
			component.onButtonPressCtrl();
		}
	}
	
	/**
	 * Calls noButtonPressForward on all of the components
	 */
	default void noButtonPressForward() {
		
		for(Component component : getComponents()) {
			component.noButtonPressForward();
		}
		
	}
	
	/**
	 * Calls noButtonPressSideways on all of the components
	 */
	default void noButtonPressSideways() {
		
		for(Component component : getComponents()) {
			component.noButtonPressSideways();
		}
	}
	
	/**
	 * Calls halt on all of the components
	 */
	default void haltComponents() {
		for(Component component : getComponents()) {
			component.halt();
		}
	}
	
	/**
	 * Calls onVehicleClick on all of the components
	 * @param
	 */
	default void onVehicleClick(Player paramPlayer) {
		for(Component component : getComponents()) {
			component.onVehicleClick(paramPlayer);
		}
	}
	
	/**
	 * Calls onDismount on all of the components
	 * @param
	 */
	default void onDismount(OfflinePlayer paramRider) {
		for(Component component : getComponents()) {
			component.onDismount(paramRider);
		}
	}
	
	/**
	 * Calls onMount on all of the components
	 * @param paramRider
	 */
	default void onMount(Player paramRider) {
		for(Component component : getComponents()) {
			component.onMount(paramRider);
		}
	}
}
