package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login, creataccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        login = (Button) findViewById(R.id.btnlogin_l);
        creataccount = (Button) findViewById(R.id.btncreate_l);
        login.setOnClickListener(this);
        creataccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnlogin_l:
                Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                startActivity(intent);
                break;
            case R.id.btncreate_l:
                Intent i = new Intent(MainLoginActivity.this, SignupActivity.class);
                startActivity(i);
                break;
        }
    }
}
