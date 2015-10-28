package client;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

	private boolean running = true;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	public static void main(String[] args) {

		Client client = new Client();
		client.run();

	}

	public void run() {

		try {

			Socket socket = new Socket("localhost", 9898);

			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());

			outputStream.writeObject("000|Hans");
			String input = (String) inputStream.readObject();

			System.out.println(input);

			while(running) {
				String input2 = (String) inputStream.readObject();

				System.out.println(input2);
			}

		} catch (IOException e) {
			System.out.println("Done");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		running = false;
		try {
			outputStream.close();
		} catch (IOException e) {

		}

		try {
			inputStream.close();
		} catch (IOException e) {

		}
	}
}