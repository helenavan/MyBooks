package com.example.android.blablawild;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.id.message;
import static android.os.Build.VERSION_CODES.M;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SearchItineraryActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE1 = "key1";
    public static final String EXTRA_MESSAGE2 = "key2";
    private EditText departure, destination;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_itinerary);

        departure = (EditText) findViewById(R.id.editTextSearchDeparture);
        destination = (EditText) findViewById(R.id.editTextSearchDestination);
        button = (Button)findViewById(R.id.button);
        button.setBackgroundColor(Color.GRAY);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(departure.getText().length() == 0 ||
                        destination.getText().length()== 0 ){
                    Context context = getApplicationContext();
                    CharSequence text = "Veuillez remplir le formulaire complètement,\nsinon grrr";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else {
                    sendMessage(button);
                }
            }
        });

    }
    public void sendMessage(View view) {
        Intent intent = new Intent(this, ViewSearchItineraryResultsListActivity.class);
        String message1 = departure.getText().toString();
        String message2 = destination.getText().toString();
        intent.putExtra(EXTRA_MESSAGE1, message1);
        intent.putExtra(EXTRA_MESSAGE2, message2);
        startActivity(intent);
    }

}
