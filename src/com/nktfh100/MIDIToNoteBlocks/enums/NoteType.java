package com.nktfh100.MIDIToNoteBlocks.enums;

import org.bukkit.Instrument;
import org.bukkit.Material;

public enum NoteType {
	BASS(Material.OAK_PLANKS, Instrument.BASS_GUITAR), PIANO(Material.GRASS_BLOCK, Instrument.PIANO), BELL(Material.GOLD_BLOCK, Instrument.BELL);

	private Material mat;
	private Instrument instrument;

	private NoteType(Material mat, Instrument instrument) {
		this.mat = mat;
		this.instrument = instrument;
	}

	public Material getMaterial() {
		return this.mat;
	}

	public Instrument getInstrument() {
		return this.instrument;
	}
}
