// Ben Andrews, Isaiah Paulson
// Client for CS410 Card game
// 9-27-17

// XXX when real GUI is implemented all 'appendToDisplay's can be changed to prints
//      *for debugging purposes

// XXX currently messages are hand typed, all actual GUI will have to do is return
//      proper string when button is pressed

// XXX some methods will have to be changed from private to public for GUI to 
//      interact with them

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Client extends JFrame implements Runnable {

	private InetAddress serverAddress;
	private InetAddress myAddress;
	private int port = 0;
	private String name;

	private DatagramSocket socket;

	private JTextArea display;
	private JTextField messageArea;
	
	enum GameState {
		GET_HELLO,
		GET_HAND,
		AWAIT_TURN,
		MY_TURN,
		AWAIT_TRICK_COMPLETION
	} private GameState gameState;
	
	private Hand hand;
	private Card lastCardPlayed; // keeps track of the last played card for verification
	

	/*
	 * Sets up socket and builds simple GUI
	 * 
	 * XXX have client create real GUI
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

		display = new JTextArea();
		display.setEditable(false);

		messageArea = new JTextField("Enter your message here");
		messageArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendPacket(event.getActionCommand());
			}
		});

		add(messageArea, BorderLayout.SOUTH);
		add(new JScrollPane(display), BorderLayout.CENTER);
		setSize(400, 300);
		setTitle(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);
		
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
	 * Sends datagram packet containing given string to server
	 * Parameters: a string
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
	 * Waits for packet to come in and processes it.
	 */
	private void waitForPackets() {
		for (;;) {
			try {
				byte[] data = new byte[128];
				DatagramPacket receiver = new DatagramPacket(data, data.length);

				socket.receive(receiver);

				processPacket(receiver);
			} catch (IOException e) {
				System.out.printf("%s\n", e);
				e.printStackTrace();
			}
		}

	}
	
	/*
	 * Processes packet, treats it differently based on current gamestate.
	 * In charge of changing gamestate when appropriate.
	 * Parameters: a datagram packet
	 */
	private void processPacket(DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());
		
		switch(gameState) {
			case GET_HELLO:
				gameState = GameState.GET_HAND;
				break;
			case GET_HAND:
				// get suit and number from message
				hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
				
				if(hand.isFull()) {
					gameState = GameState.AWAIT_TURN;
				}
				
				break;
			case AWAIT_TURN:
				if(message.equals("Your turn")) {
					// ack turn
					sendPacket("My turn");
					gameState = GameState.MY_TURN;
				}
				break;
			case MY_TURN:
				if(message.equals(hand.showCard(lastCardPlayed))) {
					 //play the card
					sendPacket(hand.showCard(lastCardPlayed));
					hand.playCard(lastCardPlayed);
					gameState = GameState.AWAIT_TRICK_COMPLETION;
				}
				break;
			case AWAIT_TRICK_COMPLETION:
				appendToDisplay(message.substring(0, 17));
				if(message.substring(0, 17).equals("The winner is player")) {
					gameState = GameState.AWAIT_TURN;
					appendToDisplay("matched");
				}
				break;
				
		}
		
		appendToDisplay(message);
	}


	/*
	 * Adds given string to dummy gui
	 */
	private void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
	
	// allows multiple clients be run from one class
	public void run() {
		go();
	}
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////
	// old methods kept around for reference, ignore them //
	////////////////////////////////////////////////////////
	
	private void sendPacketTo(String message) {
		byte[] data = message.getBytes();

		DatagramPacket sender = new DatagramPacket(data, data.length, serverAddress, port);
		
		lastCardPlayed = new Card(message.charAt(0), Integer.parseInt(message.substring(1).trim()));

		try {
			socket.send(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}






