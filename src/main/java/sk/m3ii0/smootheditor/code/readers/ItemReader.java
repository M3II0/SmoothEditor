package sk.m3ii0.smootheditor.code.readers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.editor.enums.MenuAction;
import sk.m3ii0.smootheditor.code.utils.ColorTranslator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemReader {
	
	private final static Method SET_CUSTOM_MODEL_DATA;
	
	static {
		Method SET_CUSTOM_MODEL_DATA1;
		try {
			SET_CUSTOM_MODEL_DATA1 = ItemMeta.class.getDeclaredMethod("setCustomModelData", Integer.class);
		} catch (NoSuchMethodException e) {
			SET_CUSTOM_MODEL_DATA1 = null;
		}
		SET_CUSTOM_MODEL_DATA = SET_CUSTOM_MODEL_DATA1;
		if (SET_CUSTOM_MODEL_DATA != null) {
			SET_CUSTOM_MODEL_DATA.setAccessible(true);
		}
	}
	
	public static ItemStack read(char id) {
		String path = "Items." + id + ".";
		FileConfiguration c = SmoothEditor.getOptions();
		int amount = c.getInt(path + "Amount");
		int cmd = c.getInt(path + "CustomModelData");
		String name = c.getString(path + "Name");
		List<String> lore = c.getStringList(path + "Lore");
		ItemStack item;
		if (c.getString(path + "Material").startsWith("Skull=")) {
			String texture = c.getString(path + "Material").replace("Skull=", "");
			item = createSkull(texture);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			item.setAmount(amount);
			if (hasModelData()) {
				try {
					SET_CUSTOM_MODEL_DATA.invoke(meta, cmd);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			meta.setDisplayName(ColorTranslator.colorize(name));
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setItemMeta(meta);
			return item;
		}
		item = new ItemStack(Material.valueOf(c.getString(path + "Material")));
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		if (hasModelData()) {
			try {
				SET_CUSTOM_MODEL_DATA.invoke(meta, cmd);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		meta.setDisplayName(ColorTranslator.colorize(name));
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
	
	private static ItemStack createSkull(String url) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		if (url.isEmpty())
			return head;
		
		try {
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
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return head;
	}
	
	private static boolean hasModelData() {
		return SET_CUSTOM_MODEL_DATA != null;
	}
	
}
