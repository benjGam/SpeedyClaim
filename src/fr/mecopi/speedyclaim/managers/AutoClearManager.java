package fr.mecopi.speedyclaim.managers;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import fr.mecopi.speedyclaim.Main;
import fr.mecopi.speedyclaim.objects.Claim;

public class AutoClearManager
{
	public AutoClearManager()
	{
		Task();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.PL, new Runnable() {
		    @Override
		    public void run() {
		        Task();
		    }
		}, 0, (ClaimManager.reloadCheckDelay * (60 * 20)));
	}
	public void Task()
	{
		LocalDate NOW = LocalDate.now(); NOW.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		//Basic dateFormat : NOW.toString().split("T")[0].replace("-", "/");
		List<String> lastConnectionDates = new ArrayList<String>();
		for(OfflinePlayer player : Bukkit.getOfflinePlayers())
		{
			if(new File(FileManager.claimsFolder + player.getName() + ".yml").exists())
			{
				String lastConnection = FileManager.GetLastConnection(player.getName());
				if(!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(player.getName())) && !lastConnectionDates.contains(lastConnection))
					lastConnectionDates.add(lastConnection);
			}
		}
		for(int i = 0; i < lastConnectionDates.size(); i++)
		{
			String playerName = lastConnectionDates.get(0).split(":")[0].replace(" ", "");
			LocalDate expirationDate = LocalDate.parse(lastConnectionDates.get(0).split(":")[1].replace(" ", "").replace("last_connection:", "").replace("/", "-"));
			expirationDate = expirationDate.plusMonths(ClaimManager.claimExpireMonth);
			expirationDate = expirationDate.plusYears(ClaimManager.claimExpireYear);
			expirationDate = expirationDate.plusDays(ClaimManager.claimExpireDay);
			if(NOW.isAfter(expirationDate))
			{
				List<Claim> playerClaims = ClaimManager.getClaims(playerName);
				for(Claim claim : playerClaims)
					ClaimManager.Claims.remove(claim);
				
				FileManager.DeleteFile(playerName);
				Bukkit.getConsoleSender().sendMessage(ClaimManager.sendSuccessMessage("Les claims du joueur " + playerName + " ont etes netoyees pour cause d'inactivite prolongee."));
			}
		}
		/*
		for(String lastConnection : lastConnectionDate)
		{
			String playerName = lastConnectionDate.get(0).split(":")[0].replace(" ", "");
			int yPrediction = Integer.parseInt(lastConnectionDate.get(0).split(":")[1].split("/")[0].replace(" ", "")) + ClaimManager.claimExpireYear;
			if(ClaimManager.claimExpireMonth < 12)
			{
				int mPrediction = Integer.parseInt(lastConnectionDate.get(0).split(":")[1].split("/")[1].replace(" ", "")) + ClaimManager.claimExpireMonth;
				if(mPrediction >= 12)
				{
					mPrediction = mPrediction -12;
					yPrediction++;
				}
			}
			if(actualTime.is)
		}
		*/
	}

}
