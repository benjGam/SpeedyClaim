package fr.mecopi.speedyclaim.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.mecopi.speedyclaim.Main;
import fr.mecopi.speedyclaim.objects.Claim;

public class FileManager 
{
	public static String pluginFolder = Main.PL.getDataFolder().getPath().replace("\\", "/");
	public static String claimsFolder = (pluginFolder += "/claims/");
	
	public static void IOInit()
	{
		if(!Main.PL.getDataFolder().exists())
			Main.PL.getDataFolder().mkdir();
		if(!(new File(claimsFolder).exists()))
			new File(claimsFolder).mkdir();
		InitClaims();
		InitConfig();
	}
	private static void InitConfig()
	{
		FileConfiguration configFile = Main.PL.getConfig();
		Main.PL.saveDefaultConfig();
		ClaimManager.Init(
		configFile.getInt("max_claim_count"), 
		configFile.getDouble("claim_cost"),
		configFile.getBoolean("claim_expire"),
		configFile.getInt("expire_y"),
		configFile.getInt("expire_m"),
		configFile.getInt("expire_d"),
		configFile.getDouble("claim_sell"),
		configFile.getInt("check_reload"),
		configFile.getString("currency"));
	}
	public static void InitClaims()
	{
		ClaimManager.Claims.clear();
		ClaimManager.newClaims.clear();
		for(OfflinePlayer player : Bukkit.getOfflinePlayers())
		{
			File playerClaim = new File(claimsFolder + player.getName() + ".yml");
			if(playerClaim.exists())
			{
				List<String> claimProperties = getClaimProperties(playerClaim);
				for(int i = 1; i < claimProperties.size(); i++)
				{
					if(!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(player.getName())))
						ClaimManager.Claims.add(InitClaim(claimProperties.get(i), player.getName()));
				}
			}
		}
	}
	public static void UpdateLastConnection(Player player) //When player connect
	{
		File playerClaim = new File(claimsFolder + player.getName() + ".yml");
		if(playerClaim.exists())
		{
			List<String> claimProperties = getClaimProperties(playerClaim);
			LocalDate NOW = LocalDate.now(); NOW.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			claimProperties.set(0, "last_connection: " + NOW.toString().replace("-", "/"));
			try
			{
				PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
				for(String Propertie : claimProperties)
					fileWriter.write(Propertie + "\n");
				fileWriter.close();
			} 
			catch (IOException e) { e.printStackTrace(); }	
		}
		else
			CreateDefaultClaim(playerClaim);
	}
	public static String GetLastConnection(String player)
	{
		return new String(player + " : " + getClaimProperties(new File(claimsFolder + player + ".yml")).get(0).replace(" ", "").replace("last_connection:", ""));
	}

	public static void UpdateGuest(Claim claim)
	{
		File playerClaim = new File(claimsFolder + claim.getOwner() + ".yml");
		List<String> claimProperties = getClaimProperties(playerClaim);
		int claimIndex = 0;
		for(String Line : claimProperties)
		{
			if(Line.contains(claim.getName()))
			{
				claimIndex = claimProperties.indexOf(Line);
				break;
			}
		}
		String guestBuilder = "{";
		for(String guestName : claim.getGuests())
		{ guestBuilder += "'"; if(claim.getGuests().indexOf(guestName) < claim.getGuests().size()-1) { guestBuilder += guestName + "', "; } else { guestBuilder += guestName + "'"; } } guestBuilder += "}";
		if(guestBuilder.equals("{}"))
			guestBuilder = "{''}";
		claimProperties.set(claimIndex, "guests : " + guestBuilder + " ; name : " + claim.getName() + " ; world : " + claim.getLocation().getWorld().getName() + " ; x: " + claim.getLocation().getBlockX() + " ; y: " + claim.getLocation().getBlockY() + " ; z: " + claim.getLocation().getBlockZ());
		try
		{
			PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
			for(String Propertie : claimProperties)
				fileWriter.write(Propertie + "\n");
			fileWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
	public static Claim InitClaim(String claimProperties, String playerName)
	{
		String[] Properties = claimProperties.replace(" ", "").split(";");
		String[] Guests = Properties[0].replace("guests:", "").replace("{", "").replace("}", "").replace("'", "").split(",");
		String Name = Properties[1].replace("name:", "");
		World world = Bukkit.getWorld(Properties[2].replace("world:", ""));
		int blockX = Integer.parseInt(Properties[3].replace("x:", ""));
		int blockY = Integer.parseInt(Properties[4].replace("y:", ""));
		int blockZ = Integer.parseInt(Properties[5].replace("z:", ""));
		List<String> finalGuests = new ArrayList<String>();
		for(String Guest : Guests)
		{
			if(!Guest.equals(""))
				finalGuests.add(Guest);
		}
		return new Claim(Name, playerName, finalGuests, new Location(world, blockX, blockY, blockZ));
	}
	public static void CreateDefaultClaim(File playerClaim)
	{
		try
		{
			PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
			LocalDate NOW = LocalDate.now(); NOW.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			fileWriter.write("last_connection: " + NOW.toString().replace("-", "/"));
			fileWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
	public static void AddClaim(Claim claim)
	{
		File playerClaim = new File(claimsFolder + claim.getOwner() + ".yml");
		List<String> claimProperties = getClaimProperties(playerClaim);
		try
		{
			PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
			for(String Propertie : claimProperties)
				fileWriter.write(Propertie + "\n");
			fileWriter.write("guests : {''} ; name : " + claim.getName() + " ; world : " + claim.getLocation().getWorld().getName() + " ; x: " + claim.getLocation().getBlockX() + " ; y: " + claim.getLocation().getBlockY() + " ; z: " + claim.getLocation().getBlockZ());
			fileWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
	public static void DelClaim(Claim claim)
	{
		File playerClaim = new File(claimsFolder + claim.getOwner() + ".yml");
		List<String> claimProperties = getClaimProperties(playerClaim);
		try
		{
			PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
			for(String Propertie : claimProperties)
				if(!Propertie.replace(" ", "").contains(new String("name:" + claim.getName())))
					fileWriter.write(Propertie + "\n");
			fileWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
	public static void ClearClaim(String playerName)
	{
		File playerClaim = new File(claimsFolder + playerName + ".yml");
		List<String> claimProperties = getClaimProperties(playerClaim);
		try
		{
			PrintWriter fileWriter = new PrintWriter(playerClaim, "UTF-8");
			fileWriter.write(claimProperties.get(0));
			fileWriter.close();
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
	public static List<String> getClaimProperties(File playerClaim)
	{
		List<String> Content = new ArrayList<String>();
		BufferedReader fileReader;
		String Line = "";
		try 
		{ 
			fileReader = new BufferedReader(new FileReader(playerClaim)); 
			while((Line = fileReader.readLine()) != null)
			{
				Content.add(Line);
			}
			fileReader.close();
		} catch (IOException e) { e.printStackTrace(); }
		return Content;
	}
	public static void DeleteFile(String playerName)
	{
		new File(claimsFolder + playerName + ".yml").delete();
	}
}
