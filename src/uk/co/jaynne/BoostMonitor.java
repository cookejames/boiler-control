package uk.co.jaynne;

import uk.co.jaynne.gpio.GpioControl;
import uk.co.jaynne.gpio.GpioControlPi4J;
import uk.co.jaynne.gpio.GpioPin;

public class BoostMonitor extends Thread{
	private GpioPin pin;
	private boolean heating;
	private boolean water;
	private boolean pinsHigh;
	
	/**
	 * Monitors a pin for presses and activates boost
	 * @param pin the pin to monitor
	 * @param water whether this pin controls water
	 * @param heating whether this pin controls heating
	 * @param pinsHigh are pins high (true) when pressed or low
	 */
	public BoostMonitor(GpioPin pin, boolean water, boolean heating, boolean pinsHigh) {
		this.pin = pin;
		this.heating = heating;
		this.water = water;
		this.pinsHigh = pinsHigh;
	}
	public void run() {
		GpioControl gpio = GpioControlPi4J.getInstance();
		ControlBroker control = ControlBroker.getInstance();
		gpio.setAsInput(pin);
		
		while (!Thread.interrupted()) {
			boolean status = gpio.getValue(pin);
			try {
			if (status == pinsHigh) {
				if (water) {
					control.toggleWaterBoostStatus();
				}
				if (heating) {
					control.toggleHeatingBoostStatus();
				} 
				Thread.sleep(200); //sleep to ignore multiple presses
			}
			Thread.sleep(20);
			} catch (InterruptedException e) {
				break;
			}
		}
		gpio.close(pin);
		System.out.println("Boost monitor interrupted");
	}

}
