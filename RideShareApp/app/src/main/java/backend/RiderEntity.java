package backend;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hongman Cho on 2016-11-30.
 */
public class RiderEntity extends GenericJson implements Serializable {
    @Key("_id")
    private String id;
    @Key
    private String userName;
    @Key
    private String eventname;
    @Key
    private String postContent; // driverID is essentially a userID of the driver
    @Key
    private String driverID;
    @Key
    private List<String> requestedTo; // list of all drivers to whom this rider has sent a request

    public RiderEntity(){}  //GenericJson classes must have a public empty constructor
}
