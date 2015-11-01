package server.statuscode;

import server.Client;
import server.Server;

public class MoveHandler implements StatusCodeHandler {
	private Server server;
	private Client client;

	public MoveHandler(Server server, Client client) {
		this.server = server;
		this.client = client;
	}

	@Override
	public String getStatusCode() {
		return "004";
	}

	@Override
	public void handle(String body) {

		if(!client.inGame()) return;

		// Pass the data on to the game, and let the game handle the logic and sending of game state
		client.move(body);

	}
}
