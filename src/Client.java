
// Ben Andrews
// CS366 HW3 
// 6-22-17

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
		AWAIT_TURN
	} GameState gameState;
	
	private Hand hand;
	boolean myTurn;

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
				sendPacketTo(event.getActionCommand());
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
	
	public void go() {
		sendHelloPacket(); // makes initial contact with server
		
		waitForPackets();
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
				
		}
		
		
		
		appendToDisplay(message);
	}

	private void sendHelloPacket() {
		byte[] hello = String.format("Hello Ready to Start").getBytes();

		DatagramPacket sender = new DatagramPacket(hello, hello.length, serverAddress, port);

		try {
			socket.send(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendPacketTo(String message) {
		byte[] data = message.getBytes();

		DatagramPacket sender = new DatagramPacket(data, data.length, serverAddress, port);

		try {
			socket.send(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
	
	public void run() {
		go();
	}
}
