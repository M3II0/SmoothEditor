package sk.m3ii0.smootheditor.code;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import sk.m3ii0.smootheditor.code.editor.GUI;
import sk.m3ii0.smootheditor.code.listeners.SelectionListener;

import java.io.File;

public class SmoothEditor extends JavaPlugin {
	
	/*
	*
	* Values
	*
	* */
	
	private static Plugin instance;
	private static boolean modern;
	private static FileConfiguration options;
	
	/*
	*
	* Bukkit API
	*
	* */
	
	@Override
	public void onLoad() {
		instance = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		saveResource("options.yml", false);
		File configFile = new File(getDataFolder(), "options.yml");
		options = YamlConfiguration.loadConfiguration(configFile);
	}
	
	@Override
	public void onEnable() {
		String rawVersion = getVersion().split("_")[1];
		int version = Integer.parseInt(rawVersion);
		modern = version > 12;
		Bukkit.getPluginManager().registerEvents(new SelectionListener(), this);
		GUI.register(this);
	}
	
	@Override
	public void onDisable() {
		GUI.unregister();
	}
	
	/*
	*
	* Private API
	*
	* */
	
	private String getVersion() {
		final String packageName = getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}
	
	/*
	*
	* API
	*
	* */
	
	public static FileConfiguration getOptions() {
		return options;
	}
	
	public static boolean isModern() {
		return modern;
	}
	
	public static Plugin getInstance() {
		return instance;
	}
	
}