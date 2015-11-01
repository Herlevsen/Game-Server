package server.statuscode.handlers;

import server.Server;
import server.statuscode.StatusCodeHandler;

public class DeclineInvitationHandler implements StatusCodeHandler {

	private Server server;

	public DeclineInvitationHandler(Server server) {
		this.server = server;
	}

	@Override
	public String getStatusCode() {
		return "010";
	}

	@Override
	public void handle(String body) {

		server.sendTo(body ,"010|" + body);

	}
}
