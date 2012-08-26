package uk.co.jaynne;

import java.util.Calendar;
import java.util.SortedSet;

import uk.co.jaynne.dataobjects.ScheduleObject;
import uk.co.jaynne.datasource.ScheduleSqlSource;
import uk.co.jaynne.datasource.interfaces.ScheduleSource;

public class Scheduler extends Thread{
	private ControlBroker control = ControlBroker.getInstance();
	
    public void run() {
		while (!Thread.interrupted()) {
			//Heating and water default to off
			Calendar calendar = Calendar.getInstance();
			boolean heating = false;
			boolean water = false;
			
			//Get the day and minute
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			int minute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			
			ScheduleSource ss = new ScheduleSqlSource();
			//Get the schedules for today
			SortedSet<ScheduleObject> schedules = ss.getByDay(day);
			if (schedules == null) {
				System.out.println("Error accessing data");
				break;
			}
			
			System.out.println("Time: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
			System.out.println("Water Boost is: " + control.isWaterBoostOn() + " Water Boost Finish: " + control.getWaterBoostOffTime());
			System.out.println("Heating Boost is: " + control.isHeatingBoostOn() + " Heating Boost Finish: " + control.getHeatingBoostOffTime());
			
			//Loop through all of todays schedules
			for (ScheduleObject schedule : schedules) {
				if (schedule.getEnabled()) { //if enabled
					System.out.print("*");
					System.out.print(schedule);
					//if in active period
					int timeOnMins = (schedule.getHourOn() * 60) + schedule.getMinuteOn();
					int timeOffMins = (schedule.getHourOff() * 60) + schedule.getMinuteOff();
					if (minute >= timeOnMins && minute <= timeOffMins) {
						System.out.print(" **ACTIVE**");
						//Only update the h/w status if they are false
						heating = (heating) ? heating : schedule.getHeatingOn();
						water = (water) ? water : schedule.getWaterOn();
					}
					System.out.println();
				} else {
					System.out.print("-");
					System.out.println(schedule);
				}
			}
			System.out.println("***********************");
			
			//Send heating and water status
			if (heating) {
				control.turnHeatingOn();
			} else { //heating isn't on check boost status
				if (control.isHeatingBoostOn() && calendar.getTimeInMillis() > control.getHeatingBoostOffTime()) {
					control.toggleHeatingBoostStatus();
				}
				control.turnHeatingOff();
			}
			if (water) {
				control.turnWaterOn();
			} else { //water isn't on check boost status
				if (control.isWaterBoostOn() && calendar.getTimeInMillis() > control.getWaterBoostOffTime()) {
					control.toggleWaterBoostStatus();
				}
				control.turnWaterOff();
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println("Scheduler interrupted");
    }
}
