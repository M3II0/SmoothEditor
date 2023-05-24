package sk.m3ii0.smootheditor.code.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sk.m3ii0.smootheditor.code.editor.guis.Editor;

public class ASEditorCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player p = (Player) commandSender;
			Editor.crateOrGet(p);
		}
		return true;
	}
	
}
