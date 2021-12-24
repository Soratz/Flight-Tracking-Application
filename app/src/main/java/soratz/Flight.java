package soratz;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URL;

public class Flight implements Serializable {
	private static final long serialVersionUID = 2L;
	public static int flight_number = 1;
	private Route route;
	private String aircraftModel;
	private int flightNumber;
	private String airline;
	
	private Date schDepartureTime;
	private Date schArrivalTime;
	private Date takeOffTime; 
	private Date landingTime; 
	private long delay; // 
	private long waitToLand; // 
	private FlightLog log;
	
	public static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	public Flight(Route route, Date departureTime, String aircraftModel, String airline, SystemDate systemDate) {
		this.route = route;
		this.schDepartureTime = departureTime;
		this.schArrivalTime = new Date(departureTime.getTime() + route.getTimeAsMilliseconds());
		this.takeOffTime = new Date(schDepartureTime.getTime());
		this.landingTime = new Date(schArrivalTime.getTime());
		if(aircraftModel.length() == 0) {
			this.aircraftModel = "No Model";
		} else {
			this.aircraftModel = aircraftModel;
		}
		
		if(airline.length() == 0) {
			this.airline = "No Airline";
		} else {
			this.airline = airline;
		}
		
		this.delay = 0;
		this.waitToLand = 0;
		flightNumber = flight_number++;
		writeFlightNumber();
	}
	
	public Flight(Route route, Date departureTime, String aircraftModel, String airline, SystemDate systemDate, int flightNumber) {
		this.route = route;
		this.schDepartureTime = departureTime;
		this.schArrivalTime = new Date(departureTime.getTime() + route.getTimeAsMilliseconds());
		this.takeOffTime = new Date(schDepartureTime.getTime());
		this.landingTime = new Date(schArrivalTime.getTime());
		if(aircraftModel.length() == 0) {
			this.aircraftModel = "No Model";
		} else {
			this.aircraftModel = aircraftModel;
		}
		
		if(airline.length() == 0) {
			this.airline = "No Airline";
		} else {
			this.airline = airline;
		}
		
		this.delay = 0;
		this.waitToLand = 0;
		this.flightNumber = flightNumber;
	}
	
	
	public FlightLog getLog() {
		return log;
	}

	public void setLog(FlightLog log) {
		this.log = log;
	}

	public FlightThread createThread(SystemDate systemDate, App main) {
		return new FlightThread(this, systemDate, main);
	}
	
	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public String getAircraftModel() {
		return aircraftModel;
	}

	public void setAircraftModel(String aircraftModel) {
		this.aircraftModel = aircraftModel;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public Date getSchDepartureTime() {
		return schDepartureTime;
	}

	public void setSchDepartureTime(Date departureTime) {
		this.schDepartureTime = departureTime;
	}

	public Date getSchArrivalTime() {
		return schArrivalTime;
	}

	public void setSchArrivalTime(Date arrivalTime) {
		this.schArrivalTime = arrivalTime;
	}

	public int getFlightNumber() {
		return flightNumber;
	}

	
	public Date getTakeOffTime() {
		return takeOffTime;
	}

	public Date getLandingTime() {
		return landingTime;
	}

	public long getWaitToLand() {
		return waitToLand;
	}
	
	public int getWaitAsMins() {
		return (int) (waitToLand / 60000);
	}

	public void addWaitToLand(long waitToLand) {
		this.waitToLand += waitToLand;
		landingTime.setTime(landingTime.getTime() + waitToLand);
	}

	public long getDelay() {
		return delay;
	}
	
	public int getDelayAsMins() {
		return (int) (delay / 60000);
	}

	public void addDelay(long delay) {
		this.delay += delay;
		takeOffTime.setTime(takeOffTime.getTime() + delay);
		landingTime.setTime(landingTime.getTime() + delay);
	}
	
	public void resetLandingTime() {
		landingTime.setTime(schArrivalTime.getTime() + this.delay);
		waitToLand = 0;
	}
	
	public void removeFlight() {
		Capital departFrom = route.getFromCity();
		Capital arriveTo = route.getToCity();
		departFrom.removeDepartureFlight(this);
		arriveTo.removeArrivalFlight(this);
	}
	
	public static void writeFlightNumber() {
		String fileName = "flightnumber.dat";
		try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName.toString().substring(6)))) {
			writer.writeObject(flight_number);
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void readFlightNumber() {
		String fileName = "flightnumber.dat";
		try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName))) {
			flight_number = (int) reader.readObject();
;		} catch (FileNotFoundException e) {
			System.out.println(fileName + " not found. Initializing flight number with 1.");
			flight_number = 1;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		String delayed = "";
		if(delay > 0) {
			delayed = String.format(" (Delayed %d min", getDelayAsMins());
			if(delay != 1)
				delayed += 's';
			delayed += ')';
		}
		
		String string = String.format("#%d %s to %s%s. Depart at: %s, Arrive at: %s",
				flightNumber, route.getFromCity(), route.getToCity(), delayed, formatter.format(takeOffTime), formatter.format(landingTime));
		
		return string;
	}
}
