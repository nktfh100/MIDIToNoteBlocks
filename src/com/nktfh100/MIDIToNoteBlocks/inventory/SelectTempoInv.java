package com.nktfh100.MIDIToNoteBlocks.inventory;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.nktfh100.MIDIToNoteBlocks.main.MIDIToNoteBlocks;
import com.nktfh100.MIDIToNoteBlocks.managers.ItemsManager;

public class SelectTempoInv extends CustomHolder {

	private File file;
	private double currentTempo = 120;

	public SelectTempoInv(File file) {
		super(27, ChatColor.translateAlternateColorCodes('&', "&e&lSelect tempo"));
		this.file = file;
		try {
			Sequence sequence = MidiSystem.getSequence(file);
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(sequence);
			this.currentTempo = Math.round(sequencer.getTempoInBPM());
		} catch (Exception e) {
		}
		this.update();
	}

	public void setTempo(double newTempo) {
		this.currentTempo = newTempo;
		this.update();
	}

	public void update() {
		ItemsManager itemsManager = MIDIToNoteBlocks.getInstance().getItemsManager();
		try {
			Icon icon = new Icon(itemsManager.getItem("select-tempo_tempo").getItem().getItem(file.getName(), this.currentTempo + ""));
			icon.addClickAction(new ClickAction() {
				@Override
				public void execute(Player player, InventoryClickEvent ev) {
					if (ev.getClick() == ClickType.LEFT) {
						setTempo(currentTempo - 1);
					} else if (ev.getClick() == ClickType.RIGHT) {
						setTempo(currentTempo + 1);
					}
				}
			});
			this.setIcon(13, icon);

			icon = new Icon(itemsManager.getItem("select-tempo_back").getItem().getItem());
			icon.addClickAction(new ClickAction() {
				@Override
				public void execute(Player player, InventoryClickEvent ev) {
					player.openInventory(new SelectMidiInv().getInventory());
				}
			});
			this.setIcon(18, icon);

			icon = new Icon(itemsManager.getItem("select-tempo_accept").getItem().getItem());
			icon.addClickAction(new ClickAction() {
				@Override
				public void execute(Player player, InventoryClickEvent ev) {
					player.closeInventory();
					MIDIToNoteBlocks.getInstance().placeSchem(file, currentTempo, player);
				}
			});
			this.setIcon(26, icon);

		} catch (Exception e) {

		}

	}

}
