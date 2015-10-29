package unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import server.Client;
import server.game.TicTacToe;

import static org.mockito.Mockito.*;

public class TicTacToeTest {

	TicTacToe game;
	private Client client2;
	private Client client1;

	@Before
	public void setUp() throws Exception {

		client1 = Mockito.mock(Client.class);
		client2 = Mockito.mock(Client.class);

		when(client1.getClientId()).thenReturn("id1");
		when(client2.getClientId()).thenReturn("id2");
		
		game = new TicTacToe(client1, client2);

	}

	@Test
	public void testCanNotMoveTwiceInARow() throws Exception {

		game.move("0", client1);
		game.move("1", client1);

		verify(client1, times(2)).send("011|id2,0=1.1=0.2=0.3=0.4=0.5=0.6=0.7=0.8=0");
	}

	@Test
	public void testSendsGameStartAtConstruction() throws Exception {

		verify(client1).send("011|id1,0=0.1=0.2=0.3=0.4=0.5=0.6=0.7=0.8=0");
		verify(client2).send("011|id1,0=0.1=0.2=0.3=0.4=0.5=0.6=0.7=0.8=0");

	}

	@Test
	public void testCanNotMakeInvalidMove() throws Exception {

		game.move("0", client1);
		game.move("0", client2);

		verify(client1, times(2)).send("011|id2,0=1.1=0.2=0.3=0.4=0.5=0.6=0.7=0.8=0");
		verify(client2, times(2)).send("011|id2,0=1.1=0.2=0.3=0.4=0.5=0.6=0.7=0.8=0");
	}


}
