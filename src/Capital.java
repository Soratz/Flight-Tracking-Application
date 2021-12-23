import java.io.Serializable;
import java.util.ArrayList;

public class Capital implements Serializable {
	private static final long serialVersionUID = 4L;
	private String capitalName;
	private ArrayList<Flight> departureFlights;
	private ArrayList<Flight> arrivalFlights;
	private ArrayList<Route> routes;

	public Capital(String capitalName) {
		this.capitalName = capitalName;
		departureFlights = new ArrayList<>();
		arrivalFlights = new ArrayList<>();
		routes = new ArrayList<>();
	}
	
	// TODO: We need control towers.
	
	public String getCapitalName() {
		return capitalName;
	}

	public ArrayList<Flight> getDepartureFlights() {
		return departureFlights;
	}

	public ArrayList<Flight> getArrivalFlights() {
		return arrivalFlights;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}
	
	public void addDepartureFlight(Flight flight) {
		departureFlights.add(flight);
	}
	
	public void removeDepartureFlight(Flight flight) {
		departureFlights.remove(flight);
	}
	
	public void addArrivalFlight(Flight flight) {
		arrivalFlights.add(flight);
	}
	
	public void removeArrivalFlight(Flight flight) {
		arrivalFlights.remove(flight);
	}
	
	public void addRoute(Route route) {
		routes.add(route);
	}
	
	/**
	 * 	Gets the route where the destination is given Capital parameter.
	 *  @return
	 *  Returns null if there is no such route. Otherwise, returns the route.
	 */
	
	public Route getRouteTo(Capital capital) {
		for(Route route : routes) {
			if(route.getToCity().equals(capital)) 
			return route;
		}
		
		return null;
	}
	
	/**
	 * 	Gets the flight where the route is given parameter.
	 *  @return
	 *  Returns null if there is no such flight. Otherwise, returns the flight.
	 */
	
	public Flight getDepartureFlightWith(Route route) {
		for(Flight flight : departureFlights) {
			if(flight.getRoute().equals(route)) {
				return flight;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return capitalName;
	}
}
