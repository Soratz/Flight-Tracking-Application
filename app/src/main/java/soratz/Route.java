package soratz;

import java.io.Serializable;

public class Route implements Serializable {
	private static final long serialVersionUID = 3L;
	private Capital fromCity;
	private Capital toCity;
	private int timeItTakes; // as minutes; for example 150 minutes.
	
	private Route reverseRoute; // reverse route from the destination city.
	
	
	public Route(Capital fromCity, Capital toCity, int timeItTakes) {
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.timeItTakes = timeItTakes;
	}
	
	public Route(Capital fromCity, Capital toCity, int timeItTakes, Route reverseRoute) {
		this.fromCity = fromCity;
		this.toCity = toCity;
		this.timeItTakes = timeItTakes;
		setReverseRoute(reverseRoute);
	}

	public Capital getFromCity() {
		return fromCity;
	}

	public void setFromCity(Capital fromCity) {
		this.fromCity = fromCity;
	}

	public Capital getToCity() {
		return toCity;
	}

	public void setToCity(Capital toCity) {
		this.toCity = toCity;
	}

	public int getTimeItTakes() {
		return timeItTakes;
	}

	public void setTimeItTakes(int timeItTakes) {
		this.timeItTakes = timeItTakes;
	}

	public Route getReverseRoute() {
		return reverseRoute;
	}

	public void setReverseRoute(Route route) {
		this.reverseRoute = route;
		route.reverseRoute = this;
	}

	public long getTimeAsMilliseconds() {
		return (long) timeItTakes * 60000;
	}
	
	@Override
	public String toString() {
		return "to " + toCity + " in " + timeItTakes + " minutes";
	}
}
