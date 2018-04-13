package entities.multithreading;
public class Client implements Comparable<Client>
{
	private int arrivalTime, serviceTime, id;
	
	public Client(int arrivalTime, int serviceTime, int id)
	{
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	public int getArrivalTime()
	{
		return arrivalTime;
	}
	
	public int getServiceTime()
	{
		return serviceTime;
	}
	
	public String toString()
	{
		return String.format("Client %d: arrivalTime = %d, serviceTime = %d\n", id, arrivalTime, serviceTime);
	}

	@Override
	public int compareTo(Client c) 
	{
		return this.arrivalTime - c.arrivalTime;
	}
}
