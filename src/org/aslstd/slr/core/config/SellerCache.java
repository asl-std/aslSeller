package org.aslstd.slr.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.aslstd.api.bukkit.yaml.YAML;
import org.aslstd.slr.core.SLR;
import org.aslstd.slr.core.config.items.internal.SimpleSell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import lombok.Getter;

public class SellerCache {

	@Getter private static List<SimpleSell> sellList = new ArrayList<>();

	public static void init() {
		if (!sellList.isEmpty()) sellList.clear();

		final File file = new File("plugins/aslSellerCore/materials-seller.json");
		if (!file.exists())
			YAML.exportFile(file.getName(), SLR.instance(), SLR.instance().getDataFolder());
		final Gson gson = new GsonBuilder().create();

		try {
			sellList = gson.fromJson(new JsonReader(new FileReader(file)), new TypeToken<List<SimpleSell>>() {}.getType());
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}

		//sellList.forEach(s -> EText.debug(s.toString()));

	}

}
