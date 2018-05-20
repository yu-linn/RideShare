package backend;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hongman Cho on 2016-11-29.
 */

public class EventEntity extends GenericJson implements Serializable {
    @Key("_id")
    private String id;
    @Key
    private List<String> eventAdmins;
    @Key
    private String eventName;
    @Key
    private String eventDate;
    @Key
    private String eventTime;
    @Key
    private String eventAddress;
    @Key("_geoloc")
    private double[] eventGeoloc;
    @Key
    private String postContent;
    @Key
    private String dateCreated;

    public String getEventname() {
        return eventName;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getEventDatetime() {
        return eventDate.toString();
    }

    public String getEventTime() {
        return eventTime;
    }


    public double[] getEventGeoloc() {
        return eventGeoloc;
    }
    public List<String> getEventAdmins() {
        return eventAdmins;
    }

    public EventEntity(){}  //GenericJson classes must have a public empty constructor
}
