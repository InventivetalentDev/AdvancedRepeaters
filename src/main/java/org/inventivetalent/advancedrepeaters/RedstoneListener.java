package org.inventivetalent.advancedrepeaters;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RedstoneListener implements Listener {

	public String RUNNING_PLACEHOLDER = "§8. . .";

	@EventHandler
	public void onBlockRedstone(final BlockRedstoneEvent event) {
		if (event.getBlock().getType() == Material.REPEATER) {
			final boolean on = ((Repeater) event.getBlock().getBlockData()).isPowered();
			SignData data = collectSignData(event.getBlock());
			long parsedTicksActive = data.valueActive;
			long parsedTicksInactive = data.valueInactive;
			final Sign sign = data.block;
			if (sign == null) { return; }
			if (parsedTicksActive >= 0) {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {

						Repeater repeater = (Repeater) event.getBlock().getBlockData();
						repeater.setPowered(!on);
						event.getBlock().setBlockData(repeater, true);

						sign.setLine(3, "");
						sign.update();
					}
				};

				sign.setLine(3, RUNNING_PLACEHOLDER);
				sign.update();

				if (parsedTicksActive > 0) {
					event.setNewCurrent(on ? 15 : 0);
					runnable.runTaskLater(AdvancedRepeaters.instance, !on ? parsedTicksActive : parsedTicksInactive);
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
			if (relative.getState() instanceof Sign) {
				Sign sign = (Sign) relative.getState();
				data.block = sign;
				String[] lines = sign.getLines();
				if (!AdvancedRepeaters.SIGN_TITLE_FORMAT.equalsIgnoreCase(lines[0])) { continue; }

				for (int i = 1; i < 3; i++ ) {
					String line = lines[i];

					if (line.length() > 0) {
						if (data.valueActive == -1) {
							data.valueActive = TickType.parseTicks(line);
						} else if (data.valueInactive == -1) {
							data.valueInactive = TickType.parseTicks(line);
						}
					}
				}
			}
		}
		return data;
	}

	static class SignData {
		long valueActive = -1;
		long valueInactive = -1;
		Sign block;
	}

}
