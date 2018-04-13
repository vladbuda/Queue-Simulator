package entities.multithreading;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import manager.multithreading.Manager;

public class Server implements Runnable 
{
	private ArrayBlockingQueue<Client> q;
	private int simulationTime, threadNumber, first; //first used to get the head of the queue and decrement it at each step to correctly compute the current waiting time for each server
	private float currentWaitingTime = 0;
	
	public Server(ArrayBlockingQueue<Client> q, int simulationTime, int threadNumber)
	{
		this.q = q;
		this.simulationTime = simulationTime;
		this.threadNumber = threadNumber;
	}
	
	public float getCurrentWaitingTime()
	{
		return currentWaitingTime;
	}
	
	public void computeCurrentWaitingTime()
	{
		if(q.size() == 0) currentWaitingTime = 0;
		else
		{
			Iterator<Client> iterator = q.iterator();
			int sum = first;
			while(iterator.hasNext())
			{
				sum += iterator.next().getServiceTime();
			}
			currentWaitingTime = sum;
		}
	}
	public void run() 
	{
		int k = 0, x = 0;
		boolean once = true;
		try
        {
            Thread.sleep(1000);
        } 
        catch (InterruptedException e1) 
        {
            e1.printStackTrace();
        }
		while(x < simulationTime)
		{
			if(q.peek() != null)
			{
				if(once && q.peek() != null)
				{
					first = q.peek().getServiceTime();
					once = false;
				}
				if(k == q.peek().getServiceTime())
                {
                    k = 0;
                    try
                    {
                        Client c = q.take();
                        Manager.setLog(0, c.getId(), this.threadNumber, c.getServiceTime());
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }
                    if(q.peek() != null) first = q.peek().getServiceTime();
                    else once = true;
                }
				else first--;
			}
			try 
			{
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			if(q.peek() != null) k++;
			x++;
		}
		
	}
	
}
