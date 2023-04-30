package org.aslstd.slr.api.menu;

import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.inventory.pane.SimplePane;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class SellGUI extends SimplePane {

	@Getter @Setter private double sellCost;
	@Setter private MenuMode mode;

	public SellGUI(Player p, MenuMode mode) {
		super(EText.c(mode.getFile().getString("menu-title")), mode.getHeight()*9, null);

		mode.constructBackground(this, p);
		mode.acceptToMenu(this);

		showTo(p);
	}

}
