package uk.hannam.vehiclesapi.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
	private final String n;
	private FileConfiguration fc;
	private File file;
	private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(this.getClass());
	private static final ArrayList<Config> configs = new ArrayList<>();
 
	public Config(String n) {
		this.n = n;
 
		configs.add(this);
	}
 
	public String getName() {
		if (n == null)
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return n;
	}
 
	public JavaPlugin getInstance() {
		if (plugin == null)
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return plugin;
	}
 
	public static Config getConfig(String n) {
		for (Config c : configs) {
	         if (c.getName().equals(n)) {
	             return c;
	        }
	    }
		return new Config(n);
	}
 
	public boolean delete() {
		return getFile().delete();
	}
 
	public boolean exists() {
		if (fc == null || file == null) {
			File temp = new File(getDataFolder(), getName() + ".yml");
			if (!temp.exists()) {
				return false;
			}
				file = temp;
		}
		return true;
	}
 
	public File getDataFolder() {
		File dir = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
		File d = new File(dir.getParentFile().getPath(), getInstance().getName());
		if (!d.exists()) {
			d.mkdirs();
		}
		return d;
	}
 
	public File getFile() {
		if (file == null) {
			file = new File(getDataFolder(), getName() + ".yml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
 
	public FileConfiguration getConfig() {
		if (fc == null) {
			fc = YamlConfiguration.loadConfiguration(getFile());
		}
		return fc;
	}
 
	public void resetConfig() {
		delete();
		getConfig();
	}
 
	public void saveConfig() {
		try {
			getConfig().save(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void halt() {
		configs.clear();
	}
}
