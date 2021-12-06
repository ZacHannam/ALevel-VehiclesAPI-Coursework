package uk.hannam.vehiclesapi.enums;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.database.Config;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public enum Messages {
	
	SEAT_OCCUPIED(ChatColor.GREEN + "That seat is already occupied!"),
	USER_IN_SEAT(ChatColor.GREEN + "You are already in a seat!"),
	INVALID_PERMISSION(ChatColor.RED + "Invalid Permission!"),
	COMMAND_SUMMON_VEHICLE_USAGE(ChatColor.RED + "/summonvehicle <vehicletype> [<world> <x> <y> <z>]"),
	INVALID_VEHICLE(ChatColor.RED + "Invalid Vehicle Model"),
	SUCCESSFULLY_SPAWNED_VEHICLE(ChatColor.GREEN + "Successfully summoned vehicle!"),
	RELOADED_PLUGIN(ChatColor.GREEN + "Reloaded the vehicles plugin!"),
	VEHICLES_DESTROYED(ChatColor.GREEN + "%amount% vehicle(s) have been destroyed!"),
	VEHICLES_USAGE(ChatColor.RED + "/vehicles <reload | destroynear | destroyall> [range]"),
	MUST_BE_PLAYER(ChatColor.RED + "You must be a player to perform that command!");
	
	@Getter
	private static final String CONFIG_NAME = "Config";
	
	@Setter
	private String message;
	
	@Getter
	private static final HashMap<Messages, String> customMessages = new HashMap<Messages,String>();
	
	public static void reload() {
		
		getCustomMessages().clear();
		
		Config config = new Config(getCONFIG_NAME());
		FileConfiguration configFile = config.getConfig();
		
		for(Messages message : Messages.values()) {
			
			if(configFile.isString("messages." + message.toString())) {
				String messageValue = configFile.getString("messages." + message);
				getCustomMessages().put(message, ChatColor.translateAlternateColorCodes('&', messageValue));
			} else {
				configFile.set("messages."+ message, message.getRawMessage().replace('�', '&'));
			}
			
		}
		config.saveConfig();
	}

	Messages(String paramMessage){
		this.setMessage(paramMessage);
	}
	
	public String getRawMessage() {
		return this.message;
	}
	
	public String getMessage() {
		if(customMessages.containsKey(this)) {
			return customMessages.get(this);
		}
		return this.message;
	}
	
	public void sendMessage(Player paramPlayer) {
		paramPlayer.sendMessage(this.getMessage());
	}
	
	public void sendConsoleMessage() {
		Bukkit.getLogger().info(this.getMessage());
	}
	
	public void sendCommandSender(CommandSender paramCommandSender, ImmutableMap<String, String> paramFormat) {
	
		
		String messagep = this.getMessage();
		
		for(String format : paramFormat.keySet()) {
			String formatTo = paramFormat.get(format);
			
			messagep = messagep.replaceAll(format, formatTo);
		}
		
		paramCommandSender.sendMessage(messagep);
	}
	
	public void sendCommandSender(CommandSender paramCommandSender) {
		paramCommandSender.sendMessage(this.getMessage());
	}

	@SuppressWarnings("deprecation")
	public void sendMessageWithHover(Player paramPlayer, String paramHoverText, ImmutableMap<String, String> paramFormat) {
		
		String messagep = this.getMessage();
		
		for(String format : paramFormat.keySet()) {
			String formatTo = paramFormat.get(format);
			
			messagep = messagep.replaceAll(format, formatTo);
		}
		
		TextComponent message = new TextComponent(messagep);
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(paramHoverText).create()));
		
		paramPlayer.spigot().sendMessage(message);
		
	}
}

