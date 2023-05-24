package sk.m3ii0.smootheditor.code.readers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.editor.enums.MenuAction;
import sk.m3ii0.smootheditor.code.readers.utils.XMaterial;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemReader {
	
	public static ItemStack read(char id) {
		String path = "Items." + id + ".";
		FileConfiguration c = SmoothEditor.getOptions();
		String color = c.getString(path + "Color");
		int amount = c.getInt(path + "Amount");
		String name = c.getString(path + "Name");
		List<String> lore = c.getStringList(path + "Lore");
		lore.replaceAll(var -> var.replace("&", "§"));
		ItemStack item;
		if (c.getString(path + "Material").startsWith("Skull=")) {
			String texture = c.getString(path + "Material").replace("Skull=", "");
			item = createSkull(texture);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			item.setAmount(amount);
			meta.setDisplayName(name.replace("&", "§"));
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setItemMeta(meta);
			return item;
		}
		item = XMaterial.requestXMaterial(c.getString(path + "Material"), getColor(color)).parseItem();
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name.replace("&", "§"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static MenuAction readAction(char id) {
		FileConfiguration c = SmoothEditor.getOptions();
		String path = "Items." + id + ".Action";
		String rawAction = c.getString(path);
		if (rawAction == null) return MenuAction.NONE;
		try {
			return MenuAction.valueOf(rawAction);
		} catch (IllegalArgumentException e) {
			return MenuAction.NONE;
		}
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
	
	private static ItemStack createSkull(String url) {
		ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
		if (url.isEmpty())
			return head;
		
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		
		profile.getProperties().put("textures", new Property("textures", url));
		
		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
			
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}
	
}
