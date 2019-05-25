package org.inventivetalent.advancedrepeaters;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignChange(final SignChangeEvent event) {
		if (event.isCancelled()) { return; }
		Bukkit.getScheduler().runTaskLater(AdvancedRepeaters.instance, new Runnable() {
			@Override
			public void run() {
				if (event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN) {
					Sign sign = (Sign) event.getBlock().getState();
					if (AdvancedRepeaters.SIGN_TITLE.equalsIgnoreCase(sign.getLine(0))) {
						if (!event.getPlayer().hasPermission("advancedrepeaters.create")) {
							event.setCancelled(true);
							event.getPlayer().sendMessage("§cYou don't have permission to create this sign.");
							event.getBlock().breakNaturally();
							return;
						}

						Block targetRepeater = null;
						BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN };
						for (BlockFace face : faces) {
							Block relative = event.getBlock().getRelative(face);
							if (relative.getType() == Material.REPEATER) {
								targetRepeater = relative;
							}
						}
						if (targetRepeater == null) {
							event.getPlayer().sendMessage("§cNo valid repeater found");
							return;
						}

						sign.setLine(0, AdvancedRepeaters.SIGN_TITLE_FORMAT);
						for (int i = 1; i < 4; i++) {
							String line = sign.getLine(i);
							if (line == null || line.isEmpty()) { continue; }
							try {
								long parsed = TickType.parseTicks(line);
								event.getPlayer().sendMessage("§aThis repeater now has a delay of " + line + " (" + parsed + " ticks)");
								break;
							} catch (TickType.InvalidTickTypeException tickException) {
								event.getPlayer().sendMessage("§cInvalid sign content: " + tickException.getMessage());
								event.getPlayer().sendMessage(new String[] { "§cAvailable types: ", TickType.makeTypeString() });
							} catch (NumberFormatException numberException) {
								event.getPlayer().sendMessage("§cInvalid number: " + numberException.getMessage());
							} catch (Exception e) {
								event.getPlayer().sendMessage("§cUnexpected exception while parsing sign. See console for details");
								e.printStackTrace();
							}
						}
						sign.update();
					}
				}
			}
		}, 1);
	}

}
