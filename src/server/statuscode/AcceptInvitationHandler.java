package server.statuscode;

import server.Server;

public class AcceptInvitationHandler implements StatusCodeHandler{

	private Server server;

	public AcceptInvitationHandler(Server server) {
		this.server = server;
	}

	@Override
	public String getStatusCode() {
		return "009";
	}

	@Override
	public void handle(String body) {

		String[] splitted = body.split(",");
		String invitationSenderId = splitted[0];
		String invitationReceiverId = splitted[1];
		String gameType = splitted[2];

		// Try to make the game
		server.makeGame(invitationSenderId, invitationReceiverId, gameType, getStatusCode() + "|" + body);

		// Send the list of clients
		server.sendClientList();
	}
}
