// Ben Andrews
// Server for CS410 Card game
// 10-5-17

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class Driver {
	private static JFrame startWindow = new JFrame("Start");
	private static JPanel panel = new JPanel();
	private static JButton serverButton = new JButton("Server");
	private static JButton playerButton = new JButton("Player");
	
	private static Server server = null;
	private static Client player = null;
	
	private static boolean startServer = false;
	private static boolean startPlayer = false;
	
	private static String serverIP;
	private static String playerIP;
	
	private static void startServer() {
		server = new Server(12321, serverIP);
		
		startWindow.dispose();
		//startWindow.setVisible(false);
		server.go();
	}

	private static void startPlayer() {
		player = new Client("temp", playerIP, serverIP, 12321);
		
		startWindow.dispose();
		//startWindow.setVisible(false);
		player.go();
	}
	
	public static void main(String[] args) throws InterruptedException {
		serverButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				serverIP = JOptionPane.showInputDialog("Enter your public facing IPv4 address:");
				
				startServer = true;				
			}
		});
		
		playerButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				serverIP = JOptionPane.showInputDialog("Enter the server's IPv4 address:");
				playerIP = JOptionPane.showInputDialog("Enter your public facing IPv4 address:");
				
				startPlayer = true;
			}
		});
		
		panel.add(serverButton);
		panel.add(playerButton);

		startWindow.add(panel);
		startWindow.setVisible(true);
		startWindow.setSize(200, 100);
		startWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		for(;;) {
			if(startServer) {
				startServer();
				break;
			}
			else if(startPlayer) {
				startPlayer();
				break;
			}
			
			Thread.sleep(100);
		}
	}

}
