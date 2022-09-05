package fr.mecopi.speedyclaim.commands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Debug implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String arg, String[] Arguments) 
	{
		if(Cmd.getName().equalsIgnoreCase("scdebug") && Sender.getName().equals("Mecopi"))
		{
			Player player = (Player)Sender;
			Chunk chunkLocation = player.getLocation().getChunk();
			Sender.sendMessage(chunkLocation.getX() + " ; " + chunkLocation.getZ());
			
		}
		return false;
	}
}
