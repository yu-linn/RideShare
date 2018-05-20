package backend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Comparator;

import backend.*;
import project21.rideshareapp.*;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class RideShareSystem {


//    //For serialization
//    private static final long serialVersionUID = 1L;
//    //The list of users for this system.
//    private List<User> users;
//    //The list of carpoolers that use this system.
//    private List<Rider> riders;
//    //The list of drivers that use this system.
//    private List<Driver> drivers;
//    //The list of events in this system.
//    private List<Event> events;
//    private static RideShareSystem singletonSystem;

//    /**
//     * Creates a new rideshre system with no data in it.
//     */
//    private RideShareSystem() {
//        users = new ArrayList<User>();
//        riders = new ArrayList<Rider>();
//        drivers = new ArrayList<Driver>();
//        events = new ArrayList<Event>();
//    }

//    /**
//     * Singleton Constructor.
//     * @return A Singleton representation of the System.
//     */
//    public static RideShareSystem getInstance(){
//        if (singletonSystem == null){
//            singletonSystem = new RideShareSystem();
//        }
//
//        return singletonSystem;
//    }

//    public void setClient(Client client) {
//        this.myKinveyClient = client;
//    }

    // adds new event to kinvey database
    public static void registerEvent(Client myKinveyClient, String eventname, String address, String description, Date eventDate, Date eventTime) {

        String userID = myKinveyClient.user().getId();
        // admins of the event post
        List<String> admins = new ArrayList<String>();
        admins.add(userID);
        // riders and drivers postings
        //List<String> riders = new ArrayList<String>();
        //List<String> drivers = new ArrayList<String>();
        double[] geolocation = getGeoCode(address);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        EventEntity event = new EventEntity();

        event.put("eventAdmins", admins);
        event.put("eventName", eventname);
        event.put("eventDate", eventDate);
        event.put("eventTime", eventDate);
        event.put("eventAddress", address);
        event.put("eventGeoloc", geolocation);
        event.put("eventDescription", description);
        event.put("dateCreated", sdf.format(cal.getTime()));

        // Save data to the database
        AsyncAppData<EventEntity> myEvents = myKinveyClient.appData("events", EventEntity.class);
        myEvents.save(event, new KinveyClientCallback<EventEntity>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
            }
            @Override
            public void onSuccess(EventEntity r) {
                Log.d("TAG", "saved data for event entity ");
            }
        });
    }

    // returns a google's geocode of the given address
    private static double[] getGeoCode(String address){
        double[] geo = new double[2];
        String response;
        response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            geo[0] = lat;
            geo[1] = lng;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return geo;
    }

    // helper for getGeoCode()
    public static String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
