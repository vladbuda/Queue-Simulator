package manager.multithreading;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JTextArea;

import entities.multithreading.Client;
import entities.multithreading.Server;
import gui.multithreading.GUI;

public class Manager implements Runnable
{
	private int simulationTime, noOfClients, minArrival, maxArrival, minService, maxService, noOfQueues;
	private ArrayList<Client> clients;
	private ArrayBlockingQueue<Client>[] queues;
	private ArrayBlockingQueue<Client> clientsqueue;
	private static ArrayBlockingQueue<String> logqueue;
	private Thread[] threads;
	private Server[] servers;
	private JTextArea clientsPane, logPane;
	private JTextArea[] serverPane;
	private float waitingTime;
	private int[] peakHour;
	
	public Manager(int simulationTime, int noOfClients, int minArrival, int maxArrival, int minService, int maxService, int noOfQueues, JTextArea clientsPane, JTextArea[] serverPane, JTextArea logPane)
	{
		this.simulationTime = simulationTime;
		this.noOfClients = noOfClients;
		this.minArrival = minArrival;
		this.maxArrival = maxArrival;
		this.minService = minService;
		this.maxService = maxService;
		this.noOfQueues = noOfQueues;
		this.clientsPane = clientsPane;
		this.serverPane = serverPane;
		this.logPane = logPane;
		peakHour = new int[2];
		peakHour[0] = peakHour[1] = 0;
		
		clients = new ArrayList<Client>();
		clientsqueue = new ArrayBlockingQueue<Client>(noOfClients);
		logqueue = new ArrayBlockingQueue<String>(2*noOfClients);
		generateClients();
		Collections.sort(clients);
		for(Client c : clients)
		{
			clientsqueue.add(c);
		}
		queues = new ArrayBlockingQueue[noOfQueues]; //create queues
		for(int i=0; i<noOfQueues; i++)
		{
			queues[i] = new ArrayBlockingQueue<Client>(noOfClients);
		}
		
		servers = new Server[noOfQueues];
		for(int i=0; i<noOfQueues; i++) //create servers
		{
			servers[i] = new Server(queues[i],simulationTime,i);
		}
		
		threads = new Thread[noOfQueues];
		for(int i=0; i<noOfQueues; i++) //create thread for each server
		{
			threads[i] = new Thread(servers[i]);
		}
		
		for(int i=0; i<noOfQueues; i++) //start the server threads
		{
			threads[i].start();
		}
	}
	
	private void generateClients()
	{
		Random random = new Random();
		for(int i = 1; i <= noOfClients; i++)
		{
			clients.add(new Client(minArrival+random.nextInt(maxArrival-minArrival), minService+random.nextInt(maxService-minService), i));
		}
	}
	
	public static void setLog(int action, int client, int threadNumber, int serviceTime) //write new event log
	{
		if(action == 1) 
		{
			try 
			{
				logqueue.put(String.format("Client %d entered thread %d at time %d with service time %d\n", client, threadNumber, GUI.x.get(), serviceTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			try 
			{
				logqueue.put(String.format("Client %d left thread %d at time %d\n", client, threadNumber, GUI.x.get()+1));
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
	}
	
	public float getWaitingTime()
	{
		return waitingTime;
	}
	
	public int[] getPeakHour()
	{
		return peakHour;
	}
	
	private String getClientsString() //obtain a string representation of the list of clients
	{
		String s = new String();
		if(clientsqueue.isEmpty()) s = "Empty";
		Iterator<Client> iterator = clientsqueue.iterator();
		while(iterator.hasNext())
			s = s + iterator.next().toString();
		return s;
	}
	
	private String getQueuesString(int i) //obtain a string representation for each server's queue
	{
		String s = new String();
		Iterator<Client> iterator = queues[i].iterator();
		while(iterator.hasNext()) s = s + " " + iterator.next().getId();
		return s;
	}
	
	private String getLogs() //obtain a strin representation of logs
	{
		String s = new String();
		Iterator<String> iterator = logqueue.iterator();
		while(iterator.hasNext()) s = s + iterator.next();
		return s;
	}
	
	public boolean checkIfOver() //check if the list of clients and the queue are empty
	{
		if(clientsqueue.size() != 0) return false;
		for(int i=0 ; i<noOfQueues; i++)
		{
			if(queues[i].size() != 0) return false;
		}
		return true;
	}
	
	public void run()
	{
		int index, sum;
		while(GUI.x.get() <= simulationTime)
		{
			clientsPane.setText(getClientsString());
			for(int i = 0; i < noOfQueues; i++)
				serverPane[i].setText(getQueuesString(i));
			logPane.setText(getLogs());
			sum = 0;
			for(int i = 0; i < noOfQueues; i++)
			{
				sum += queues[i].size();
			}
			if(sum > peakHour[1]) //compute peak hour
			{
				peakHour[0] = GUI.x.get();
				peakHour[1] = sum;
			}
			if(clientsqueue.peek() != null)
			{
				if(clientsqueue.peek().getArrivalTime() == GUI.x.get()) //if the simulation reached a client's arrival time
				{
					for(int i = 0; i < noOfQueues; i++) servers[i].computeCurrentWaitingTime(); //refresh waiting time for each server
					float minTime = servers[0].getCurrentWaitingTime();
					index = 0;
					for(int i = 1; i < noOfQueues; i++) //find the server with minimum waiting time
					{
						if(servers[i].getCurrentWaitingTime() < minTime)
						{
							minTime = servers[i].getCurrentWaitingTime();
							index = i;
						}
					}
					waitingTime += minTime; //add that time to global waiting time
					try //place the client in the queue
					{
						Client c = clientsqueue.take();
						queues[index].put(c);
						setLog(1, c.getId(), index, c.getServiceTime()); //register new event
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
