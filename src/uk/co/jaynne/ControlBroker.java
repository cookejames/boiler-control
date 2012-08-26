package uk.co.jaynne;

import java.util.Calendar;

import uk.co.jaynne.dataobjects.ConfigObject;
import uk.co.jaynne.datasource.ConfigSqlSource;
import uk.co.jaynne.datasource.interfaces.ConfigSource;
import uk.co.jaynne.gpio.GpioControl;
import uk.co.jaynne.gpio.GpioControlFramboos;
import uk.co.jaynne.gpio.GpioPin;

public class ControlBroker {
	private boolean heatingOn;
	private boolean waterOn;
	public static GpioPin RED_LED = GpioPin.PIN8_GPIO14; //TODO get from DB
	public static GpioPin BLUE_LED = GpioPin.PIN10_GPIO15;
	public static GpioPin SWITCH1 = GpioPin.PIN12_GPIO18;
	public static GpioPin SWITCH2 = GpioPin.PIN13_GPIO21;
	private boolean LED_ON = false;
	private boolean LED_OFF = true;
	private ConfigSource config;
	
	
	private GpioControl gpio;
	
	private ControlBroker() {
		gpio = GpioControlFramboos.getInstance();
		config = new ConfigSqlSource();
		
		heatingOn = false;
		waterOn = false;
		
    	//Start with water and heating off incase they have been left in an improper state
		deactivateHeating();
		deactivateWater();
	}
	
	private static class SingletonHolder { 
        public static final ControlBroker INSTANCE = new ControlBroker();
	}

	public static ControlBroker getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public boolean turnHeatingOn() {
		//check if the heating is already on
		if (heatingOn) {
			return true;
		} // don't turn on if this is a holiday period
		if (isHolidayPeriod()) {
			return false;
		}
		
		return activateHeating();
	}
	
	private boolean activateHeating() {
		gpio.setValue(BLUE_LED, LED_ON);
		heatingOn = true;
		System.out.println("**HEATING ON, ITS GONA GET TOASTY**");
		return true;
	}
	
	public boolean turnHeatingOff() {
		if (!heatingOn) {
			return true;
		}
		if (isHeatingBoostOn()) { //don't turn off if boosted
			return false;
		}
		return deactivateHeating();
	}
	
	private boolean deactivateHeating() {
		gpio.setValue(BLUE_LED, LED_OFF);
		heatingOn = false;
		System.out.println("**HEATING OFF, BRR**");
		return true;
	}
	
	public boolean turnWaterOn() {
		//check if the water is already on
		if (waterOn) {
			return true;
		} // don't turn on if this is a holiday period
		if (isHolidayPeriod()) {
			return false;
		}
		
		return activateWater();
	}
	
	private boolean activateWater() {
		gpio.setValue(RED_LED, LED_ON);
		waterOn = true;
		System.out.println("**WATER ON, YOU CAN SHOWER IN A BIT**");
		return true;
	}
	
	public boolean turnWaterOff() {
		if (!waterOn) {
			return true;
		}
		if (isWaterBoostOn()) {
			return false;
		}
		return deactivateWater();
	}
	
	private boolean deactivateWater() {
		gpio.setValue(RED_LED, LED_OFF);
		waterOn = false;
		System.out.println("**WATER OFF, BETTER BE CLEAN ALREADY**");
		return true;
	}
	
	public boolean isHolidayPeriod(){
		//TODO
		return false;
	}
	
	public void toggleWaterBoostStatus() {
		boolean waterBoost = isWaterBoostOn();
		if (waterBoost) {
			System.out.println("**WATER BOOST TOGGLED OFF**");
			config.set("waterBoost", !waterBoost);
		} else {
			System.out.println("**WATER BOOST TOGGLED ON**");
			
			long thistime = Calendar.getInstance().getTimeInMillis();
			int boostTimeInMins = config.get("boostTime").getIntValue();
			long boostTimeInMillis = boostTimeInMins * 60 * 1000;
			config.set("waterBoostOffTime", thistime + boostTimeInMillis);
			config.set("waterBoost", !waterBoost);
			turnWaterOn();
		}
	}
	
	/**
	 * Gets the water boost off time in millis since the 'epoch'
	 * @return long
	 */
	public long getWaterBoostOffTime() {
		ConfigObject time = config.get("waterBoostOffTime");
		if (time != null) {
			return time.getLongValue();
		} else {
			return 0;
		}
	}
	
	public boolean isWaterBoostOn() {
		ConfigObject water = config.get("waterBoost");
		if (water == null) {
			return false;
		} else {
			return water.getBoolValue();
		}
	}
	
	public void toggleHeatingBoostStatus() {
		boolean heatingBoost = isHeatingBoostOn();
		if (heatingBoost) {
			System.out.println("**HEATING BOOST TOGGLED OFF**");
			config.set("heatingBoost", !heatingBoost);
		} else {
			System.out.println("**HEATING BOOST TOGGLED ON**");
			
			long thistime = Calendar.getInstance().getTimeInMillis();
			int boostTimeInMins = config.get("boostTime").getIntValue();
			long boostTimeInMillis = boostTimeInMins * 60 * 1000;
			config.set("heatingBoostOffTime", thistime + boostTimeInMillis);
			config.set("heatingBoost", !heatingBoost);
			turnHeatingOn();
		}
	}
	
	/**
	 * Gets the water boost off time in millis since the 'epoch'
	 * @return long
	 */
	public long getHeatingBoostOffTime() {
		ConfigObject time = config.get("heatingBoostOffTime");
		if (time != null) {
			return time.getLongValue();
		} else {
			return 0;
		}
	}
	
	public boolean isHeatingBoostOn() {
		ConfigObject heating = config.get("heatingBoost");
		if (heating == null) {
			return false;
		} else {
			return heating.getBoolValue();
		}
	}
	
	public boolean isHeatingOn() {
		return heatingOn;
	}
	
	public boolean isWaterOn() {
		return waterOn;
	}
	
	/**
	 * Open GPIO connections
	 */
	public void open() {
		gpio.setAsOutput(BLUE_LED);
		gpio.setAsOutput(RED_LED);
	}
	
	/**
	 * Close GPIO connections
	 */
	public void close() {
		gpio.close(BLUE_LED);
		gpio.close(RED_LED);
	}
}
