package com.elena.listentogether.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.elena.listentogether.R;

public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onLogin(View view) {
        Intent openLoginActivity = new Intent(this, MainActivity.class);
        startActivity(openLoginActivity);
    }
}
