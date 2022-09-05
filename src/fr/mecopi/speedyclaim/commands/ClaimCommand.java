package fr.mecopi.speedyclaim.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import fr.mecopi.speedyclaim.managers.ClaimManager;
import fr.mecopi.speedyclaim.managers.FileManager;
import fr.mecopi.speedyclaim.managers.VaultManager;
import fr.mecopi.speedyclaim.objects.Claim;
import fr.mecopi.speedyclaim.objects.SetDefinitiveTask;
import net.md_5.bungee.api.ChatColor;

public class ClaimCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String arg, String[] Arguments) 
	{
		if(Sender instanceof Player)
		{
			Player player = (Player)Sender;
			if(Cmd.getName().equalsIgnoreCase("claim"))
			{
				if(Arguments.length >= 1)
				{
					switch(Arguments[0].toLowerCase())
					{
						case "undo":
							UndoClaim(player);
							break;
						case "sall":
							SellAll(player);
							break;
						case "clear":
							if(player.hasPermission("claim.admin.clear") && Arguments.length == 2)
								ClearClaims(player, Arguments[1]);
							else
								player.sendMessage(ClaimManager.sendErrorMessage("/claim help"));
							break;
						case "gadd":
							if(Arguments.length == 3)
							{
								AddGuest(player, Arguments[1], Arguments[2]);
							}
							else
								player.sendMessage(ClaimManager.sendErrorMessage("/claim help"));
							break;
						case "gdel":
							if(Arguments.length == 3)
							{
								DelGuest(player, Arguments[1], Arguments[2]);
							}
							else
								player.sendMessage(ClaimManager.sendErrorMessage("/claim help"));
							break;
						case "help":
							Help(player);
						case "glist":
							if(Arguments.length == 3 && player.hasPermission("claim.admin.glist"))
								GList(player, Arguments[1], Arguments[2]);
							else if(Arguments.length == 2)
								GList(player, Arguments[1]);
							else
								player.sendMessage(ClaimManager.sendErrorMessage("/claim glist <nom_de_parcelle>"));
							break;
						case "list":
							if(Arguments.length == 2 && player.hasPermission("claim.admin.list"))
								List(player, Arguments[1]);
							else if(Arguments.length == 1)
								List(player);
							else
								player.sendMessage(ClaimManager.sendErrorMessage("/claim list"));
							break;
						default:
							if(!Arguments[0].contains("undo") && !Arguments[0].contains("sall") && !Arguments[0].contains("clear") && !Arguments[0].contains("gadd") && !Arguments[0].contains("gdel") && !Arguments[0].contains("help") && !Arguments[0].contains("glist"))
									SimpleClaim(player, Arguments[0]);
							break;
					}	
				}
				else
					player.sendMessage(ClaimManager.sendErrorMessage("/claim help"));
				return true;
			}
		}
		return false;
	}
	private void setDefinitiveClaim(String player)
	{
		if(ClaimManager.getNewClaim(player) != null)
			ClaimManager.CreateClaim(ClaimManager.getNewClaim(player), player);
	}
	private void SimpleClaim(Player player, String Name)
	{
		setDefinitiveClaim(player.getName());
		if(ClaimManager.getClaim(ClaimManager.ConvertBlockLocation(player.getLocation())) != null)
		{
			player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle est déjà protégée."));
			return;
		}
		else if(ClaimManager.preventClaim(ClaimManager.ConvertBlockLocation(player.getLocation())) != null)
		{
			player.sendMessage(ClaimManager.sendErrorMessage("Une autre parcelle se trouve déjà dans la zone."));
			return;
		}
		else if(ClaimManager.getClaim(Name, player.getName()) != null)
		{
			player.sendMessage(ClaimManager.sendErrorMessage("Vous possèdez déjà une parcelle portant ce nom."));
			return;
		}
		else if(ClaimManager.getClaims(player.getName()).size() == ClaimManager.maxClaimCount)
		{
			player.sendMessage(ClaimManager.sendErrorMessage("Vous avez atteint la limite de parcelle."));
			return;
		}
		else if(VaultManager.getMoney(player.getName()) < ClaimManager.claimCost)
		{
				player.sendMessage(ClaimManager.sendErrorMessage("Vous n'avez pas assez d'argent pour acheter cette parcelle."));
				return;
		}
		else
		{
			Claim claim = new Claim(Name, player.getName(), new ArrayList<String>() , ClaimManager.ConvertBlockLocation(player.getLocation()));
			ClaimManager.newClaims.add(claim);
			ClaimManager.SetTorch(claim, Material.TORCH);
			player.sendMessage(ClaimManager.sendSuccessMessage("Vous obtenez cette parcelle pour " + ClaimManager.claimCost + " " + ClaimManager.currency + " !\nVous disposez de 30 secondes de rétractation (/claim undo)."));
			new SetDefinitiveTask(claim, player.getName());
		}
	}
	private void UndoClaim(Player player)
	{
		Claim claim = ClaimManager.getNewClaim(player.getName());
		if(claim != null)
		{
			ClaimManager.newClaims.remove(claim);
			ClaimManager.SetTorch(claim, Material.AIR);
			player.sendMessage(ClaimManager.sendSuccessMessage("La claim a bien été supprimée."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous n'avez aucune claim à supprimer."));
	}
	private void SellAll(Player player)
	{
		List<Claim> playerClaims = ClaimManager.getClaims(player.getName());
		if(playerClaims.size() > 0)
		{
			FileManager.ClearClaim(player.getName());
			for(Claim claim : playerClaims)
				ClaimManager.Claims.remove(claim);
			double totalRefund = ClaimManager.claimSellAmount * playerClaims.size();
			player.sendMessage(ClaimManager.sendSuccessMessage("Vous avez été crédité de " + totalRefund + " " + ClaimManager.currency + " suite à la vente de vos " + playerClaims.size() + " parcelles."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous ne possèdez aucune parcelle."));
	}
	private void ClearClaims(Player player, String playerName)
	{
		List<Claim> playerClaims = ClaimManager.getClaims(playerName);
		if(playerClaims.size() > 0)
		{
			FileManager.ClearClaim(playerName);
			for(Claim claim : playerClaims)
				ClaimManager.Claims.remove(claim);
			player.sendMessage(ClaimManager.sendSuccessMessage("Les " + playerClaims.size() + " parcelle(s) de " + playerName + " ont bien été supprimée(s)."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage(playerName + " ne possède aucune parcelle."));
	}
	private void AddGuest(Player player, String claimName, String playerName)
	{
		Claim claim = ClaimManager.getClaim(claimName, player.getName());
		if(claim != null)
		{
			if(!claim.getGuests().contains(playerName))
			{
				claim.getGuests().add(playerName);
				FileManager.UpdateGuest(claim);
				player.sendMessage(ClaimManager.sendSuccessMessage("Le joueur " + playerName + " a bien été ajouté à la liste des invités."));
				if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName)))
					Bukkit.getPlayer(playerName).sendMessage(ClaimManager.sendSuccessMessage(player.getName() + " vous invité dans sa parcelle !"));
			}
			else
				player.sendMessage(ClaimManager.sendErrorMessage("Le joueur " + playerName + " fait déjà parti de la liste des invités."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous n'avez aucune parcelle portant le nom " + claimName + ". /claim list"));
	}
	private void DelGuest(Player player, String claimName, String playerName)
	{
		Claim claim = ClaimManager.getClaim(claimName, player.getName());
		if(claim != null)
		{
			if(claim.getGuests().contains(playerName))
			{
				claim.getGuests().remove(playerName);
				FileManager.UpdateGuest(claim);
				player.sendMessage(ClaimManager.sendSuccessMessage("Le joueur " + playerName + " a été retiré de la liste des invités."));
			}
			else
				player.sendMessage(ClaimManager.sendErrorMessage("Le joueur " + playerName + " ne fait parti de la liste des invités."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous n'avez aucune parcelle portant le nom " + claimName + ". /claim list"));
	}
	private void Help(Player player)
	{
		ChatColor chatColor = ChatColor.GOLD;
		String helpMessage = ClaimManager.sendSuccessMessage(chatColor + "Listes des commandes\n");
		if(player.hasPermission("claim.admin.unclaim"))
			helpMessage += chatColor + "/UNCLAIM <nom_de_parcelle> <joueur> : Permet de supprimer la claim d'un joueur. (Ne le rembourse pas)\n";
		if(player.hasPermission("claim.admin.clear"))
			helpMessage += chatColor + "/CLAIM clear <joueur> : Permet de supprimer toutes les claims d'un joueur (Ne le rembourse pas)\n";
		if(player.hasPermission("claim.admin.glist"))
			helpMessage += chatColor + "/CLAIM glist <nom_de_parcelle> : Permet d'avoir la liste de tout les invités d'une parcelle.\n";
		if(player.hasPermission("claim.admin.list"))
			helpMessage += chatColor + "/CLAIM list <joueur> : Permet d'avoir la liste de toutes les parcelles d'un joueur.\n";
		helpMessage += chatColor + "/CLAIM <nom_de_parcelle> : Permet de protéger une zone\n(- " + ClaimManager.claimCost + " " + ClaimManager.currency + ")\n";
		helpMessage += chatColor + "/UNCLAIM <nom_de_parcelle> : Permet de supprimer une parcelle (+ " + ClaimManager.claimSellAmount + " " + ClaimManager.currency + ")\n";
		helpMessage += chatColor + "/CLAIM sall : Permet de vendre toutes vos parcelles.\n";
		helpMessage += chatColor + "/CLAIM gadd <nom_de_parcelle> <joueur> : Permet d'ajouter un joueur à vos invités.\n";
		helpMessage += chatColor + "/CLAIM gdel <nom_de_parcelle> <joueur> : Permet de retirer un joueur de vos invités.\n";
		helpMessage += chatColor + "/CLAIM list : Permet d'avoir la liste de tout vos terrains.\n";
		player.sendMessage(helpMessage);
	}
	private void GList(Player player, String claimName)
	{
		Claim claim = ClaimManager.getClaim(claimName, player.getName());
		if(claim != null)
		{
			if(claim.getGuests().size() >= 1)
			{
				player.sendMessage(ClaimManager.sendSuccessMessage("Listes des invités de la parcelle " + claimName));
				String guestBuilder = "";
				for(int i = 0 ; i < claim.getGuests().size()-1; i++)
				{
					guestBuilder += claim.getGuests().get(i) + ", ";
				}
				guestBuilder += claim.getGuests().get(claim.getGuests().size()-1);
				player.sendMessage(ChatColor.GOLD + guestBuilder);
			}
			else
				player.sendMessage(ClaimManager.sendErrorMessage("Vous n'avez aucun invité sur cette parcelle."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous ne possèdez aucune parcelle portant le nom " + claimName + "."));
	}
	private void GList(Player player, String claimName, String playerName)
	{
		Claim claim = ClaimManager.getClaim(claimName, playerName);
		if(claim != null)
		{
			if(claim.getGuests().size() >= 1)
			{
				player.sendMessage(ClaimManager.sendSuccessMessage("Listes des invités de la parcelle " + claimName));
				String guestBuilder = "";
				for(int i = 0 ; i < claim.getGuests().size()-1; i++)
				{
					guestBuilder += claim.getGuests().get(i) + ", ";
				}
				guestBuilder += claim.getGuests().get(claim.getGuests().size()-1);
				player.sendMessage(ChatColor.GOLD + guestBuilder);
			}
			else
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle n'a aucun invité sur cette parcelle."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Le joueur " + playerName + " ne possède aucune parcelle s'appellant " + claimName + "."));
	}
	private void List(Player player)
	{
		List<Claim> claim = ClaimManager.getClaims(player.getName());
		if(claim.size() >= 1)
		{
			player.sendMessage(ClaimManager.sendSuccessMessage("Listes de vos parcelles"));
			String claimNames = "";
			for(int i = 0; i < claim.size(); i++)
				claimNames += claim.get(i).getName() + ", ";
			claimNames += claim.get(claim.size()-1).getName();
			player.sendMessage(ChatColor.GOLD + claimNames);
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous ne possèdez aucune parcelle."));
	}
	private void List(Player player, String playerName)
	{
		List<Claim> claim = ClaimManager.getClaims(playerName);
		if(claim.size() >= 1)
		{
			player.sendMessage(ClaimManager.sendSuccessMessage("Listes des parcelles de " + playerName));
			String claimNames = "";
			for(int i = 0; i < claim.size() -1; i++)
				claimNames += claim.get(i).getName() + ", ";
			claimNames += claim.get(claim.size()-1).getName();
			player.sendMessage(ChatColor.GOLD + claimNames);
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage(playerName + " ne possède aucune parcelle."));
	}

}
