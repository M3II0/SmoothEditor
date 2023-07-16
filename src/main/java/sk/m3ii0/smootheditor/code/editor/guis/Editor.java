package sk.m3ii0.smootheditor.code.editor.guis;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.editor.GUI;
import sk.m3ii0.smootheditor.code.editor.enums.ActionDirection;
import sk.m3ii0.smootheditor.code.editor.enums.ActionValue;
import sk.m3ii0.smootheditor.code.editor.enums.MenuAction;
import sk.m3ii0.smootheditor.code.listeners.SelectionListener;
import sk.m3ii0.smootheditor.code.readers.ItemReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Editor extends GUI {
	
	/*
	*
	* Values
	*
	* */
	
	private final static Map<UUID, Editor> editors;
	private final static List<String> layout;
	private final static Map<Character, ItemStack> items;
	private final static Map<Character, MenuAction> actions;
	private final static Map<UUID, ActionDirection> directions;
	private final static Map<UUID, Double> changes;
	private final static Map<UUID, ActionValue> values;
	
	/*
	*
	* Static constructor
	*
	* */
	
	static {
		editors = new HashMap<>();
		layout = SmoothEditor.getOptions().getStringList("MenuLayout");
		items = new HashMap<>();
		actions = new HashMap<>();
		directions = new HashMap<>();
		changes = new HashMap<>();
		values = new HashMap<>();
		char[] ids = ItemReader.getItems();
		for (char var : ids) {
			ItemStack itemStack = ItemReader.read(var);
			MenuAction action = ItemReader.readAction(var);
			items.put(var, itemStack);
			actions.put(var, action);
		}
	}
	
	/*
	*
	* Builders
	*
	* */
	
	public static void crateOrGet(Player player) {
		if (editors.containsKey(player.getUniqueId())) {
			editors.get(player.getUniqueId()).open(player);
		}
		Editor editor = new Editor(player);
		editors.put(player.getUniqueId(), editor);
	}
	
	public static Editor removeAndCreate(Player player) {
		Editor editor = new Editor(player);
		editors.put(player.getUniqueId(), editor);
		return editor;
	}
	
	public static void remove(UUID player) {
		editors.remove(player);
	}
	
	/*
	*
	* Constructor
	*
	* */
	
	private Editor(Player player) {
		
		super("tes", "Editor", 6);
		
		if (!SelectionListener.hasSelectedEntity(player)) {
			player.sendMessage(SmoothEditor.getOptions().getString("OpenHandler.SelectEntity").replace("&", "§"));
			return;
		}
		
		Entity rawAs = SelectionListener.getSelectedEntity(player);
		
		if (rawAs == null) {
			player.sendMessage(SmoothEditor.getOptions().getString("OpenHandler.EntityNull").replace("&", "§"));
			return;
		}
		
		ArmorStand as = (ArmorStand) rawAs;
		ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
		double change = changes.getOrDefault(player.getUniqueId(), 1.0);
		ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
		
		int counter = 0;
		for (String line : layout) {
			for (char var : line.toCharArray()) {
				if (items.containsKey(var)) {
					ItemStack item = items.get(var).clone();
					ItemMeta meta = item.getItemMeta();
					String name = (meta.hasDisplayName())? meta.getDisplayName() : "";
					meta.setDisplayName(setPlaceholders(name, as, direction, value, change));
					if (meta.hasLore()) {
						List<String> lore = meta.getLore();
						lore.replaceAll(loreLine -> setPlaceholders(loreLine, as, direction, value, change));
						meta.setLore(lore);
					}
					item.setItemMeta(meta);
					setItem(counter, GUI.item(item, createFunction(as, actions.get(var))));
				}
				++counter;
			}
		}
		
		open(player);
	}
	
	private void updateContents(ArmorStand as, ActionDirection direction, ActionValue value, double change) {
		int counter = 0;
		for (String line : layout) {
			for (char var : line.toCharArray()) {
				if (items.containsKey(var)) {
					ItemStack item = items.get(var).clone();
					ItemMeta meta = item.getItemMeta();
					String name = (meta.hasDisplayName())? meta.getDisplayName() : "";
					meta.setDisplayName(setPlaceholders(name, as, direction, value, change));
					if (meta.hasLore()) {
						List<String> lore = meta.getLore();
						lore.replaceAll(loreLine -> setPlaceholders(loreLine, as, direction, value, change));
						meta.setLore(lore);
					}
					item.setItemMeta(meta);
					setItem(counter, GUI.item(item, createFunction(as, actions.get(var))));
				}
				++counter;
			}
		}
		for (HumanEntity entity : getInventory().getViewers()) {
			((Player) entity).updateInventory();
		}
	}
	
	private static String setPlaceholders(String text, ArmorStand armorStand, ActionDirection direction, ActionValue value, double change) {
		boolean visibility = armorStand.isVisible();
		boolean baseplate = armorStand.hasBasePlate();
		boolean arms = armorStand.hasArms();
		boolean size = armorStand.isSmall();
		boolean customname = armorStand.isCustomNameVisible();
		boolean gravity = armorStand.hasGravity();
		return text
		 .replace("{visibility}", translateBoolean(visibility))
		 .replace("{baseplate}", translateBoolean(baseplate))
		 .replace("{arms}", translateBoolean(arms))
		 .replace("{size}", translateBoolean(size))
		 .replace("{customname}", translateBoolean(customname))
		 .replace("{gravity}", translateBoolean(gravity))
		 .replace("{id}", armorStand.getEntityId() + "")
		 .replace("{direction}", direction.name())
		 .replace("{change}", value.parse(change) + "");
	}
	
	private static String translateBoolean(boolean value) {
		FileConfiguration c = SmoothEditor.getOptions();
		String var1 = c.getString("Placeholders.ConditionTrue");
		String var2 = c.getString("Placeholders.ConditionFalse");
		return Boolean.toString(value).replace("true", var1.replace("&", "§")).replace("false", var2.replace("&", "§"));
	}
	
	private static EulerAngle createAngle(Player player, EulerAngle old, ActionDirection direction, double change) {
		ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
		if (value == ActionValue.PLUS) {
			if (change < 0) change = -change;
		}
		if (value == ActionValue.MINUS) {
			if (change > 0) change = -change;
		}
		return old.add((direction == ActionDirection.X)? change : 0.0, (direction == ActionDirection.Y)? change : 0.0, (direction == ActionDirection.Z)? change : 0.0);
	}
	
	private static FunctionalInterface createFunction(ArmorStand armorStand, MenuAction action) {
		boolean visibility = armorStand.isVisible();
		boolean baseplate = armorStand.hasBasePlate();
		boolean arms = armorStand.hasArms();
		boolean size = armorStand.isSmall();
		boolean customname = armorStand.isCustomNameVisible();
		boolean gravity = armorStand.hasGravity();
		switch (action) {
			case UPDATE_HEAD: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setHeadPose(createAngle(player, armorStand.getHeadPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case UPDATE_BODY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setBodyPose(createAngle(player, armorStand.getBodyPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case UPDATE_RIGHT_ARM: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setRightArmPose(createAngle(player, armorStand.getRightArmPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case UPDATE_LEFT_ARM: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setLeftArmPose(createAngle(player, armorStand.getLeftArmPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case UPDATE_RIGHT_LEG: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setRightLegPose(createAngle(player, armorStand.getRightLegPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case UPDATE_LEFT_LEG: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setLeftLegPose(createAngle(player, armorStand.getLeftLegPose(), direction, change));
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_ARMS_VISIBILITY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setArms(!arms);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_BASEPLATE_VISIBILITY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setBasePlate(!baseplate);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_GRAVITY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setGravity(!gravity);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_SIZE: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setSmall(!size);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_VISIBILITY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setVisible(!visibility);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case CHANGE_CUSTOM_NAME_VISIBILITY: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					armorStand.setCustomNameVisible(!customname);
					((Editor) gui).updateContents(armorStand, direction, value, change);
				};
			}
			case VALUE_TO_PLUS: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					values.put(player.getUniqueId(), ActionValue.PLUS);
					((Editor) gui).updateContents(armorStand, direction, ActionValue.PLUS, (change < 0)? -change : change);
				};
			}
			case VALUE_TO_MINUS: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					values.put(player.getUniqueId(), ActionValue.MINUS);
					((Editor) gui).updateContents(armorStand, direction, ActionValue.MINUS, change);
				};
			}
			case DIRECTION_TO_X: {
				return (player, gui, slot, clickType) -> {
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					directions.put(player.getUniqueId(), ActionDirection.X);
					((Editor) gui).updateContents(armorStand, ActionDirection.X, value, change);
				};
			}
			case DIRECTION_TO_Y: {
				return (player, gui, slot, clickType) -> {
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					directions.put(player.getUniqueId(), ActionDirection.Y);
					((Editor) gui).updateContents(armorStand, ActionDirection.Y, value, change);
				};
			}
			case DIRECTION_TO_Z: {
				return (player, gui, slot, clickType) -> {
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					double change = changes.getOrDefault(player.getUniqueId(), 0.1);
					directions.put(player.getUniqueId(), ActionDirection.Z);
					((Editor) gui).updateContents(armorStand, ActionDirection.Z, value, change);
				};
			}
			case CHANGE_TO_0_1: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					changes.put(player.getUniqueId(), 0.1);
					((Editor) gui).updateContents(armorStand, direction, value, 0.1);
				};
			}
			case CHANGE_TO_0_5: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					changes.put(player.getUniqueId(), 0.5);
					((Editor) gui).updateContents(armorStand, direction, value, 0.5);
				};
			}
			case CHANGE_TO_1: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					changes.put(player.getUniqueId(), 1.0);
					((Editor) gui).updateContents(armorStand, direction, value, 1.0);
				};
			}
			case CHANGE_TO_10: {
				return (player, gui, slot, clickType) -> {
					ActionDirection direction = directions.getOrDefault(player.getUniqueId(), ActionDirection.X);
					ActionValue value = values.getOrDefault(player.getUniqueId(), ActionValue.PLUS);
					changes.put(player.getUniqueId(), 10.0);
					((Editor) gui).updateContents(armorStand, direction, value, 10.0);
				};
			}
		}
		return (player, gui, slot, clickType) -> {};
	}
	
}
