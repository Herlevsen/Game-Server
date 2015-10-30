package server.game;

import server.Client;

public class TicTacToe implements Game {

	private final Client player1;

	private final Client player2;

	private Client turn;

	private int[] gameBoard = {0, 0, 0, 0, 0, 0, 0, 0, 0};

	/**
	 * TicTacToe constructor
	 * Decide who has the first turn
	 *
	 * @param player1 1st player
	 * @param player2 2nd player
	 */
	public TicTacToe(Client player1, Client player2) {
		this.player1 = player1;
		this.player2 = player2;
		turn = player1;

		// Send start state to clients
		sendGameState();
	}

	public Client getPlayer1() {
		return player1;
	}

	public Client getPlayer2() {
		return player2;
	}

	@Override
	public void move(String data, Client client) {
		int position = -1;
		try {
			position = Integer.parseInt(data);
		} catch (NumberFormatException e) {
			// Send original state
			sendGameState();

			return;
		}

		// Check if move is valid
		if(!isPlayersTurn(client) || !isValidMove(position)) {
			// Send same state back
			sendGameState();

			return;
		}

		// Make move
		gameBoard[position] = client == player1 ? 1 : 2;

		// Check if won and if won send lose and won status codes - draw


		// Change turn
		turn = turn == player1 ? player2 : player1;

		// Send state response to both players
		sendGameState();


	}

	private void sendGameState() {

		StringBuilder stringBuilder = new StringBuilder(100);

		stringBuilder.append("011|");
		stringBuilder.append(turn.getClientId());
		stringBuilder.append(",");

		int length = gameBoard.length;

		for(int i = 0; i < length; i++) {

			stringBuilder.append(i);
			stringBuilder.append("=");
			stringBuilder.append(gameBoard[i]);

			if( i != length - 1 ) stringBuilder.append(".");

		}

		String response = stringBuilder.toString();

		player1.send(response);
		player2.send(response);

	}

	/**
	 * Move is valid if position is between 0-8, and not taken
	 *
	 * @param position The board position
	 * @return true if move is valid
	 */
	private boolean isValidMove(int position) {
		if( !(position >= 0) || !(position <= 8) ) return false;

		return gameBoard[position] == 0;
	}

	private boolean isPlayersTurn(Client client) {
		return client == turn;
	}
}
