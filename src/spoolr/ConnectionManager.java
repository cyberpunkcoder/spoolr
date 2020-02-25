package spoolr;

import java.util.ArrayList;

/**
 * Manages multiple connections and calls their collective connect or disconnect.
 * Waits until all the connections are finished until the main is notified that
 * connections are complete.
 * 
 * @author cyberpunkprogrammer
 *
 */

public class ConnectionManager extends Manager
{
	private ArrayList<Connection> connectionList;
	private ArrayList<Connection> completeList;

	public ConnectionManager(Main main)
	{
		super(main);
		connectionList = new ArrayList<Connection>();
		completeList = new ArrayList<Connection>();
	}
	
	/**
	 * Adds a connection for the connection manager to keep track of.
	 * 
	 * @param connection The connection to keep track of.
	 */
	public void addConnection(Connection connection)
	{
		connection.setManager(this);
		connectionList.add(connection);
	}
	
	/**
	 * Connects the connections in sequence.
	 */
	public void connectAll()
	{
		completeList = new ArrayList<Connection>();
		
		connectNext();
	}
	
	/**
	 * Connects the next item in the connection list.
	 * If there are no items in the connection list main is notified that
	 * all connections are complete.
	 */
	private void connectNext()
	{
		if(connectionList.size() > 0)
		{
			connectionList.get(0).connect();
		} else
		{
			main.allConnectionsComplete();
		}
	}

	/**
	 * Disconnects all of the added connections in order they were added.
	 */
	public void disconnectAll()
	{
		for(Connection connection : completeList)
		{
			connection.disconnect();
		}
	}
	
	/**
	 * Called whenever a connection has completed.
	 * 
	 * @param connection The connection that has completed.
	 */
	public void connectionComplete(Connection connection)
	{
		for(int i = 0; i < connectionList.size(); i++)
		{
			if(connection.getClass().equals(connectionList.get(i).getClass()))
			{
				connectionList.remove(i);
				completeList.add(connection);
			}
		}
		
		connectNext();
	}
}
