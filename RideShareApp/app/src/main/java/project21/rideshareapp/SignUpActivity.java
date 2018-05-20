package project21.rideshareapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

/*

This activity is for a registering as a new user.

 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signupBtn;
    private EditText username;
    private EditText password;
    private TextView title;
    private Client mKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize kinvey client
        mKinveyClient = new Client.Builder("kid_SyuqVJSZx", "02f8b837e69143a2bd04321a3998259e"
                , this.getApplicationContext()).build();

        // connecting layout widgets
        username = (EditText) findViewById(R.id.username_edit_text);
        password = (EditText) findViewById(R.id.password_edit_text);
        title = (TextView) findViewById(R.id.titleTextView);
        signupBtn = (Button) findViewById(R.id.signup_btn);

        // minimal title styling
        title = (TextView) findViewById(R.id.titleTextView);
        title.setText(Html.fromHtml("ride<b>Share</b>"));

        // set up onClick listener for login button and signup button
        signupBtn.setOnClickListener(this);

    }

    // defines action when onClick
    @Override
    public void onClick(View v) {
        if (v == signupBtn) {

            // TODO: Create a user and add them to the database
            // For right now, I've just copied what Zack had.

            mKinveyClient.user().create(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {

                // if signup fails (most likely due to username already exists)
                @Override
                public void onFailure(Throwable t) {
                    CharSequence text = "This username already exists";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    Log.d("signup Failure", t.toString());
                }

                // signup success
                @Override
                public void onSuccess(User u) {
                    CharSequence text = "Your account has been created";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            });
        }
    }

    // Upon a successful user creation, return them to the login screen
    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
