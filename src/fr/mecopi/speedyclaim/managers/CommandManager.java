package fr.mecopi.speedyclaim.managers;

import org.bukkit.Bukkit;

import fr.mecopi.speedyclaim.commands.*;

public class CommandManager 
{
	public CommandManager()
	{
		Bukkit.getPluginCommand("claim").setExecutor(new ClaimCommand());
		Bukkit.getPluginCommand("scdebug").setExecutor(new Debug());
		Bukkit.getPluginCommand("unclaim").setExecutor(new UnclaimCommand());
	}
}
