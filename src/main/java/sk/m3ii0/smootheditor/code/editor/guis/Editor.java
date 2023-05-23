package sk.m3ii0.smootheditor.code.editor.guis;

import org.bukkit.entity.Player;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.editor.GUI;
import sk.m3ii0.smootheditor.code.readers.ItemReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Editor extends GUI {
	
	/*
	*
	* Values
	*
	* */
	
	private final static List<String> layout;
	
	/*
	*
	* Static constructor
	*
	* */
	
	static {
		layout = SmoothEditor.getOptions().getStringList("MenuLayout");
	}
	
	/*
	*
	* Constructor
	*
	* */
	
	public Editor(Player player) {
		
		super("tes", "Editor", 6);
		
		Map<Character, MenuItem> items = new HashMap<>();
		char[] ids = ItemReader.getItems();
		
		for (char var : ids) {
			items.put(var, GUI.item(ItemReader.read(var)));
		}
		
		int counter = 0;
		for (String line : layout) {
			for (char var : line.toCharArray()) {
				if (items.containsKey(var)) {
					setItem(counter, items.get(var));
				}
				++counter;
			}
		}
		
		open(player);
	}
	
}
