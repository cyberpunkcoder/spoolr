package spoolr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to run a bash script in the /scripts/ folder.
 * It has the ability to read the output of a script and return it to the system manager.
 * 
 * @author cyberpunkprogrammer
 *
 */

public class Script implements Runnable
{
	private SystemManager parent;
	
	private String name;
	private String output;
	
	private int timeout;
	
	public Script(String name, SystemManager parent, int timeout)
	{
		this.name = name;
		this.parent = parent;
		this.timeout = timeout;
		output = "";
	}

	/**
	 * Runs the specified script.
	 */
	@Override
	public void run()
	{
		if(System.getProperty("os.name").contains("Linux"))
		{
			Process process;
			
			try
			{
				process = Runtime.getRuntime().exec("./" + name, null, new File("scripts/"));
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
				String line = null;
				while ((line = stdInput.readLine()) != null)
				{
				    output += line + "\n";
				}
				
				try
				{
					process.waitFor(timeout, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		parent.scriptCompleted(this);
	}
	
	/**
	 * Returns the name of the script.
	 * 
	 * @return The name of the script.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the output of the script after it has ran.
	 * 
	 * @return The output of the script.
	 */
	public String getOutput()
	{
		return output;
	}
}
