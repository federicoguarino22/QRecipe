package it.uninsubria.qrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import it.uninsubria.qrecipe.modelli.Ordine;

public class DeliveryInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_info);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        TextView date = findViewById(R.id.Data);
        // textView is the TextView view that should display it
        date.setText(currentDateTimeString);
        final OrdineAdapter ordineAdapter= new OrdineAdapter(this);
        ListView listView = findViewById(R.id.list_orders);
        listView.setAdapter(ordineAdapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ordineRef = database.getReference();
        Query ordineQuery =  ordineRef.child("ordini");
        ordineQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //scorrere tutti gli ordini per prendere il valore
                for(DataSnapshot data:snapshot.getChildren()){
                    Ordine ordine =  data.getValue(Ordine.class);
                    if(ordine.getStato().equals("in_corso"))
                         ordineAdapter.add(ordine);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ...
            }
        });

        //ogni volta che viene cliccato un oggetto della lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //recupero ordine cliccato
                Ordine ordine = ordineAdapter.getItem(position);
                //passare ai dettagli dell'ordine
                Intent intent_ordine = new Intent(DeliveryInfo.this, DeliveryScanner.class);
                intent_ordine.putExtra("orderID", ordine.getId());
                startActivity(intent_ordine);
            }
        });
    }
}