package uk.hannam.vehiclesapi.enums;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public enum Permissions {

	COMMAND_SUMMON_VEHICLE("vehiclesapi.summonvehicle"),
	COMMAND_VEHICLES_RELOAD("vehiclesapi.vehicles.reload"),
	COMMAND_VEHICLES_DESTROYNEAR("vehiclesapi.vehicles.destroy.near"),
	COMMAND_VEHICLES_DESTROYALL("vehiclesapi.vehicles.destroy.all");
	
	@Getter
	@Setter
	private String permission;
	
	Permissions(String paramPermission){
		this.setPermission(paramPermission);
	}

	public boolean hasPermission(Player paramPlayer) {
		return paramPlayer.hasPermission(this.getPermission());
	}
	
}
