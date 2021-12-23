import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
enum Result { WAITING, LANDED, TOOK_OFF };

public class FlightLog implements Serializable {
	private static final long serialVersionUID = 10L;
	private Flight flight;
	private Date arrivedDate;
	private Result result;
	private static String fileName = "report.txt";
	
	FlightLog(Flight flight) {
		this.flight = flight;
		this.result = Result.WAITING;
	}

	public Flight getFlight() {
		return flight;
	}
	
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
		//System.out.println(this.toString());
		writeToFile(this.toString() + "\n");
	}
	
	public static void writeToFile(String string) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
			writer.write(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Date getArrivedDate() {
		return arrivedDate;
	}

	public void setArrivedDate(Date arrivedDate) {
		this.arrivedDate = arrivedDate;
	}
	
	public void reset() {
		this.result = Result.WAITING;
		this.arrivedDate = null;
	}
	
	private String format(Date date) {
		return Flight.formatter.format(date);
	}
	
	@Override
	public String toString() {
		String string = String.format("#%d %s, %s (%s-%s): ", 
				flight.getFlightNumber(), flight.getAircraftModel(), flight.getAirline(), flight.getRoute().getFromCity(), flight.getRoute().getToCity());
		if(result == Result.WAITING) {
			string += String.format("Waiting at airport. Scheduled departure at: %s, Scheduled arrival at: %s.", 
					format(flight.getSchDepartureTime()), format(flight.getSchArrivalTime()));
		}
		else if(result == Result.TOOK_OFF) {
			string += String.format("Took off at %s and planning to arrive at %s. Scheduled departure was %s. Scheduled arrival was %s. There is ", 
					format(flight.getTakeOffTime()), format(flight.getLandingTime()), format(flight.getSchDepartureTime()), format(flight.getSchArrivalTime()));
			if(flight.getDelay() > 0) {
				string += String.format("%d mins delay.", flight.getDelayAsMins());
			} else {
				string += "no delay.";
			}
		}
		else { // Landed
			int delay = flight.getDelayAsMins();
			int waitToLand = flight.getWaitAsMins();
			string += String.format("Landed at %s. It took off at %s. Scheduled departure was %s. Scheduled arrival was %s. There is ", 
					format(flight.getLandingTime()), format(flight.getTakeOffTime()), format(flight.getSchDepartureTime()), format(flight.getSchArrivalTime()));
			if(delay > 0) {
				string += delay + " mins delay. ";
			} else {
				string += "no delay. ";
 			}
			
			if(waitToLand > 0) {
				string += "The flight waited permission for " + waitToLand + " mins.";
			} else {
				string += "The flight didn't wait for permission.";
			}
		}
		
		return string;
	}
 }
