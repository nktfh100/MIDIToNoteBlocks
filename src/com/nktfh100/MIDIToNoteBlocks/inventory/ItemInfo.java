package com.nktfh100.MIDIToNoteBlocks.inventory;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.nktfh100.MIDIToNoteBlocks.utils.Utils;

public class ItemInfo {

	private Integer slot;
	private Material mat;
	private String title = "";
	private ArrayList<String> lore = new ArrayList<String>();

	public ItemInfo(Integer slot, Material mat, String title, ArrayList<String> lore) {
		this.slot = slot;
		this.mat = mat;
		this.title = title;
		this.lore = lore;
	}

	public ItemStack getItem(String... values) {
		ItemStack line = Utils.createItem(this.getMat(), this.getTitle(values), 1, this.getLore(values));
		Utils.addItemFlag(line, ItemFlag.HIDE_ATTRIBUTES);
		return line;
	}

	private String replaceValues(String line, String... values) {
		if (line == null || line.isEmpty()) {
			return "";
		}
		int i = 0;
		for (String val : values) {
			if (i == 0) {
				line = line.replace("%value%", val);
			} else {
				line = line.replace("%value" + i + "%", val);
			}
			i++;
		}
		return line;
	}

	// ------- title -------

	public String getTitle(String... values) {
		if (this.title == null || this.title.isEmpty()) {
			return "";
		}
		return replaceValues(this.title, values);
	}

	// ------- lore -------

	public ArrayList<String> getLore(String... values) {
		if (this.lore == null || this.lore.size() == 0) {
			return new ArrayList<String>();
		}
		ArrayList<String> newLore = new ArrayList<String>();
		for (String line : this.lore) {
			newLore.add(this.replaceValues(line, values));
		}
		return newLore;
	}

	public Integer getSlot() {
		return slot;
	}

	public Material getMat() {
		return mat;
	}

	public void setMat(Material to) {
		this.mat = to;
	}
}
