// Server class

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
	ServerSocket serverSocket;
	
	public static ArrayList<Socket> socketList = new ArrayList<Socket>();
	
	public Server()
	{
		setupServer();
	}
	
	/*
	 * Setup server, wait for clients to connect and put them in new threads
	 */
	public synchronized void setupServer()
	{
		int players = 0;
		try
		{
			System.out.printf("Setting up server...");
			serverSocket = new ServerSocket(12321);
			System.out.printf("success\n");
			
			while(players < 3)
			{
				System.out.printf("Server is listening on port 12321...");
				Socket clientSocket = serverSocket.accept();
				socketList.add(clientSocket);
				System.out.printf("connected to %s\n", clientSocket.getInetAddress().getHostAddress());
				new Thread(new HandleClient(clientSocket)).start();
				players++;
			}
			
			while(!socketList.isEmpty())
			{
				Thread.sleep(1000);
			}
			
			serverSocket.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	class HandleClient implements Runnable
	{
		private Socket clientSocket;
		
		public HandleClient(Socket client)
		{
			clientSocket = client;
		}

		@Override
		public synchronized void run()
		{
			int numBytes = 0;
			byte[] bytes = new byte[256];
			String data = null;
			
			try
			{
				InputStream input = new DataInputStream(clientSocket.getInputStream());
				OutputStream output = new DataOutputStream(clientSocket.getOutputStream());
				
				System.out.printf("Waiting for message...");
				while( (numBytes = input.read(bytes)) >= 0 )
				{
					data = new String(bytes, 0, numBytes);
					System.out.printf("received \"%s\" from client.\n", data);
					
					System.out.printf("Sending \"%s\" to client...", data);
					output.write(bytes, 0, numBytes);
					System.out.printf("success\n\n");
					
					System.out.printf("Waiting for message...");					
				}
				
				input.close();
				output.close();
				
				socketList.remove(clientSocket);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.printf("\nClient disconnected, exiting\n");
					
		}
		
	}
}

