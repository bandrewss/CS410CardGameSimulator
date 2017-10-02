// Ben Andrews
// Server for CS410 Card game
// 9-27-17

// server helper class, all public data to make a
//  struct-like object

import java.net.InetAddress;
import java.util.ArrayList;

public class PlayerStruct {
	public ArrayList<TrickStruct> tricks = new ArrayList<TrickStruct>();
	public InetAddress address;
	public int port;
	public Hand hand;
	public Card trickCard;
	public boolean canWin;
	
	public String name;

	public PlayerStruct(InetAddress a, int p) {
		address = a;
		port = p;
		hand = new Hand();
		trickCard = null;
		canWin = false;
	}
}
