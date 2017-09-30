// starts three clients

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientDrivers
{

	public static void main(String[] args) throws InterruptedException
	{
		// use these to do everything on your local machine
		Client alice = new Client("Alice", "127.0.0.2", "127.0.0.1", 12321);
		Client bob = new Client("Bob", "127.0.0.3", "127.0.0.1", 12321);
		Client charles = new Client("Charles", "127.0.0.4", "127.0.0.1", 12321);
		
		/*
		// use these to connect to different computer
		Client alice = new Client("Alice", "yourIP", "serverIP", 12321);
		Client bob = new Client("Bob", "yourIP", "serverIP", 12321);
		Client charles = new Client("yourIP", "127.0.0.4", "serverIP", 12321);
		*/
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		executorService.execute(alice);
		executorService.execute(bob);
		executorService.execute(charles);
		
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

	}

}
