package io.github.ngspace.confessionbot;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import io.github.ngspace.nnupref.IncompatibleTypeException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
	
	public static final Map<Long, Server> servers = new HashMap<Long, Server>();
	
	
	@Override public synchronized void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Server server = null;
		try {
			server = getServer(event.getGuild().getIdLong());
		} catch (IOException e) {
			event.reply("Error occured").setEphemeral(true).queue();
			event.getChannel().sendMessage("Failed to load or create server").queue();
			e.printStackTrace();
			return;
		}
		
		
		switch (event.getName()) {
			case "confess": {
				String userhash = getHash(event.getUser());
				
				if (server.logmessages()&&isUserBanned(userhash,server)) {
					event.reply("Your confessions are too weird, you've been banned.").setEphemeral(true).queue();
					break;
				}
				int messageId = getMessageNum(userhash,server);
				
				event.reply("Submitting confession").setEphemeral(true).queue();
				
				EmbedBuilder embed = new EmbedBuilder();
				embed.setAuthor("An anonymous user confesses:");
				embed.setDescription(event.getOptionsByName("content").get(0).getAsString());
				embed.setColor(Color.cyan);
				if (messageId!=-1) embed.setFooter("Message id: "+messageId);
				event.getChannel().sendMessageEmbeds(embed.build()).queue();
				break;
			}
			case "bansinner": {
				try {
					server.bannedUserhashes().set(server.msgIds().getString(event.getOption("messageid").getAsString()),
							true);
				} catch (NullPointerException | IncompatibleTypeException | IOException e) {
					e.printStackTrace();
				}
				event.reply("Banned sinner").setEphemeral(true).queue();
				break;
			}
			case "pardonsinner": {
				try {
					server.bannedUserhashes().remove(server.msgIds().getString(event.getOption("messageid")
							.getAsString()));
				} catch (NullPointerException | IncompatibleTypeException | IOException e) {e.printStackTrace();}
				
				event.reply("Pardoned sinner").setEphemeral(true).queue();
				break;
			}
			case "setmessagelogging": {
				try {
					server.serversettings().set("logmessages", event.getOption("messagelogging").getAsBoolean());
				} catch (NullPointerException | IncompatibleTypeException | IOException e) {e.printStackTrace();}
				
				event.reply("Set messagelogging for this server to " + event.getOption("messagelogging").getAsBoolean()).setEphemeral(true).queue();
				break;
			}
			default:
				event.reply("Unknown command").setEphemeral(true).queue();
		}
	}
	
	
	
	public String getHash(User user) {
		return DigestUtils.sha256Hex(String.valueOf(user.getIdLong()));
	}
	
	
	
	public Server getServer(long serverID) throws IOException {
		Server s = servers.get(serverID);
		if (s==null) servers.put(serverID, (s=Server.createOrLoadServer(serverID)));
		return s;
	}
	
	
	
	public int getMessageNum(String userhash, Server server) {
		if (!server.logmessages()) return -1;
		int id = server.msgIds().size()+1;
		try {
			server.msgIds().set(String.valueOf(id), userhash);
		} catch (NullPointerException | IncompatibleTypeException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to attach messageid", e);
		}
		return id;
	}
	
	
	
	public boolean isUserBanned(String userhash, Server server) {
		return server.bannedUserhashes().containsKey(userhash) && server.bannedUserhashes().getBoolean(userhash);
	}
}
