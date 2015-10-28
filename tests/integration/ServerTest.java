package integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import server.IdGenerator;
import server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ServerTest {

	private IdGenerator idGenerator;
	private Server server;
	private Thread serverThread;

	@Before
	public void setUp() throws Exception {
		idGenerator = Mockito.mock(IdGenerator.class);
		server = new Server(idGenerator);
		serverThread = new Thread(server);
		serverThread.start();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testCanConnectOneClient() throws Exception {

		Mockito.when(idGenerator.generate()).thenReturn("a0b257f5-5b0b-40bc-9793-aa324b7e3ab2");

		Socket socket = new Socket("localhost", 9898);

		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

		outputStream.writeObject("000|Hans");
		String input = (String) inputStream.readObject();

		outputStream.close();
		inputStream.close();
		server.stop();

		Assert.assertEquals(input, "008|a0b257f5-5b0b-40bc-9793-aa324b7e3ab2,Hans");

	}

	@Test
	public void testCanConnectMultipleClients() throws Exception {

		when(idGenerator.generate()).
				thenReturn("a0b257f5-5b0b-40bc-9793-aa324b7e3ab2");

		TestRunnable client1 = makeClient("Hans");
		TestRunnable client2 = makeClient("John");
		TestRunnable client3 = makeClient("Joe");

		new Thread(client1).start();
		new Thread(client2).start();
		new Thread(client3).start();

		Thread.sleep(200);

		Assert.assertEquals("008|a0b257f5-5b0b-40bc-9793-aa324b7e3ab2,Hans", client1.getOutput());
		Assert.assertEquals("008|a0b257f5-5b0b-40bc-9793-aa324b7e3ab2,John", client2.getOutput());
		Assert.assertEquals("008|a0b257f5-5b0b-40bc-9793-aa324b7e3ab2,Joe", client3.getOutput());

		server.stop();

	}

	@Test
	public void testCanInvitePersonToGame() throws Exception {

		Mockito.when(idGenerator.generate()).
				thenReturn("id1").
				thenReturn("id2");

		String input = "";

		Socket client1 = new Socket("localhost", 9898);
		Socket client2 = new Socket("localhost", 9898);

		ObjectOutputStream oSClient1 = new ObjectOutputStream(client1.getOutputStream());
		ObjectInputStream iSClient1 = new ObjectInputStream(client1.getInputStream());


		ObjectOutputStream oSClient2 = new ObjectOutputStream(client2.getOutputStream());

		oSClient1.writeObject("000|Hans");
		// Connection established
		input = (String) iSClient1.readObject();
		String id1 = input.split("\\|")[1].split(",")[0];


		oSClient2.writeObject("000|John");

		ObjectInputStream iSClient2 = new ObjectInputStream(client2.getInputStream());
		// Connection established
		input = (String) iSClient2.readObject();

		// Find player 2 id
		String id2 = input.split("\\|")[1].split(",")[0];

		// Player list
		input = (String) iSClient2.readObject();

		// Send invite
		oSClient1.writeObject("005|" + id2 + "," + "tictactoe" + "," + id1);

		input = (String) iSClient2.readObject();

		assertEquals("005|id2,tictactoe,id1", input);

		server.stop();
	}

	@Test
	public void testCanAcceptGameInvitation() throws Exception {

		Mockito.when(idGenerator.generate()).
				thenReturn("id1").
				thenReturn("id2");

		String input = "";

		Socket client1 = new Socket("localhost", 9898);
		Socket client2 = new Socket("localhost", 9898);

		ObjectOutputStream o1 = new ObjectOutputStream(client1.getOutputStream());
		ObjectInputStream i1 = new ObjectInputStream(client1.getInputStream());

		// Client 1 connection
		o1.writeObject("000|Hans");
		input = (String) i1.readObject();
		String id1 = input.split("\\|")[1].split(",")[0];

		ObjectOutputStream o2 = new ObjectOutputStream(client2.getOutputStream());
		ObjectInputStream i2 = new ObjectInputStream(client2.getInputStream());

		o2.writeObject("000|John");

		// Connection established
		input = (String) i2.readObject();

		// Read player list - One for own connection and one for other connection
		i1.readObject();
		i1.readObject();

		// Find player 2 id
		String id2 = input.split("\\|")[1].split(",")[0];

		// Player list
		input = (String) i2.readObject();

		// Send invite
		o1.writeObject("005|" + id2 + "," + "tictactoe" + "," + id1);

		input = (String) i2.readObject();
		String[] splitted = input.split("\\|");
		String invitationReceiverId = splitted[1].split(",")[0];
		String invitationSenderId = splitted[1].split(",")[2];
		String gameType = splitted[1].split(",")[1];


		o2.writeObject("009|" + invitationSenderId + "," + invitationReceiverId + "," + gameType);

		input = (String) i1.readObject();

		assertEquals("009|" + invitationSenderId + "," + invitationReceiverId + "," + gameType, input);

		server.stop();

	}

	private TestRunnable makeClient(String name) throws Exception {

		return new TestRunnable() {
			@Override
			public void run() {

				try {
					Socket socket = new Socket("localhost", 9898);

					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

					outputStream.writeObject("000|" + name);
					output = (String) inputStream.readObject();
				} catch (Exception e) {

				}
			}
		};

	}

	private class TestRunnable implements Runnable {

		String output = "";

		@Override
		public void run() {



		}

		public String getOutput() {
			return output;
		}
	}
}
