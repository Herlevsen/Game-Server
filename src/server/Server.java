package server;

import server.game.Game;
import server.game.TicTacToe;
import server.statuscode.StatusCodeHandlerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {


	/**
	 * List of clients that are connected, but not yet playing a game
	 */
	List<Client> connectedClients = new Vector<>();

	/**
	 * List of games currently being played
	 */
	List<Game> currentGames = new Vector<>();

	private IdGenerator idGenerator;
	private boolean running;
	private ServerSocket serverSocket;

	public static void main(String[] args) {

		Server server = new Server(new IdGenerator());
		server.run();

	}

	public Server(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * Listen for new connecting sockets
	 * Read and interpret connection data
	 * Create new Client object, with username and an unique id
	 * Add client to list of connected users
	 * Send connection successful message to client
	 */
	public void run() {

		running = true;

		try {
			serverSocket = new ServerSocket(9898);
		} catch (IOException e) {
			System.out.println("Server could not start");
			return;
		}

		while(running) {

			try {
				Socket socket = serverSocket.accept();

				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

				String[] input = ((String) inputStream.readObject()).split("\\|");

				String username = input[1];
				String id = idGenerator.generate();

				if(nameTaken(username)){
					outputStream.writeObject("007|");
					continue;
				}

				Client client = new Client(username, id, inputStream, outputStream, this, new StatusCodeHandlerFactory(this));
				connectedClients.add(client);

				client.start();
				client.send("008|" + client.getClientId() + "," + client.getNickname());

				sendClientList();

			} catch (IOException e) {
				System.out.println("IOException - socket may be closed");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

	}

	private boolean nameTaken(String username)
    {
		for(Client c : connectedClients) {
			if(c.getNickname().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Send list of clients, to all clients that is connected but not currently playing
	 */
	public void sendClientList() {

		StringBuilder stringBuilder = new StringBuilder(800);
		stringBuilder.append("006|");
		Iterator<Client> iterator = connectedClients.iterator();

		while (iterator.hasNext()) {

			Client client = iterator.next();

			stringBuilder.append(client.getClientId() + "." + client.getNickname());
			if (iterator.hasNext()) stringBuilder.append(",");
		}

		for (Client client : connectedClients) {

			client.send(stringBuilder.toString());

		}

	}

	/**
	 * Remove client from list. Call this when client disconnects
	 *
	 * @param client The client object to remove
	 */
	public void removeClient(Client client) {

		connectedClients.remove(client);
		sendClientList();

	}

	/**
	 * Send data to a specific user
	 *
	 * @param id   Id of the user to send the data to
	 * @param data Data to send to the user
	 */
	public void sendTo(String id, String data) {

		// Find the client
		for (Client client : connectedClients) {

			if (client.getClientId().equals(id)) {
				client.send(data);
				break;
			}

		}

	}

	/**
	 * Send data to a specific user
	 *
	 * @param id   Id of the user to send the data to
	 * @param data Data to send to the user
	 */
	public void sendToCurrent(String id, String data) {

		Client client = null;
		TicTacToe currentgame = null;

		for (Game game : currentGames) {
			if (game instanceof TicTacToe) {
				TicTacToe specificGame = (TicTacToe) game;
				if (specificGame.getPlayer1().getClientId().equals(id)) {

					currentgame = specificGame;

					client = specificGame.getPlayer2();
					connectedClients.add(client);
					sendClientList();

					client = specificGame.getPlayer1();



					break;
				} else if (specificGame.getPlayer2().getClientId().equals(id)) {

					currentgame = specificGame;

					client = specificGame.getPlayer1();
					connectedClients.add(client);
					sendClientList();

					client = specificGame.getPlayer2();
					break;


				}

			}

		}

		client.send(data);
		connectedClients.add(client);

		currentGames.remove(currentgame);

		sendClientList();
	}

	public void stop() {

		running = false;

		try {
			serverSocket.close();
		} catch (IOException e) {

		}

	}

	public List<Game> getCurrentGames() {
		return currentGames;
	}

	/**
	 * @param invitationSenderId   Client who send the invitation
	 * @param invitationReceiverId Client who received the invitation
	 * @param gameType             Game to be played
	 * @param input
	 * @return Return true if game was created successfully
	 */
	public void makeGame(String invitationSenderId, String invitationReceiverId, String gameType, String input) {

		Client player1 = null;
		Client player2 = null;
		Game game = null;

		// If it's TicTacToe:
		if (gameType.equals("tictactoe")) {

			// Find players by their id
			for (Client client : connectedClients) {

				if (player1 == null) {

					if (!client.getClientId().equals(invitationReceiverId) && !client.getClientId().equals(invitationSenderId))
						continue;

					player1 = client;

				} else {

					if (!client.getClientId().equals(invitationReceiverId) && !client.getClientId().equals(invitationSenderId))
						continue;

					player2 = client;

					break;
				}

			}

			if(player1 == null || player2 == null) return;

			game = new TicTacToe(player1, player2);
		}

		// Give players a reference to the game
		player1.setGame(game);
		player2.setGame(game);


		// Notify sender that the invitation was accepted
		sendTo(invitationSenderId, input);

        // Start game
        game.startGame();

		// Remove from connected clients
		connectedClients.remove(player1);
		connectedClients.remove(player2);

		// Add game
		currentGames.add(game);
	}
}
