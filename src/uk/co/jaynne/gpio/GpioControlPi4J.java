package uk.co.jaynne.gpio;

import java.util.HashMap;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * GpioControl using the Pi4J library http://www.pi4j.com/ 
 * @author james
 *
 */
public class GpioControlPi4J implements GpioControl{
	
	private HashMap<GpioPin, GpioPinDigitalInput> inpins;
	private HashMap<GpioPin, GpioPinDigitalOutput> outpins;
	private GpioController gpio;
	private PinState outDefaultState;
	
	private GpioControlPi4J() {
		gpio = GpioFactory.getInstance();
		inpins = new HashMap<uk.co.jaynne.gpio.GpioPin, GpioPinDigitalInput>();
		outpins = new HashMap<uk.co.jaynne.gpio.GpioPin, GpioPinDigitalOutput>();
		outDefaultState = PinState.LOW;
	}
	
	private static class SingletonHolder { 
        public static final GpioControlPi4J INSTANCE = new GpioControlPi4J();
	}

	public static GpioControlPi4J getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public synchronized GpioPin setAsOutput(GpioPin pin) {
		if (outpins.containsKey(pin)) {
			return pin; //already setup
		}
		if (inpins.containsKey(pin)) {
			inpins.remove(pin); //remove inpin
		}
		
		Pin translatedPin = getPin(pin);
		GpioPinDigitalOutput outpin = gpio.provisionDigitalOutputPin(translatedPin, pin.toString() , outDefaultState);
		outpins.put(pin, outpin);
		return pin;
	}

	@Override
	public synchronized GpioPin setAsInput(GpioPin pin) {
		if (inpins.containsKey(pin)) {
			return pin; //already setup
		}
		if (outpins.containsKey(pin)) {
			outpins.remove(pin); //remove inpin
		}
		
		Pin translatedPin = getPin(pin);
		GpioPinDigitalInput outpin = gpio.provisionDigitalInputPin(translatedPin, pin.toString());
		inpins.put(pin, outpin);
		return pin;
	}

	@Override
	public synchronized GpioPin setValue(GpioPin pin, boolean value) {
		//Check if this pin is assigned as an out pin
		if (isInPin(pin)) {
			setAsOutput(pin);
		}
		if (!isOutPin(pin)) {
			setAsOutput(pin);
		}
		if (value) {
			outpins.get(pin).high();
		} else {
			outpins.get(pin).low();
		}
		return pin;
	}

	@Override
	public synchronized boolean getValue(GpioPin pin) {
		//Check if this pin is assigned as an out pin
		if (isOutPin(pin)) {
			setAsInput(pin);
		}
		if (!isInPin(pin)) {
			setAsInput(pin);
		}
		PinState state = inpins.get(pin).getState();
		if (state == PinState.HIGH) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized void close(GpioPin pin) {
		//Close if outpin
		if (isOutPin(pin)) {
			outpins.get(pin).setShutdownOptions(true, outDefaultState);
			outpins.remove(pin);
		}
		if (isInPin(pin)) {
			inpins.get(pin).setShutdownOptions(true);
			inpins.remove(pin);
		}
		//call the shutdown method to forcibly close anything left open
		gpio.shutdown();
	}
	
	public synchronized boolean isInPin(GpioPin pin) {
		return inpins.containsKey(pin);
	}
	
	public synchronized boolean isOutPin(GpioPin pin) {
		return outpins.containsKey(pin);
	}
	
	/*
	public synchronized void addListener(GpioPin pin, GpioListener listener) {
		if (isOutPin(pin)) {
			setAsInput(pin);
		}
		if (!isInPin(pin)) {
			setAsInput(pin);
		}
		com.pi4j.io.gpio.GpioPin listenPin = inpins.get(pin);
		listenPin.addListener(listener);
	}
	
	public synchronized void removeListeners(GpioPin pin){
		if (isOutPin(pin) || !isInPin(pin)) {
			return;
		}
		inpins.get(pin).removeAllListeners();
	}
	*/
	
	/**
	 * Translates the enum to the pin numbers used by the Framboos library
	 * @param pin GpioPin
	 * @return int pin no
	 */
	private synchronized Pin getPin(GpioPin pin) {
		switch(pin) {
			case PIN3_GPIO0: return RaspiPin.GPIO_08;
			case PIN5_GPIO1: return RaspiPin.GPIO_09;
			case PIN7_GPIO4: return RaspiPin.GPIO_09;
			case PIN8_GPIO14: return RaspiPin.GPIO_15;
			case PIN10_GPIO15: return RaspiPin.GPIO_16;
			case PIN11_GPIO17: return RaspiPin.GPIO_00;
			case PIN12_GPIO18: return RaspiPin.GPIO_01;
			case PIN13_GPIO21: return RaspiPin.GPIO_02;
			case PIN15_GPIO22: return RaspiPin.GPIO_03;
			case PIN16_GPIO23: return RaspiPin.GPIO_04;
			case PIN18_GPIO24: return RaspiPin.GPIO_05;
			case PIN19_GPIO10: return RaspiPin.GPIO_12;
			case PIN21_GPIO9: return RaspiPin.GPIO_13;
			case PIN22_GPIO25: return RaspiPin.GPIO_06;
			case PIN23_GPIO11: return RaspiPin.GPIO_04;
			case PIN24_GPIO8: return RaspiPin.GPIO_10;
			case PIN26_GPIO7: return RaspiPin.GPIO_11;
			default: return null;
		}
	}

}
