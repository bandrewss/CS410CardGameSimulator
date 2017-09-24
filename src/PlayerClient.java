import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient
{
	public PlayerClient()
	{
		buildClient();
	}
	
	public void buildClient()
	{
		try
		{
			System.out.printf("Attempting to connect to 127.0.0.1 on port 12321...");
			Socket socket = new Socket("127.0.0.7", 12321);
			System.out.printf("success\n");
			
			InputStream input = new DataInputStream(socket.getInputStream());
			OutputStream output = new DataOutputStream(socket.getOutputStream());
					
			Scanner scanner = new Scanner(System.in);
			
			String dataOut;
			byte[] bytesOut;
			byte[] bytesIn;
			String dataIn;
			
			do
			{
				System.out.printf("Enter message to send to server:\n");
				System.out.printf("or enter 'exit session' to exit\n");
				dataOut = scanner.nextLine();
				bytesOut = dataOut.getBytes();
				
				System.out.printf("Sending...");
				output.write(bytesOut, 0, bytesOut.length);
				System.out.printf("sent: \"%s\"\n", dataOut);
				
				
				bytesIn = new byte[bytesOut.length];
				
				System.out.printf("Waiting for echo...");
				input.read(bytesIn, 0, bytesIn.length);
				dataIn = new String(bytesIn, 0, bytesIn.length);
				System.out.printf("received: \"%s\"\n\n", dataIn);
			} while(!dataOut.equals("exit session"));
			
			scanner.close();
			socket.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		PlayerClient player = new PlayerClient();

	}

}
