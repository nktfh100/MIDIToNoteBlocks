package com.nktfh100.MIDIToNoteBlocks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nktfh100.MIDIToNoteBlocks.inventory.SelectMidiInv;

public class MIDICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (player.isOp()) {
			player.openInventory(new SelectMidiInv().getInventory());
		}
		return true;
	}
}
