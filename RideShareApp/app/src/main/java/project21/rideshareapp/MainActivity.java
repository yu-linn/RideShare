package project21.rideshareapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import com.facebook.FacebookSdk;

import backend.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;

    private LoginButton fbLoginButton;
    private Button  loginBtn;
    private Button signupBtn;
    private ImageButton eventsBtn;
    private EditText username;
    private EditText password;
    private TextView title;
    private Client myKinveyClient;
    private ProgressDialog dialog;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        // initialize kinvey client
        myKinveyClient = new Client.Builder("kid_SyuqVJSZx", "02f8b837e69143a2bd04321a3998259e"
                , this.getApplicationContext()).build();

        // connecting layout widgets
        username = (EditText) findViewById(R.id.username_edit_text);
        userName = username.getText().toString();
        password = (EditText) findViewById(R.id.password_edit_text);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signupBtn = (Button) findViewById(R.id.signup_btn);
        eventsBtn = (ImageButton) findViewById(R.id.events_btn);
        fbLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        title = (TextView) findViewById(R.id.titleTextView);

        // set up onClick listener for login button and signup button
        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        eventsBtn.setOnClickListener(this);

        // minimal title styling
        title = (TextView) findViewById(R.id.titleTextView);
        title.setText(Html.fromHtml("ride<b>Share</b>"));
        this.myKinveyClient.user().logout().execute();
        // For demo purpose, login page will always show (if logged in, log out)
        if (this.myKinveyClient.user().isUserLoggedIn()){
            Log.d("logged out kinvey", "kinvey");
            this.myKinveyClient.user().logout().execute();
        }else if (isLoggedInFb()){
            Log.d("logged out facebook", "fb");
            LoginManager.getInstance().logOut();
        }

        // set up facebook login callback
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("KinveyAndroidFb", "Login");
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Log.d("KinveyAndroidFb", accessToken.getToken());
                //final Client mKinveyClient = new Client.Builder("kid_SJ8Xa7nR", "8678ea1a7ccf4e7096a2ed3e138616ff", getApplicationContext()).build();
                myKinveyClient.user().loginFacebook(accessToken.getToken(), new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable e) {
                        Log.d("KinveyAndroidFb", "failed Kinvey facebook login", e);
                    }
                    @Override
                    public void onSuccess(User u) {
                        Log.d("KinveyAndroidFb", "successfully logged in with facebook");
                        goToHomePage();
                    }
                });
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancelled",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "Facebook Login Error",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    // defines action when onClick
    @Override
    public void onClick(View v) {
        if (v == loginBtn){

            // Hack: Make sure no user is logged in, shouldn't matter, but I keep running into bugs
            myKinveyClient.user().logout().execute();
            dialog = ProgressDialog.show(MainActivity.this, "", "Logging in", true, false);
            myKinveyClient.user().login(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {
                // if login fails
                @Override
                public void onFailure(Throwable t) {
                    if (dialog.isShowing()){
                        dialog.cancel();
                    }
                    Log.d("TEST", t.getMessage());
                    CharSequence text = "Wrong username or password.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    Log.d("login failed", t.toString());
                }

                // login succeeded, go to home page
                @Override
                public void onSuccess(User u) {
                    if (dialog.isShowing()){
                        dialog.cancel();
                    }
                    CharSequence text = "Welcome back!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    goToHomePage();
                }
            });
        }else if (v == signupBtn){
            goToSignUp();
        }else if (v == eventsBtn){
            goToFeaturedEvents();
        }
    }

    // Start HomeActivity
    private void goToHomePage(){
        final String username = myKinveyClient.user().getUsername();
        final Query queryUser = new Query().equals("userID", username);

        final AsyncAppData<UserEntity> myUsers = myKinveyClient.appData("users", UserEntity.class);
        myUsers.get(queryUser, new KinveyListCallback<UserEntity>(){
            @Override
            public void onSuccess(UserEntity[] userEntities){
                if(userEntities.length != 0) {
                    Log.d("Q", "found user");
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("newUser", "false");
                    startActivity(intent);
                } else {
                    Log.d("Q", "did not find user");
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("newUser", "true");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Eh whatever, don't do anything
            }
        });
    }

    // Start SignUpActivity
    private void goToSignUp(){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    // Start FeaturedEventsActivity
    private void goToFeaturedEvents(){
        Intent intent = new Intent(getApplicationContext(), FeaturedEventsActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    // Check if user is logged in through facebook
    private boolean isLoggedInFb(){
        AccessToken token;
        token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            return false;
        }else return true;
    }

    public void onBackPressed() {
        // Do not allow going back to previous activity
    }

}
