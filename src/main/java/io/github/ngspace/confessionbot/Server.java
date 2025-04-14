package io.github.ngspace.confessionbot;

import java.io.File;
import java.io.IOException;

import io.github.ngspace.nnupref.NNUPref;
import io.github.ngspace.nnupref.NNUPrefBuilder;

public record Server(NNUPref msgIds, NNUPref bannedUserhashes, NNUPref serversettings) {
	
	public static Server createOrLoadServer(long serverID) throws IOException {
		
		File serverdir = new File("servers/"+serverID);
		boolean create = !serverdir.exists();
		if (create) serverdir.mkdirs();
		
		NNUPrefBuilder serversettings = new NNUPrefBuilder(new File("servers/"+serverID+"/settings.npref"));
		serversettings.setAutoCreateMissingFile(true);
		serversettings.setAutoSave(true);
		serversettings.setSafeSave(true);
		NNUPref settings = serversettings.build();
		if (create) settings.set("logmessages", true);
		
		NNUPrefBuilder msg_userids = new NNUPrefBuilder(new File("servers/"+serverID+"/msg_userids.npref"));
		msg_userids.setAutoCreateMissingFile(true);
		msg_userids.setAutoSave(true);
		msg_userids.setSafeSave(true);

		NNUPrefBuilder builder = new NNUPrefBuilder(new File("servers/"+serverID+"/banned.npref"));
		builder.setAutoCreateMissingFile(true);
		builder.setAutoSave(true);
		builder.setSafeSave(true);
		
		return new Server(msg_userids.build(), builder.build(), settings);
	}
	
	public boolean logmessages() {
		if (!serversettings.containsKey("logmessages")) return false;
		return serversettings.getBoolean("logmessages");
	}
}
