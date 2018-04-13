package edu.ro.utcn.calc.thread.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import entities.multithreading.Client;
import entities.multithreading.Server;

public class TestThread
{
	public static void main(String[] args)
	{
		ArrayList<Client> clients = new ArrayList<Client>();
		int[] arrivalInterval = new int[2];
		int[] serviceTime = new int[2];
		
		int n, simulationTime;
//		Scanner scan = new Scanner(System.in);
		Random random = new Random();
		arrivalInterval[0] = 1;
		arrivalInterval[1] = 28;
		
		serviceTime[0] = 1;
		serviceTime[1] = 5;
		
		n = 10;
		simulationTime = 30;
		
		/*for(int i = 0; i < n; i++)
		{
			clients.add(new Client(arrivalInterval[0]+random.nextInt(arrivalInterval[1]), serviceTime[0]+random.nextInt(serviceTime[1])));
		}*/
		/*clients.add(new Client(4,6));
		clients.add(new Client(5,10));
		clients.add(new Client(5,2));
		clients.add(new Client(5,4));
		clients.add(new Client(5,4));*/
		//Collections.sort(clients);
		for(Client c : clients)
		{
			System.out.println(c);
		}
		
		
		BlockingQueue<Integer>[] q = new ArrayBlockingQueue[2];
		q[0] = new ArrayBlockingQueue<Integer>(n);
		q[1] = new ArrayBlockingQueue<Integer>(n);
		
		Thread[] threads = new Thread[2];
		Server[] servers = new Server[2];
	//	servers[0] = new Server (q[0],30, 0);
	//	servers[1] = new Server (q[1],30, 1);
		threads[0] = new Thread(servers[0]);
		threads[1] = new Thread(servers[1]);
		
		threads[0].start();
		threads[1].start();
		
		int index, k = 1, x = 0;
		ArrayList<Client> toRemove = new ArrayList<Client>();
		while(x < simulationTime)
		{
			for(Client c : clients)
			{
				if(c.getArrivalTime() == k)
				{
					for(int j = 0; j < 2; j++)
						servers[j].computeCurrentWaitingTime();
					float minTime = servers[0].getCurrentWaitingTime();
					index = 0;
					for(int i = 1; i < 2; i++)
					{
						if(servers[i].getCurrentWaitingTime() < minTime)
						{
							minTime = servers[i].getCurrentWaitingTime();
							index = i;
						}
					} //add a call to computeavergewaiting after each insert, also consider making it synchronous
				//	System.out.println(minTime);
					
				//	System.out.println(index);
					try 
					{
						//System.out.println("Client " + c.getServiceTime() + " added in second "+ k);
						q[index].put(c.getServiceTime());
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					toRemove.add(c);
				}
			}
			clients.removeAll(toRemove);
			toRemove.clear();
			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			k++;
			x++;
		}
	}
}
