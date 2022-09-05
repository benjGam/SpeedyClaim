package fr.mecopi.speedyclaim.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultManager 
{
	public static Economy economy = null;
	public static boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	    if(economyProvider != null) 
	    {
	    	economy = economyProvider.getProvider();
	    }
	    return (economy != null);
	}
	@SuppressWarnings("deprecation")
	public static void playerClaim(String player)
	{
		economy.withdrawPlayer(player, ClaimManager.claimCost);
	}
	@SuppressWarnings("deprecation")
	public static void refundPlayer(String player)
	{
		economy.depositPlayer(player, ClaimManager.claimSellAmount);
	}
	@SuppressWarnings("deprecation")
	public static double getMoney(String player)
	{
		return economy.getBalance(player);
	}
}
