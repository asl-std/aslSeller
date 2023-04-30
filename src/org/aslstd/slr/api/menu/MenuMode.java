package org.aslstd.slr.api.menu;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.aslstd.api.bukkit.items.ItemStackUtil;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.bukkit.yaml.YAML;
import org.aslstd.api.inventory.element.SimpleElement;
import org.aslstd.api.inventory.page.LockedPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lombok.Getter;

public abstract class MenuMode {

	@Getter private static ConcurrentMap<String,MenuMode> registeredModes = new ConcurrentHashMap<>();

	protected String key;
	@Getter private YAML file;

	protected boolean valid = true;
	@Getter protected int height;
	protected String title;

	public MenuMode(String key, YAML file) {
		this.key = key;
		this.file = file;

		/*if (valid)
		else
			EText.warn("SellerMode " + key + " cannot be registered due to issues happens, check messages before to understand what the trouble.");
		 */

		registeredModes.put(key, this);
	}

	public abstract void acceptToMenu(SellGUI menu);

	public abstract void sellItems(InventoryClickEvent e, SimpleElement element);

	public void constructBackground(SellGUI gui, Player p) {
		gui.setPage(new LockedPage(height, EText.c(getFile().getString("menu-title"))));

		final Integer[] slots = new Integer[height*9];
		final SimpleElement[] bgItems = new SimpleElement[height*9];
		boolean fillChecked = false;

		for (final String key : file.getStringList("background-items")) {
			final String[] split = { key.substring(0, key.lastIndexOf("=")), key.substring(key.lastIndexOf('=')+1) };
			if (split[0] == null) continue;

			if (!fillChecked && split[1] != null && split[1].equalsIgnoreCase("fill")) {
				gui.getPage().fill(new SimpleElement(ItemStackUtil.toStack(split[0]), true));
				break;
			} else
				fillChecked = true;

			if (split[1].contains(",")) {
				final String[] vals = split[1].split(",");
				for (final String i : vals) {
					final int val = NumUtil.parseInteger(i);
					slots[val] = val;
					bgItems[val] = new SimpleElement(ItemStackUtil.toStack(split[0]), true);
				}
			} else {
				final int val = NumUtil.parseInteger(split[1]);
				slots[val] = val;
				bgItems[val] = new SimpleElement(ItemStackUtil.toStack(split[0]), true);
			}
		}

		Stream.of(slots).filter(i -> Objects.nonNull(i) && bgItems[i] != null).forEach(i -> gui.getPage().add(bgItems[i], i%9, i/9, false));

	}


}
