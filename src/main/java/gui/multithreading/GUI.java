package gui.multithreading;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import manager.multithreading.Manager;

public class GUI implements Runnable
{
	private JTextField simulationTimeField, noOfClientsField, minArrivalField, maxArrivalField, minServiceField, maxServiceField, noOfQueuesField;
	private JButton startButton;
	private JFrame frame, simulationFrame;
	private int simulationTime, noOfClients, minArrival, maxArrival, minService, maxService, noOfQueues;
	public static AtomicInteger x;
	private Thread simulator, managerThread;
	private JTextArea clients, log, timer;
	private JTextArea[] servers;
	private JScrollPane pane1, pane2;
	private JScrollPane[] serverPane;
	private Manager manager;
	
	public GUI()
	{
		frame = new JFrame("Simulator");
		
		simulationTimeField = new JTextField("Simulation interval (seconds)");
		noOfClientsField = new JTextField("Number of clients");
		minArrivalField = new JTextField("Min arrival");
		maxArrivalField = new JTextField("Max arrival");
		minServiceField = new JTextField("Min service");
		maxServiceField = new JTextField("Max service");
		noOfQueuesField = new JTextField("Number of queues (3-5)");
		startButton = new JButton("Start");
		startButton.setFocusable(false);
		x = new AtomicInteger(-1);
		
		simulationTimeField.setBounds(50, 70, 200, 30);
		noOfClientsField.setBounds(50, 120, 200, 30);
		noOfQueuesField.setBounds(50,170,200,30);
		minArrivalField.setBounds(50, 220, 90, 30);
		maxArrivalField.setBounds(160, 220, 90, 30);
		minServiceField.setBounds(50, 270, 90, 30);
		maxServiceField.setBounds(160,270,90,30);
		
		simulationTimeField.addMouseListener(new mouseHandler());
		noOfClientsField.addMouseListener(new mouseHandler());
		noOfQueuesField.addMouseListener(new mouseHandler());
		minArrivalField.addMouseListener(new mouseHandler());
		maxArrivalField.addMouseListener(new mouseHandler());
		minServiceField.addMouseListener(new mouseHandler());
		maxServiceField.addMouseListener(new mouseHandler());
		
		startButton.addActionListener(new buttonHandler());
		startButton.setBounds(50,320,200,30);
		
		frame.add(simulationTimeField);
		frame.add(noOfClientsField);
		frame.add(noOfQueuesField);
		frame.add(minArrivalField);
		frame.add(maxArrivalField);
		frame.add(minServiceField);
		frame.add(maxServiceField);
		frame.add(startButton);
		
		frame.setLayout(null);
		frame.setSize(305, 480);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void run()
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		x.incrementAndGet();
		managerThread.start();
		
		while(x.get() <= simulationTime)
		{
			timer.setText("Time: "+x);
			if(manager.checkIfOver())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			x.incrementAndGet();
		}
		JOptionPane.showMessageDialog(null, String.format("Average waiting time: %.2f seconds\n Peak time: %d with %d clients",(manager.getWaitingTime()/noOfClients),manager.getPeakHour()[0],manager.getPeakHour()[1]),"Results", JOptionPane.ERROR_MESSAGE, null);
	}
	
	private void starts()
	{
		simulationFrame = new JFrame();
		clients = new JTextArea();
		log = new JTextArea();
		timer = new JTextArea();
		timer.setEditable(false);
		timer.setBounds(50,40,100,20);
		timer.setBackground(simulationFrame.getBackground());
		servers = new JTextArea[noOfQueues];
		for(int i = 0; i < noOfQueues; i++)
		{
			servers[i] = new JTextArea();
		}
		serverPane = new JScrollPane[noOfQueues];
		for(int i = 0; i < noOfQueues; i++)
		{
			serverPane[i] = new JScrollPane(servers[i], JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		if( noOfQueues == 5)
		{
			serverPane[0].setBounds(50,340,500,30);
			serverPane[1].setBounds(50,390,500,30);
			serverPane[2].setBounds(50,440,500,30);
			serverPane[3].setBounds(50,490,500,30);
			serverPane[4].setBounds(50,540,500,30);
		}
		else if (noOfQueues == 4)
		{
			serverPane[0].setBounds(50,365,500,30);
			serverPane[1].setBounds(50,415,500,30);
			serverPane[2].setBounds(50,465,500,30);
			serverPane[3].setBounds(50,515,500,30);
		}
		else if (noOfQueues == 3)
		{
			serverPane[0].setBounds(50,390,500,30);
			serverPane[1].setBounds(50,440,500,30);
			serverPane[2].setBounds(50,490,500,30);
		}
		for(int i = 0; i < noOfQueues; i++)
		{
			simulationFrame.add(serverPane[i]);
		}
		
		pane1 = new JScrollPane(clients,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane1.setBounds(50,70,500,250);
		pane2 = new JScrollPane(log,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		pane2.setBounds(50, 590, 500, 250);
		
		simulationFrame.add(pane1);
		simulationFrame.add(pane2);
		simulationFrame.add(timer);
		
		simulationFrame.setLayout(null);
		simulationFrame.setSize(600,900);
		simulationFrame.setResizable(false);
		simulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulationFrame.setLocationRelativeTo(null);
		frame.setVisible(false);
		simulationFrame.setVisible(true);
		
		manager = new Manager(simulationTime, noOfClients, minArrival, maxArrival, minService, maxService, noOfQueues,clients, servers, log);
		managerThread = new Thread(manager, "managerThread");
		simulator = new Thread(this, "simulator");
		simulator.start();
	}
	
	private class buttonHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(simulationTimeField.getText().length() == 0 || noOfClientsField.getText().length() == 0 || noOfQueuesField.getText().length() == 0 || minArrivalField.getText().length() == 0 ||  maxArrivalField.getText().length() == 0
					|| minServiceField.getText().length() == 0 || maxServiceField.getText().length() == 0 ) JOptionPane.showMessageDialog(null, String.format("%s", "Input must not contain blank spaces" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null);
			else
				{
				try
				{
					simulationTime = Integer.parseInt(simulationTimeField.getText());
					noOfClients = Integer.parseInt(noOfClientsField.getText());
					noOfQueues = Integer.parseInt(noOfQueuesField.getText());
					minArrival = Integer.parseInt(minArrivalField.getText());
					maxArrival = Integer.parseInt(maxArrivalField.getText());
					minService = Integer.parseInt(minServiceField.getText());
					maxService = Integer.parseInt(maxServiceField.getText());
				}
				catch(NumberFormatException exception)
				{
					JOptionPane.showMessageDialog(null, String.format("%s", "Input must"
							+ " contain integers only" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null);
				}
					if(minArrival > maxArrival) JOptionPane.showMessageDialog(null, String.format("%s", "Min arrival must be smaller than max arrival" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null);
					else if(minService > maxService) JOptionPane.showMessageDialog(null, String.format("%s", "Min service must be smaller than max service" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null); 
					else if(minArrival >= simulationTime) JOptionPane.showMessageDialog(null, String.format("%s", "Min arrival must be smaller than simulation time" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null);
					else if(noOfQueues < 3 || noOfQueues > 5) JOptionPane.showMessageDialog(null, String.format("%s", "Number of queues must be between 3 and 5" ), "Invalid format", JOptionPane.ERROR_MESSAGE, null);
					else 
					{
						starts();
						frame.dispose();
					}
				}
		
		}
	}
	private class mouseHandler implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == simulationTimeField) simulationTimeField.setText(null);
			else if(e.getSource() == noOfClientsField) noOfClientsField.setText(null);
			else if(e.getSource() == noOfQueuesField) noOfQueuesField.setText(null);
			else if(e.getSource() == minArrivalField) minArrivalField.setText(null);
			else if(e.getSource() == maxArrivalField) maxArrivalField.setText(null);
			else if(e.getSource() == minServiceField) minServiceField.setText(null);
			else if(e.getSource() == maxServiceField) maxServiceField.setText(null);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
