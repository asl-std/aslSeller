package org.aslstd.slr.api.mode;

import org.aslstd.api.bukkit.yaml.YAML;
import org.aslstd.api.inventory.element.SimpleElement;
import org.aslstd.slr.api.menu.MenuMode;
import org.aslstd.slr.api.menu.SellGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Sellall extends MenuMode {

	public Sellall(String key, YAML file) {
		super(key, file);
	}

	@Override
	public void acceptToMenu(SellGUI menu) {
	}

	@Override
	public void sellItems(InventoryClickEvent e, SimpleElement element) {
	}

}
