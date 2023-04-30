package org.aslstd.slr.api.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aslstd.api.bukkit.items.InventoryUtil;
import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.utils.BasicMetaAdapter;
import org.aslstd.api.bukkit.value.util.ArrayUtil;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.bukkit.yaml.YAML;
import org.aslstd.api.inventory.element.SimpleElement;
import org.aslstd.slr.api.menu.MenuMode;
import org.aslstd.slr.api.menu.SellGUI;
import org.aslstd.slr.core.SLR;
import org.aslstd.slr.core.config.GConfig;
import org.aslstd.slr.core.config.SellerCache;
import org.aslstd.slr.core.config.items.ItemConfiguration;
import org.aslstd.slr.core.config.items.internal.SimpleSell;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;

public class Seller extends MenuMode {

	@Getter private static Seller currentSeller;

	private static YAML cfg;
	private static List<Integer> emptySlots = new LinkedList<>();
	private static List<String> lore;
	private static String costLine;

	public static void init() {
		if (!emptySlots.isEmpty()) emptySlots.clear();
		cfg = YAML.of("menu-seller.yml", SLR.instance());
		final List<String> slots = cfg.getStringList("item-slots");

		for (final String line : slots) {
			final String[] split = line.replaceAll("\\s+", "").split(",");
			for (final String num : split)
				if (NumUtil.isNumber(num))
					emptySlots.add(NumUtil.parseInteger(num));
		}

		lore = cfg.getStringList("item-lore");
		costLine = cfg.getString("cost-line", "&3 Цена: &6%cost% &7/ 1 ед.", true);

		currentSeller = new Seller("seller");
	}

	private List<ItemConfiguration> sellItems = new ArrayList<>();
	private Map<Integer,ItemConfiguration> items = new HashMap<>();

	public Seller(String key) {
		super(key, cfg);
		height = cfg.getInt("height", 4, true);

		if (!sellItems.isEmpty()) sellItems.clear();
		final List<SimpleSell> prepaired = ArrayUtil.getRandomized(emptySlots.size(), null , SellerCache.getSellList());

		prepaired.forEach(s -> sellItems.add(s.build()));

		valid = true;
	}

	public void update() {
		if (!sellItems.isEmpty()) sellItems.clear();
		if (!items.isEmpty()) items.clear();
		final List<SimpleSell> prepaired = ArrayUtil.getRandomized(emptySlots.size(), null , SellerCache.getSellList());

		prepaired.forEach(s -> sellItems.add(s.build()));
	}

	@Override
	public void acceptToMenu(SellGUI menu) {
		final List<ItemConfiguration> copy = new LinkedList<>(sellItems);
		for (final int slot : emptySlots) {
			if (copy.isEmpty()) break;
			final ItemConfiguration item = copy.remove(0);

			ItemStack stack = BasicMetaAdapter.setLore(item.getIcon(), lore);
			stack = BasicMetaAdapter.addLore(stack, costLine.replaceAll("%cost%", String.valueOf(item.getCost())));

			final SimpleElement element = new SimpleElement(stack, true);
			element.setFunction(e -> {
				sellItems(e, element);
				menu.getPage().update(e.getClickedInventory(), slot%9, slot/9);
			});
			menu.getPage().add(element, slot%9, slot/9, true);
			items.put(slot, item);
		}

	}

	@Override
	public void sellItems(InventoryClickEvent e, SimpleElement element) {

		if (!items.containsKey(e.getSlot())) return;

		int amount = 0;

		if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT)
			amount = sellItem(1, (Player) e.getWhoClicked(), element.getIcon().getType(), element.getIcon().getItemMeta().hasDisplayName() ? element.getIcon().getItemMeta().getDisplayName() : null);
		if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT)
			amount = sellItem(64, (Player) e.getWhoClicked(), element.getIcon().getType(), element.getIcon().getItemMeta().hasDisplayName() ? element.getIcon().getItemMeta().getDisplayName() : null);
		if (e.getClick() == ClickType.MIDDLE)
			amount = sellItem(-1, (Player) e.getWhoClicked(), element.getIcon().getType(), element.getIcon().getItemMeta().hasDisplayName() ? element.getIcon().getItemMeta().getDisplayName() : null);

		if (amount <= 0) return;
		final ItemConfiguration it = items.get(e.getSlot());

		final ItemStack icon = element.getIcon();
		final ItemMeta meta = icon.getItemMeta();
		final List<String> lore = meta.getLore();

		it.setSelledAmount(it.getSelledAmount() + amount);
		double moneyReceived = 0;

		if (amount > 64) {
			final int payments = amount/64;
			final int last = amount%64;
			for (int i = 0 ; i < payments ; i++) {
				moneyReceived += (64 * it.getCost());
				it.setSelledAmount(it.getSelledAmount()+64);
				it.setCost(SLR.getMainConfig().getSellCost(it.getDefCost(), it.getSelledAmount()));
			}
			if (last > 0) {
				moneyReceived += (last * it.getCost());
				it.setSelledAmount(it.getSelledAmount()+last);
				it.setCost(SLR.getMainConfig().getSellCost(it.getDefCost(), it.getSelledAmount()));

			}
		} else {
			moneyReceived += (amount * it.getCost());
			it.setSelledAmount(it.getSelledAmount()+amount);
			it.setCost(SLR.getMainConfig().getSellCost(it.getDefCost(), it.getSelledAmount()));
		}

		SLR.getEco().depositPlayer((OfflinePlayer) e.getWhoClicked(), moneyReceived);
		EText.send(e.getWhoClicked(), GConfig.MONEY_RECEIVED.replace("%amount", String.valueOf(amount)).replace("%money", EText.format(moneyReceived)));

		lore.remove(lore.size()-1);
		meta.setLore(lore);
		icon.setItemMeta(meta);
		element.changeIcon(BasicMetaAdapter.addLore(icon, costLine.replaceAll("%cost%", String.valueOf(it.getCost()))));
	}

	public int sellItem(int amount, Player p, Material type, String displayName) {
		if (amount == -1) {
			amount = InventoryUtil.count(p, type);
			if (amount <= 0) return 0;
			InventoryUtil.decreaseItemChecksNameAmount(new ItemStack(type, 1), displayName, p, amount);
		} else {
			if (InventoryUtil.count(p, type) < amount) { EText.send(p, GConfig.NOT_ENOUGH_ITEMS); return 0; }
			InventoryUtil.decreaseItemChecksNameAmount(new ItemStack(type, 1), displayName, p, amount);
		}

		return amount;
	}

}
