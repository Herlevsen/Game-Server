package server.statuscode;

import server.Server;
import server.game.GameType;

public class SendInvitationHandler implements StatusCodeHandler {

	private Server server;

	public SendInvitationHandler(Server server) {
		this.server = server;
	}

	@Override
	public String getStatusCode() {
		return "005";
	}

	@Override
	public void handle(String body) {

		String[] splitted = body.split(",");
		String receiverId = splitted[0];
		String game = splitted[1];

		// Check if game is valid and break if not
		boolean gameIsValid = false;

		for(GameType gameType : GameType.values()) {

			if(gameType.toString().equalsIgnoreCase(game)) {
				gameIsValid = true;
				break;
			}

		}

		if(!gameIsValid) return;

		// Send invitation
		server.sendTo(receiverId, getStatusCode() + "|" + body);

	}

}
