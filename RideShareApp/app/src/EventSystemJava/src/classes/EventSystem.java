package phaseII;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Comparator;


public class EventSystem implements Serializable {
	

    //For serialization
    private static final long serialVersionUID = 1L;
    //The list of users for this system.
    private List<User> users;
    //The list of carpoolers that use this system.
    private List<Rider> riders;
    //The list of drivers that use this system.
    private List<Driver> drivers;
    //The list of events in this system.
    private List<Event> events;
    private static EventSystem singletonSystem;

    /**
     * Creates a new system with no data in it.
     */
    private EventSystem() {
        users = new ArrayList<User>();
        riders = new ArrayList<Rider>();
        drivers = new ArrayList<Driver>();
        events = new ArrayList<Event>();
    }

    /**
     * Singleton Constructor.
     * @return A Singleton representation of the System.
     */
    public static EventSystem getInstance(){
        if (singletonSystem == null){
            singletonSystem = new EventSystem();
        }

        return singletonSystem;
    }

    /**
     * Load a EventSystem object from file
     * @param filePath The path to the save.obj file.
     */
    public static void load(String filePath){
        FileInputStream fis;
        ObjectInputStream ois;

        try {
            fis = new FileInputStream(filePath + "/save.obj");
            ois = new ObjectInputStream(fis);

            singletonSystem = (EventSystem) ois.readObject();

            ois.close();
            fis.close();
        } catch (ClassNotFoundException e) {
            singletonSystem = new EventSystem();
        } catch (IOException e) {
            singletonSystem = new EventSystem();
        }
    }

    /**
     * Serialize this EventSystem.
     * @param filePath The path to the folder that save.obj will be written to.
     * @throws IOException If the save is not successful.
     */
    public void save(String filePath) throws IOException {
        FileOutputStream fos  = new FileOutputStream(filePath + "/save.obj");
        ObjectOutputStream oos  = new ObjectOutputStream(fos);

        oos.writeObject(this);

        oos.close();
        fos.close();
    }

    /**
     * Destroys the system Singleton,
     * setting it to null.
     */
    public void destroy(){

        // Used in test cases
        this.singletonSystem = null;
    }


	
	
}
