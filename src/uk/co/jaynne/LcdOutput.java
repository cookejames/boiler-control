package uk.co.jaynne;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.jaynne.lcd.LcdDisplay;


public class LcdOutput extends Thread{
	public void run() {
		LcdDisplay lcd = LcdDisplay.getInstance();
		ControlBroker control = ControlBroker.getInstance();
		while (!Thread.interrupted()) {
			//Get time
			DateFormat timeFormat = new SimpleDateFormat("HH:mm");
			Calendar cal = Calendar.getInstance();
			String time = timeFormat.format(cal.getTime());
			//Get boost status
			boolean hBoost = control.isHeatingBoostOn();
			boolean wBoost = control.isWaterBoostOn();
			//Get heating and water status
			boolean heating = control.isHeatingOn();
			boolean water = control.isWaterOn();
			//Get holiday status
			boolean holiday = control.isHolidayPeriod();
			
			//Create output strings
			String line1 = time + " W:";
			line1 += (water) ? "+" : "-";
			line1 += " H:";
			line1 += (heating) ? "+" : "-";
			
			String line2;
			if (holiday) {
				line2 = "Holiday On";
			} else {
				line2 = "BOOST W:";
				line2 += (wBoost) ? "+" : "-";
				line2 += " H:";
				line2 += (hBoost) ? "+" : "-";
			}
			
			lcd.write(LcdDisplay.LCD_LINE1, line1, LcdDisplay.CENTER);
			lcd.write(LcdDisplay.LCD_LINE2, line2, LcdDisplay.CENTER);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		lcd.write(LcdDisplay.LCD_LINE1, "Shutting Down", LcdDisplay.CENTER);
		lcd.write(LcdDisplay.LCD_LINE2, "Goodbye", LcdDisplay.CENTER);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		lcd.close();
		System.out.println("LCD output interrupted");
	}
}
