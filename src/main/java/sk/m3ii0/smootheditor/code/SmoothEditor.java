package sk.m3ii0.smootheditor.code;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	/*
	*
	* Values
	*
	* */
	
	private static Plugin instance;
	private static boolean modern;
	
	/*
	*
	* Bukkit API
	*
	* */
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(getVersion());
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
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
	
	public static boolean isModern() {
		return modern;
	}
	
	public static Plugin getInstance() {
		return instance;
	}
	
}