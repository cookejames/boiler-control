package uk.co.jaynne.gpio;

import java.util.HashMap;

import com.pi4j.io.gpio.Gpio;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * GpioControl using the Pi4J library http://www.pi4j.com/ 
 * @author james
 *
 */
public class GpioControlPi4J implements GpioControl{
	
	private HashMap<uk.co.jaynne.gpio.GpioPin, com.pi4j.io.gpio.GpioPin> inpins;
	private HashMap<uk.co.jaynne.gpio.GpioPin, com.pi4j.io.gpio.GpioPin> outpins;
	private Gpio gpio;
	private PinState outDefaultState;
	
	private GpioControlPi4J() {
		gpio = GpioFactory.createInstance();
		inpins = new HashMap<uk.co.jaynne.gpio.GpioPin, com.pi4j.io.gpio.GpioPin>();
		outpins = new HashMap<uk.co.jaynne.gpio.GpioPin, com.pi4j.io.gpio.GpioPin>();
		outDefaultState = PinState.HIGH;
	}
	
	private static class SingletonHolder { 
        public static final GpioControlPi4J INSTANCE = new GpioControlPi4J();
	}

	public static GpioControlPi4J getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public GpioPin setAsOutput(GpioPin pin) {
		if (outpins.containsKey(pin)) {
			return pin; //already setup
		}
		if (inpins.containsKey(pin)) {
			inpins.remove(pin); //remove inpin
		}
		
		Pin translatedPin = getPin(pin);
		com.pi4j.io.gpio.GpioPin outpin = gpio.provisionOuputPin(translatedPin, pin.toString() , outDefaultState);
		outpins.put(pin, outpin);
		return pin;
	}

	@Override
	public GpioPin setAsInput(GpioPin pin) {
		if (inpins.containsKey(pin)) {
			return pin; //already setup
		}
		if (outpins.containsKey(pin)) {
			outpins.remove(pin); //remove inpin
		}
		
		Pin translatedPin = getPin(pin);
		com.pi4j.io.gpio.GpioPin outpin = gpio.provisionInputPin(translatedPin, pin.toString());
		inpins.put(pin, outpin);
		return pin;
	}

	@Override
	public GpioPin setValue(GpioPin pin, boolean value) {
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
	public boolean getValue(GpioPin pin) {
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
	public void close(GpioPin pin) {
		//Close if outpin
		if (isOutPin(pin)) {
			outpins.get(pin).setShutdownOptions(true, outDefaultState);
			outpins.remove(pin);
		}
		if (isInPin(pin)) {
			inpins.get(pin).setShutdownOptions(true);
			inpins.remove(pin);
		}
	}
	
	public boolean isInPin(GpioPin pin) {
		return inpins.containsKey(pin);
	}
	
	public boolean isOutPin(GpioPin pin) {
		return outpins.containsKey(pin);
	}
	
	/**
	 * Translates the enum to the pin numbers used by the Framboos library
	 * @param pin GpioPin
	 * @return int pin no
	 */
	private Pin getPin(GpioPin pin) { //TODO include all pins
		switch(pin) {
			case PIN8_GPIO14: return Pin.GPIO_15;
			case PIN10_GPIO15: return Pin.GPIO_16;
			case PIN12_GPIO18: return Pin.GPIO_01;
			case PIN13_GPIO21: return Pin.GPIO_02;
			case PIN16_GPIO23: return Pin.GPIO_04;
			case PIN18_GPIO24: return Pin.GPIO_05;
			case PIN19_GPIO10: return Pin.GPIO_12;
			case PIN22_GPIO25: return Pin.GPIO_06;
			case PIN24_GPIO8: return Pin.GPIO_10;
			case PIN26_GPIO7: return Pin.GPIO_11;
			default: return null;
		}
	}

}