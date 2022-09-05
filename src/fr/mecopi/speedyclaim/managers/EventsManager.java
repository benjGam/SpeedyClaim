package fr.mecopi.speedyclaim.managers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.mecopi.speedyclaim.objects.Claim;

public class EventsManager implements Listener
{
	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		Claim claim = ClaimManager.getClaim(e.getBlock().getLocation());
		if(claim != null)
		{
			if(!claim.getOwner().equals(player.getName()) && !claim.getGuests().contains(player.getName())) //Add perm
			{
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
				e.setCancelled(true);
			}
		}
		else
		{
			Claim newClaim = ClaimManager.getNewClaim(e.getBlock().getLocation());
			if(newClaim != null && newClaim.getOwner().equals(player.getName()))
			{
				ClaimManager.CreateClaim(newClaim, player.getName());
			}
			else if(newClaim != null && !newClaim.getOwner().equals(player.getName()))
			{
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
				e.setCancelled(true);
			}
			
		}
	}
	@EventHandler 
	public void onPlace(BlockPlaceEvent e)
	{
		Player player = e.getPlayer();
		Claim claim = ClaimManager.getClaim(e.getBlock().getLocation());
		if(claim != null)
		{
			if(!claim.getOwner().equals(player.getName()) && !claim.getGuests().contains(player.getName())) //Add perm
			{
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
				e.setCancelled(true);
			}
		}
		else
		{
			Claim newClaim = ClaimManager.getNewClaim(e.getBlock().getLocation());
			if(newClaim != null && newClaim.getOwner().equals(player.getName()))
			{
				ClaimManager.CreateClaim(newClaim, player.getName());
			}
			else if(newClaim != null && !newClaim.getOwner().equals(player.getName()))
			{
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent e)
	{
		if(e.getInventory() == null || e.getInventory().getLocation() == null)
			return;
		if(e.getPlayer() instanceof Player)
		{
			Player player = (Player)e.getPlayer();
			Claim claim = ClaimManager.getClaim(e.getInventory().getLocation());
			if(claim != null)
			{
				if(!claim.getOwner().equals(player.getName()) && !claim.getGuests().contains(player.getName()) && e.getInventory().getType() != InventoryType.PLAYER) //add perm
				{
					player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
					e.setCancelled(true);
				}
			}
			Claim newClaim = ClaimManager.getNewClaim(e.getInventory().getLocation());
			if(newClaim != null)
			{
				if(newClaim.getOwner().equals(player.getName()))
				{
					ClaimManager.CreateClaim(newClaim, player.getName());
				}
				else
				{
					player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onEntityTakeDamage(EntityDamageByEntityEvent e)
	{
		if(e.getEntityType().equals(EntityType.PLAYER) || ClaimManager.isMonster(e.getEntity()))
			return;
		if(!(e.getDamager() instanceof Player))
			return;
		Player player = (Player)e.getDamager();
		Claim claim = ClaimManager.getClaim(e.getEntity().getLocation());
		if(claim != null)
		{
			if(!claim.getOwner().equals(player.getName()) && !claim.getGuests().contains(player.getName())) //Perm add
			{
				player.getLocale();
				e.getEntityType().name();
				player.sendMessage(ClaimManager.sendErrorMessage("Cette entité ne vous appartient pas."));
				e.setCancelled(true);
			}
		}
		else
		{
			Claim newClaim = ClaimManager.getNewClaim(e.getEntity().getLocation());
			if(newClaim != null && newClaim.getOwner().equals(player.getName()))
			{
				ClaimManager.CreateClaim(newClaim, player.getName());
			}
			else if(newClaim != null && !newClaim.getOwner().equals(player.getName()))
			{
				player.sendMessage(ClaimManager.sendErrorMessage("Cette parcelle ne vous appartient pas."));
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onConnect(PlayerJoinEvent e)
	{
		FileManager.UpdateLastConnection(e.getPlayer());
	}
}
