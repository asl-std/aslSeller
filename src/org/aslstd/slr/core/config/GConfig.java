package org.aslstd.slr.core.config;

import org.aslstd.api.bukkit.message.EText;
import org.aslstd.api.bukkit.value.util.NumUtil;
import org.aslstd.api.bukkit.yaml.EJConf;
import org.aslstd.api.expression.Expression;
import org.aslstd.api.expression.exceptions.ParsingException;
import org.aslstd.api.expression.parser.ExpressionParser;
import org.aslstd.slr.core.SLR;

public class GConfig extends EJConf {

	public static String NOT_ENOUGH_ITEMS, MONEY_RECEIVED, SELLER_UPDATED;
	public static long updatePeriod;
	public static boolean priceDeacrease;

	private String formula;

	public GConfig() {
		super(SLR.instance().getDataFolder() + "/config.yml", SLR.instance());
	}

	@Override
	public void loadConfig() {
		NOT_ENOUGH_ITEMS = getString("messages.not-enough.items", "&4У вас недостаточно предметов для продажи.", true);
		MONEY_RECEIVED = getString("messages.money-received", "&4Вы продали %amount товара на сумму %money !", true);
		SELLER_UPDATED = getString("messages.seller-updated", "&aТовары скупщика были обновлены !", true);
		updatePeriod = getLong("seller.update-period", 14400, true);
		priceDeacrease = getBoolean("price-settings.price-decrease", true, true);
		formula = getString("price-settings.price-formula", "(price*(1-(0.002*items))/(1+(0.002*items)))", true);
		getSellCost(20, 10);
	}

	public double getSellCost(double cost, int selledItems) {
		double result = cost;
		try {
			final Expression expr = new ExpressionParser().parse(formula.replaceAll("price", String.valueOf(cost)).replaceAll("items", String.valueOf(selledItems)));
			result = expr.evaluate();
		} catch (final ParsingException e) { EText.warn("Something went wrong while testing price-formula"); }

		return NumUtil.parseDouble(EText.format(result));
	}
}
