package fr.mecopi.speedyclaim.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.mecopi.speedyclaim.Main;
import fr.mecopi.speedyclaim.objects.*;

import net.md_5.bungee.api.ChatColor;

public class ClaimManager 
{
	public static int maxClaimCount = 0;
	public static double claimCost = 0;
	public static boolean claimExpire = false;
	public static int claimExpireYear = 0;
	public static int claimExpireMonth = 0;
	public static int claimExpireDay = 0;
	public static double claimSellAmount = 0;
	public static int reloadCheckDelay = 0;
	public static String currency = "";
	
	public static void Init(int first, double second, boolean third, int four, int five, int six, double seven, int eight, String nine)
	{
		maxClaimCount = first;
		claimCost = second;
		claimExpire = third;
		claimExpireYear = four;
		claimExpireMonth = five;
		claimExpireDay = six;
		claimSellAmount = seven;
		reloadCheckDelay = eight;
		currency = nine;
		if(claimExpire)
			new AutoClearManager();
	}
	
	// Init out
	
	private static final String errorMessage = "[" + ChatColor.RED + "SpeedyClaim" + ChatColor.WHITE + "] ";
	private static final String sucessMessage = "[" + ChatColor.GREEN + "SpeedyClaim" + ChatColor.WHITE + "] ";
	public static List<Claim> Claims = new ArrayList<Claim>();
	public static List<Claim> newClaims = new ArrayList<Claim>();
	
	public static String sendSuccessMessage(String Message)
	{
		return sucessMessage + Message;
	}
	public static String sendErrorMessage(String Message)
	{
		return errorMessage + Message;
	}
	
	//Buyed claim
	
	public static void CreateClaim(Claim claim, String player)
	{
		if(newClaims.contains(claim))
		{
			VaultManager.playerClaim(player);
			newClaims.remove(claim);
			FileManager.AddClaim(claim);
			Claims.add(claim);
		}
	}
	public void SetDefinitiveClaim(Claim claim, Player player)
	{
		Bukkit.getScheduler ().runTaskLater (Main.PL, () -> CreateClaim(claim, player.getName()), 600); //20 ticks = 1 seconde
	}
	public static void SetTorch(Claim claimLocation, Material materialToSet)
	{
		Block b1 = claimLocation.getLocation().getWorld().getHighestBlockAt(new Location(claimLocation.getLocation().getWorld(), claimLocation.getLocation().getBlockX()+7, claimLocation.getLocation().getBlockY(), claimLocation.getLocation().getBlockZ()+7));
		Block b2 = claimLocation.getLocation().getWorld().getHighestBlockAt(new Location(claimLocation.getLocation().getWorld(), claimLocation.getLocation().getBlockX()+7, claimLocation.getLocation().getBlockY(), claimLocation.getLocation().getBlockZ()-8));
		Block b3 = claimLocation.getLocation().getWorld().getHighestBlockAt(new Location(claimLocation.getLocation().getWorld(), claimLocation.getLocation().getBlockX()-8, claimLocation.getLocation().getBlockY(), claimLocation.getLocation().getBlockZ()+7));
		Block b4 = claimLocation.getLocation().getWorld().getHighestBlockAt(new Location(claimLocation.getLocation().getWorld(), claimLocation.getLocation().getBlockX()-8, claimLocation.getLocation().getBlockY(), claimLocation.getLocation().getBlockZ()-8));
		claimLocation.getLocation().getWorld().getBlockAt(new Location(b1.getWorld(), b1.getLocation().getBlockX(), b1.getLocation().getBlockY()+1, b1.getLocation().getBlockZ())).setType(materialToSet);
		claimLocation.getLocation().getWorld().getBlockAt(new Location(b2.getWorld(), b2.getLocation().getBlockX(), b2.getLocation().getBlockY()+1, b2.getLocation().getBlockZ())).setType(materialToSet);
		claimLocation.getLocation().getWorld().getBlockAt(new Location(b3.getWorld(), b3.getLocation().getBlockX(), b3.getLocation().getBlockY()+1, b3.getLocation().getBlockZ())).setType(materialToSet);
		claimLocation.getLocation().getWorld().getBlockAt(new Location(b4.getWorld(), b4.getLocation().getBlockX(), b4.getLocation().getBlockY()+1, b4.getLocation().getBlockZ())).setType(materialToSet);
	}
	//Getting Claims functions
	public static Location ConvertBlockLocation(Location location)
	{
		return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	public static Claim getClaim(String player)
	{
		for(Claim claim : Claims)
		{
			if(claim.getOwner().equals(player))
				return claim;
		}
		return null;
	}
	public static List<Claim> getClaims(String player)
	{
		List<Claim> playerClaims = new ArrayList<Claim>();
		for(Claim claim : Claims)
		{
			if(claim.getOwner().equals(player))
				playerClaims.add(claim);
		}
		return playerClaims;
	}
	public static Claim getClaim(Location actionLocation)
	{
		for(Claim claim : Claims)
		{
			if(claim.getClaim(actionLocation) != null)
				return claim;
		}
		return null;
	}
	public static Claim getClaim(String Name, String playerName)
	{
		for(Claim claim : Claims)
		{
			if(claim.getName().equals(Name) && claim.getOwner().equals(playerName))
				return claim;
		}
		return null;
	}
	public static Claim preventClaim(Location playerLocation)
	{
		for(Claim claim : Claims)
		{
			if(claim.Prevent(playerLocation) != false)
			{
				return claim;
			}
		}
		return null;
	}
	public static Claim getNewClaim(String player)
	{
		for(Claim claim : newClaims)
		{
			if(claim.getOwner().equals(player))
				return claim;
		}
		return null;
	}
	public static Claim getNewClaim(Location actionLocation)
	{
		for(Claim claim : newClaims)
		{
			if(claim.getClaim(actionLocation) != null)
				return claim;
		}
		return null;
	}
	public static boolean isMonster(Entity e)
	{
		switch(e.getType())
		{
			case CAVE_SPIDER:
				return true;
			case SPIDER:
				return true;
			case SKELETON:
				return true;
			case WITHER_SKELETON:
				return true;
			case STRAY:
				return true;
			case BLAZE:
				return true;
			case CREEPER:
				return true;
			case MAGMA_CUBE:
				return true;
			case ENDERMITE:
				return true;
			case EVOKER:
				return true;
			case GUARDIAN:
				return true;
			case ELDER_GUARDIAN:
				return true;
			case GHAST:
				return true;
			case HOGLIN:
				return true;
			case DROWNED:
				return true;
			case PHANTOM:
				return true;
			case PILLAGER:
				return true;
			case SILVERFISH:
				return true;
			case ZOMBIE:
				return true;
			case RAVAGER:
				return true;
			case SHULKER:
				return true;
			case SLIME:
				return true;
			case WITCH:
				return true;
			case VEX:
				return true;
			case VINDICATOR:
				return true;
			case ZOGLIN:
				return true;
			case HUSK:
				return true;
			case ZOMBIE_VILLAGER:
				return true;
			case ZOMBIFIED_PIGLIN:
				return true;
			case ENDERMAN:
				return true;
			default:
				return false;
		}
		
	}
}
