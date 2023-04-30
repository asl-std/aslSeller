package org.aslstd.slr.core.config.items.internal;

import javax.annotation.Nullable;

import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.utils.BasicMetaAdapter;
import org.aslstd.api.bukkit.value.util.MathUtil;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.slr.core.config.items.ItemConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SimpleSell implements ItemConfiguration {

	@SerializedName(value = "cost-min")
	@Getter private double costMin;
	@SerializedName(value = "cost-max")
	@Getter private double costMax;

	@Expose(serialize = false, deserialize = false)
	@Getter @Setter private double cost;
	@Expose(serialize = false, deserialize = false)
	@Getter private double defCost;

	@Getter private String material;
	@Nullable private Material type;
	@SerializedName(value = "display-name")
	@Getter private String data;

	@Getter @Setter private int selledAmount = 0;

	private ItemStack icon;

	@Override
	public Material getType() {
		return type == null ? (type = Material.matchMaterial(material)) : type;
	}

	@Override
	public ItemStack getIcon() {
		return icon == null ? BasicMetaAdapter.setDisplayName(new ItemStack(getType(), 1), data) : icon;
	}

	public SimpleSell(Material type, String data, double cost) {
		this.type = type;
		this.data = data;
		defCost = cost;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "SimpleSell{material:" + getType().name()
				+ " cost-min:" + costMin
				+ " cost-max:" + costMax
				+ " display-name:" + (data == null ? "none" : data) + "}";
	}

	@Override
	public ItemConfiguration build() {
		return new SimpleSell(getType(), data, NumUtil.parseDouble(EText.format(MathUtil.getRandomRange(costMin, costMax))));
	}

}
