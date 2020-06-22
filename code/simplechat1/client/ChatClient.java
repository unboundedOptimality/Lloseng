// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	
	String loginId = null;
	

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		openConnection();
	}

	// Instance methods ************************************************

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	
	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromClientUI(String message) {
		try {
			message = message.replaceAll("\\s+","");	// removing spaces and tabs
			if (message.startsWith("#")) {
				if (message.equals("#quit")) {
					quit();
				} else if (message.equals("#logoff")) {
					closeConnection();
				} else if (message.startsWith("#sethost<")
						&& message.endsWith(">")
						&& !(message.equals("#sethost<>"))) {
					setHost(message.substring(9, message.length() - 1));
				} else if (message.startsWith("#setport<")
						&& message.endsWith(">")
						&& !(message.equals("#setport<>"))) {
					try {
						int parsedParameter = Integer.parseInt(message.substring(9, message.length() - 1));
						setPort(parsedParameter);
					} catch (Exception e) {
						System.out.println("Invalid port format: " + message.substring(9, message.length() - 1)
								+ "\nInteger value expected.");
					}
				} else if (message.equals("#login")) {
					if (isConnected()) {
						System.out.println("Invalid command: #login\nClient already connected to server.");
					} else {
						openConnection();
					}
				} else if (message.equals("#gethost")) {
					System.out.println(getHost());
				} else if (message.equals("#getport")) {
					System.out.println(getPort());
				} else {
					System.out.println("Invalid command: " + message
							+ "\n\n"
							+ "   #quit : terminate client\n"
							+ "   #logoff : logoff\n"
							+ "   #sethost<host> : set host\n"
							+ "   #setport <port> : set port\n"
							+ "   #login : login\n"
							+ "   #gethost : display host name\n"
							+ "   #getport : display port number");
				}
			} else {
				sendToServer(message);
			}
		} catch (IOException e) {
			clientUI.display("Could not send message to server.  Terminating client.");
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
//End of ChatClient class
