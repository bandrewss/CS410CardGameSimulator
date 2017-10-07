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
	final private int ROUNDS = 17;
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
		
		appendToDisplay(String.format("Server IP: %s", addr));
	}
	
	/*
	 * Starts the game logic.
	 */
	public void go() {
		boolean playing = true;
		
		setupGame();
		
		while(playing) {
			playing = startGame();
			
			if(playing) {
				setupRematch();
			}
			else {
				proclaimGameExit();
			}
		}
		
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
	 * Sets up a rematch game.
	 */
	private void setupRematch() {
		proclaimRematch();
		
		deck.clearDeck();
		deck.buildDeck();
		deck.shuffle();
		
		dealCards();
		appendToDisplay("\nStarting Rematch");
	}
	
	/*
	 * Deals each player 17 cards (entire deck -1)
	 */
	private void dealCards() {
		int playerNumber;
		Card card;
		
		for(int i = 0; i < 51; ++i) {
			playerNumber = i % 3;
			card = deck.pop();
			sendCardToPlayer(players[playerNumber], playerNumber, card);
			players[playerNumber].hand.recieveCard(card.getSuit(), card.getNum());
		}
	}
	
	/*
	 * After every player has a hand, starts the gameplay.
	 */
	private boolean startGame() {
		turnCycle = TurnCycle.FIRST_PLAY;
		
		// dictate playing of a trick
		for(int round = 0; round < ROUNDS; ++round) {
			playATrick();
			
			// set the next turn to the winner of the trick
			currentTurn = decideWinnerOfTrick();	
		}
			
		
		proclaimWinnerOfGame(decideWinnerOfGame());
		
		clearGameData();
		
		askForRematch();
		
		return waitRematchRequests();
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
				proclaimCardPlay(card);
				
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
	 * Decide which player won the trick.
	 * Returns: the index of the winner of the trick.
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
	 * Decide who one the game based on player score.
	 * Returns: the index of the player who won.
	 */
	private int decideWinnerOfGame() {
		int winner = PLAYER_0;
		boolean foundWinner = true;
		int tiePlayerA = -1;
		int tiePlayerB = -1;
		
		if(players[PLAYER_1].tricks.size() > players[winner].tricks.size()) {
			winner = PLAYER_1;
			foundWinner = true;
		}
		else if(players[PLAYER_1].tricks.size() == players[winner].tricks.size()) {
			foundWinner = false;
			tiePlayerA = winner;
			tiePlayerB = PLAYER_1;
		}
		
		if(players[PLAYER_2].tricks.size() == players[winner].tricks.size()) {
			tiePlayerA = winner;
			tiePlayerB = PLAYER_2;
			foundWinner = false;
		} else if (players[PLAYER_2].tricks.size() > players[winner].tricks.size()) {
			winner = PLAYER_2;
			foundWinner =  true;
		}
		
		if(!foundWinner) {
			winner = settleTie(tiePlayerA, tiePlayerB); 
				
		}
		
		// set currentTurn to the winner so the winner will play first in case of a rematch.
		currentTurn = winner;
		
		return winner;
	}
	
	/*
	 * If two players have won the same amount of tricks, 
	 *  calculate the score of their tricks and return it.
	 *  Parameters: the indexes of the two players who are tied.
	 *  Returns: the index of the player who won.
	 */
	private int settleTie(int playerA, int playerB) {
		int playerScoreA = 0;
		int playerScoreB = 0;
		
		for(Trick trick:players[playerA].tricks) {	
			playerScoreA += trick.a.getNum();
			playerScoreA += trick.b.getNum();
			playerScoreA += trick.c.getNum();
		}
		
		for(Trick trick:players[playerB].tricks) {	
			playerScoreB += trick.a.getNum();
			playerScoreB += trick.b.getNum();
			playerScoreB += trick.c.getNum();
		}
		
		
		return playerScoreA > playerScoreB ? playerA : playerB;
	}
	
	/*
	 * Clears all the game data from players
	 */
	private void clearGameData() {
		deck.clearDeck();
		for(PlayerStruct player:players) {
			player.hand.clearHand();
			player.tricks.clear();
			player.trickCard = null;
			player.canWin = false;
			player.wantRematch = false;
		}
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
		byte[] buffer = String.format("Hello, you are Player: %1d", n).getBytes();

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
	 * Tells players who did not just play card what card was played and who played it.
	 */
	private void proclaimCardPlay(Card c) {
		byte[] buffer = String.format("Player%d played: %s", currentTurn, c.toString()).getBytes();

		// get another player
		int player = (currentTurn +1) %3;
		DatagramPacket message0 = new DatagramPacket(buffer, buffer.length, players[player].address, players[player].port);
		
		// get the other player
		player = (player +1) %3;
		DatagramPacket message1 = new DatagramPacket(buffer, buffer.length, players[player].address, players[player].port);
		

		try {
			socket.send(message0);
			socket.send(message1);
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
	 * Tells all players who won the last trick.
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
	 * Tells all players who won the game and the scores of all of the players.
	 */
	private void proclaimWinnerOfGame(int n) {
		String finalScores = String.format("Player0: %d\nPlayer1: %d\nPlayer2: %d",
								players[PLAYER_0].tricks.size(),
								players[PLAYER_1].tricks.size(),
								players[PLAYER_2].tricks.size());
		
		byte[] buffer = String.format("Player%d won the game\nFinal Scores:\n%s", n, finalScores).getBytes();

		
		for(PlayerStruct player:players) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.address, player.port);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		appendToDisplay(String.format("Game Over. Player%d won.\nFinalScores:\n%s", n, finalScores));
	}
	
	/*
	 * Asks all players if they want a rematch.
	 */
	private void askForRematch() {
		byte[] buffer = String.format("Would you like a rematch?").getBytes();
		
		
		for(PlayerStruct player:players) {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.address, player.port);
		
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		appendToDisplay("\nAsked Players if they want a rematch.\nWaiting for responses...");
	}
	
	/*
	 * Waits for incoming packet from all players asking for a rematch, or one packet asking to quit.
	 * Returns: true if a rematch is requested by all three players, 
	 *  else false if one of them requests to quit.
	 */
	private boolean waitRematchRequests() {
		int rematchCount = 0;
		boolean quitting = false;
		
		DatagramPacket packet;
		InetAddress address;
		String message;
		
		while(rematchCount < 3 && !quitting) {
			packet = waitForPacket();
			message = new String(packet.getData(), 0, packet.getLength());
			address = packet.getAddress();
			
			for(PlayerStruct player:players) {
				if(player.address.equals(address) && !player.wantRematch) {
					if(message.equals("rematch")) {
						player.wantRematch = true;
						++rematchCount;
						
						appendToDisplay(String.format("%d %s for a rematch", rematchCount, rematchCount == 1 ? "request" : "requests"));
					}
					else if(message.equals("exit")) {
						quitting = true;
						
						appendToDisplay("Requested to quit");
					}
					break;
				}
			}
		}
		
		return !quitting;
	}
	
	/*
	 * Let all players know a rematch is underway
	 */
	private void proclaimRematch() {
		byte[] buffer = String.format("Rematch underway").getBytes();
		
		for(PlayerStruct player:players) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.address, player.port);
			
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void proclaimGameExit() {
		byte[] buffer = String.format("Quit requested, exiting game").getBytes();
		
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
}





