package backend;


import com.kinvey.java.LinkedResources.LinkedGenericJson;
import com.google.api.client.util.Key;

import java.io.Serializable;

public class UserEntity extends LinkedGenericJson implements Serializable {
    @Key("_id")
    private String userID;
    @Key
    private String firstName;
    @Key
    private String lastName;
    @Key
    private String city;
    @Key
    private String province; // Format of city, province (province could be state, area, etc)
    @Key
    private String about;
    @Key
    private int numTimesRider;
    @Key
    private int numTimesPassenger;

    public String getUserID(){
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getAbout() {
        return about;
    }

    public int getNumTimesRider() {
        return numTimesRider;
    }

    public int getNumTimesPassenger(){
        return numTimesPassenger;
    }

    // Instantiate as a linked entity
    public UserEntity(){
        putFile("coverPhoto");
        putFile("profilePhoto");
    }

    public String toString(){
        String result =
                "ID: " + userID +
                "\nFirst: " + firstName +
                "\nLast: " + lastName +
                        "\nCity: " + city +
                        "\nProvince: " + province +
                        "\nAbout: " + about +
                        "\nNum rider: " + numTimesRider +
                        "\nNum pass: " + numTimesPassenger;

        return result;

    }
}
