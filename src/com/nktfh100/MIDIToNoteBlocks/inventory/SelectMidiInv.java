package com.nktfh100.MIDIToNoteBlocks.inventory;

import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.nktfh100.MIDIToNoteBlocks.main.MIDIToNoteBlocks;
import com.nktfh100.MIDIToNoteBlocks.managers.ItemsManager;

public class SelectMidiInv extends CustomHolder {

	private static final Integer pageSize = 45;

	private Integer page = 1;

	public SelectMidiInv() {
		super(54, ChatColor.translateAlternateColorCodes('&', "&e&lSelect midi file"));

		this.update();
	}

	public void update() {
		MIDIToNoteBlocks plugin = MIDIToNoteBlocks.getInstance();
		ItemsManager itemsManager = plugin.getItemsManager();
		this.clearInv();
		
		ArrayList<File> midiFiles = new ArrayList<File>();

		File midisFolder = new File(plugin.getDataFolder(), "midi");
		File[] files = midisFolder.listFiles();
		for (File file : files) {
			try {
				if (file.isFile() && file.getName().endsWith(".mid")) {
					Sequence sequence = MidiSystem.getSequence(file);
					if (sequence.getDivisionType() != Sequence.PPQ) {
						continue;
					}
					midiFiles.add(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Integer totalItems = midiFiles.size();
		Integer totalPages = (int) Math.ceil((double) totalItems / (double) pageSize);
		
		this.setIcon(49, new Icon(itemsManager.getItem("select-midi_current-page").getItem().getItem(this.page + "", totalPages + "")));
		
		if (totalPages > 1) {
			final Integer currentPage_ = this.page;
			if (this.page > 1) {
				Icon icon = new Icon(itemsManager.getItem("select-midi_previous-page").getItem().getItem(this.page + "", totalPages + ""));
				icon.addClickAction(new ClickAction() {
					@Override
					public void execute(Player player, InventoryClickEvent ev) {
						setPage(currentPage_ - 1);
					}
				});
				this.setIcon(48, icon);
			}
			if (this.page < totalPages) {
				Icon icon = new Icon(itemsManager.getItem("select-midi_next-page").getItem().getItem(this.page + "", totalPages + ""));
				icon.addClickAction(new ClickAction() {
					@Override
					public void execute(Player player, InventoryClickEvent ev) {
						setPage(currentPage_ + 1);
					}
				});
				this.setIcon(50, icon);
			}
		}

		Integer startIndex = (this.page - 1) * pageSize;
		Integer endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);
		for (int i = startIndex; i <= endIndex; i++) {
			try {
				File file = midiFiles.get(i);
				Sequence sequence = MidiSystem.getSequence(file);
				Icon icon = new Icon(itemsManager.getItem("select-midi_midi-file").getItem().getItem(file.getName(), sequence.getTracks().length + ""));
				icon.addClickAction(new ClickAction() {
					@Override
					public void execute(Player player, InventoryClickEvent ev) {
						player.openInventory(new SelectTempoInv(file).getInventory());
					}
				});
				this.addIcon(icon);
			} catch (Exception e) {
			}
		}
	}

	public void setPage(Integer newPage) {
		this.page = newPage;
		this.update();
	}
}
