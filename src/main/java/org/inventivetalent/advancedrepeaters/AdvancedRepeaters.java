package org.inventivetalent.advancedrepeaters;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedRepeaters extends JavaPlugin {

	public static AdvancedRepeaters instance;

	public static final String SIGN_TITLE        = "[AR]";
	public static final String SIGN_TITLE_FORMAT = "§7[§6A§cR§7]";

	@Override
	public void onEnable() {
		instance = this;

		Bukkit.getPluginManager().registerEvents(new RedstoneListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);

		new Metrics(this);
	}
}
