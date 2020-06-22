import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import client.ChatClient;
import common.ChatIF;

public class ServerConsole implements ChatIF {

	final public static int DEFAULT_PORT = 5555;

	EchoServer server;
	
	public EchoServer getServer() {
		return server;
	}

	
	public ServerConsole(int port) {
		this.server = new EchoServer(port, this);
	}


	public void accept() {
		try {
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message;
			
			while (true) {
				message = fromConsole.readLine();
				
				String trimmedMessage = message.replaceAll("\\s+","");	// removing spaces and tabs
				if (trimmedMessage.startsWith("#")) {
					processCommand(message, trimmedMessage);
					continue;
				}
				server.sendToAllClients("SERVER MSG> " + message);
				System.out.println("SERVER MSG> " + message);
			}
		} catch (Exception e) {
			System.out.println("Unexpected error while reading from input to server's console!");
			System.out.println(e);
		}
	}
	
	private void processCommand(String originalMessage, String trimmedMessage) throws IOException {
		if (trimmedMessage.equals("#quit")) {
			System.exit(0);
		} else if (trimmedMessage.equals("#stop")) {
			server.stopListening();
		} else if (trimmedMessage.equals("#close")) {
			server.close();
		} else if (trimmedMessage.startsWith("setport<")
				&& trimmedMessage.endsWith(">")
				&& !(trimmedMessage.contentEquals("#setport<>"))) {
			try {
				int parsedParameter = Integer.parseInt(trimmedMessage.substring(9, trimmedMessage.length() - 1));
				server.setPort(parsedParameter);
			} catch (Exception e) {
				System.out.println("Invalid port format: " + trimmedMessage.substring(9, trimmedMessage.length() - 1)
						+ "\nInteger value expected.");
			}
		} else if (trimmedMessage.equals("#start")) {
			if (server.isListening()) {
				System.out.println("Invalid command: #start\nServer is already listening.");
			} else {
				server.listen();
			}
		} else if (trimmedMessage.equals("#getport")) {
			System.out.println(server.getPort());
		} else {
			System.out.println("Invalid command: " + originalMessage
					+ "\n\n"
					+ "   #quit : quit server\n"
					+ "   #stop : stop listening for new clients\n"
					+ "   #close : stop listening for new clients and disconnect all existing clients\n"
					+ "   #setport <port> : set port\n"
					+ "   #start : start listening for new clients\n"
					+ "   #getport : display port number");
		}
	}

	@Override
	public void display(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (Throwable t) {
			port = DEFAULT_PORT;
		}
		ServerConsole serverConsole = new ServerConsole(port);
		EchoServer server = serverConsole.getServer();
		try {
			server.listen();
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
		serverConsole.accept();
	}

}
