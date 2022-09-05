package fr.mecopi.speedyclaim.objects;

import org.bukkit.Bukkit;

import fr.mecopi.speedyclaim.Main;
import fr.mecopi.speedyclaim.managers.ClaimManager;

public class SetDefinitiveTask 
{
	public SetDefinitiveTask(Claim claim, String player)
	{
		Bukkit.getScheduler().runTaskLater (Main.PL, () -> ClaimManager.CreateClaim(claim, player), 600); //20 ticks = 1 seconde (30 secondes)
	}
}
