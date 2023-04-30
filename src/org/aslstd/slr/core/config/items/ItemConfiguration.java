package org.aslstd.slr.core.config.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemConfiguration {

	double getCostMin();

	double getCostMax();

	double getDefCost();

	double getCost();

	void setCost(double cost);

	Material getType();

	String getData();

	ItemConfiguration build();

	ItemStack getIcon();

	int getSelledAmount();

	void setSelledAmount(int amount);

}
