package project21.rideshareapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.LinkedResources.LinkedFile;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import backend.UserEntity;

/**
 * Allows the user to edit their own profile.
 */

public class ProfileEditActivity extends AppCompatActivity {

    private Client myKinveyClient;
    private Button profileEditBtn;
    private Button chooseProfilePhotoBtn;
    private Button chooseCoverPhotoBtn;

    private TextView errorTextView;
    private TextView profileOkTextView;
    private TextView coverOkTextView;

    private String firstName;
    private String lastName;
    private String city;
    private String province;
    private String about;

    // Intent request codes
    final int PROFILE_PHOTO_REQUEST = 1;
    final int COVER_PHOTO_REQUEST = 2;

    private Uri profilePhotoUri = null;
    private Uri coverPhotoUri = null;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        errorTextView = (TextView) findViewById(R.id.errorTextView);
        profileOkTextView = (TextView) findViewById(R.id.profileOk);
        coverOkTextView = (TextView) findViewById(R.id.coverOk);

        userName = getIntent().getStringExtra("userName");

        // Setup Kinvey calls
        myKinveyClient = Client.sharedInstance();
        final String username = myKinveyClient.user().getUsername();
        final Query queryUser = new Query().equals("userID", username);

        // Submit new user data to Kinvey
        profileEditBtn = (Button) findViewById(R.id.updateBtn);
        profileEditBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final AsyncAppData<UserEntity> myUsers = myKinveyClient.appData("users", UserEntity.class);
                myUsers.get(queryUser, new KinveyListCallback<UserEntity>() {
                    @Override
                    public void onSuccess(UserEntity[] userEntities) {
                        // Check to see if adding a new user or updating existing user
                        if (userEntities.length == 0) {
                            fetchData(null);
                            addUserToDatabase(username, null);
                        } else {
                            // Subsequent edits only take in non-blank fields
                            fetchData(userEntities[0]);
                            addUserToDatabase(username, userEntities[0]);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("TAG", "failed to find in users", throwable);
                    }
                });

            }
        });

        // Open photo chooser
        chooseProfilePhotoBtn = (Button) findViewById(R.id.chooseProfilePhotoBtn);
        chooseProfilePhotoBtn.setOnClickListener(new openPhotoChooser(PROFILE_PHOTO_REQUEST));

        chooseCoverPhotoBtn = (Button) findViewById(R.id.chooseCoverPhotoBtn);
        chooseCoverPhotoBtn.setOnClickListener(new openPhotoChooser(COVER_PHOTO_REQUEST));
    }

    /**
     * Adds an onClick method for the photo chooser buttons.
     */
    public class openPhotoChooser implements View.OnClickListener {

        private int requestCode;

        /**
         * Constructor.
         *
         * @param requestCode integer code to indicate which button is being pressed
         */
        public openPhotoChooser(int requestCode){
            this.requestCode = requestCode;
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
            startActivityForResult(chooser, requestCode);
        }
    }

    /**
     * Decodes the correct URI from the chosen image depending on which button was pressed.
     *
     * @param requestCode corresponding code to indicate which activity
     * @param resultCode resulting code from activity
     * @param data data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE_PHOTO_REQUEST
                    || requestCode == COVER_PHOTO_REQUEST) {

                Uri selectedImageUri = (data == null) ? null : data.getData();

                if (requestCode == PROFILE_PHOTO_REQUEST && selectedImageUri != null) {
                    profilePhotoUri = selectedImageUri;
                    profileOkTextView.setText("Picture chosen!");
                } else if (requestCode == COVER_PHOTO_REQUEST && selectedImageUri != null) {
                    coverPhotoUri = selectedImageUri;
                    coverOkTextView.setText("Picture chosen!");
                }
            }
        }
    }

    /**
     * Determines the data to be updated with the current database.
     * If the user is null, then grab all elements from the database.
     *
     * @param user user to check information for, if any
     */
    public void fetchData(UserEntity user) {

        // Fetch XML elements for blanks
        firstName = ((EditText) findViewById(R.id.firstNameEditText)).getText().toString();
        lastName = ((EditText) findViewById(R.id.lastNameEditText)).getText().toString();
        city = ((EditText) findViewById(R.id.cityEditText)).getText().toString();
        province = ((EditText) findViewById(R.id.provEditText)).getText().toString();
        about = ((EditText) findViewById(R.id.aboutEditText)).getText().toString();

        // Grab updated user information
        if (user != null) {
            if (firstName.length() == 0) {
                firstName = user.getFirstName();
            }

            if (lastName.length() == 0) {
                lastName = user.getLastName();
            }

            if (city.length() == 0) {
                city = user.getCity();
            }

            if (province.length() == 0) {
                province = user.getProvince();
            }

            if (about.length() == 0) {
                about = user.getAbout();
            }
        }
    }

    /**
     * Updates the Kinvey database with the updated profile information.
     * Checks for existing user information to not overwrite everything.
     *
     * @param userID user to update
     * @param user current user data, if any
     */
    public void addUserToDatabase(String userID, UserEntity user) {

        // On first registration, can only have blank "about" field
        if (user == null
                && (firstName.length() == 0 || lastName.length() == 0
                || city.length() == 0 || province.length() == 0)) {
            errorTextView.setText("You must enter a first and last name, city, and province/state!");
        } else {

            // Setup updated information
            final UserEntity userUpdate = new UserEntity();
            userUpdate.put("userID", userID);
            userUpdate.put("firstName", firstName);
            userUpdate.put("lastName", lastName);
            userUpdate.put("city", city);
            userUpdate.put("province", province);
            userUpdate.put("about", about);

            // Save the correct number of rides
            if (user == null) {
                userUpdate.put("numTimesRider", 0);
                userUpdate.put("numTimesPassenger", 0);
            } else {
                userUpdate.put("_id", myKinveyClient.user().getId());
                userUpdate.put("numTimesRider", user.getNumTimesRider());
                userUpdate.put("numTimesPassenger", user.getNumTimesPassenger());
            }

            // Save to database
            AsyncAppData<UserEntity> myUsers = myKinveyClient.appData("users", UserEntity.class);
            myUsers.save(userUpdate, new KinveyClientCallback<UserEntity>() {
                @Override
                public void onFailure(Throwable e) {
                    Log.e("TAG", "Failed to save user data", e);
                }

                @Override
                public void onSuccess(UserEntity user) {
                    // Attach the images to the database
                    Bitmap bitmap;
                    ByteArrayOutputStream stream;
                    byte[] bytes;

                    // Save the chosen image, if any
                    if (profilePhotoUri != null) {
                        try {
                            stream = new ByteArrayOutputStream();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    profilePhotoUri);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            bytes = stream.toByteArray();

                            stream.flush();
                            stream.close();

                            userUpdate.putFile("profilePhoto", new LinkedFile("profile"));
                            userUpdate.getFile("profilePhoto").setInput(new ByteArrayInputStream(bytes));
                        } catch (IOException e) {
                            Log.e("Photo", "error retrieving bitmap from profile Uri");
                        }

                    }

                    if (coverPhotoUri != null) {
                        try {
                            stream = new ByteArrayOutputStream();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    coverPhotoUri);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            bytes = stream.toByteArray();

                            stream.flush();
                            stream.close();

                            userUpdate.putFile("coverPhoto", new LinkedFile("cover"));
                            userUpdate.getFile("coverPhoto").setInput(new ByteArrayInputStream(bytes));
                        } catch (IOException e) {
                            Log.e("Photo", "error retrieving bitmap from cover Uri");
                        }
                    }

                    // Link the picture with the user
                    myKinveyClient.linkedData("users", UserEntity.class).save(userUpdate, null, null);

                    // Return to profile page to display new user data
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                }
            });

        }


    }

}

