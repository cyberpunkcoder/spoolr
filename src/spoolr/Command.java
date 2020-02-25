package spoolr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is used to execute a command in a Linux terminal.
 * If the current machine is a windows machine, the command is ignored.
 * 
 * @author cyberpunkprogrammer
 *
 */

public class Command implements Runnable
{
	private String commandText;
	private String outputText;
	private SystemManager parent;
	
	/**
	 * Constructor for adding the command info later.
	 */
	public Command()
	{
		commandText = "";
		outputText = "";
	}
	
	/**
	 * Constructor for creating a full command.
	 * 
	 * @param commandText The command that will be executed.
	 * @param parent A system manager for the command to report back too.
	 */
	public Command(String commandText, SystemManager parent)
	{
		this.commandText = commandText;
		this.parent = parent;
	}
	
	/**
	 * Runs the command.
	 */
	@Override
	public void run()
	{
		if(System.getProperty("os.name").contains("Linux"))
		{
			Process process = null;
			
			try
			{
				process = Runtime.getRuntime().exec(commandText);
		
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
				String line = "";
				
				while ((line = reader.readLine()) != null)
				{
					outputText += line + "\n";
				}
		
				try
				{
					process.waitFor();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		parent.commandCompleted(this);
	}
	
	/**
	 * Returns the output text of the command.
	 * 
	 * @return The output text of the command.
	 */
	public String getOutputText()
	{
		if(outputText == null) return "";
		return outputText;
	}
}