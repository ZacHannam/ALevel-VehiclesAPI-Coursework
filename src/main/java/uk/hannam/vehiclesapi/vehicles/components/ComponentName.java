package uk.hannam.vehiclesapi.vehicles.components;

import lombok.Getter;
import lombok.Setter;

public enum ComponentName {
	
	FRAME("FRAME"),
	INVENTORY("INVENTORY"),
	SEAT("SEAT"),
	PASSANGER_SEAT("PASSANGER_SEAT"),
	DRIVER_SEAT("DRIVER_SEAT"),
	STEERING_WHEEL("STEERING_WHEEL"),
	WHEELBASE("WHEELBASE");
	
	@Getter
	@Setter
	String name; // stores the unique name for each Component Type
	
	/**
	 * Used as a final for all Component Names used inside of the API. All reference unique strings to allow the API to work.
	 * @param paramName
	 */
	ComponentName(String paramName){
		this.setName(paramName);
	}
}
