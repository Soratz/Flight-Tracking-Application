import java.util.Date;

public class FlightThread extends Thread {
	private static long CLOSE_TO_TOWER = 20 * 60000;
	private Flight flight;
	private Thread thread;
	private SystemDate systemDate;
	private boolean permission;
	App main;
	
	public enum State {WAITING_DEPARTURE, ON_AIR, WAITING_PERM, PERM_GRANTED, ARRIVED}
	private State state;

	
	public FlightThread(Flight flight, SystemDate systemDate, App main) {
		this.flight = flight;
		this.systemDate = systemDate;
		this.permission = false;
		this.main = main;
		flight.setLog(new FlightLog(flight));
		
		setState();
	}
	
	@Override
	public void run() {
		while(state != State.ARRIVED) {
			try {
				Thread.sleep(100);
				if(systemDate.isEnabled()) {
					switch(state) {
					case WAITING_DEPARTURE:
						if(systemDate.getDate().compareTo(flight.getTakeOffTime()) > 0) {
							changeState(State.ON_AIR);
							flight.getLog().setResult(Result.TOOK_OFF);
						}	
						break;
					case ON_AIR:
						if(systemDate.getTime() >= (flight.getLandingTime().getTime() - CLOSE_TO_TOWER)) {
							changeState(State.WAITING_PERM);
							main.getControlTowerPanel().updatePermissionList();
						}
						break;
					case WAITING_PERM:
						if(permission) {
							changeState(State.PERM_GRANTED); 
							main.getControlTowerPanel().updatePermissionList();
						}		
						else if(systemDate.getDate().compareTo(flight.getLandingTime()) > 0) {
							flight.addWaitToLand(60000); // 1 minute waiting.	
						}
						break;
					case PERM_GRANTED:
						if(systemDate.getDate().compareTo(flight.getLandingTime()) > 0) {
							changeState(State.ARRIVED);
						}
						break;
					case ARRIVED:
						break;
					default:
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		flight.getLog().setArrivedDate(new Date(flight.getLandingTime().getTime()));
		flight.getLog().setResult(Result.LANDED);
		thread = null;
	}
	
	public void start() {
		if(thread == null) {
			thread = new Thread(this, flight.getAirline() + flight.getFlightNumber());
			thread.start();
		}
	}
	
	public void setState() {
		if(systemDate.getDate().compareTo(flight.getTakeOffTime()) <= 0) {
			changeState(State.WAITING_DEPARTURE);
		}
		else if(systemDate.getDate().compareTo(flight.getLandingTime()) < 0) {
			changeState(State.ON_AIR);
			flight.getLog().setResult(Result.TOOK_OFF);
		}	
		else {
			changeState(State.ARRIVED);
			flight.getLog().setResult(Result.TOOK_OFF);
		}	
	}
	
	private void changeState(State newState) {
		if(main.getControlTowerPanel() != null)
			main.getControlTowerPanel().updateFlightLists();
		this.state = newState;
	}
	
	public void setPermission(boolean permission) {
		this.permission = permission;
	}
	
	public static long getCloseToTower() {
		return CLOSE_TO_TOWER;
	}
	
	public static void setCloseToTower(int minutes) {
		CLOSE_TO_TOWER = minutes * 60000;
	}
	
	public Flight getFlight() {
		return this.flight;
	}
	
	public State getFlightState() {
		return state;
	}
	
	public int timeRemaining() { // as minutes
		return (int) (flight.getLandingTime().getTime() - systemDate.getTime()) / 60000;
	}
	
	@Override
	public String toString() {
		String string = String.format("#%d From %s to %s", flight.getFlightNumber(), flight.getRoute().getFromCity(), flight.getRoute().getToCity());
		if(state == State.ON_AIR) {
			string += String.format(". Landing at: %s ", Flight.formatter.format(flight.getLandingTime()));
		}
		else if(state == State.WAITING_DEPARTURE) {
			string += ". Take off at: " + Flight.formatter.format(flight.getTakeOffTime());
		}
		else if(state == State.WAITING_PERM) {
			string += ". Waiting for permission.";
		}
		else if(state == State.PERM_GRANTED) {
			string += ". Permission granted. Landing at: " + Flight.formatter.format(flight.getLandingTime());
		}
		else if(state == State.ARRIVED) {
			if(flight.getLog().getArrivedDate() != null) {
				string += ". Successfully landed at: " + Flight.formatter.format(flight.getLog().getArrivedDate());
			} else {
				string += ". Successfully landed.";
			}
		}
		
		return string;
	}
}
