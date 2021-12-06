package uk.hannam.vehiclesapi.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;

public class Database {
	
	// Contains a list of all of the loaded databases so there is no loading the same database.
	@Getter
	private static final HashMap<String, Database> databases = new HashMap<String, Database>();
	
	// The main class
	@Getter
	private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(this.getClass());
	
	@Getter
	@Setter
	private String fileName;
	
	// connection to the database
	@Getter
	@Setter
	private Connection connection;
	
	/**
	 * Returns the database that is associated to the 
	 * 
	 * @param paramName
	 * @return
	 */
	public static Database getDatabase(String paramName) {
		if(getDatabases().containsKey(paramName)) {
			return getDatabases().get(paramName);
		}
		return new Database(paramName);
	}
	
	/**
	 * Returns the datapath as a string
	 * @return
	 */
	public String getDataFolderPath() {
		File dir = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
		File d = new File(dir.getParentFile().getPath(), getPlugin().getName());
		
		return d.getPath();
	}
	
	/**
	 * Creates the connection to the database
	 * @param paramName
	 * @return
	 */
	private Connection getConnection(String paramName) {
		
		try {
		
			String url = "jdbc:sqlite:" + getDataFolderPath() + File.separator + paramName + ".db";
			
			Class.forName("org.sqlite.JDBC");
			
			return DriverManager.getConnection(url);
			
		} catch(Exception e) {
		
			e.printStackTrace();
			
		}
		return null;
        
	}
	
	public ResultSet executeQuery(String paramInstruction) {
		Statement statement;
		try {
			statement = this.getConnection().createStatement();
			ResultSet result = statement.executeQuery(paramInstruction);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void executeUpdate(String paramInstruction) {
		Statement statement;
		try {
			statement = this.getConnection().createStatement();
			statement.executeUpdate(paramInstruction);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public DatabaseMetaData getMetaData() {
		DatabaseMetaData meta;
		try {
			meta = this.getConnection().getMetaData();
			return meta;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public Database(String paramName) {
		getDatabases().put(paramName, this);
		
		this.setConnection(getConnection(paramName));
	}
	
	public void halt() {
		try {
			this.connection.close();
			getDatabases().remove(this.getFileName());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
