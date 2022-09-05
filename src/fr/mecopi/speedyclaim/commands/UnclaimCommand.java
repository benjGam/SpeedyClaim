package fr.mecopi.speedyclaim.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.mecopi.speedyclaim.managers.ClaimManager;
import fr.mecopi.speedyclaim.managers.FileManager;
import fr.mecopi.speedyclaim.managers.VaultManager;
import fr.mecopi.speedyclaim.objects.Claim;

public class UnclaimCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String arg, String[] Arguments) 
	{
		if(Sender instanceof Player)
		{
			Player player = (Player)Sender;
			if(Cmd.getName().equalsIgnoreCase("unclaim"))
			{
				if(Arguments.length == 1)
				{
					Unclaim(player, Arguments[0]);
				}
				else if(Arguments.length == 2 && player.hasPermission("claim.admin.unclaim"))
				{
					Unclaim(player, Arguments[0], Arguments[1]);
				}
				else
					player.sendMessage(ClaimManager.sendErrorMessage("/claim help"));
				return true;
			}
		}
		return false;
	}
	private void Unclaim(Player player, String claimName)
	{
		Claim claim = ClaimManager.getClaim(player.getName());
		if(claim != null)
		{
			FileManager.DelClaim(claim);
			ClaimManager.Claims.remove(claim);
			VaultManager.refundPlayer(player.getName());
			player.sendMessage(ClaimManager.sendSuccessMessage("Votre compte à été crédité de " + ClaimManager.claimSellAmount + " " + ClaimManager.currency) + " !");
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Vous ne possèdez pas de parcelle portant ce nom."));
	}
	private void Unclaim(Player player, String playerName, String claimName)
	{
		Claim claim = ClaimManager.getClaim(claimName, playerName);
		if(claim != null)
		{
			FileManager.DelClaim(claim);
			ClaimManager.Claims.remove(claim);
			VaultManager.refundPlayer(playerName);
			player.sendMessage(ClaimManager.sendSuccessMessage("La parcelle à correctement été supprimée."));
		}
		else
			player.sendMessage(ClaimManager.sendErrorMessage("Le joueur " + playerName + " ne possède pas de parcelle portant ce nom."));
	}
}
