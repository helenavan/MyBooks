package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        login = (Button) findViewById(R.id.log_btn);
        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.log_btn:
                Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                startActivity(intent);
                break;
        }
    }
}
