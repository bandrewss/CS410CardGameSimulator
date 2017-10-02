// Ben Andrews, Isaiah Paulson
// Server for CS410 Card game
// 9-27-17

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Server extends JFrame {
	final private int PLAYER_0 = 0;
	final private int PLAYER_1 = 1;
	final private int PLAYER_2 = 2;
	final private int MAX_PLAYERS = 3;
	private PlayerStruct players[] = new PlayerStruct[MAX_PLAYERS];
	
	private int currentTurn;
	private Deck deck;
	
	private enum TurnCycle {
		FIRST_PLAY,
		SECOND_PLAY,
		LAST_PLAY
	} private TurnCycle turnCycle;

	private DatagramSocket socket;
	private InetAddress address;

	private JTextArea display;

	/*
	 * Sets up socket and builds simple GUI
	 */
	public Server(int port, String addr) {
		try {
			address = InetAddress.getByName(addr);
			socket = new DatagramSocket(port, address);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		display = new JTextArea();
		display.setEditable(false);
		add(new JScrollPane(display), BorderLayout.CENTER);
		setSize(400, 300);
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	/*
	 * Starts the game logic.
	 */
	public void go() {
		setupGame();
		
		startGame();
	}

	/*
	 * Waits for three players to connect sets up the game.
	 */
	private void setupGame() {
		deck = new Deck();
		deck.shuffle();
		
		currentTurn = PLAYER_0;
		
		int playerCount = 0;
		DatagramPacket packet;
		while(playerCount < 3)
		{
			packet = waitForPacket();
			
			if(processHello(packet, playerCount)) {
				++playerCount;
			}
		}
		
		dealCards();
		
		appendToDisplay("\nStarting Game:");
	}
	
	/*
	 * Deals each player 17 cards (entire deck -1)
	 */
	private boolean dealCards() {
		int playerNumber;
		Card card;
		
		for(int i = 0; i < 51; ++i) {
			playerNumber = i % 3;
			card = deck.pop();
			sendCardToPlayer(players[playerNumber], playerNumber, card);
			players[playerNumber].hand.recieveCard(card.getSuit(), card.getNum());
		}
		
		return false;
	}
	
	/*
	 * After every player has a hand, starts the gameplay.
	 */
	private void startGame() {
		turnCycle = TurnCycle.FIRST_PLAY;
		
		for(int round = 0; round < 17; ++round) {
			playATrick();
			
			// set the next turn to the winner of the trick
			currentTurn = decideWinnerOfTrick();	
		}
			
		
		// proclaim winner
		// check to see if rematch is wanted.
	}
	
	/*
	 * Plays one trick of the game
	 */
	private void playATrick() {
		Card card;
		char trickSuit = ' ';
		DatagramPacket packet = null;
		boolean trickOver = false;
		boolean legalPlay = true;
		
		while(!trickOver) {
			if(legalPlay) {
				sendTurnToPlayer(players[currentTurn], currentTurn);
				while(!ackPlayerAcceptTurn(players[currentTurn], currentTurn));
			}
			
			while( (card = processCardPlay(players[currentTurn], currentTurn, (packet = waitForPacket()) )) == null);
				
			if( (legalPlay = legalCardPlay(players[currentTurn], card, trickSuit)) ) {
				sendClearToPlayCard(players[currentTurn], card);
				
				players[currentTurn].trickCard = card;
				
				if(turnCycle == TurnCycle.FIRST_PLAY) {
					trickSuit = card.getSuit();
				}
				else if(turnCycle == TurnCycle.LAST_PLAY) {
					trickOver = true;
				}
				
				turnCycle = TurnCycle.values()[(turnCycle.ordinal() +1) %TurnCycle.values().length];
				currentTurn = (currentTurn +1) %3;
			}
			else {
				sendRejectTo(packet.getAddress(), packet.getPort());
			}
		}
		
		appendToDisplay(String.format("Trick done"));
	}
	
	/*
	 * Checks if the card was a valid play.
	 *   Removes the card from the players hand
	 * Parameter: The player that attempted to play a card,
	 *   the card that needs to be validated
	 *   the suit of the current trick
	 */
	private boolean legalCardPlay(PlayerStruct player, Card card, char trickSuit) {
		boolean legal = false;
		
		if(player.hand.containsCard(card)) {
			switch (turnCycle) {
				case FIRST_PLAY:
					legal = true;
					player.canWin = true;
					break;
				case SECOND_PLAY:
				case LAST_PLAY:
					if(card.getSuit() == trickSuit) {
						legal = true;
						player.canWin = true;
						break;
					}
					else {
						if(!player.hand.containsSuit(trickSuit)) {
							legal = true;
							player.canWin = false;
						}
					}
					
			}
		}
		
		if(legal) { 
			player.trickCard = card;
		}
		
		return legal;
	}
	
	/*
	 * 
	 */
	private int decideWinnerOfTrick() {
		int winner = -1;
		
		if( players[PLAYER_0].trickCard.getNum() > players[PLAYER_1].trickCard.getNum() ) {
			winner = players[PLAYER_0].canWin ? PLAYER_0 : PLAYER_1;
		}
		else {
			winner = players[PLAYER_1].canWin ? PLAYER_1 : PLAYER_0;
		}
		
		if( players[PLAYER_2].trickCard.getNum() > players[winner].trickCard.getNum() ) {
			winner = players[PLAYER_2].canWin ? PLAYER_2 : winner;
		}
		else {
			winner = players[winner].canWin ? winner : PLAYER_2;
		}
		
		players[winner].tricks.add(new Trick(players[PLAYER_0].trickCard, 
												   players[PLAYER_1].trickCard, 
												   players[PLAYER_2].trickCard));
		
		proclaimWinnerOfTrick(winner);
		appendToDisplay(String.format("The winner is: %d\nPlayer%d has a score of: %d\n", winner, winner, players[winner].tricks.size()));
	
		return winner;
	}
	
	/*
	 * Waits for a packet to come in and returns it.
	 */
	private DatagramPacket waitForPacket() {
		DatagramPacket packet = null;
		
		try {
			byte[] data = new byte[128];
			packet = new DatagramPacket(data, data.length);

			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	/*
	 * Processes a player hello packet. 
	 * Makes sure the player sent proper hello message,
	 * and that they have a unique IP address.
	 * Parameters: a datagram packet, and a player index
	 * 
	 */
	private boolean processHello(DatagramPacket packet, int n) {
		String message = new String(packet.getData(), 0, packet.getLength());
		InetAddress address = packet.getAddress();
		
		boolean unique = message.equals("Hello Ready to Start");
		
		for(int i = 0; i < n && unique; ++i) {
			unique = players[i].address != address;
		}
		
		if(unique) {
			players[n] = new PlayerStruct(address, packet.getPort());
			sendHelloToPlayer(players[n], n);
		}
		else {
			sendRejectToPlayer(packet);
		}
		
		return unique;
	}
	
	/*
	 * Let the player know they have joined the game.
	 * Parameters: A player, and their player number
	 */
	private void sendHelloToPlayer(PlayerStruct player, int n) {
		byte[] buffer = String.format("Hello, you are player: %1d", n).getBytes();

		DatagramPacket greeter = new DatagramPacket(buffer, buffer.length, player.address, player.port);

		try {
			socket.send(greeter);
			appendToDisplay(String.format("Player%d connected.", n));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Let the connecting player know they can't play
	 * Parameters: The packet the rejected player sent
	 */
	private void sendRejectToPlayer(DatagramPacket packet) {
		byte[] buffer = String.format("You cannot play").getBytes();

		DatagramPacket reject = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());

		try {
			socket.send(reject);
			appendToDisplay(String.format("Rejected Player"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	/*
	 * Process a card play by player, return the card.
	 */
	private Card processCardPlay(PlayerStruct player, int n, DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());
		InetAddress address = packet.getAddress();
		Card card = null;
		
		// validate that the card came from the correct player
		try {
			if(address.equals(player.address)) {
				char suit = message.charAt(0);
				int number = Integer.parseInt(message.substring(1).trim());
				
				card = new Card(suit, number);
			}
		}
		catch (NumberFormatException e) {
			appendToDisplay("Processed illegal card");
		}
		
		return card;
	}
	
	/*
	 * Tells a player that they're clear to play their card.
	 *   Removes the card from that players hand on the server's side.
	 * Parameters: The player to send to, and their card.
	 */
	private void sendClearToPlayCard(PlayerStruct player, Card card) {
		byte[] buffer = String.format("%c%d", card.getSuit(), card.getNum()).getBytes();

		DatagramPacket message = new DatagramPacket(buffer, buffer.length, player.address, player.port);
		
		player.hand.removeCard(card);

		try {
			socket.send(message);
			appendToDisplay(String.format("Player%d played %c%d", currentTurn, card.getSuit(), card.getNum()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Tells a player the packet they just sent wasn't a valid card.
	 * Parameter: The player, their number
	 */
	private void sendRejectTo(InetAddress address, int port) {
		byte[] buffer = String.format("Not a valid play").getBytes();

		DatagramPacket message = new DatagramPacket(buffer, buffer.length, address, port);

		try {
			socket.send(message);
			appendToDisplay(String.format("Invalid play processed"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Send a player a card.
	 * Parameters: A player, their player number, and a card
	 */
	private void sendCardToPlayer(PlayerStruct player, int n, Card card) {
		byte[] buffer = String.format("Card: %c%d", card.getSuit(), card.getNum()).getBytes();

		DatagramPacket cardPacket = new DatagramPacket(buffer, buffer.length, player.address, player.port);
		
		try {
			socket.send(cardPacket);
			appendToDisplay(String.format("Sent player%d Card: %c%s", n, card.getSuit(), card.getNum()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Send a player their turn
	 * Parameters: A player, their player number
	 */
	private void sendTurnToPlayer(PlayerStruct player, int n) {
		byte[] buffer = String.format("Your turn").getBytes();

		DatagramPacket cardPacket = new DatagramPacket(buffer, buffer.length, player.address, player.port);
		
		try {
			socket.send(cardPacket);
			appendToDisplay(String.format("Player%d's turn", n));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Acks that a player received their turn
	 * Parameters: A player, their player number
	 */
	private boolean ackPlayerAcceptTurn(PlayerStruct player, int n) {
		DatagramPacket packet = waitForPacket();
		
		String message = new String(packet.getData(), 0, packet.getLength());
		InetAddress address = packet.getAddress();
		
		boolean correct = message.equals("My turn") && address.equals(player.address);
				
		if(correct) {
			appendToDisplay(String.format("Player%d accepted turn", currentTurn));
		}
		
		return correct;
	}
	
	/*
	 * 
	 */
	private void proclaimWinnerOfTrick(int n) {
		byte[] buffer = String.format("The winner is player%d", n).getBytes();

		
		for(PlayerStruct player:players) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.address, player.port);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Add a string to the GUI console.
	 * Parameters: A string to append.
	 */
	private void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////
	// old methods kept around for reference, ignore them //
	////////////////////////////////////////////////////////
	
	// in hindsight these four methods should be generalized into one or two...
	
	private void processPacket(DatagramPacket packet) {
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		String message = new String(packet.getData(), 0, packet.getLength());

		// boolean holds if the packet was a 'hello' packet
		boolean isHello = message.substring(0, 4).equals("$~$`");

		if (isHello) {
			// the clients name is the first word after the '$-$`' indicator
			System.out.printf("%s\n", message);

			int newClient = buildNewClient(message.substring(5), packet.getAddress(), packet.getPort());
			if (newClient >= 0)
				sendHelloToClient(newClient);
			else
				sendRejectPacket(address, port);
		} else {
			// the intended receiver is the first word of the message, the rest
			// of the message is the message to send
			String destName = message.substring(0, message.indexOf(' '));
			String data = message.substring(message.indexOf(' '));

			boolean clientExists = false;
			for (PlayerStruct client : players) {
				if (client != null && client.name.equals(destName)) {
					// get the name of the sender
					String name = getNameGivenIP(packet.getAddress());

					sendMessageToClient(client, name, data);
					clientExists = true;

					break;
				}
			}

			if (!clientExists)
				sendClientNotFoundPacket(address, port, destName);
		}
	}

	private void sendHelloToClient(int cliNum) {
		byte[] hello = String.format("Hello %s, you are client number: %d\n", players[cliNum].name, cliNum).getBytes();

		DatagramPacket greeter = new DatagramPacket(hello, hello.length, players[cliNum].address, players[cliNum].port);

		try {
			socket.send(greeter);
			appendToDisplay(String.format("Client %d is %s.\n", cliNum, players[cliNum].name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendRejectPacket(InetAddress addr, int port) {
		byte[] reject = "You cannot be my client.\n".getBytes();

		DatagramPacket rejector = new DatagramPacket(reject, reject.length, addr, port);

		try {
			socket.send(rejector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendClientNotFoundPacket(InetAddress addr, int port, String name) {
		byte[] reject = new String(String.format("Server: I do not know who %s is.\n", name)).getBytes();

		DatagramPacket rejector = new DatagramPacket(reject, reject.length, addr, port);

		try {
			socket.send(rejector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessageToClient(PlayerStruct cli, String sender, String message) {
		byte[] messanger = new String(String.format("%s: %s\n", sender, message)).getBytes();

		DatagramPacket greeter = new DatagramPacket(messanger, messanger.length, cli.address, cli.port);

		try {
			socket.send(greeter);
			appendToDisplay(String.format("Sent a message from %s to %s\n", sender, cli.name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// returns -1 on failure, else the index of the client in the array
		private int buildNewClient(String name, InetAddress addr, int port) {
			int madeClient = -1;
			for (int i = 0; i < MAX_PLAYERS; ++i) {
				if (players[i] == null) {
					players[i] = new PlayerStruct(addr, port);
					madeClient = i;
					break;
				} else if (name == players[i].name || addr.equals(players[i].address))
					break; // duplicate client
			}

			return madeClient;
		}

		// returns the name of a client given an IP
		private String getNameGivenIP(InetAddress addr) {
			String name = null;

			for (PlayerStruct client : players) {
				if (client.address.equals(addr)) {
					name = client.name;
					break;
				}
			}

			return name;
		}
}





