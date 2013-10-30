package com.gawdl3y.bukkit.VoteSiteManager;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class for VoteSiteManager
 * @author Schuyler Cebulskie
 * @version 1.1.1
 */
public class VoteSiteManager extends JavaPlugin {
	private Logger log;
	private List<String> sites;
	
	@Override
	public void onEnable() {
		// Retrieve sites list
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		sites = getConfig().getStringList("sites");
		
		// Do some stuff
		log = getLogger();
		getCommand("vsm").setExecutor(new VSMCommandExecutor(this, sites));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("vote")) {
			// Vote command
			if(sites.size() > 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("vote-message")));
				
				if(getConfig().getInt("vote-site-delay") <= 0) {
					// Instantly show all sites
					for(String site : sites) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("vote-site-prefix") + site));
				} else {
					// Show one site at a time
					getServer().getScheduler().scheduleSyncDelayedTask(this, new VoteSiteOutput(this, sender, sites, false), getConfig().getInt("vote-site-delay"));
				}
			} else {
				sender.sendMessage("§6There are no voting sites.");
			}
			
			return true;
		} else if(cmd.getName().equalsIgnoreCase("broadcastvote")) {
			// Broadcast vote command
			if(sites.size() > 0) {
				getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast-message")));
				
				if(getConfig().getInt("broadcast-site-delay") <= 0) {
					// Instantly show all sites
					for(String site : sites) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcast-site-prefix") + site));
				} else {
					// Show one site at a time
					getServer().getScheduler().scheduleSyncDelayedTask(this, new VoteSiteOutput(this, sender, sites, true), getConfig().getInt("broadcast-site-delay"));
				}
			} else {
				sender.sendMessage("§cThere are no voting sites.");
			}
			
			return true;
		}
		
		return false;
	}

    /**
     * Saves the sites list to the config
     */
	public void saveSites() {
		getConfig().set("sites", sites);
		saveConfig();
    }

    /**
     * Reload the plugin
     */
	public void reload() {
		log.info("Reloading configuration...");
		
		// Reload config
		reloadConfig();
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		// Retrieve sites list
		sites = getConfig().getStringList("sites");
		
		log.info("Configuration reloaded.");
	}
}