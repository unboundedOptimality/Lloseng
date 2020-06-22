// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
	
	
	// Class variables *************************************************
	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	
	// Instance variables *************************************************
	
	private ChatIF clientUI = null;

	// Constructors ****************************************************
	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);
	}
	
	public EchoServer(int port, ChatIF clientUI) {
		super(port);
		this.clientUI = clientUI;
	}
	
	

	// Instance methods ************************************************
	
	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		
		if (msg instanceof String) {
			String msg_casted2String = msg.toString();
			String msg_casted2String_whiteSpaceTrimmed = msg_casted2String.replaceAll("\\s+","");
			if (msg_casted2String_whiteSpaceTrimmed.startsWith("#")) {
				if (msg_casted2String_whiteSpaceTrimmed.startsWith("#login<")
						&& msg_casted2String_whiteSpaceTrimmed.endsWith(">")) {
					int beginningIndex = msg_casted2String.indexOf('<') + 1;
					int endingIndex = msg_casted2String.lastIndexOf('>') - 1;
					String inputId = msg_casted2String.substring(beginningIndex, endingIndex + 1);
					
					
					if (client.getInfo("login_id") == null) {
						client.setInfo("login_id", inputId);
					} else {
						try {
							client.sendToClient("Invalid command: Client is already logged in.");
						} catch (IOException e) {}
						try {
							
							client.close();
							
						} catch (IOException e) {
							System.out.printf("Unexpected exception or possible abuse by client ID %s\n",
									client.getInfo("login_id").toString());
						}
					}
					
				}
			} else {
				
				String senderBy_String = client.getInfo("login_id").toString();
				String concatenated_msg_senderBy = senderBy_String + ": " + msg.toString();
				
				System.out.println("Message received: " + msg + " from " + client);
				this.sendToAllClients(concatenated_msg_senderBy);
				
				
				
			}
		}

	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	// Class methods ***************************************************
	
	/**
	 * This method is responsible for the creation of the server instance (there is
	 * no UI in this phase).
	 *
	 * @param args[0] The port number to listen on. Defaults to 5555 if no argument
	 *                is entered.
	 */
	public static void main(String[] args) {

		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(args[0]); // Get port from command line
		} catch (Throwable t) {
			port = DEFAULT_PORT; // Set port to 5555
		}

		EchoServer sv = new EchoServer(port);

		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}
//End of EchoServer class
