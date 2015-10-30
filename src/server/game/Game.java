package server.game;

import server.Client;

public interface Game {

	/**
	 * Client sends data explaining the move they want to make in the game
	 * This method is responsible for interpreting the data, validating if the move is possible,
	 * and finally sending the game state back to the players
	 *
	 * @param data The data for making the move
	 * @param client The Client who sent the move
	 */
	public void move(String data, Client client);

    /**
     * Start the game and send the initial state
     */
    public void startGame();
}
