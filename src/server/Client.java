package server;

import server.game.Game;
import server.game.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Client extends Thread {

	private String name;

	private String id;

	private ObjectInputStream inputStream;

	private ObjectOutputStream outputStream;

	private Server server;

	private String[] gameTypes = {"tictactoe"};

	Game game;

	/**
	 * @param name Name of the player
	 * @param id Unique id
	 * @param inputStream Input stream for incoming communication
	 * @param outputStream Output stream for outgoing communication
	 */
	public Client(String name, String id, ObjectInputStream inputStream, ObjectOutputStream outputStream, Server server) {
		this.name = name;
		this.id = id;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.server = server;
	}

	/**
	 * Listens for input from a remote client and acts accordingly
	 */
	@Override
	public void run() {

		while(true) {

			try {
				String input = (String) inputStream.readObject();

				String[] inputArr = input.split("\\|");

				String statusCode = inputArr[0];
				String body = inputArr[1];
				String[] splitted;

				switch(statusCode) {

					case "001":
						break;
					case "002":
						break;
					case "003":
						break;
					case "004":
						if(!inGame()) break;

						// Pass the data on to the game, and let the game handle the logic and sending of game state
						game.move(body, this);

						break;
					case "005": // Game invitation
                        splitted = body.split(",");
						String receiverId = splitted[0];
						String game = splitted[1];

						// Check if game is valid and break if not
						boolean gameIsValid = false;

						for(String gameType : gameTypes) {
							if(gameType.equals(game)) {
								gameIsValid = true;
								break;
							}
						}

						if(!gameIsValid) break;

						// Send invitation
						server.sendTo(receiverId, input);

						break;
					case "009": // Accept game invitation
						splitted = body.split(",");
						String invitationSenderId = splitted[0];
						String invitationReceiverId = splitted[1];
						String gameType = splitted[2];

                        // Try to make the game
						server.makeGame(invitationSenderId, invitationReceiverId, gameType, input);

						break;
					case "010": // Decline game invitation

						server.sendTo(body ,"010|" + body);

						break;

				}


			} catch (IOException e) {
				server.removeClient(this);
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

	}

	private boolean inGame() {
		return game != null;
	}

	public String getNickname() {
		return name;
	}

	public String getClientId() {
		return id;
	}

	public void send(String data) {

		try {
			outputStream.writeObject(data);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setGame(Game game) {
		this.game = game;
	}
}
