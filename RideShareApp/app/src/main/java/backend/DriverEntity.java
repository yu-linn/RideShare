package backend;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hongman Cho on 2016-11-30.
 */
public class DriverEntity extends GenericJson implements Serializable {

    @Key("_id")
    private String id;
    @Key
    private String userName;
    @Key
    private String eventName;
    @Key
    private String eventId;
    @Key
    private String postContent;
    @Key
    private String meetingAddress;
    @Key
    private double[] meetingGeoloc;
    @Key
    private String meetingDate;
    @Key
    private String meetingTime;
    // number of total seats in car
    @Key
    private int totalSeats;
    // number of seats left for riders.
    @Key
    private int numSeatsLeft;
    // total price for the round trip.
    @Key
    private float feePerRider;
    @Key
    private List<String> acceptedRiders; // accepted riders (userIDs) for carpool.
    @Key
    private List<String> requestedTo; // List of Riders (userIDs) who have sent a request to this Driver

    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    public KinveyMetaData getMeta() {
        return meta;
    }

    public DriverEntity(){}  //GenericJson classes must have a public empty constructor
}
