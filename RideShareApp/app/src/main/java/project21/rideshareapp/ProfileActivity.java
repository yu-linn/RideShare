package project21.rideshareapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Locale;

import backend.UserEntity;

/*

This activity is for a user's profile page.
Should allow them to edit and view their settings, etc

 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Profile data
    private TextView name;
    private TextView location;
    private TextView about;

    private String userName;

    private ImageView profileBg;
    private ImageView profilePortrait;
    private ImageButton homepageBtn;

    // Rideshare stats
    private TextView numDriven;
    private TextView numPassenger;

    private Client myKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userName = getIntent().getStringExtra("userName");

        setTitle("Profile");
        Toolbar profileToolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(profileToolbar);

        myKinveyClient = Client.sharedInstance();

        // Populate labels with the correct information
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        about = (TextView) findViewById(R.id.aboutText);
        profileBg = (ImageView) findViewById(R.id.profileBg);
        profilePortrait = (ImageView) findViewById(R.id.profile_portrait);
        homepageBtn = (ImageButton) findViewById(R.id.goBackHome);
        homepageBtn.setOnClickListener(this);

        numDriven = (TextView) findViewById(R.id.numDriven);
        numPassenger = (TextView) findViewById(R.id.numPassenger);

        this.getData();
    }

    // Add action to the profile edit actionbar item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Adds the toolbar menu to the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    /**
     * Makes a call to the database to get the user info based on their id.
     * This includes cover photo and profile photos.
     */
    private void getData(){

        // Fetch current user data (error checking for blank fields)
        final String username = myKinveyClient.user().getUsername();
        final Query queryUser = new Query().equals("userID", username);

        final AsyncAppData<UserEntity> myUsers = myKinveyClient.appData("users", UserEntity.class);

        myUsers.get(queryUser, new KinveyListCallback<UserEntity>(){
            @Override
            public void onSuccess(UserEntity[] userEntities){
                if(userEntities.length != 0){
                    name.setText(userEntities[0].getFirstName() + " " + userEntities[0].getLastName());
                    location.setText(userEntities[0].getCity() + ", " + userEntities[0].getProvince());
                    about.setText(userEntities[0].getAbout());

                    numDriven.setText(String.format(Locale.US, "%d Rides Driven",
                            userEntities[0].getNumTimesRider()));
                    numPassenger.setText(String.format(Locale.US, "%d Time Passenger",
                            userEntities[0].getNumTimesPassenger()));

                    /**
                     * Define a Kinvey callback to load the images. I'm pretty sure this is synchronous, meaning
                     * that there will be some delay from when the page is loaded to grabbing the actual images.
                     */
                    myKinveyClient.linkedData("users", UserEntity.class).getEntity(userEntities[0].getUserID(), new KinveyClientCallback<UserEntity>() {
                        @Override
                        public void onSuccess(UserEntity userEntity) {
                            try {

                                /*

                                NOTE: PLEASE SEE THE BELOW HELPER METHOD!

                                 */
                                FileOutputStream fStream;
                                ByteArrayOutputStream bos;
                                Bitmap bmp;
                                java.io.FileInputStream in;

                                // Load the profile picture
                                fStream = getApplicationContext().openFileOutput("profilePhoto.png", Context.MODE_PRIVATE);
                                bos = userEntity.getFile("profilePhoto").getOutput();

                                bos.writeTo(fStream);
                                bos.flush();
                                fStream.flush();
                                bos.close();
                                fStream.close();

                                in = openFileInput("profilePhoto.png");
                                bmp = BitmapFactory.decodeStream(in);
                                profilePortrait.setImageBitmap(bmp);

                                // Load the cover
                                fStream = getApplicationContext().openFileOutput("coverPhoto.png", Context.MODE_PRIVATE);
                                bos = userEntity.getFile("coverPhoto").getOutput();

                                bos.writeTo(fStream);
                                bos.flush();
                                fStream.flush();
                                bos.close();
                                fStream.close();

                                in = openFileInput("coverPhoto.png");
                                bmp = BitmapFactory.decodeStream(in);
                                profileBg.setImageBitmap(bmp);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    }, null);
                } else {
                    name.setText("Default First Last");
                    location.setText("DefaultCity, DefaultProvince");
                    about.setText("Try and update your profile!");
                    numDriven.setText("0 times");
                    numPassenger.setText("0 times");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("TAG", "failed to find in users", throwable);
            }
        });

    }


    /*
    NOTE: For some reason, when I tried to simplify the code with this helper function,
          the app would throw null pointer errors, but the above ugly pasted one works,
          dunno why...
     */
    /*
    private java.io.FileInputStream fetchImage(String key, UserEntity userEntity) {
        FileOutputStream fStream;
        ByteArrayOutputStream bos;
        java.io.FileInputStream in = null;

        try {
            fStream = getApplicationContext().openFileOutput(key + ".png", Context.MODE_PRIVATE);
            bos = userEntity.getFile(key).getOutput();
            bos.writeTo(fStream);

            bos.flush();
            fStream.flush();
            bos.close();
            fStream.close();

            in = openFileInput(key + ".png");

        } catch (IOException e) {}
        return in;
    }
    */

    @Override
    public void onClick(View view) {
        if(view == homepageBtn){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
    }

}
