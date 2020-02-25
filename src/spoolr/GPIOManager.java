package spoolr;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * This manager is used to turn on or off the GPIO pins on the computer module.
 * Additionally, it is used to listen for signal from the input GPIO pins.
 * 
 * This manager reverses or forwards the motor and even stops it if needed.
 * 
 * @author cyberpunkprogrammer
 *
 */

public class GPIOManager extends Manager
{
	private boolean enabled;
	
	GpioController gpio;
	GpioPinDigitalInput augerSensorPin;
	GpioPinDigitalInput chuteButtonPin;
	GpioPinDigitalOutput motorPowerPin;
	GpioPinDigitalOutput motorForwardPin;
	GpioPinDigitalOutput motorReversePin;
	
	public GPIOManager(Main main)
	{
		super(main);
		enabled = false;
	}
	
	/**
	 * Sets the auger manager as the controller of this manager.
	 * The auger manager is a more abstracted controller of the GPIOManager.
	 * It ensures that all safety features are implemented on the GPIO.
	 * 
	 * @param augerManager The manager for the auger.
	 */
	public void setController(AugerManager augerManager)
	{
		this.augerController = augerManager;
	}
	
	/**
	 * Sets the initial state and listening states of the GPIO pins.
	 */
	public void init()
	{
		enabled = main.resourceManager.getBoolean("GPIO_ENABLED");
		
		if(enabled)
		{
			gpio = GpioFactory.getInstance();
			
			augerSensorPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, PinPullResistance.PULL_UP);
			chuteButtonPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP);
			motorPowerPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "motorPower", PinState.LOW);
			motorForwardPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "motorForward", PinState.LOW);
			motorReversePin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "motorReverse", PinState.LOW);
	
			augerSensorPin.setShutdownOptions(true, PinState.LOW);
			motorPowerPin.setShutdownOptions(true, PinState.LOW);
			motorForwardPin.setShutdownOptions(true, PinState.LOW);
			motorReversePin.setShutdownOptions(true, PinState.LOW);
			
			augerSensorPin.addListener(new GpioPinListenerDigital()
			{
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
				{
					if(event.getState() == PinState.LOW)
					{
						/**
						 * The sensor has picked up a magnetic field.
						 */
						augerController.augerSensorDetected();
					}
				}
			});
			
			chuteButtonPin.addListener(new GpioPinListenerDigital()
			{
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
				{
					if(event.getState() == PinState.LOW)
					{
						/**
						 * The chute button has been pressed.
						 */
						
						if(main.debugMode)
						{
							System.out.println("Handle Pressed");
						}
						
						if(main.currentScene instanceof VendingController)
						{
							((VendingController) main.currentScene).setChuteButtonInstruction(false);
							augerController.startAugerRequested();
						}
						
					} else if(event.getState() == PinState.HIGH)
					{
						/**
						 * The chute button has been released.
						 */
						
						if(main.debugMode)
						{
							System.out.println("Handle Released");
						}
						
						augerController.stopAugerRequested();
					}
				}
			});
		}
	}
	
	/**
	 * Returns true if the motor is powering forward.
	 * 
	 * @return True if the motor is powered forward.
	 */
	public boolean isMotorForward()
	{
		if(enabled)
		{
			return motorPowerPin.getState() == PinState.HIGH;
		} else
		{
			return motorForward;
		}
	}
	
	/**
	 * Returns true if the motor is powering reverse.
	 * 
	 * @return True if the motor is powered reverse.
	 */
	public boolean isMotorReverse()
	{
		if(enabled)
		{
			return motorReversePin.getState() == PinState.HIGH;
		} else
		{
			return motorReverse;
		}
	}
	
	/**
	 * Stops the motor.
	 */
	public void stopMotor()
	{
		if(enabled)
		{
			motorPowerPin.low();
			motorForwardPin.low();
			motorReversePin.low();
		} else
		{
			motorForward = false;
			motorReverse = false;
		}
		
		if(main.debugMode)
		{
			System.out.println("Motor Stopped");
		}
	}
	
	/**
	 * Starts the motor powering forwards.
	 */
	public void motorForward()
	{
		if(enabled)
		{
			motorPowerPin.high();
			motorForwardPin.high();
			motorReversePin.low();
		} else
		{
			motorForward = true;
		}
		
		if(main.debugMode)
		{
			System.out.println("Motor Forward");
		}
	}
	
	/**
	 * Starts the motor powering reverse.
	 */
	public void motorReverse()
	{
		if(enabled)
		{
			motorPowerPin.high();
			motorForwardPin.low();
			motorReversePin.high();
		} else
		{
			motorReverse = true;
		}
		
		if(main.debugMode)
		{
			System.out.println("Motor Reverse");
		}
	}
}

