package server.statuscode;

import server.Client;
import server.Server;
import server.statuscode.handlers.DeclineInvitationHandler;

public class StatusCodeHandlerFactory {

	private Server server;

	/**
	 * @param server Server object
	 */
	public StatusCodeHandlerFactory(Server server) {
		this.server = server;
	}

	/**
	 * @param statusCode The status code
	 * @param client The client that sent the status code
	 * @return An appropriate StatusCodeHandler instance
	 * @throws StatusCodeHandlerNotFoundException
	 */
	public StatusCodeHandler build(String statusCode, Client client) throws StatusCodeHandlerNotFoundException {

		switch(statusCode) {

			case "001":

				return new OpponentLeftGameHandler(server);

			case "004":

				return new MoveHandler(server, (Client) client);

			case "005": // Game invitation

				return new SendInvitationHandler(server);

			case "009": // Accept game invitation

				return new AcceptInvitationHandler(server);

			case "010": // Decline game invitation

				return new DeclineInvitationHandler(server);

		}

		throw new StatusCodeHandlerNotFoundException(statusCode);

	}
}