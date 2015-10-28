package server.game;

import server.Client;

public class TicTacToe implements Game {

	private final Client player1;
	private final Client player2;
	private Client turn;

	public TicTacToe(Client player1, Client player2) {
		this.player1 = player1;
		this.player2 = player2;
		turn = player1;
	}

	@Override
	public void move(String data) {



	}

}
