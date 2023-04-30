package org.aslstd.slr.core.task;

import org.aslstd.api.bukkit.message.EText;
import org.aslstd.slr.api.menu.MenuMode;
import org.aslstd.slr.api.mode.Seller;
import org.aslstd.slr.core.SLR;
import org.aslstd.slr.core.config.GConfig;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SellerUpdateTask extends BukkitRunnable {

	private static SellerUpdateTask task;

	public static void start() {
		if (task != null) return;
		task = new SellerUpdateTask();
		task.runTaskTimer(SLR.instance(), GConfig.updatePeriod*20, GConfig.updatePeriod*20);
	}

	public static void stop() {
		if (task == null) return;
		Bukkit.getScheduler().cancelTask(task.getTaskId());
		task = null;
	}

	private SellerUpdateTask() {}

	@Override
	public void run() {
		((Seller)MenuMode.getRegisteredModes().get("seller")).update();
		Bukkit.getOnlinePlayers().forEach( p -> EText.send(p, GConfig.SELLER_UPDATED));
	}

}
