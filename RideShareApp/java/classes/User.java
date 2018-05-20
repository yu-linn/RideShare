package phaseII;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.EventNotFoundException;

public class User implements Serializable {
	
	// login userID
	private String userID;
	// login password
	private String passWord;
	//The last name of the user.
	private String lastName;
	//The first name of the user.
	private String firstName;
	//The email of the user.
	private String email;
	//The address of the user.
	private String address;
	// phone number of the user.
	private String phoneNumber;
	// brief introduction about the user.
	private String bio;
	
	// eventIDs this user created
	private List<Integer> eventIDs;
	
	public User(String userID, String passWord, String firstName, String lastName, String email, 
			String address, String phoneNumber, String bio) {
	
		this.userID = userID;
		this.passWord = passWord;
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.bio = bio;
		
		this.eventIDs = new ArrayList<Integer>();
	}
	
	public void addEvent(int event) {
		this.eventIDs.add(event);
	}
	
	public void removeEvent(Event event) throws EventNotFoundException {
		if (this.eventIDs.contains(event)) {
			this.eventIDs.remove(event);
			return;
		}
		throw new EventNotFoundException();
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + ", passWord=" + passWord + ", lastName=" + lastName + ", firstName="
				+ firstName + ", email=" + email + ", address=" + address + ", phoneNumber=" + phoneNumber + ", bio="
				+ bio + "]";
	}
	
	public boolean equals(User user) {
		if (this.userID == user.userID) {
			return true;
		}
		return false;
	}
	
	// getters and setters from here.
	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getUserID() {
		return userID;
	}

	public List<Integer> getEventIDs() {
		return eventIDs;
	}

}
