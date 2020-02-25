package spoolr;

/**
 * This class is used to run scripts or commands to do system level jobs.
 * All scripts or commands report back to this manager and their outputs can
 * determine actions to be taken by this manager.
 * 
 * @author cyberpunkprogrammer
 *
 */

public class SystemManager extends Manager
{
	public SystemManager(Main main)
	{
		super(main);
		clearTerminal();
	}
	
	/**
	 * Clears the terminal that the software is running in.
	 */
	public void clearTerminal()
	{
		Thread commandThread = new Thread(new Command("clear", this));
		commandThread.run();
	}
	
	/**
	 * Runs a script in the /scripts/ folder.
	 * If the script does not complete by the timeout given, it is ignored.
	 * 
	 * @param scriptName The name of the script file.
	 * @param timeout The specified time out in milliseconds.
	 */
	private void runScript(String scriptName, int timeout)
	{
		Script script = new Script(scriptName, this, timeout);
		Thread thread = new Thread(script);
		thread.start();
	}
	
	/**
	 * Shuts down the system.
	 */
	public void shutDown()
	{
		main.logManager.writeLog("Machine shut down.");
		runScript("shutdown.sh", 0);
	}
	
	/**
	 * Restarts the system.
	 */
	public void restart()
	{
		main.logManager.writeLog("Machine restarting.");
		runScript("restart.sh", 0);
	}
	
	/**
	 * Resets the system, loads a stable build and resets the dat files.
	 */
	public void reset()
	{
		runScript("reset.sh", 0);
	}
	
	public void getUsbDevices()
	{
		runScript("devices.sh", 5000);
	}
	
	
	public void disconnect()
	{
		runScript("disconnect.sh", 0);
	}
	
	public void hostWifi()
	{
		runScript("hostwifi.sh", 30000);
	}
	
	/**
	 * Kills the WiFi hot spot by running the "killwifi.sh" script.
	 */
	public void killWiFi()
	{
		runScript("killwifi.sh", 30000);
	}
	
	/**
	 * Starts the VPN for remote access.
	 */
	public void startVPN()
	{
		runScript("startvpn.sh", 30000);
	}
	
	/**
	 * Stops the VPN for remote access.
	 */
	public void stopVPN()
	{
		runScript("stopvpn.sh", 30000);
	}
	
	public void checkForUpdate()
	{
		String branch = "master";
		
		if(main.updateManager.getBetaMode())
		{
			branch = "beta";
		}
		
		runScript("updatecheck.sh" + " " + branch, 30000);
	}
	
	/**
	 * Runs the update script.
	 */
	public void update()
	{
		String branch = "master";
		
		if(main.updateManager.getBetaMode())
		{
			branch = "beta";
		}
		
		runScript("update.sh" + " " + branch, 30000);
	}
	
	/**
	 * Email Scripts
	 */
	
	/**
	 * Sends test email.
	 */
	
	public void sendTestEmail(String recipient, String machineName, String dateTime)
	{
		if(main.debugMode)
		{
			System.out.println("sendtestemail.sh" + " " + recipient + " " + machineName + " " + dateTime);
		}
		
		runScript("sendtestemail.sh" + " " + recipient + " " + machineName + " " + dateTime, 90000);
	}
	
	public void sendSaleEmail(String recipient, String machineName, String dateTime, String dollarAmount, String paymentMethod, String bushelsRemaining)
	{
		runScript("sendsaleemail.sh" + " " + recipient + " " + machineName + " " + dateTime + " " + dollarAmount + " " + paymentMethod + " " + bushelsRemaining, 90000);
	}
	
	public void sendFillNeededEmail(String recipient, String machineName, String dateTime, String bushelsRemaining)
	{
		runScript("sendfillneededemail.sh" + " " + recipient + " " + machineName + " " + dateTime + " " + bushelsRemaining, 90000);
	}
	
	public void sendProblemEmail(String recipient, String machineName, String dateTime, String errorCode, String errorDescription)
	{
		runScript("sendproblememail.sh" + " " + recipient + " " + machineName + " " + dateTime + " " + errorCode + " " + errorDescription, 90000);
	}
	
	public void sendOutOfOrderEmail(String recipient, String machineName, String dateTime, String errorCode, String errorDescription)
	{
		runScript("sendoutoforder.sh" + " " + recipient + " " + machineName + " " + dateTime + " "+ errorCode + " " + errorDescription, 90000);
	}
	
	/**
	 * Connects to the network by running the connect script.
	 */
	public void connectNetwork()
	{
		String apn = main.resourceManager.getString("NETWK_APN");
		String username = main.resourceManager.getString("NETWK_USERNAME");
		String password = main.resourceManager.getString("NETWK_PASSWORD");

		runScript("connect.sh " + apn + " " + username + " " + password, 180000);
	}
	
	/**
	 * Called when a command is completed.
	 * 
	 * @param command The command that has completed.
	 */
	public void commandCompleted(Command command)
	{
		
	}
	
	/**
	 * Called when a script is completed.
	 * 
	 * @param script The script that has completed.
	 */
	public void scriptCompleted(Script script)
	{
		if(script.getName().contains("devices.sh"))
		{
			main.cashConnection.findBillAcceptor(script.getOutput());
		}
		else if(script.getName().contains("connect.sh"))
		{
			main.networkConnection.setStatusValue(script.getOutput());
			main.networkConnection.checkIfConnected();
		}
		else if (script.getName().contains("updatecheck.sh"))
		{
			main.updateManager.setUpdateAvailable(script.getOutput().contains("true"));
		}
		else if (script.getName().contains("disconnect.sh"))
		{
			if(!main.networkConnection.hasStatusValue("Reconnecting"))
			{
				main.networkConnection.setStatusValue(script.getOutput());
				main.networkConnection.checkIfConnected();
			}
		} else if (script.getName().contains("sendtestemail.sh"))
		{
			main.emailManager.setStatus(script.getOutput());
			
			if(main.debugMode)
			{
				System.out.println(script.getOutput());
			}
			
		} else if (script.getName().contains("sendsaleemail.sh"))
		{
			main.emailManager.setStatus(script.getOutput());
			
			if(main.debugMode)
			{
				System.out.println(script.getOutput());
			}
			
		} else if (script.getName().contains("sendfillneededemail.sh"))
		{
			main.emailManager.setStatus(script.getOutput());
			
			if(main.debugMode)
			{
				System.out.println(script.getOutput());
			}
			
		} else if (script.getName().contains("sendproblememail.sh"))
		{
			main.emailManager.setStatus(script.getOutput());
			
			if(main.debugMode)
			{
				System.out.println(script.getOutput());
			}
			
		} else if (script.getName().contains("sendoutoforder.sh"))
		{
			main.emailManager.setStatus(script.getOutput());
			
			if(main.debugMode)
			{
				System.out.println(script.getOutput());
			}
		}
	}
}