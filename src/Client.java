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
    private JButton jButton1;
    private JButton jButton10;
    private JButton jButton11;
    private JButton jButton12;
    private JButton jButton13;
    private JButton jButton14;
    private JButton jButton15;
    private JButton jButton16;
    private JButton jButton17;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JButton jButton7;
    private JButton jButton8;
    private JButton jButton9;
    private JScrollPane jScrollPane1;
    JButton [] cardButtons= {jButton1,jButton2,jButton3,jButton4,jButton5,jButton6,jButton7,jButton8,jButton9,
            jButton10,jButton11,jButton12,jButton13,jButton14,jButton15,jButton16,jButton17};
	enum GameState {
		GET_HELLO,
		GET_HAND,
		AWAIT_TURN,
		MY_TURN,
		AWAIT_TRICK_COMPLETION
	} private GameState gameState;
	
	private Hand hand;
	private int lastCardPlayed; // keeps track of the last played card for verification
	

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
	        jButton1 = new JButton();
	        jButton2 = new JButton();
	        jButton3 = new JButton();
	        jButton4 = new JButton();
	        jButton5 = new JButton();
	        jButton6 = new JButton();
	        jButton7 = new JButton();
	        jButton8 = new JButton();
	        jButton9 = new JButton();
	        jButton10 = new JButton();
	        jButton11 = new JButton();
	        jButton12 = new JButton();
	        jButton13 = new JButton();
	        jButton14 = new JButton();
	        jButton15 = new JButton();
	        jButton16 = new JButton();
	        jButton17 = new JButton();

	        
	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	        display.setColumns(20);
	        display.setRows(5);
	        jScrollPane1.setViewportView(display);

	        jButton1.setText("jButton1");
	        
	        jButton2.setText("jButton2");
	        
	        jButton3.setText("jButton3");

	        jButton4.setText("jButton4");

	        jButton5.setText("jButton5");

	        jButton6.setText("jButton6");

	        jButton7.setText("jButton7");

	        jButton8.setText("jButton8");

	        jButton9.setText("jButton9");

	        jButton10.setText("jButton10");

	        jButton11.setText("jButton11");

	        jButton12.setText("jButton12");

	        jButton13.setText("jButton13");

	        jButton14.setText("jButton14");

	        jButton15.setText("jButton15");

	        jButton16.setText("jButton16");

	        jButton17.setText("jButton17");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addGroup(layout.createSequentialGroup()
	                            .addGap(57, 57, 57)
	                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
	                        .addGroup(layout.createSequentialGroup()
	                            .addContainerGap()
	                            .addComponent(jButton1)
	                            .addGap(18, 18, 18)
	                            .addComponent(jButton2)
	                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                            .addComponent(jButton3))
	                        .addGroup(layout.createSequentialGroup()
	                            .addContainerGap()
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                                    .addComponent(jButton7)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                    .addComponent(jButton8)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                    .addComponent(jButton9))
	                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                                    .addComponent(jButton4)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                    .addComponent(jButton5)
	                                    .addGap(18, 18, 18)
	                                    .addComponent(jButton6))))
	                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                            .addContainerGap()
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addGroup(layout.createSequentialGroup()
	                                    .addComponent(jButton13)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                    .addComponent(jButton14)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                    .addComponent(jButton15))
	                                .addGroup(layout.createSequentialGroup()
	                                    .addComponent(jButton10)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                    .addComponent(jButton11)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                    .addComponent(jButton12)))))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(62, 62, 62)
	                        .addComponent(jButton16)
	                        .addGap(30, 30, 30)
	                        .addComponent(jButton17)))
	                .addContainerGap(66, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton1)
	                    .addComponent(jButton2)
	                    .addComponent(jButton3))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton4)
	                    .addComponent(jButton5)
	                    .addComponent(jButton6))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton7)
	                    .addComponent(jButton8)
	                    .addComponent(jButton9))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton10)
	                    .addComponent(jButton11)
	                    .addComponent(jButton12))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton13)
	                    .addComponent(jButton14)
	                    .addComponent(jButton15))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButton16)
	                    .addComponent(jButton17))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        pack();
	        setSize(400, 300);
			setTitle(name);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setAlwaysOnTop(true);
			
//Start GUI
		/*
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
		*/
//End GUI	
		gameState = GameState.GET_HELLO;
		hand = new Hand();
	}//End Client Constructor
	
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

  int count=1;
	private void processPacket(DatagramPacket packet) {
		String message = new String(packet.getData(), 0, packet.getLength());
		
		switch(gameState) {
			case GET_HELLO:
				gameState = GameState.GET_HAND;
				break;
			case GET_HAND:
				// get suit and number from message
				// hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
				//set button text here
				//hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
				//cardButtons[0].setText(message);
				if(count==1)
				{
				hand.recieveCard(message.charAt(6), Integer.parseInt(message.substring(7).trim()));
		       jButton1.setText(message);
			 count++;
			 break;
				}
				else if(count==2)
				{
					
		       jButton2.setText(message);
			 count++;
			 break;
				}
				else if(count==3)
				{
		       jButton3.setText(message);
			 count++;
			 break;
				}
				else if(count==4)
				{
		       jButton4.setText(message);
			 count++;
			 break;
				}
				else if(count==5)
				{
		       jButton5.setText(message);
			 count++;
			 break;
				}
				else if(count==6)
				{
		       jButton6.setText(message);
			 count++;
			 break;
				}
				else if(count==7)
				{
		       jButton7.setText(message);
			 count++;
			 break;
				}
				else if(count==8)
				{
		       jButton8.setText(message);
			 count++;
			 break;
				}
				else if(count==9)
				{
		       jButton9.setText(message);
			 count++;
			 break;
				}
				else if(count==10)
				{
		       jButton10.setText(message);
			 count++;
			 break;
				}
				else if(count==11)
				{
		       jButton11.setText(message);
			 count++;
			 break;
				}
				else if(count==12)
				{
		       jButton12.setText(message);
			 count++;
			 break;
				}
				else if(count==13)
				{
		       jButton13.setText(message);
			 count++;
			 break;
				}
				else if(count==14)
				{
		       jButton14.setText(message);
			 count++;
			 break;
				}
				else if(count==15)
				{
		       jButton15.setText(message);
			 count++;
			 break;
				}
				else if(count==16)
				{
		       jButton16.setText(message);
			 count++;
			 break;
				}
				else if(count==17)
				{
		       jButton17.setText(message);
			 count++;
			 
			 break;
				}
				
				
				//if(hand.isFull()) {
				else {
					gameState = GameState.AWAIT_TURN;
					count++;
					break;
				}
				
				
			case AWAIT_TURN:
				
				if(message.equals("Your turn")) {
					// ack turn
					sendPacket("My turn");
					//appendToDisplay(message);
					gameState = GameState.MY_TURN;
				}
				break;
			case MY_TURN:
				if(message.equals(hand.showCard(lastCardPlayed))) {
					// play the card
					
					sendPacket(hand.showCard(lastCardPlayed));
					hand.playCard(lastCardPlayed);
					gameState = GameState.AWAIT_TRICK_COMPLETION;
				}
				break;
			case AWAIT_TRICK_COMPLETION:
				break;
				
		}
		if(count>18)
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

		try {
			socket.send(sender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}






