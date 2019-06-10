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
			final boolean inputIsOn = event.getBlock().getBlockPower() > 0;
			final boolean selfIsOn = ((Repeater) event.getBlock().getBlockData()).isPowered();
			final boolean eventIsOn = event.getOldCurrent() > 0;

			// helpful for debugging
			//System.out.println("Triggered and is " + (!(((Repeater)event.getBlock().getBlockData()).isPowered()) ? "NOT" : "") + " powered");
			//System.out.println("with current of "+event.getOldCurrent()+" > "+event.getNewCurrent());
			//System.out.println("and block power of "+event.getBlock().getBlockPower());

			SignData data = collectSignData(event.getBlock());
			long parsedTicksIn = data.valueIn;
			long parsedTicksOut = data.valueOut;
			final Sign sign = data.block;
			if (sign == null) { return; }
			if (parsedTicksIn >= 0) {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						Repeater repeater = (Repeater) event.getBlock().getBlockData();
						repeater.setPowered(inputIsOn && !eventIsOn);
						event.getBlock().setBlockData(repeater, true);

						sign.setLine(3, "");
						sign.update();
					}
				};

				sign.setLine(3, RUNNING_PLACEHOLDER);
				sign.update();

				if (parsedTicksIn > 0) {
					event.setNewCurrent(event.getOldCurrent());
					runnable.runTaskLater(AdvancedRepeaters.instance, parsedTicksIn);
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

				if (lines[1].length() > 0) { data.valueIn = TickType.parseTicks(lines[1]); }
				if (lines[2].length() > 0) { data.valueOut = TickType.parseTicks(lines[2]); }

				return data;
			}
		}
		return data;
	}

	static class SignData {
		long valueIn  = -1;
		long valueOut = -1;
		Sign block;
	}

}
