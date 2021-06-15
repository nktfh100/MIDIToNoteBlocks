package com.nktfh100.MIDIToNoteBlocks.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nktfh100.MIDIToNoteBlocks.commands.MIDICommand;
import com.nktfh100.MIDIToNoteBlocks.events.InvClick;
import com.nktfh100.MIDIToNoteBlocks.info.NoteInfo;
import com.nktfh100.MIDIToNoteBlocks.managers.ItemsManager;
import com.nktfh100.MIDIToNoteBlocks.utils.Utils;

public class MIDIToNoteBlocks extends JavaPlugin {

	private static MIDIToNoteBlocks instance;

	public MIDIToNoteBlocks() {
		instance = this;
	}

//	private final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	private ItemsManager itemsManager;

	@Override
	public void onEnable() {
		this.getCommand("midi").setExecutor(new MIDICommand());

		this.itemsManager = new ItemsManager(this);
		this.itemsManager.loadItems();

		this.getServer().getPluginManager().registerEvents(new InvClick(), this);

		File midisFolder = new File(this.getDataFolder(), "midi");
		if (!midisFolder.exists()) {
			midisFolder.mkdirs();
		}
	}

	public void placeSchem(File file, Double tempo, Player player) {
		try {
			Sequence sequence = MidiSystem.getSequence(file);
			if (sequence.getDivisionType() != Sequence.PPQ) {
				player.sendMessage("Division type is not PPQ and not supported");
				return;
			}

//			System.out.println("BPM " + tempo);
//			System.out.println("Length in seconds: " + (sequence.getMicrosecondLength() * 0.000001));

			double ticks_per_quarter = sequence.getResolution();
			double µs_per_quarter = 1000000 * 60 / tempo;
			double µs_per_tick = µs_per_quarter / ticks_per_quarter;
			double seconds_per_tick = µs_per_tick / 1000000;

			for (int trackNum = 0; trackNum < sequence.getTracks().length; trackNum++) {
				Track track = sequence.getTracks()[trackNum];
				ArrayList<MidiEvent> keyOnEvents = new ArrayList<MidiEvent>();
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage shortMsg = (ShortMessage) message;
						if (shortMsg.getCommand() == ShortMessage.NOTE_ON && shortMsg.getData2() > 0) { // Data2 = velocity
							keyOnEvents.add(event);
						}
					}
				}
				if (keyOnEvents.size() == 0) {
					continue;
				}
				// Group notes that are clicked at the same time
				ArrayList<ArrayList<MidiEvent>> keyOnEventsGrouped = new ArrayList<ArrayList<MidiEvent>>();
				for (int i = 0; i < keyOnEvents.size(); i++) {
					MidiEvent event = keyOnEvents.get(i);
					MidiEvent prevEvent = null;
					Double eventSeconds = (event.getTick() * seconds_per_tick);
					Double prevEventSeconds = null;
					if (keyOnEventsGrouped.size() > 0) {
						prevEvent = keyOnEventsGrouped.get(keyOnEventsGrouped.size() - 1).get(0);
						prevEventSeconds = (prevEvent.getTick() * seconds_per_tick);
					}
					if (prevEvent == null) {
						keyOnEventsGrouped.add(new ArrayList<MidiEvent>(Arrays.asList(event)));
						continue;
					}
					if (Math.abs(eventSeconds - prevEventSeconds) < 0.09d) {
						keyOnEventsGrouped.get(keyOnEventsGrouped.size() - 1).add(event);
					} else {
						keyOnEventsGrouped.add(new ArrayList<MidiEvent>(Arrays.asList(event)));
					}
				}

				Location startLoc = player.getLocation().add(-2, 2, 0);
				for (int i = 0; i < trackNum; i++) {
					if (sequence.getTracks()[i].size() > 0) {
						startLoc.add(0, 4, 0);
					}
				}
				startLoc.add(0, -1, 0);
				startLoc.getBlock().setType(Material.GRASS_BLOCK);
				startLoc.add(0, 1, 0);

				startLoc.getBlock().setType(Material.REPEATER);

				Location redstoneStartLoc = startLoc.clone();
				redstoneStartLoc.add(0, -1, -1);
				redstoneStartLoc.getBlock().setType(Material.GRASS_BLOCK);
				redstoneStartLoc.add(0, 1, 0);
				redstoneStartLoc.getBlock().setType(Material.REDSTONE_WIRE);
				if (trackNum != sequence.getTracks().length - 1) {
					redstoneStartLoc.add(1, 0, 0);
					redstoneStartLoc.getBlock().setType(Material.GRASS_BLOCK);
					redstoneStartLoc.add(0, 1, 0);
					redstoneStartLoc.getBlock().setType(Material.REDSTONE_WIRE);
					redstoneStartLoc.add(0, 0, -1);
					redstoneStartLoc.getBlock().setType(Material.GRASS_BLOCK);
					redstoneStartLoc.add(0, 1, 0);
					redstoneStartLoc.getBlock().setType(Material.REDSTONE_WIRE);
					redstoneStartLoc.add(-1, 0, 0);
					redstoneStartLoc.getBlock().setType(Material.GRASS_BLOCK);
					redstoneStartLoc.add(0, 1, 0);
					redstoneStartLoc.getBlock().setType(Material.REDSTONE_WIRE);
				}

				startLoc.add(0, 0, 1);
				startLoc.getBlock().setType(Material.GRASS_BLOCK);
				startLoc.add(-1, 0, 0);
				Utils.placeRepeater(startLoc, 1, BlockFace.WEST);
				startLoc.add(0, -1, 0);
				startLoc.getBlock().setType(Material.GRASS_BLOCK);
				startLoc.add(0, 1, 0);

				BlockFace currentDir = BlockFace.WEST; // EAST = + : WEST = -
				int minX = startLoc.getBlockX() - 20;
				int maxX = startLoc.getBlockX() + 20;

				for (int i = 0; i < keyOnEventsGrouped.size(); i++) {
					ArrayList<MidiEvent> currentGroup = keyOnEventsGrouped.get(i);
					ArrayList<MidiEvent> nextGroup = null;
					if (i + 1 < keyOnEventsGrouped.size()) {
						nextGroup = keyOnEventsGrouped.get(i + 1);
					}

					if (i == 0) {
						double timeToNextKey_ = 0;
						if (nextGroup != null) {
							timeToNextKey_ = (currentGroup.get(0).getTick() * seconds_per_tick);
						}
						double delayNeeded_ = timeToNextKey_ / 0.1;
						double extraDelay_ = Math.round(delayNeeded_ % 4);

						for (int ii = 0; ii < Math.floor(delayNeeded_ / 4); ii++) {
							startLoc.add(currentDir == BlockFace.WEST ? -1 : 1, 0, 0);
							Utils.placeRepeater(startLoc, 4, currentDir);
							if (Utils.shouldSwitchFace(minX, maxX, startLoc, currentDir)) {
								Utils.placeCorner(startLoc, currentDir);
								currentDir = Utils.switchFace(currentDir);
							}
						}
						if (extraDelay_ > 0) {
							startLoc.add(currentDir == BlockFace.WEST ? -1 : 1, 0, 0);
							Utils.placeRepeater(startLoc, (int) extraDelay_, currentDir);
							if (Utils.shouldSwitchFace(minX, maxX, startLoc, currentDir)) {
								Utils.placeCorner(startLoc, currentDir);
								currentDir = Utils.switchFace(currentDir);
							}
						}
					}

					startLoc.add(currentDir == BlockFace.WEST ? -1 : 1, 0, 0);

					if (currentGroup.size() == 1) {
						MidiEvent event = currentGroup.get(0);
						ShortMessage shortMsg = (ShortMessage) event.getMessage();
						int key = shortMsg.getData1() - 20; // To get actual piano note

						boolean isNoteOk = true;
						NoteInfo note = Utils.getNoteFromKey(key);

						if (note.getType() == null || (note.getActualNote() < 0 && note.getActualNote() > 24)) {
							isNoteOk = false;
						}

						if (isNoteOk) {
							Utils.placeNoteBlock(startLoc, note);
						} else {
							startLoc.getBlock().setType(Material.BEDROCK);
						}
					} else {
						startLoc.getBlock().setType(Material.GRASS_BLOCK);

						Location sideLoc = startLoc.clone();
						int noteI = 0;
						for (MidiEvent event : currentGroup) {
							if (noteI >= 3) { // Can play maximum of 3 grouped notes
								break;
							}

							ShortMessage shortMsg = (ShortMessage) event.getMessage();
							int key = shortMsg.getData1() - 20; // To get actual piano key
//							int octave = (key / 12) - 1;
//							int note = key % 12;
//							String noteName = NOTE_NAMES[note];
							boolean isNoteOk = true;
							NoteInfo note = Utils.getNoteFromKey(key);

							if (note.getType() == null || (note.getActualNote() < 0 && note.getActualNote() > 24)) {
								isNoteOk = false;
							}

							if (noteI == 0) {
								sideLoc.add(0, 0, 1);
							} else if (noteI == 1) {
								sideLoc.add(0, 1, 0);
							} else if (noteI == 2) {
								sideLoc.add(0, 0, -1);
							}

							if (isNoteOk) {
								Utils.placeNoteBlock(sideLoc, note);
							} else {
								sideLoc.getBlock().setType(Material.BEDROCK);
							}
							if (noteI == 0) {
								sideLoc.add(0, 0, -1);
							} else if (noteI == 1) {
								sideLoc.add(0, -1, 0);
							} else if (noteI == 2) {
								sideLoc.add(0, 0, 1);
							}

							noteI++;
						}
					}
					double timeToNextKey = 0;
					if (nextGroup != null) {
						timeToNextKey = (nextGroup.get(0).getTick() * seconds_per_tick) - (currentGroup.get(0).getTick() * seconds_per_tick);
					}
					double delayNeeded = timeToNextKey / 0.1;
					double extraDelay = Math.round(delayNeeded % 4);

					for (int ii = 0; ii < Math.floor(delayNeeded / 4); ii++) {
						startLoc.add(currentDir == BlockFace.WEST ? -1 : 1, 0, 0);
						Utils.placeRepeater(startLoc, 4, currentDir);
						if (Utils.shouldSwitchFace(minX, maxX, startLoc, currentDir)) {
							Utils.placeCorner(startLoc, currentDir);
							currentDir = Utils.switchFace(currentDir);
						}
					}
					if (extraDelay > 0) {
						startLoc.add(currentDir == BlockFace.WEST ? -1 : 1, 0, 0);
						Utils.placeRepeater(startLoc, (int) extraDelay, currentDir);
						if (Utils.shouldSwitchFace(minX, maxX, startLoc, currentDir)) {
							Utils.placeCorner(startLoc, currentDir);
							currentDir = Utils.switchFace(currentDir);
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static MIDIToNoteBlocks getInstance() {
		return instance;
	}

	public ItemsManager getItemsManager() {
		return itemsManager;
	}

}
