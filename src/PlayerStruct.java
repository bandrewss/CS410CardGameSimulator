// Ben Andrews
// Server for CS410 Card game
// 9-27-17

// server helper class, all public data to make a
//  struct-like object

import java.net.InetAddress;

public class PlayerStruct {
	public InetAddress address;
	public int port;
	public Hand hand;
	
	public String name;

	public PlayerStruct(InetAddress a, int p) {
		address = a;
		port = p;
		hand = new Hand();
	}
}
