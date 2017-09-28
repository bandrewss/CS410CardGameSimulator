
// Ben Andrews
// CS366 HW3 
// 6-22-17

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
	
	private int currentTurn = 0;
	
	

	private DatagramSocket socket;
	private InetAddress address;

	private JTextArea display;

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

		setupGame();
		
		//startGame();

		waitForPackets();
	}

	private void setupGame() {
		// get deck, shuffle it
		
		int playerCount = 0;
		while(playerCount < 3)
		{
			try {
				byte[] data = new byte[128];
				DatagramPacket receiver = new DatagramPacket(data, data.length);

				socket.receive(receiver);

				System.out.printf("received packet");
				if(processHello(receiver, playerCount)) {
					playerCount++;
					System.out.printf(" playerCount:%d\n", playerCount);
				}
				
			} catch (IOException e) {
				System.out.printf("%s\n", e);
				e.printStackTrace();
			}
		}
		
		System.out.printf("Three clients");
		
		// deal cards
		
		// return
	}
	
	public boolean processHello(DatagramPacket packet, int n) {
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
		
		return unique;
	}
	
	private void startGame() {
		// tell the player who starts its there turn
		
		// pass the turn as needed
		
		// proclaim winner of trick
		// keep track of score
		// start next round
		
		
		// proclaim winner
		// check to see if rematch is wanted.
	}

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
	
	private void sendHelloToPlayer(PlayerStruct player, int n) {
		byte[] buffer = String.format("Hello, you are player: %1d", n).getBytes();

		DatagramPacket greeter = new DatagramPacket(buffer, buffer.length, player.address, player.port);

		try {
			socket.send(greeter);
			appendToDisplay(String.format("Client %d connected.\n", n));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// in hindsight these four methods should be generalized into one or two...

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

	private void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(s);
			}
		});
	}

}
