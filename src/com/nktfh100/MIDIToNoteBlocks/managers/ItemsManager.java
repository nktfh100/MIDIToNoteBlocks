package com.nktfh100.MIDIToNoteBlocks.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nktfh100.MIDIToNoteBlocks.inventory.ItemInfo;
import com.nktfh100.MIDIToNoteBlocks.inventory.ItemInfoContainer;
import com.nktfh100.MIDIToNoteBlocks.main.MIDIToNoteBlocks;

public class ItemsManager {

	private MIDIToNoteBlocks plugin;
	
	private final static ArrayList<String> nums = new ArrayList<String>(Arrays.asList("", "2", "3"));
	private HashMap<String, ItemInfoContainer> items = new HashMap<String, ItemInfoContainer>();

	public ItemsManager(MIDIToNoteBlocks instance) {
		this.plugin = instance;
	}

	public void loadItems() {
		File itemsConfigFIle = new File(plugin.getDataFolder(), "items.yml");
		if (!itemsConfigFIle.exists()) {
			try {
				plugin.saveResource("items.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration itemsConfig = YamlConfiguration.loadConfiguration(itemsConfigFIle);
		try {
			this.items = new HashMap<String, ItemInfoContainer>();

			ConfigurationSection itemsSC = itemsConfig.getConfigurationSection("items");
			Set<String> itemsKeys = itemsSC.getKeys(false);
			for (String key : itemsKeys) {
				try {
					ConfigurationSection itemSC = itemsSC.getConfigurationSection(key);

					ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();

					for (String num : nums) {
						Integer slot = itemSC.getInt("slot", 0);
						String matStr = itemSC.getString("mat" + num, "BARRIER");
						Material mat_ = Material.getMaterial(matStr);

						String title = ChatColor.translateAlternateColorCodes('&', itemSC.getString("title" + num, " "));
						ArrayList<String> lore = new ArrayList<String>(itemSC.getStringList("lore" + num));
						for (int ii = 0; ii < lore.size(); ii++) {
							lore.set(ii, ChatColor.translateAlternateColorCodes('&', lore.get(ii)));
						}
						ItemInfo itemInfo = new ItemInfo(slot, mat_, title, lore);
						items.add(itemInfo);
					}

					this.items.put(key, new ItemInfoContainer(items.get(0), items.get(1), items.get(2)));
				} catch (Exception e) {
					e.printStackTrace();
					plugin.getLogger().warning("Something is wrong with your items.yml file! (" + key + ")");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().warning("Something is wrong with your items.yml file!");
		}
	}

	public ItemInfoContainer getItem(String key) {
		ItemInfoContainer out = this.items.get(key);
		if (out == null) {
			plugin.getLogger().warning("Item '" + key + "' is missing from your items.yml file!");
			ItemInfo itemInfo = new ItemInfo(0, Material.BARRIER, "ITEM MISSING", new ArrayList<String>());
			return new ItemInfoContainer(itemInfo, itemInfo, itemInfo);
		}
		return out;
	}
}
