package uk.hannam.vehiclesapi.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;

public class VehiclesDatabase extends Database{

	private static final String TABLE_NAME = "vehicles";
	
	public VehiclesDatabase() {
		super("Vehicles");
		
		this.createDatabase();
	}
	
	private void createDatabase() {
		
		DatabaseMetaData meta = super.getMetaData();
		try {
			
			ResultSet tables = meta.getTables(null, null, TABLE_NAME, null);
			
			if(!tables.next()) {
				
				this.executeUpdate("CREATE TABLE " + TABLE_NAME + " (vehicleUUID String, type int, meta String);");
				
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void saveVehicle(Vehicle paramVehicle) {
		
		String meta = paramVehicle.getMeta().toString();
		
		ResultSet query = this.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE vehicleUUID == '" + paramVehicle.getUuid().toString()  + "';");
		
		try {
			if(query != null && query.next()) {
				this.executeUpdate("UPDATE vehicles SET meta = '" + meta + "' WHERE vehicleUUID == '" + paramVehicle.getUuid().toString()  + "';");
			} else {
				this.executeUpdate("INSERT INTO vehicles (vehicleUUID, type, meta) VALUES ('" +
			paramVehicle.getUuid() + "', " + paramVehicle.getTypeID() + ", '" + meta + "');");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadAllWithID(int paramID) {
		
		ResultSet query = this.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE type == " + paramID + ";");
		
		try {
			while(query.next()) {
				VehiclesAPI.getVehicleManager().createFromDatabaseQuery(query.getString("vehicleUUID"), query.getInt("type"), query.getString("meta"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public void removeVehicle(UUID paramUUID) {
		// TODO Auto-generated method stub
		this.executeUpdate("DELETE FROM " + TABLE_NAME + " WHERE vehicleUUID == '" + paramUUID.toString() + "';");
	}

}
