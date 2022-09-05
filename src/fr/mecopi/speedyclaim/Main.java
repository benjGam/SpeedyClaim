package fr.mecopi.speedyclaim;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.mecopi.speedyclaim.managers.ClaimManager;
import fr.mecopi.speedyclaim.managers.CommandManager;
import fr.mecopi.speedyclaim.managers.EventsManager;
import fr.mecopi.speedyclaim.managers.FileManager;
import fr.mecopi.speedyclaim.managers.VaultManager;

public class Main extends JavaPlugin
{
	public static Plugin PL;
	@Override
	public void onEnable()
	{
		PL = this;
		VaultManager.setupEconomy();
		FileManager.IOInit();
		Bukkit.getPluginManager().registerEvents(new EventsManager(), this);
		new CommandManager();
		Bukkit.getConsoleSender().sendMessage(ClaimManager.sendSuccessMessage("Enabled sucessfully"));
	}
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ClaimManager.sendSuccessMessage("Disabled sucessfully"));
	}
}
