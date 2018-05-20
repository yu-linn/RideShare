package backend;

import java.io.Serializable;
import java.util.List;

import exceptions.DriverAlreadySetException;
import exceptions.DriverNotFoundException;
import exceptions.RequestCapFullException;
import exceptions.RiderNotFoundException;

public class Rider implements Serializable {

	private String userId;
	private int eventID;
	private String postContent;
	// driverID is essentially a userID of the driver
	private String driverID;
	// list of all drivers to whom this rider has sent a request
	private List<String> requestedList;

	private static final int requestCap = 5;
	private int requestCount;
	
	public Rider(String userId, int eventID, String postContent, List<String> requestedList) {

		this.userId = userId;
		this.eventID = eventID;
		this.postContent = postContent;
		this.requestCount = 0;
		this.driverID = null;
	}
	
	// attempt to add driver to requested list.
	public void addToRequest(String driver) throws RequestCapFullException {
		if (this.requestCount < this.requestCap) {
			this.requestedList.add(driver);
			this.requestCount++;
			return;
		}
		throw new RequestCapFullException();
	}
	
	// attempt to remove driver from requested list.
	public void delFromRequest(String driver) throws DriverNotFoundException {
		if (this.requestedList.contains(driver)) {
			this.requestedList.remove(driver);
			this.requestCount--;
			return;
		}
		throw new DriverNotFoundException();
	}
	
	// attempts to set the given driver.
	public void setDriver(String driverID) throws DriverAlreadySetException {
		if (this.driverID == null) {
			this.driverID = driverID;
			return;
		}
		throw new DriverAlreadySetException();
	}

	// unsets the current driver.
	public void unsetDriver() {
		this.driverID = null;
	}
	
	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getUserId() {
		return userId;
	}

	public int getEventID() {
		return eventID;
	}

	public String getDriverID() {
		return driverID;
	}

	public List<String> getRequestedList() {
		return requestedList;
	}
	
}
