package io.github.ngspace.confessionbot;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class ConfessionBot {
	
	public Map<Long, Server> servers = new HashMap<Long, Server>();
	
	public ConfessionBot(String TOKEN) {
		
        JDA jda = JDABuilder.createLight(TOKEN, EnumSet.noneOf(GatewayIntent.class))
			.addEventListeners(new CommandListener(this))
			.build();
		
		CommandListUpdateAction commands = jda.updateCommands();
		
		commands.addCommands(Commands.slash("confess", "Confess your sins").addOption(OptionType.STRING, "content",
				"What would you like to confess?", true));
		
		var permissions = DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS);
		
		commands.addCommands(
			Commands.slash("bansinner", "Ban the sinners").setDefaultPermissions(permissions).addOption(
					OptionType.STRING, "messageid", "What is the sinner's message?", true),
			Commands.slash("pardonsinner", "Pardon the sinners").setDefaultPermissions(permissions).addOption(
					OptionType.STRING, "messageid", "What is the sinner's message?", true),
			Commands.slash("setmessagelogging", "Should give messages one-way uids or not (Banning will not be possible"
					+ " if disabled)")
					.addOption(OptionType.BOOLEAN, "messagelogging","true or false", true));
		
		commands.queue();
	}
	
	
	
	public String getHash(User user) {
		return DigestUtils.sha256Hex(String.valueOf(user.getIdLong()));
	}
	
	
	
	public Server getServer(long serverID) throws IOException {
		Server s = servers.get(serverID);
		if (s==null) servers.put(serverID, (s=Server.createOrLoadServer(serverID)));
		return s;
	}
}
