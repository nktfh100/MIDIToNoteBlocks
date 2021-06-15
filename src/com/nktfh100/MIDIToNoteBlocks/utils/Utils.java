package com.nktfh100.MIDIToNoteBlocks.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nktfh100.MIDIToNoteBlocks.enums.NoteType;
import com.nktfh100.MIDIToNoteBlocks.info.NoteInfo;

public class Utils {

	private static final int firstBassPlayableNote = 10;
	private static final int lastBassPlayableNote = 33; // 34
	private static final int firstPlayableNote = 34;
	private static final int lastPlayableNote = 58;
	private static final int firstBellPlayableNote = 59; // 58
	private static final int lastBellPlayableNote = 82;

	public static void placeNoteBlock(Location loc, NoteInfo noteInfo) {
		loc.add(0, -1, 0);
		loc.getBlock().setType(noteInfo.getType().getMaterial());

		loc.add(0, 1, 0);

		Block noteBlock = loc.getBlock();
		noteBlock.setType(Material.NOTE_BLOCK);
		NoteBlock blockData = (NoteBlock) noteBlock.getBlockData();
		blockData.setNote(new Note(noteInfo.getActualNote()));
		blockData.setInstrument(noteInfo.getType().getInstrument());

		noteBlock.setBlockData(blockData);

		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.AIR);
		loc.add(0, -1, 0);
	}

	public static void placeRepeater(Location loc, int delay, BlockFace direction) {
		loc.add(0, -1, 0);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.REPEATER);
		Repeater repeaterData = (Repeater) loc.getBlock().getBlockData();
		repeaterData.setFacing(direction == BlockFace.WEST ? BlockFace.EAST : BlockFace.WEST);
		repeaterData.setDelay(delay);
		loc.getBlock().setBlockData(repeaterData);
	}

	public static void placeCorner(Location loc, BlockFace direction) {
		loc.add(direction == BlockFace.WEST ? -1 : 1, 0, 0);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 0, 1);

		loc.add(0, -1, 0);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.REDSTONE_WIRE);

		loc.add(0, -1, 1);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.REDSTONE_WIRE);

		loc.add(0, -1, 1);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.REDSTONE_WIRE);

		loc.add(0, -1, 0);
		loc.add(direction == BlockFace.WEST ? 1 : -1, 0, 0);
		loc.getBlock().setType(Material.GRASS_BLOCK);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.REDSTONE_WIRE);
	}

	public static BlockFace switchFace(BlockFace face) {
		return face == BlockFace.WEST ? BlockFace.EAST : BlockFace.WEST;
	}

	public static boolean shouldSwitchFace(int minX, int maxX, Location loc, BlockFace face) {
		if (face == BlockFace.WEST) {
			if (loc.getBlockX() < minX) {
				return true;
			}
		} else {
			if (loc.getBlockX() > maxX) {
				return true;
			}
		}
		return false;
	}

	public static NoteInfo getNoteFromKey(int key) {
		int actualNote = -1;
		NoteType noteType = null;
		if (key <= lastBassPlayableNote && key >= firstBassPlayableNote) {
			actualNote = key - lastBassPlayableNote + 24;
			noteType = NoteType.BASS;
		} else if (key <= lastPlayableNote && key >= firstPlayableNote) {
			actualNote = key - lastPlayableNote + 24;
			noteType = NoteType.PIANO;
		} else if (key <= lastBellPlayableNote && key >= firstBellPlayableNote) {
			actualNote = key - lastBellPlayableNote + 24;
			noteType = NoteType.BELL;
		}
		return new NoteInfo(noteType, actualNote);
	}

//	public static int getTempoInBPM(MetaMessage mm) {
//		byte[] data = mm.getData();
//		if (mm.getType() != 81 || data.length != 3) {
//			throw new IllegalArgumentException("mm=" + mm);
//		}
//		int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
//		int tempo = Math.round(60000001f / mspq);
//		return tempo;
//	}

	public static ItemStack setItemName(ItemStack item, String name, ArrayList<String> lore) {
		if (item != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			ArrayList<String> metaLore = new ArrayList<String>();

			for (String lore_ : lore) {
				metaLore.add(lore_);
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack setItemLore(ItemStack item, ArrayList<String> lore) {
		if (item != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> metaLore = new ArrayList<String>();
			for (String lore_ : lore) {
				metaLore.add(ChatColor.translateAlternateColorCodes('&', lore_));
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack enchantedItem(ItemStack item, Enchantment ench, int lvl) {
		if (item != null && item.getType() != Material.AIR && item.getItemMeta() != null) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(ench, lvl, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createItem(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
		}
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount) {
		ItemStack item = createItem(mat, name);
		item.setAmount(amount);
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount, String... lore) {
		ItemStack item = createItem(mat, name, amount);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();

			ArrayList<String> metaLore = new ArrayList<String>();

			for (String lorecomments : lore) {
				metaLore.add(lorecomments);
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return (item);
	}

	public static ItemStack createItem(Material mat, String name, int amount, ArrayList<String> lore) {
		ItemStack item = createItem(mat, name, amount);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();

			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return (item);
	}

	public static ItemStack createEnchantedItem(Material mat, String name, Enchantment ench, int lvl, String... lore) {
		ItemStack item = createItem(mat, name, 1);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(ench, lvl, true);
			ArrayList<String> metaLore = new ArrayList<String>();

			for (String lorecomments : lore) {
				metaLore.add(lorecomments);
			}
			meta.setLore(metaLore);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		return (item);
	}

	public static ItemStack addItemFlag(ItemStack item, ItemFlag... flag) {
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(flag);
			item.setItemMeta(meta);
		}
		return item;
	}
}
