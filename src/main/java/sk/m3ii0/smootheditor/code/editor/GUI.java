package sk.m3ii0.smootheditor.code.editor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUI implements Listener {
	
	/*
	 *
	 * Static values
	 *
	 * */
	
	private static GUI listener;
	private static final Map<UUID, GUI> cache = new HashMap<>();
	
	/*
	 *
	 * Register / Unregister
	 *
	 * */
	
	public static void register(Plugin plugin) {
		listener = new GUI();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}
	
	public static void unregister() {
		if (listener != null) {
			HandlerList.unregisterAll(listener);
		}
	}
	
	/*
	 *
	 * Event handler
	 *
	 * */
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		UUID uuid = e.getWhoClicked().getUniqueId();
		if (cache.containsKey(uuid)) {
			GUI gui = cache.get(uuid);
			if (gui != null) {
				e.setCancelled(true);
				Player player = (Player) e.getWhoClicked();
				int rawSlot = e.getRawSlot();
				ClickType clickType = e.getClick();
				gui.parseClick(player, clickType, rawSlot);
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if (cache.containsKey(uuid)) {
			GUI gui = cache.remove(uuid);
			if (gui != null) {
				gui.close((Player) e.getPlayer());
			}
		}
	}
	
	/*
	 *
	 * RAM values
	 *
	 * */
	
	private String id;
	private final Map<Integer, MenuItem> items = new HashMap<>();
	int viewers = 0;
	private String title;
	private int size;
	private Inventory inventory;
	
	/*
	 *
	 * Constructors
	 *
	 * */
	
	protected GUI() {}
	
	public GUI(String id, String title, int size) {
		this.id = id;
		this.title = title;
		this.size = size;
		this.inventory = Bukkit.createInventory(null, size*9, title);
	}
	
	/*
	 *
	 * Items API
	 *
	 * */
	
	public static MenuItem item(ItemStack itemStack) {
		return new MenuItem() {
			@Override
			public ItemStack item() {
				return itemStack;
			}
			
			@Override
			public FunctionalInterface click() {
				return new FunctionalInterface() {
					@Override
					public void onClick(Player player, GUI gui, int slot, ClickType clickType) {
					
					}
				};
			}
		};
	}
	
	public static MenuItem item(ItemStack itemStack, FunctionalInterface click) {
		return new MenuItem() {
			@Override
			public ItemStack item() {
				return itemStack;
			}
			
			@Override
			public FunctionalInterface click() {
				return click;
			}
		};
	}
	
	public interface FunctionalInterface {
		void onClick(Player player, GUI gui, int slot, ClickType clickType);
	}
	
	public interface MenuItem {
		ItemStack item();
		FunctionalInterface click();
	}
	
	public void addItem(MenuItem item) {
		items.put(items.size(), item);
		inventory.addItem(item.item());
	}
	
	public void setItem(int slot, MenuItem item) {
		items.put(slot, item);
		inventory.setItem(slot, item.item());
	}
	
	public void updateItem(int slot, MenuItem item) {
		items.put(slot, item);
		inventory.setItem(slot, item.item());
	}
	
	/*
	 *
	 * Getters
	 *
	 * */
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getSize() {
		return size;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	/*
	 *
	 * Functions
	 *
	 * */
	
	public void open(Player player) {
		viewers++;
		player.openInventory(inventory);
		cache.put(player.getUniqueId(), this);
	}
	
	public void close(Player player) {
		player.closeInventory();
		cache.remove(player.getUniqueId());
	}
	
	private void parseClick(Player player, ClickType clickType, int slot) {
		MenuItem item = items.get(slot);
		if (item != null) {
			item.click().onClick(player, this, slot, clickType);
		}
	}
	
}