package uk.co.jaynne.dataobjects;

/**
 * Represents a single schedule item
 * @author James Cooke
 */
public class ScheduleObject implements Comparable<ScheduleObject>{
	private int id = 0;
	private int group = 0;
	private int day = 0;
	private int hourOn = 0;
	private int minuteOn = 0;
	private int hourOff = 0;
	private int minuteOff = 0;
	private boolean heatingOn = false;
	private boolean waterOn = false;
	private boolean enabled = false;
	
	
	public ScheduleObject(int id, int group, int day, int hourOn, int minuteOn, 
			int hourOff, int minuteOff, boolean heatingOn, boolean waterOn, 
			boolean enabled) {
		this.id = id;
		this.group = group;
		this.day = day;
		this.hourOn = hourOn;
		this.minuteOn = minuteOn;
		this.hourOff = hourOff;
		this.minuteOff = minuteOff;
		this.heatingOn = heatingOn;
		this.waterOn = waterOn;
		this.enabled = enabled;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}


	/**
	 * @return the hourOn
	 */
	public int getHourOn() {
		return hourOn;
	}


	/**
	 * @return the hourOff
	 */
	public int getHourOff() {
		return hourOff;
	}


	/**
	 * @return the minuteOn
	 */
	public int getMinuteOn() {
		return minuteOn;
	}


	/**
	 * @return the minuteOff
	 */
	public int getMinuteOff() {
		return minuteOff;
	}


	/**
	 * @return the heatingOn
	 */
	public boolean getHeatingOn() {
		return heatingOn;
	}


	/**
	 * @return the waterOn
	 */
	public boolean getWaterOn() {
		return waterOn;
	}

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}


	/**
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}


	@Override
	/**
	 * Orders by Day > minute on > minute off
	 */
	public int compareTo(ScheduleObject o) {
		if (day != o.getDay()) {
			return day - o.getDay();
		}
		if (minuteOn != o.getMinuteOn()) {
			return minuteOn - o.getMinuteOn();
		}
		return minuteOff - o.getMinuteOff();
	}
	
	public String toString() {
		return "Day:" + day + " Time On:" + hourOn + ":" + minuteOn + " Time Off:" + 
				hourOff + ":" + minuteOff + " Heating On:" + heatingOn + " Water On:" + 
				waterOn + " Group:" + group + " Enabled:" + enabled;
	}
}
