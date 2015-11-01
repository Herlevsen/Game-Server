package server;

import server.game.Game;
import server.statuscode.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client extends Thread {

	private String name;

	private String id;

	private ObjectInputStream inputStream;

	private ObjectOutputStream outputStream;

	private Server server;
	private StatusCodeHandlerFactory statusCodeHandlerFactory;

	private String[] gameTypes = {"tictactoe"};

	Game game;

	/**
	 * @param name Name of the player
	 * @param id Unique id
	 * @param inputStream Input stream for incoming communication
	 * @param outputStream Output stream for outgoing communication
	 * @param statusCodeHandlerFactory Factory to construct the StatusCodeHandlers
	 */
	public Client(String name, String id, ObjectInputStream inputStream, ObjectOutputStream outputStream, Server server, StatusCodeHandlerFactory statusCodeHandlerFactory) {
		this.name = name;
		this.id = id;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.server = server;
		this.statusCodeHandlerFactory = statusCodeHandlerFactory;
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

				StatusCodeHandler statusCodeHandler = statusCodeHandlerFactory.build(statusCode, this);
				statusCodeHandler.handle(body);


			} catch (IOException e) {
				server.removeClient(this);
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (StatusCodeHandlerNotFoundException e) {
				System.out.println(e.getMessage());
			}

		}

	}

	public boolean inGame() {
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

	public void move(String body) {
		game.move(body, this);
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
