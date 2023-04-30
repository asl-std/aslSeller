package org.aslstd.slr.core;

import org.aslstd.api.ejcore.plugin.EJPlugin;
import org.aslstd.slr.api.mode.Seller;
import org.aslstd.slr.core.command.SLRHandler;
import org.aslstd.slr.core.config.GConfig;
import org.aslstd.slr.core.config.SellerCache;
import org.aslstd.slr.core.task.SellerUpdateTask;
import org.bukkit.plugin.RegisteredServiceProvider;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

public class SLR extends EJPlugin {

	private static SLR instance;
	public static SLR instance() { return instance; }

	@Getter private static Economy eco;

	@Getter private static GConfig mainConfig;

	@Override
	public void init() {
		instance = this;
		mainConfig = new GConfig();

		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		eco = rsp.getProvider();

		if (eco == null || !eco.isEnabled())
			throw new RuntimeException("Vault or any other economy providers was not installed");

		SellerCache.init();
		Seller.init();

		new SLRHandler().registerHandler();
		SellerUpdateTask.start();
	}

	@Override
	public void disabling() {
		SellerUpdateTask.stop();
	}


}
