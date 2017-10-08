
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

	...

	public void go() {
		sendPacket("Hello Ready to Start"); // makes initial contact with server

		waitForPackets();
	}

	private void sendPacket(String s) {
		byte[] message = s.getBytes();

		DatagramPacket packet = new DatagramPacket(message, message.length, serverAddress, port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	...

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

	...

	private boolean processPacket(DatagramPacket packet) {
		// turn the packet into a useable string

		if(/*quitting*/) {
			// exit the game
		}
		else
		switch (gameState)
		case GET_HELLO:
			// if the packet is a hello reply from the server
			if (/*first reply from server*/) {
				// get ready to recieve hand
			}
			break;
		case GET_HAND:
			// turn message into card object

			if (/*hand is full*/) {
				// get ready to start the game
				gamestate = AWAIT_TURN
			}
			break;
		// wait until the server says its the players turn
		case AWAIT_TURN:;
			if (/*server says its my turn*/) {
				// let the player know
				gamestate = MY_TURN
			}
			else if(/*the game is over*/) {
				// let the player know
				gamestate = GAME_ENDED
			}
			else if(/*sever said someone else played*/) {
				// let the player know
			}
			break;
		case MY_TURN:
			if /*server confirms my play*/ {
				// play the card
				gameState = AWAIT_TRICK_COMPLETION;
			}
			else {
				// display any error messages
			}
			break;
		case AWAIT_TRICK_COMPLETION:
			if (/*server says the trick ended*/) {
				// let the player know
				gameState = AWAIT_TURN;
			}
			else if(/*server said someone else played*/) {
				// let the player know
			}
			break;
		case GAME_ENDED:
			if(/*server asks for rematch*/ {
				// let the player know
				gameState = AWAIT_REMATCH;
			}
			break;
		case AWAIT_REMATCH:
			if(/*server says rematch is happening*/) {
				// let the player know
				gameState = GameState.GET_HAND;
			}
		} // end switch statement

		return /*true if the game is still alive*/
	}
	
	...

}
