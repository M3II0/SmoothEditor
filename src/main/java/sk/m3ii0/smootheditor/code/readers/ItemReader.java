package sk.m3ii0.smootheditor.code.readers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.readers.utils.XMaterial;

import java.util.List;
import java.util.Set;

public class ItemReader {
	
	public static ItemStack read(char id) {
		String path = "Items." + id + ".";
		FileConfiguration c = SmoothEditor.getOptions();
		String color = c.getString(path + "Color");
		int amount = c.getInt(path + "Amount");
		String name = c.getString(path + "Name");
		List<String> lore = c.getStringList("Lore");
		lore.replaceAll(var -> var.replace("&", "§"));
		ItemStack item;
		if (SmoothEditor.isModern()) {
			String finalColor = (color.isEmpty() || color.equalsIgnoreCase("none"))? "" : color.toUpperCase() + "_";
			item = XMaterial.fromString(finalColor + c.getString(path + "Material")).parseItem();
			item.setAmount(amount);
		} else {
			item = XMaterial.requestXMaterial(c.getString(path + "Material"), getColor(color)).parseItem();
			item.setAmount(amount);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name.replace("&", "§"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static char[] getItems() {
		FileConfiguration c = SmoothEditor.getOptions();
		Set<String> items = c.getConfigurationSection("Items").getValues(false).keySet();
		char[] result = new char[items.size()];
		int counter = 0;
		for (String var : items) {
			char id = var.charAt(0);
			result[counter] = id;
			++counter;
		}
		return result;
	}
	
	private static byte getColor(String color) {
		switch (color.toUpperCase()) {
			case "ORANGE": return (byte) 1;
			case "MAGENTA": return (byte) 2;
			case "LIGHT_BLUE": return (byte) 3;
			case "YELLOW": return (byte) 4;
			case "LINE": return (byte) 5;
			case "PINK": return (byte) 6;
			case "GRAY": return (byte) 7;
			case "LIGHT_GRAY": return (byte) 8;
			case "CYAN": return (byte) 9;
			case "PURPLE": return (byte) 10;
			case "BLUE": return (byte) 11;
			case "BROWN": return (byte) 12;
			case "GREEN": return (byte) 13;
			case "RED": return (byte) 14;
			case "BLACK": return (byte) 15;
		}
		return 0;
	}
	
}
