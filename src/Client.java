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
import javax.*;

@SuppressWarnings("serial")
public class Client extends JFrame implements Runnable {

	private InetAddress serverAddress;
	private InetAddress myAddress;
	private int port = 0;
	private String name;

	private DatagramSocket socket;

	private JTextArea display;
	private JButton[] cardButtons = new JButton[17];
	private JScrollPane jScrollPane1;

	enum GameState {
		GET_HELLO, GET_HAND, AWAIT_TURN, MY_TURN, AWAIT_TRICK_COMPLETION
	}

	private GameState gameState;

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
		jScrollPane1 = new javax.swing.JScrollPane();
		display = new javax.swing.JTextArea();
		display.setEditable(false);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jScrollPane1 = new JScrollPane();
		display = new JTextArea();

		for (int i = 0; i < cardButtons.length; ++i) {
			cardButtons[i] = new JButton();
			cardButtons[i].setText(String.format("%d", i));
		}

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		display.setColumns(20);
		display.setRows(5);
		jScrollPane1.setViewportView(display);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup().addGap(57, 57, 57).addComponent(
												jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277,
												javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(layout
												.createSequentialGroup().addContainerGap().addComponent(cardButtons[0])
												.addGap(18, 18, 18)
												.addComponent(cardButtons[1])
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(cardButtons[2]))
										.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
														.createSequentialGroup().addComponent(cardButtons[6])
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(cardButtons[7])
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(cardButtons[8]))
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(cardButtons[3])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[4]).addGap(18, 18, 18)
																.addComponent(cardButtons[5]))))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addContainerGap().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(cardButtons[12])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[13])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(cardButtons[14]))
														.addGroup(layout.createSequentialGroup()
																.addComponent(cardButtons[9])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(cardButtons[10])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[11])))))
								.addGroup(
										layout.createSequentialGroup().addGap(62, 62, 62).addComponent(cardButtons[15])
												.addGap(30, 30, 30).addComponent(cardButtons[16])))
						.addContainerGap(66, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[0]).addComponent(cardButtons[1]).addComponent(cardButtons[2]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[3]).addComponent(cardButtons[4]).addComponent(cardButtons[5]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[6]).addComponent(cardButtons[7]).addComponent(cardButtons[8]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[9]).addComponent(cardButtons[10]).addComponent(cardButtons[11]))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[12]).addComponent(cardButtons[13]).addComponent(cardButtons[14]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[15]).addComponent(cardButtons[16]))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
		setSize(400, 500);
		setTitle(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);

		// Start GUI
		/*
		 * display = new JTextArea(); display.setEditable(false);
		 * 
		 * messageArea = new JTextField("Enter your message here");
		 * messageArea.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent event) {
		 * sendPacket(event.getActionCommand()); } });
		 * 
		 * add(messageArea, BorderLayout.SOUTH); add(new JScrollPane(display),
		 * BorderLayout.CENTER); setSize(400, 300); setTitle(name);
		 * setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setVisible(true);
		 * setAlwaysOnTop(true);
		 */
		// End GUI
		
		gameState = GameState.GET_HELLO;
		hand = new Hand();
	}// End Client Constructor

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
	 * Processes packet, treats it differently based on current gamestate. In
	 * charge of changing gamestate when appropriate. Parameters: a datagram
	 * packet
	 */
	private void processPacket(DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());

		switch (gameState) {
		case GET_HELLO:
			gameState = GameState.GET_HAND;
			break;
		case GET_HAND:
			// get suit and number from message
			hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
			cardButtons[hand.getHandSize() - 1].setText(message);

			if (hand.isFull()) {
				appendToDisplay("Wait for your turn");
				gameState = GameState.AWAIT_TURN;
			}

			break;
		case AWAIT_TURN:
			if (message.equals("Your turn")) {
				appendToDisplay(message);
				// ack turn
				sendPacket("My turn");
				// appendToDisplay(message);
				gameState = GameState.MY_TURN;
			}
			break;
		case MY_TURN:
			if (message.equals(lastCardPlayed.toString())) {
				// play the card

				sendPacket(lastCardPlayed.toString());
				hand.removeCard(lastCardPlayed);
				appendToDisplay("Wait for the trick to complete");
				gameState = GameState.AWAIT_TRICK_COMPLETION;
			}
			break;
		case AWAIT_TRICK_COMPLETION:
			appendToDisplay(message.substring(0, 17));
			if (message.substring(0, 17).equals("The winner is player")) {
				gameState = GameState.AWAIT_TURN;
				appendToDisplay("matched");
			}
			break;

		}
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
