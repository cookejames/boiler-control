package uk.co.jaynne;

import java.util.Calendar;

import uk.co.jaynne.datasource.ConfigSqlSource;
import uk.co.jaynne.datasource.interfaces.ConfigSource;
import uk.co.jaynne.gpio.GpioControl;
import uk.co.jaynne.gpio.GpioControlFramboos;
import uk.co.jaynne.gpio.GpioPin;

public class ControlBroker {
	private boolean heatingOn;
	private boolean waterOn;
	private boolean waterBoost;
	private int waterBoostFinish;
	private boolean heatingBoost;
	private int heatingBoostFinish;
	private static int minsInHour = 60; 
	private static int minsInDay = 24 * minsInHour; 
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
		waterBoost = false;
		waterBoostFinish = 0;
		heatingBoost = false;
		heatingBoostFinish = 0;
		
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
		if (heatingBoost) { //don't turn off if boosted
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
		if (waterBoost) {
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
		if (waterBoost) {
			System.out.println("**WATER BOOST TOGGLED OFF**");
			waterBoost = !waterBoost;
		} else {
			System.out.println("**WATER BOOST TOGGLED ON**");
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int minute = Calendar.getInstance().get(Calendar.MINUTE);
			
			int thisminute = hour * minsInHour + minute;
			int boostTime = config.get("boostTime").getIntValue();
			waterBoostFinish = thisminute + boostTime % minsInDay;
			waterBoost = !waterBoost;
			turnWaterOn();
		}
	}
	
	public int getWaterBoostOffTime() {
		return waterBoostFinish;
	}
	
	public boolean isWaterBoostOn() {
		return waterBoost;
	}
	
	public void toggleHeatingBoostStatus() {
		if (heatingBoost) {
			System.out.println("**HEATING BOOST TOGGLED OFF**");
			heatingBoost = !heatingBoost;
		} else {
			System.out.println("**HEATING BOOST TOGGLED ON**");
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int minute = Calendar.getInstance().get(Calendar.MINUTE);
			
			int thisminute = hour * minsInHour + minute;
			int boostTime = config.get("boostTime").getIntValue();
			heatingBoostFinish = thisminute + boostTime % minsInDay;
			heatingBoost = !heatingBoost;
			turnHeatingOn();
		}
	}
	
	public int getHeatingBoostOffTime() {
		return heatingBoostFinish;
	}
	
	public boolean isHeatingBoostOn() {
		return heatingBoost;
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
