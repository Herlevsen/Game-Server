package server.statuscode;

import server.Server;

public class OpponentLeftGameHandler implements StatusCodeHandler {
	private Server server;

	public OpponentLeftGameHandler(Server server) {
		this.server = server;
	}

	@Override
	public String getStatusCode() {
		return "001";
	}

	@Override
	public void handle(String body) {

		server.sendToCurrent(body, "001|" + body);

	}
}
