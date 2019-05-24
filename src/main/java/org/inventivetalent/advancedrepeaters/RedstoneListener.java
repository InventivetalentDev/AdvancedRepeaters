package org.inventivetalent.advancedrepeaters;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RedstoneListener implements Listener {

	public String RUNNING_PLACEHOLDER = "§8. . .";

	@EventHandler
	public void onBlockRedstone(final BlockRedstoneEvent event) {
		if (event.getBlock().getType() == Material.DIODE_BLOCK_OFF || event.getBlock().getType() == Material.DIODE_BLOCK_ON) {
			final boolean on = (event.getBlock().getType() == Material.DIODE_BLOCK_ON);
			SignData data = collectSignData(event.getBlock());
			final byte prevData = event.getBlock().getData();
			long parsedTicks = data.value;
			final Sign sign = data.block;
			if (sign == null) { return; }
			if (parsedTicks >= 0) {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						if (on && (event.getBlock().getType() == Material.DIODE_BLOCK_ON)) {
							event.getBlock().setTypeIdAndData(Material.DIODE_BLOCK_OFF.getId(), prevData, true);
						} else if (!on && (event.getBlock().getType() == Material.DIODE_BLOCK_OFF && event.getBlock().isBlockPowered())) {
							event.getBlock().setTypeIdAndData(Material.DIODE_BLOCK_ON.getId(), prevData, true);
						}
						sign.setLine(3, null);
						sign.update();
					}
				};

				sign.setLine(3, RUNNING_PLACEHOLDER);
				sign.update();

				if (parsedTicks > 0) {
					event.setNewCurrent(on ? 15 : 0);
					runnable.runTaskLater(AdvancedRepeaters.instance, parsedTicks);
				} else {
					runnable.run();
				}
			}
		}
	}

	private SignData collectSignData(Block origin) {
		SignData data = new SignData();
		BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP };
		for (BlockFace face : faces) {
			Block relative = origin.getRelative(face);
			if (relative.getType() == Material.SIGN_POST || relative.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) relative.getState();
				data.block = sign;
				String[] lines = sign.getLines();
				if (!AdvancedRepeaters.SIGN_TITLE_FORMAT.equalsIgnoreCase(lines[0])) { continue; }

				for (String s : lines) {
					if (s == null || s.isEmpty()) { continue; }
					try {
						data.value = TickType.parseTicks(s);
						return data;
					} catch (Exception e) {
					}
				}
			}
		}
		return data;
	}

	static class SignData {
		long value = -1;
		Sign block;
	}

}
