package server.statuscode;

public class StatusCodeHandlerNotFoundException extends Exception {

	public StatusCodeHandlerNotFoundException(String statusCode) {
		super("Could not find appropriate StatusCodeHandler for status code: " + statusCode);
	}
}
