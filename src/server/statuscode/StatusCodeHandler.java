package server.statuscode;

import server.Client;
import server.Server;

public interface StatusCodeHandler {

	public String getStatusCode();

	public void handle(String body);

}
