// Ben Andrews
// CS366 HW3
// 6-22-17

// struct-like objects are frowned upon in java,
//  but I felt as though it was appropriate in this case.

import java.net.InetAddress;

public class ClientStruct 
{
	public String name;
	public InetAddress address;
	public int port;
	
	public ClientStruct(String n, InetAddress a, int p)
	{
		name = n;
		address = a;
		port = p;
	}
}
