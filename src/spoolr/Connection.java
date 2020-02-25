package spoolr;

/**
 * The abstract class for a connection.
 * A connection is something that the software must connect too.
 * The connection manager can be used to wait untill multiple
 * connections are complete before performing an action.
 * 
 * @author cyberpunkprogrammer
 */

public abstract class Connection
{
	private Thread timeoutTimer;
	private Thread reconnectTimer;
	
	protected Main main;
	protected ConnectionManager connectionManager;
	
	public Connection(Main main)
	{
		this.main = main;
		
		timeoutTimer = new Thread();
		reconnectTimer = new Thread();
	}
	
	/**
	 * Sets the connection manager of the connection.
	 * 
	 * @param connectionManager
	 */
	public void setManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
	
	/**
	 * Starts a timeout timer in case there is a failure in the connection.
	 * 
	 * @param duration The number of milliseconds the timeout timer will wait.
	 */
	protected void startTimeoutTimer(int duration)
	{
		timeoutTimer = new Thread(() ->
		{
			try
			{
				Thread.sleep(duration);
				connectionComplete();
			} catch (InterruptedException e)
			{
				/**
				 * Thread has been interrupted and would normally
				 * throw an exception. Threads are interrupted to
				 * save on resources.
				 */
			}
		});

		timeoutTimer.start();
	}
	
	/**
	 * Stops the timeout timer.
	 */
	protected void stopTimeoutTimer()
	{
		timeoutTimer.interrupt();
		timeoutTimer = new Thread();
	}
	
	/**
	 * Stars the reconnect timer.
	 * When the timer expires the connection will attempt to reconnect.
	 * 
	 * @param duration The number of milliseconds until reconnect.
	 */
	protected void startReconnectTimer(int duration)
	{
		reconnectTimer = new Thread(() ->
		{
			try
			{
				Thread.sleep(duration);
				reconnect();
			} catch (InterruptedException e)
			{
				//Caught if timer canceled.
			}
		});

		reconnectTimer.start();
	}
	
	/**
	 * Stops the reconnect timer.
	 */
	protected void stopReconnectTimer()
	{
		reconnectTimer.interrupt();
		reconnectTimer = new Thread();
	}
	
	protected abstract void connectionSuccessful();
	
	protected abstract void connectionFailed();
	
	public abstract void connect();
	
	public abstract void disconnect();
	
	public abstract void reconnect();
	
	public abstract void checkIfConnected();
	
	protected abstract void connectionComplete();
}
