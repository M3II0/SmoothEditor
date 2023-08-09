package sk.m3ii0.smootheditor.code.listeners;

import me.jet315.minions.MinionAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import sk.m3ii0.smootheditor.code.SmoothEditor;
import sk.m3ii0.smootheditor.code.editor.guis.Editor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionListener implements Listener {
	
	/*
	*
	* Values
	*
	* */
	
	private static Map<UUID, Entity> selection = new HashMap<>();
	
	/*
	*
	* Listeners
	*
	* */
	
	@EventHandler
	public void playerInteract(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() == null) return;
		UUID uuid = e.getPlayer().getUniqueId();
		Entity entity = e.getRightClicked();
		boolean isAs = e.getRightClicked().getType() == EntityType.ARMOR_STAND;
		boolean hasAs = selection.containsKey(uuid);
		boolean isDown = e.getPlayer().isSneaking();
		if (isAs && isDown) {
			if (MinionAPI.isMinion(entity)) return;
			if (hasAs) {
				int id = 0;
				Entity old = selection.get(uuid);
				if (old != null) {
					id = old.getEntityId();
				}
				if (id == entity.getEntityId()) {
					e.getPlayer().sendMessage(SmoothEditor.getOptions().getString("EntitySelection.AlreadySelected").replace("{id}", id + "").replace("&", "§"));
					Editor.crateOrGet(e.getPlayer());
					return;
				}
				selection.remove(uuid);
				e.getPlayer().sendMessage(SmoothEditor.getOptions().getString("EntitySelection.Removed").replace("{id}", id + "").replace("&", "§"));
			}
			selection.put(uuid, entity);
			e.getPlayer().sendMessage(SmoothEditor.getOptions().getString("EntitySelection.Selected").replace("{id}", entity.getEntityId() + "").replace("&", "§"));
			Editor.removeAndCreate(e.getPlayer()).open(e.getPlayer());
		}
	}
	
	@EventHandler
	public void destroyArmorstand(EntityDeathEvent e) {
		boolean isAs = e.getEntityType() == EntityType.ARMOR_STAND;
		Entity entity = e.getEntity();
		if (selection.containsValue(entity)) {
			for (Map.Entry<UUID, Entity> entry : selection.entrySet()) {
				if (entry.getValue().equals(entity)) {
					selection.remove(entry.getKey());
					Player player = Bukkit.getPlayer(entry.getKey());
					if (player == null) {
						return;
					}
					player.sendMessage(SmoothEditor.getOptions().getString("EntitySelection.Destroyed").replace("{id}", entity.getEntityId() + "").replace("&", "§"));
					Editor.remove(entry.getKey());
				}
			}
		}
	}
	
	/*
	*
	* API
	*
	* */
	
	public static boolean hasSelectedEntity(Player player) {
		return selection.containsKey(player.getUniqueId());
	}
	
	public static Entity getSelectedEntity(Player player) {
		return selection.get(player.getUniqueId());
	}
	
	public static void removeSelectedEntity(Player player) {
		selection.remove(player.getUniqueId());
	}
	
}
