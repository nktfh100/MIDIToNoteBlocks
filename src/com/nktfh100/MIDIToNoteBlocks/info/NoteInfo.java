package com.nktfh100.MIDIToNoteBlocks.info;

import com.nktfh100.MIDIToNoteBlocks.enums.NoteType;

public class NoteInfo {

	private NoteType type;
	private int actualNote = -1;

	public NoteInfo(NoteType type, int actualNote) {
		this.type = type;
		this.actualNote = actualNote;
	}

	public NoteType getType() {
		return type;
	}

	public int getActualNote() {
		return actualNote;
	}

}
