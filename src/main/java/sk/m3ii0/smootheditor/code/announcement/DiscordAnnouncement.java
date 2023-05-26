package sk.m3ii0.smootheditor.code.announcement;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class DiscordAnnouncement {
	
	private static BukkitTask task;
	private static BaseComponent[] message;
	
	public static void runWithMessage(Plugin plugin, String spigot, String discord, String pluginName) {
		int size = 0;
		BaseComponent[] first = new ComponentBuilder("\n§c" + pluginName + " §f-> §7You're using testing version of this plugin!\n\n").create();
		size += first.length;
		BaseComponent[] second = new ComponentBuilder(" §f> §6Click to open SpigotMC page\n")
		 .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§6Click to open SpigotMC page")))
		 .event(new ClickEvent(ClickEvent.Action.OPEN_URL, spigot))
		 .create();
		size += second.length;
		BaseComponent[] third = new ComponentBuilder(" §f> §bClick to remove this message\n")
		 .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§bClick to remove")))
		 .event(new ClickEvent(ClickEvent.Action.OPEN_URL, discord))
		 .create();
		size += third.length;
		BaseComponent[] fourth = new ComponentBuilder("\n§7(Removing message is free, condition is to join our Discord Server and download .jar from there)\n").create();
		size += first.length;
		message = new BaseComponent[size];
		int counter = 0;
		for (BaseComponent var : first) {
			message[counter] = var;
			++counter;
		}
		for (BaseComponent var : second) {
			message[counter] = var;
			++counter;
		}
		for (BaseComponent var : third) {
			message[counter] = var;
			++counter;
		}
		for (BaseComponent var : fourth) {
			message[counter] = var;
			++counter;
		}
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			Bukkit.spigot().broadcast(message);
		}, 0, 24000);
	}
	
	public static void close() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}
	
}
