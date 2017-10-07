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
		GET_HELLO, GET_HAND, AWAIT_TURN, MY_TURN, AWAIT_TRICK_COMPLETION, GAME_ENDED, AWAIT_REMATCH
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
	
	/*
	 * Plays a card that matches the given string.
	 * Parameters: a string representation of a card.
	 */
	public void playCard(String s) {
		Card c = new Card(s);
		lastCardPlayed = c; 
		
		sendPacket(s);
	}
	
	/*
	 * Sends to the server that a rematch is desired by the player
	 */
	public void requestRematch() {
		sendPacket("rematch");
	}
	
	public void requestExit() {
		sendPacket("exit");
	}

	/*
	 * Waits for packet to come in and processes it.
	 */
	private void waitForPackets() {
		DatagramPacket receiver = null;
		
		do {
			try {
				byte[] data = new byte[128];
				receiver = new DatagramPacket(data, data.length);

				socket.receive(receiver);
			} catch (IOException e) {
				System.out.printf("%s\n", e);
				e.printStackTrace();
			}
		} while(receiver != null && processPacket(receiver));
		// while the packet is not null, 
		//  and the packet did not contain an exit request

	}

	/*
	 * Processes packet, treats it differently based on current gamestate. In
	 * charge of changing gamestate when appropriate. 
	 * Parameters: a datagram packet
	 * Returns: true unless the sever said to exit the game
	 */
	private boolean processPacket(DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());
		boolean exiting = false;
		
		if(message.equals("Quit requested, exiting game")) {
			gui.appendToDisplay(message);
			gui.removeAllButtons();
			
			exiting = true;
		}
		else // else implicitly controls switch	
		switch (gameState) {
		// getting first reply from the server
		case GET_HELLO:
			// if the packet is a hello reply from the server
			if (message.substring(0, 22).equals("Hello, you are Player:")) {
				gui.appendToDisplay(message);
				gui.appendToDisplay("Please wait while other players connect\n");
				gui.setTitle(message.substring(15));
				gameState = GameState.GET_HAND;
			}
			break;
		// getting players hand from the server, one card at a time
		case GET_HAND:
			// get suit and number from message, put it in the hand
			hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));

			if (hand.isFull()) {
				gui.appendToDisplay("Starting Game:");
				hand.sortHand();
				gui.setButtons(hand);				
				gui.appendToDisplay("Wait for your turn");
				gameState = GameState.AWAIT_TURN;
			}

			break;
		// wait until the server says its the players turn
		case AWAIT_TURN:;
			if (message.equals("Your turn")) {
				gui.appendToDisplay(message);
				// ack turn
				sendPacket("My turn");
				gameState = GameState.MY_TURN;
			}
			// if the game is over
			//substring(8, 20)
			else if(message.contains("won the game")) {
				gui.appendToDisplay(message);
				gameState = GameState.GAME_ENDED;
			}
			// if the message contains another players play
			else if(message.substring(0, 6).equals("Player")) {
				gui.appendToDisplay(message);
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
			// most likely an error message, display it
			else {
				gui.appendToDisplay(message);
			}
			break;
		// wait for the other players to complete their trick
		case AWAIT_TRICK_COMPLETION:
			// if the message is declaring a winner
			if (message.length() >= 20 && message.substring(0, 20).equals("The winner is player")) {
				gui.appendToDisplay("Trick ended,");
				gui.appendToDisplay(String.format("%s\n", message));
				gameState = GameState.AWAIT_TURN;
			}
			else if(message.contains("played")) {
				gui.appendToDisplay(message);
			}
			
			break;
		// game is over
		case GAME_ENDED:
			if(message.equals("Would you like a rematch?")) {
				gui.appendToDisplay(String.format("\n%s", message));
				gui.removeAllButtons();
				hand.clearHand();
				gui.setEndGameButtons();
				gameState = GameState.AWAIT_REMATCH;
			}
			break;
		case AWAIT_REMATCH:
			if(message.equals("Rematch underway")) {
				gui.appendToDisplay("Starting Rematch:");
				gameState = GameState.GET_HAND;
			}
		}
		
		return !exiting;
	}
	
	
	// allows multiple clients be run from one class
	public void run() {
		go();
	}
}
