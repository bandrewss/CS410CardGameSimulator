// Ben Andrews, Isaiah Paulson
// Client for CS410 Card game
// 9-27-17



// XXX currently messages are hand typed, all actual GUI will have to do is return
//      proper string when button is pressed

// XXX some methods will have to be changed from private to public for GUI to 
//      interact with them


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client implements Runnable {

	private InetAddress serverAddress;
	private InetAddress myAddress;
	private int port = 0;
	private String name;

	private DatagramSocket socket;

	enum GameState {
		GET_HELLO, GET_HAND, AWAIT_TURN, MY_TURN, AWAIT_TRICK_COMPLETION, GAME_ENDED
	}

	private GameState gameState;

	private Hand hand;
	private Card lastCardPlayed; // keeps track of the last played card for verification
	
	GUI gui;

	/*
	 * Sets up socket and uses the GUI class to build the client GUI
	 */
	public Client(String n, String myAddr, String serverAddr, int p) {
		name = n;
		port = p;
		try {
			serverAddress = InetAddress.getByName(serverAddr);
			myAddress = InetAddress.getByName(myAddr);

			socket = new DatagramSocket(port, myAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		gui = new GUI(n, this);
		
		gameState = GameState.GET_HELLO;
		hand = new Hand();
	}
	

	/*
	 * Starts the client game logic.
	 */
	public void go() {
		sendPacket("Hello Ready to Start"); // makes initial contact with server

		waitForPackets();
	}

	/*
	 * Sends datagram packet containing given string to server Parameters: a
	 * string
	 */
	private void sendPacket(String s) {
		byte[] message = s.getBytes();

		DatagramPacket packet = new DatagramPacket(message, message.length, serverAddress, port);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void playCard(String s) {
		Card c = new Card(s);
		lastCardPlayed = c; 
		
		sendPacket(s);
	}

	/*
	 * Waits for packet to come in and processes it.
	 */
	private void waitForPackets() {
		for (;;) {
			try {
				byte[] data = new byte[128];
				DatagramPacket receiver = new DatagramPacket(data, data.length);

				socket.receive(receiver);
				
				// process the packet that just came in
				processPacket(receiver);
			} catch (IOException e) {
				System.out.printf("%s\n", e);
				e.printStackTrace();
			}
		}

	}

	/*
	 * Processes packet, treats it differently based on current gamestate. In
	 * charge of changing gamestate when appropriate. 
	 * Parameters: a datagram packet
	 */
	private void processPacket(DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());

		switch (gameState) {
		// getting first reply from the server
		case GET_HELLO:
			if (message.substring(0, 22).equals("Hello, you are player:")) {
				gameState = GameState.GET_HAND;
			}
			break;
		// getting players hand from the server, one card at a time
		case GET_HAND:
			// get suit and number from message
			hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
			String cardName=message.substring(6);

			if (hand.isFull()) {
				hand.sortHand();
				gui.setButtons(hand);				
				gui.appendToDisplay("Wait for your turn");
				gameState = GameState.AWAIT_TURN;
			}

			break;
		// wait until the server says its the players turn
		case AWAIT_TURN:
			if (message.equals("Your turn")) {
				gui.appendToDisplay(message);
				// ack turn
				sendPacket("My turn");
				gameState = GameState.MY_TURN;
			}
			// if the message contains another players play
			else if(message.substring(0, 6).equals("Player")) {
				gui.appendToDisplay(message);
			}
			// if the game is over
			else if(message.substring(8).equals("won the game")) {
				gui.appendToDisplay(message);
				gameState = GameState.GAME_ENDED;
			}
			break;
		// is players turn
		case MY_TURN:
			// if the servers reply matches the card the player requested to play
			if (message.equals(lastCardPlayed.toString())) {
				// play the card
				hand.removeCard(new Card(message));
				gui.removeCardButton(message);
				gui.appendToDisplay(String.format("You played: %s", message));
				gui.appendToDisplay("Wait for the trick to complete");
				gameState = GameState.AWAIT_TRICK_COMPLETION;
			}
			else {
				gui.appendToDisplay(message);
			}
			break;
		// wait for the other players to complete their trick
		case AWAIT_TRICK_COMPLETION:
			if (message.length() >= 20 && message.substring(0, 20).equals("The winner is player")) {
				gui.appendToDisplay("Trick ended,");
				gui.appendToDisplay(String.format("%s\n", message));
				gameState = GameState.AWAIT_TURN;
			}
			
			break;
		// game is over
		case GAME_ENDED:
			break;
		}
	}
	
	
	// allows multiple clients be run from one class
	public void run() {
		go();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////
	// old methods kept around for reference, ignore them //
	////////////////////////////////////////////////////////
	
	/*
	 * Adds given string to dummy gui
	 *
	private void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
	*/

	private void sendPacketTo(String message) {
		byte[] data = message.getBytes();

		DatagramPacket sender = new DatagramPacket(data, data.length, serverAddress, port);

		try {
			socket.send(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
