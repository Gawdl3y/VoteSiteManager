package com.gawdl3y.bukkit.VoteSiteManager;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The /vsm command executor
 * @author Schuyler Cebulskie
 * @version 1.1.1
 */
public class VSMCommandExecutor implements CommandExecutor {
	private final VoteSiteManager plugin;
	private List<String> sites;

    /**
     * A list of config settings that can be changed with /vsm config
     */
	private static final List<String> configOptions = Arrays.asList("vote-site-delay", "broadcast-site-delay", "vote-message", "broadcast-message", "vote-site-prefix", "broadcast-site-prefix");

    /**
     * Constructor
     * @param plugin The plugin instance
     * @param sites  The list of voting sites
     */
	public VSMCommandExecutor(VoteSiteManager plugin, List<String> sites) {
		this.plugin = plugin;
		this.sites = sites;
	}

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("list")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.list")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				// List sites with indices
				if(sites.size() > 0) {
					for(int i = 0; i < sites.size(); i++) sender.sendMessage(i + ": " + sites.get(i));
				} else {
					sender.sendMessage("§cThere are no voting sites.");
				}
				
				return true;
			} else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("new")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.add")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				if(args.length >= 2) {
					// Validate the URL
					try {
						new URI(args[1]);
						if(!(args[1].contains("http"))) throw new Exception();
					} catch(Exception e) {
						sender.sendMessage("§cURL is invalid! Make sure to include http://");
						return false;
					}
					
					// Add the site and save
					sites.add(args[1]);
					plugin.saveSites();
					
					sender.sendMessage("§2Voting site \"" + args[1] + "\" added.");
					return true;
				} else {
					sender.sendMessage("§cToo few arguments!");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.remove")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				if(args.length >= 2) {
					// Remove the site
					String site = null;
					try {
						// Try using index
						site = sites.remove(Integer.parseInt(args[1]));
					} catch(Exception e) {
						try {
							// Try using value
							if(!sites.remove(args[1])) site = ""; else site = args[1];
						} catch(IndexOutOfBoundsException e2) {}
					}
					
					// Save
					plugin.saveSites();
					sender.sendMessage(site != null ? "§2Voting site \"" + site + "\" removed." : "§cCould not find site \"" + args[0] + "\".");
					return true;
				} else {
					sender.sendMessage("§cToo few arguments!");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("clear")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.clear")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				// Clear sites
				if(args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
					// Confirmed; clear all of the sites
					sites.clear();
					plugin.saveSites();
				} else {
					// Confirmation message
					sender.sendMessage("§6Are you sure you want to remove all of the voting sites? This cannot be undone. Use \"/vsm clear confirm\" to confirm.");
				}
				
				return true;
			} else if(args[0].equalsIgnoreCase("config")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.config")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				if(args.length >= 2) {
					// Make sure it can be changed with the command
					if(configOptions.contains(args[1])) {
						String current = plugin.getConfig().get(args[1]).toString();
						
						if(args.length == 2) {
							// Output current value
							sender.sendMessage("Current value for configuration setting \"" + args[1] + "\" is \"" + current + "\".");
							return true;
						} else if(args.length >= 3) {
							try {
								// Try it as an integer
								plugin.getConfig().set(args[1], Integer.parseInt(args[2]));
							} catch(NumberFormatException e) {
								plugin.getConfig().set(args[1], args[2]);
							}
							
							// Save
							plugin.saveConfig();
							sender.sendMessage("Configuration setting \"" + args[1] + "\" has been changed from \"" + current + "\" to \"" + args[2] +"\".");
							return true;
						}
					} else {
						// Build a string of the possible settings
						String settings = "";
						for(String setting : configOptions) {
							settings += setting + " ";
						}
						
						sender.sendMessage("§cInvalid configuration setting \"" + args[1] + "\". Must be one of: " + settings);
						return true;
					}
				} else {
					sender.sendMessage("§cToo few arguments!");
					return false;
				}
			} else if(args[0].equalsIgnoreCase("reload")) {
				// Check permission
				if(!sender.hasPermission("VoteSiteManager.manage.reload")) {
					sender.sendMessage("§cYou don't have permission to do that!");
					return true;
				}
				
				// Reload!
				plugin.reload();
				sender.sendMessage("§2VoteSiteManager configuration reloaded.");
				return true;
			}
		} else {
			return false;
		}
		
		return false;
	}
}
