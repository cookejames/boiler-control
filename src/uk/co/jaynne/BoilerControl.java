package uk.co.jaynne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Program to control a boiler with two channels - heating and water.
 * Schedules are set in a database with the option to override them via
 * "boost" buttons
 * @author James Cooke
 *
 */
public class BoilerControl {

	public static void main(String[] args) {
		//The scheduler thread deals with checking whether any channels are due to come on
		Thread scheduler = new Thread(new Scheduler());
		scheduler.start();
		//Monitors the water boost button for presses
		Thread wBoost = new Thread(new BoostMonitor(ControlBroker.SWITCH1, true, false));
		wBoost.start();
		//Monitors the heating boost button for presses
		Thread hBoost = new Thread(new BoostMonitor(ControlBroker.SWITCH2, false, true));
		hBoost.start();
		//LCD output
		Thread lcd = new Thread(new LcdOutput());
		lcd.start();

		// open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = "";

		//Check input for a press of the q button to exit
		while (!input.equals("q")) {
			try {
				System.out.println("Press q to exit");
				input = br.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error trying to read your response!");
				System.exit(1);
			}
		}
		try {
			System.out.println("Stopping scheduler");
			scheduler.interrupt();
			scheduler.join();
			System.out.println("Stopping water boost monitor");
			wBoost.interrupt();
			wBoost.join();
			System.out.println("Stopping heating boost monitor");
			hBoost.interrupt();
			hBoost.join();
			System.out.println("Stopping lcd output");
			lcd.interrupt();
			lcd.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Turning heating and water off");
		ControlBroker control = ControlBroker.getInstance();
		if (control.isWaterBoostOn()) {
			control.toggleWaterBoostStatus();
		}
		if (control.isHeatingBoostOn()) {
			control.toggleHeatingBoostStatus();
		}
		control.turnHeatingOff();
		control.turnWaterOff();
		control.close();
		System.out.println("Exiting");
	}
}
