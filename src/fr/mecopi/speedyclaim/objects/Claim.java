package fr.mecopi.speedyclaim.objects;

import java.util.List;

import org.bukkit.Location;

public class Claim 
{
	private String Name;
	private String Owner;
	private List<String> Guest;
	private Location claimLocation;
	
	public Claim(String nameEntry, String ownerEntry, List<String> guestsEntry, Location claimLocationEntry)
	{
		Name = nameEntry;
		Owner = ownerEntry;
		Guest = guestsEntry;
		claimLocation = claimLocationEntry; 
	}
	
	public String getName()
	{
		return Name;
	}
	public String getOwner()
	{
		return Owner;
	}
	public List<String> getGuests()
	{
		return Guest;
	}
	public Claim getClaim(Location playerLocation)
	{
		if(((playerLocation.getBlockX() >= (claimLocation.getBlockX()-8)) && (playerLocation.getBlockX() <= (claimLocation.getBlockX()+7)) && (playerLocation.getBlockZ() >= (claimLocation.getBlockZ()-8)) && (playerLocation.getBlockZ() <= (claimLocation.getBlockZ()+7))) && (playerLocation.getBlockY() >= 0))
		{
			return this;
		}
		return null;
	}
	public Location getLocation()
	{
		return claimLocation;
	}
	
	//Function
	
	public boolean Prevent(Location playerLocation)
	{
		if((playerLocation.getBlockX()-8 > claimLocation.getBlockX()+7) || (playerLocation.getBlockZ()-8 > claimLocation.getBlockZ()+7) || (playerLocation.getBlockX()+7 < claimLocation.getBlockX()-8) || playerLocation.getBlockZ()+7 < claimLocation.getBlockZ()-8)
		{
			return false;
		}
		return true;
	}
	
}
