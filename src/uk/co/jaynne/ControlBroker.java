package uk.co.jaynne;

import java.util.Calendar;

import uk.co.jaynne.dataobjects.ConfigObject;
import uk.co.jaynne.datasource.ConfigSqlSource;
import uk.co.jaynne.datasource.interfaces.ConfigSource;
import uk.co.jaynne.gpio.GpioControl;
import uk.co.jaynne.gpio.GpioControlFramboos;
import uk.co.jaynne.gpio.GpioPin;

public class ControlBroker {
	public static GpioPin RELAY1 = GpioPin.PIN8_GPIO14; //TODO get from DB
	public static GpioPin RELAY2 = GpioPin.PIN10_GPIO15;
	public static GpioPin SWITCH1 = GpioPin.PIN12_GPIO18;
	public static GpioPin SWITCH2 = GpioPin.PIN13_GPIO21;
	private static boolean RELAY_ON = false;
	private static boolean RELAY_OFF = true;
	private ConfigSource config;
	
	
	private GpioControl gpio;
	
	private ControlBroker() {
		gpio = GpioControlFramboos.getInstance();
		config = new ConfigSqlSource();
		
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
		if (isHeatingOn()) {
			return true;
		} // don't turn on if this is a holiday period
		if (isHolidayPeriod()) {
			return false;
		}
		
		return activateHeating();
	}
	
	private boolean activateHeating() {
		gpio.setValue(RELAY2, RELAY_ON);
		config.set("heatingStatus", true);
		System.out.println("**HEATING ON, ITS GONA GET TOASTY**");
		return true;
	}
	
	public boolean turnHeatingOff() {
		if (!isHeatingOn()) {
			return true;
		}
		if (isHeatingBoostOn()) { //don't turn off if boosted
			return false;
		}
		return deactivateHeating();
	}
	
	private boolean deactivateHeating() {
		gpio.setValue(RELAY2, RELAY_OFF);
		config.set("heatingStatus", false);
		System.out.println("**HEATING OFF, BRR**");
		return true;
	}
	
	public boolean turnWaterOn() {
		//check if the water is already on
		if (isWaterOn()) {
			return true;
		} // don't turn on if this is a holiday period
		if (isHolidayPeriod()) {
			return false;
		}
		
		return activateWater();
	}
	
	private boolean activateWater() {
		gpio.setValue(RELAY1, RELAY_ON);
		config.set("waterStatus", true);
		System.out.println("**WATER ON, YOU CAN SHOWER IN A BIT**");
		return true;
	}
	
	public boolean turnWaterOff() {
		if (!isWaterOn()) {
			return true;
		}
		if (isWaterBoostOn()) {
			return false;
		}
		return deactivateWater();
	}
	
	private boolean deactivateWater() {
		gpio.setValue(RELAY1, RELAY_OFF);
		config.set("waterStatus", false);
		System.out.println("**WATER OFF, BETTER BE CLEAN ALREADY**");
		return true;
	}
	
	public boolean isHolidayPeriod(){
		return Calendar.getInstance().getTimeInMillis() < config.get("holidayUntil").getLongValue();
	}
	
	public void toggleWaterBoostStatus() {
		boolean waterBoost = isWaterBoostOn();
		if (waterBoost) {
			System.out.println("**WATER BOOST TOGGLED OFF**");
			config.set("waterBoost", !waterBoost);
		} else {
			if (isHolidayPeriod()) {
				System.out.println("**WATER BOOST IS ON HOLIDAY**");
				return;
			}
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
			if (isHolidayPeriod()) {
				System.out.println("**HEATING BOOST IS ON HOLIDAY**");
				return;
			}
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
		return config.get("heatingStatus").getBoolValue();
	}
	
	public boolean isWaterOn() {
		return config.get("waterStatus").getBoolValue();
	}
	
	/**
	 * Open GPIO connections
	 */
	public void open() {
		gpio.setAsOutput(RELAY2);
		gpio.setAsOutput(RELAY1);
	}
	
	/**
	 * Close GPIO connections
	 */
	public void close() {
		gpio.close(RELAY2);
		gpio.close(RELAY1);
	}
}
