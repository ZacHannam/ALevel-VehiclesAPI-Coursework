package uk.hannam.vehiclesapi.events.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import uk.hannam.vehiclesapi.events.protocollib.wrappers.WrapperPlayClientSteerVehicle;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.user.User;
import uk.hannam.vehiclesapi.vehicles.Vehicle;

public class PlayClientSteerVehicle {

	public PlayClientSteerVehicle() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(VehiclesAPI.getPlugin(), ListenerPriority.HIGH, PacketType.Play.Client.STEER_VEHICLE) {
					
				@Override
				public void onPacketReceiving(PacketEvent event) {
					
					if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
						
						if(VehiclesAPI.getUserManager().isUser(event.getPlayer().getUniqueId())) {
							User user = VehiclesAPI.getUserManager().getUser(event.getPlayer().getUniqueId());
							if(user.inDriverSeat()) {
								
								PacketContainer packet = event.getPacket();
								WrapperPlayClientSteerVehicle wrapper = new WrapperPlayClientSteerVehicle(packet);
								
								Vehicle vehicle = user.getVehicle();
								
								if(wrapper.getForward() > 0.0) {
									vehicle.buttonPressW();
								} else if(wrapper.getForward() < 0.0){
									vehicle.buttonPressS();
								} else if(wrapper.getForward() == 0.0){
									vehicle.noButtonPressForward();
								}
								
								if(wrapper.getSideways() > 0.0) {
									vehicle.buttonPressA();
								} else if(wrapper.getSideways() < 0.0){
									vehicle.buttonPressD();
								} else if(wrapper.getSideways() == 0.0){
									vehicle.noButtonPressSideways();
								}
								
								if(wrapper.isJump()) {
									vehicle.buttonPressSpace();
								}
								
								if(wrapper.isUnmount()) {
									vehicle.buttonPressCtrl();
								}
								
							}
						}			
					}
					
				}
					
		});
		
	}
}
