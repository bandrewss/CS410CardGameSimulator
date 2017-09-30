// Ben Andrews
// CS366 HW3 
// 6-22-17

public class ServerDriver 
{

	public static void main(String[] args) 
	{
		Server server = new Server(12321, "127.0.0.1");
		//Server server = new Server(12321, "10.19.82.22");
		
		server.go();

	}

}
