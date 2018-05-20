package phaseII;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.UserNotFoundException;

public class Event implements Serializable {
	
	private int eventID;
	private List<String> eventAdmins;
	private String eventname;
	private String eventDatetime;
	private List<Float> eventGeoloc;
	private String postContent;

    private ArrayList<Integer> driverIDs;
    private ArrayList<Integer> riderIDs;
	
	public Event (int eventID, String eventCreator, String eventname, String eventDatetime, List<Float> eventGeoloc,
			String postContent) {
		
		this.eventID = eventID;
		this.eventname = eventname;
		this.eventDatetime = eventDatetime;
		this.eventGeoloc = eventGeoloc;
		this.postContent = postContent;
		
		this.eventAdmins = new ArrayList<String>();
		// event creator is added first, more users can be added.
		this.eventAdmins.add(eventCreator);
        this.driverIDs = new ArrayList<Integer>;
        this.riderIDs = new ArrayList<Integer>;
	}
	
	public void addEventAdmin(String userID) {
		this.eventAdmins.add(userID);
	}
	
	public void delEventAdmin(String userID) throws UserNotFoundException {
		if (this.eventAdmins.contains(userID)) {
			this.eventAdmins.remove(userID);
			return;
		}
		throw new UserNotFoundException();
	}
	
	public boolean equals (Event event) {
		if (this.eventID == event.eventID) {
			return true;
		}
		return false;
	}


	// getters and setters from here
	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}

	public String getEventDatetime() {
		return eventDatetime;
	}

	public void setEventDatetime(String eventDatetime) {
		this.eventDatetime = eventDatetime;
	}

	public List<Float> getEventGeoloc() {
		return eventGeoloc;
	}

	public void setEventGeoloc(List<Float> eventGeoloc) {
		this.eventGeoloc = eventGeoloc;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public int getEventID() {
		return eventID;
	}

	public List<String> getEventAdmins() {
		return eventAdmins;
	}

    public ArrayList<Integer> getDriverIDs() {
        return driverIDs;
    }

    public ArrayList<Integer> getRiderIDs() {
        return riderIDs;
    }

}
