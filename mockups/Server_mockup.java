
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

	...

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

		...

		appendToDisplay(String.format("Server IP: %s", addr));
	}

	...

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

	...

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

	...

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

	...

}
