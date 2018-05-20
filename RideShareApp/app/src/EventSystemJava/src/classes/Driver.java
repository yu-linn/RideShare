package phaseII;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.CarCapFullException;
import exceptions.RequestCapFullException;
import exceptions.RiderNotFoundException;

public class Driver implements Serializable {
	
	private String userID;
	private int eventID;
	private String postContent;
	private String meetingAddress;
	private List<Integer> meetGeoloc;
	private String meetDatetime;
	// number of seats for riders.
	private int numSeatsLeft;
	// total price for the round trip.
	private float price;
	
	// accepted riders for carpool.
	private List<String> acceptedList;
	// List of Riders who have sent a request to this Driver
	private List<String> requestedList;
	
	private static final int requestCap = 5;
	private int requestCount;
	
	public Driver(String userID, int eventID, String postContent, String meetingAddress,
				  List<Integer> meetGeoloc, String meetDatetime, int numSeatsLeft, float price) {
		
		this.userID = userID;
		this.eventID = eventID;
		this.postContent = postContent;
		this.meetingAddress = meetDatetime;
		this.meetGeoloc = meetGeoloc;
		this.meetDatetime = meetDatetime;
		this.numSeatsLeft = numSeatsLeft;
		// just input a null if price is not given.
		this.price = price;

		this.acceptedList = new ArrayList<String>();
		this.requestedList = new ArrayList<String>();
		this.requestCount = 0;
	}

    // Constructor for if price is not given
    public Driver(String userID, int eventID, String postContent, String meetingAddress,
                  List<Integer> meetGeoloc, String meetDatetime, int numSeatsLeft){
        this(userID, eventID, postContent, meetingAddress, meetGeoloc, meetDatetime, numSeatsLeft, null);
    }
	
	// attempt to add rider to requested list.
	public void addToRequest(String rider) throws RequestCapFullException {
		if (this.requestCount < this.requestCap) {
			this.requestedList.add(rider);
			this.requestCount++;
			return;
		}
		throw new RequestCapFullException();
	}
	
	// attempt to remove rider from requested list.
	public void delFromRequest(String rider) throws RiderNotFoundException {
		if (this.requestedList.contains(rider)) {
			this.requestedList.remove(rider);
			this.requestCount--;
			return;
		}
		throw new RiderNotFoundException();
	}
	
	// attempt to add rider to accepted list.
	public void addToAccept(String rider) throws CarCapFullException {
		if (this.numSeatsLeft > 0) {
			this.acceptedList.add(rider);
			this.numSeatsLeft--;
			return;
		}
		throw new CarCapFullException();
	}
	
	// attempt to remove rider from accepted list.
	public void delFromAccept(String rider) throws RiderNotFoundException {
		if (this.acceptedList.contains(rider)) {
			this.acceptedList.remove(rider);
			this.numSeatsLeft++;
			return;
		}
		throw new RiderNotFoundException();
	}
	
	@Override
	public String toString() {
		return "Driver [userID=" + userID + ", eventID=" + eventID + ", postContent=" + postContent
				+ ", meetingAddress=" + meetingAddress + ", meetGeoloc=" + meetGeoloc + ", meetDatetime=" + meetDatetime
				+ ", numSeatsLeft=" + numSeatsLeft + ", price=" + price + ", acceptedList=" + acceptedList + ", requestedList="
				+ requestedList + "]";
	}

	public boolean equals(Driver driver) {
		if (this.userID == driver.userID && this.eventID == driver.eventID) {
			return true;
		}
		return false;
	}

	public ArrayList<Integer> sortRidersByDistance(Event event){
        ArrayList<Integer> riderIDs = event.getRiderIDs();
	}

	// getters and setters from here.
	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getMeetingAddress() {
		return meetingAddress;
	}

	public void setMeetingAddress(String meetingAddress) {
		this.meetingAddress = meetingAddress;
	}

	public List<Integer> getMeetGeoloc() {
		return meetGeoloc;
	}

	public void setMeetGeoloc(List<Integer> meetGeoloc) {
		this.meetGeoloc = meetGeoloc;
	}

	public String getMeetDatetime() {
		return meetDatetime;
	}

	public void setMeetDatetime(String meetDatetime) {
		this.meetDatetime = meetDatetime;
	}

	public int getnumSeatsLeft() {
		return numSeatsLeft;
	}

	public void setnumSeatsLeft(int numSeatsLeft) {
		this.numSeatsLeft = numSeatsLeft;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getUserID() {
		return userID;
	}

	public int getEventID() {
		return eventID;
	}

	public int getNumSeatsLeft() {
		return numSeatsLeft;
	}

	public List<String> getAcceptedList() {
		return acceptedList;
	}

	public List<String> getRequestedList() {
		return requestedList;
	}
	
}
