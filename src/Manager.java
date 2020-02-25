package tfgui;

/**
 * This is the abstract class for a manager.
 * Managers must inherit a main class in their constructor.
 * This is so that managers have a handle to update the current user interface.
 * 
 * @author cyberpunkprogrammer
 *
 */
public abstract class Manager
{
	protected Main main;
	
	public Manager(Main main)
	{
		this.main = main;
	}
}
