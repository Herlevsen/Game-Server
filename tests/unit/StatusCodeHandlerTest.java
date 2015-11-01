package unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import server.Client;
import server.Server;
import server.statuscode.*;
import server.statuscode.handlers.DeclineInvitationHandler;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.*;

public class StatusCodeHandlerTest {

	Server server;

	@Before
	public void setUp() throws Exception {
		server = Mockito.mock(Server.class);
	}

	@Test
	public void testFactory() throws Exception {

		// Construct the factory
		StatusCodeHandlerFactory factory = new StatusCodeHandlerFactory(server);

		Object obj1 = factory.build("001", mock(Client.class));
		Object obj2 = factory.build("004", mock(Client.class));
		Object obj3 = factory.build("005", mock(Client.class));
		Object obj4 = factory.build("009", mock(Client.class));
		Object obj5 = factory.build("010", mock(Client.class));

		assertThat(obj1, instanceOf(OpponentLeftGameHandler.class));
		assertThat(obj2, instanceOf(MoveHandler.class));
		assertThat(obj3, instanceOf(SendInvitationHandler.class));
		assertThat(obj4, instanceOf(AcceptInvitationHandler.class));
		assertThat(obj5, instanceOf(DeclineInvitationHandler.class));
	}

	@Test
	public void testInviteStatusCode() throws Exception {

		SendInvitationHandler sendInvitationHandler = new SendInvitationHandler(server);
		sendInvitationHandler.handle("id2,tictactoe,id1");

		verify(server, times(1)).sendTo("id2", "005|id2,tictactoe,id1");

	}

	@Test
	public void testDoesNotSendInviteWhenGameTypeIsInvalid() throws Exception {

		SendInvitationHandler sendInvitationHandler = new SendInvitationHandler(server);
		sendInvitationHandler.handle("id2,nonexistentgame,id1");

		verify(server, never()).sendTo("", "");

	}
}
