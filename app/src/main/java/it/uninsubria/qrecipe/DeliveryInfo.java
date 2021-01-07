package it.uninsubria.qrecipe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class DeliveryInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_info);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        TextView date = findViewById(R.id.editTextDate);
        // textView is the TextView view that should display it
        date.setText(currentDateTimeString);

    }
}