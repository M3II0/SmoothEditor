package sk.m3ii0.smootheditor.code;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import sk.m3ii0.smootheditor.code.announcement.UpdateChecker;
import sk.m3ii0.smootheditor.code.commands.ASEditorCommand;
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
	private static final String vversion = "0.6";
	
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
		getCommand("aseditor").setExecutor(new ASEditorCommand());
		new UpdateChecker(this, "SmoothEditor", "https://www.spigotmc.org/resources/110033/", "smootheditor.admin", vversion, 110033);
	}
	
	@Override
	public void onDisable() {
		GUI.unregister();
		HandlerList.unregisterAll(this);
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