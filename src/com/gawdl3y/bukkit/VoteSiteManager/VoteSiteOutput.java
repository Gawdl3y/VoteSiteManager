package com.gawdl3y.bukkit.VoteSiteManager;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * A runnable to output the voting sites
 * @author Schuyler Cebulskie
 * @version 1.1.1
 */
public class VoteSiteOutput implements Runnable {
	private final VoteSiteManager plugin;
	private final CommandSender sender;
	private final List<String> sites;
	private final boolean broadcast;
	private final int delay;
	private final String prefix;
	
	private int currentSite = 0;

    /**
     * Constructor
     * @param plugin    The plugin instance
     * @param sender    The sender of the command
     * @param sites     The list of voting sites
     * @param broadcast Whether or not this should display to all players
     */
	public VoteSiteOutput(VoteSiteManager plugin, CommandSender sender, List<String> sites, boolean broadcast) {
		this.plugin = plugin;
		this.sender = sender;
		this.sites = sites;
		this.broadcast = broadcast;
		
		this.delay = plugin.getConfig().getInt(broadcast ? "broadcast-site-delay" : "vote-site-delay");
		this.prefix = plugin.getConfig().getString(broadcast ? "broadcast-site-prefix" : "vote-site-prefix");
	}

	@Override
	public void run() {
        // Send the message
		if(broadcast) {
			plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + sites.get(currentSite)));
        } else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + sites.get(currentSite)));
        }
		
		currentSite++;

        // Schedule another run
		if(currentSite < sites.size()) plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
	}
	
}
