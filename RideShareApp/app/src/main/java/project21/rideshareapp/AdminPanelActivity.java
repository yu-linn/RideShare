package project21.rideshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
    This activity is for an admin's panel, this might not be needed,
    but I figured we should create it in case it has some use.
 */

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
    }
}
