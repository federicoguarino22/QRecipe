package it.uninsubria.qrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.List;

import it.uninsubria.qrecipe.modelli.Ingrediente;
import it.uninsubria.qrecipe.modelli.IngredienteOrdine;
import it.uninsubria.qrecipe.modelli.Ordine;

public class DeliveryDetails extends AppCompatActivity {
    private Ordine ordine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        //dichiaro le istanze del layout
        final TextView orderId = findViewById(R.id.OrderID);
        final TextView prezzoTot = findViewById(R.id.QRresult);
        final Button consegna = findViewById(R.id.consegna);
        final ListView lista = findViewById(R.id.list_order_ingredients);
        final OrderDetailsAdapter ordineAdapter = new OrderDetailsAdapter(DeliveryDetails.this);
        //assoccia l'adpter con la listview
        lista.setAdapter(ordineAdapter);

        //restituisce l'intent con cui è stata chiamata l'activity
        Intent intent = getIntent();
        String orderID = intent.getStringExtra("orderID");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        Query orderQuery = dbRef.child("ordini").orderByChild("id").equalTo(orderID);
        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //quando cambia l'ordine pulisce l'adapter e lo ricrea
                    ordineAdapter.clear();
                    orderId.setText(orderID);
                    ordine = snapshot.getChildren().iterator().next().getValue(Ordine.class);
                    Query ingredienti = dbRef.child("ingredienti");
                    ingredienti.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //scorro lista di ingredienti dell'ordine
                            for(IngredienteOrdine ingredienteOrdine: ordine.getIngredienti()){
                                //restituisce l'ingrediente
                                Ingrediente ingrediente= snapshot.child(ingredienteOrdine.getId()).getValue(Ingrediente.class);
                                if(ingrediente!=null){
                                    //associa l'ingrediente all'ingrediente ricetta dell'adapter
                                    ingredienteOrdine.setIngrediente(ingrediente);
                                    ordineAdapter.add(ingredienteOrdine);
                                }
                                else{
                                    Log.w("DeliveryDetails", "Ingrediente nullo " + ingredienteOrdine.getId());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // ...
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ...
            }
        });
        //implementazione bottone consegna
        consegna.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onClick(View v) {
               Intent intent_order =  new Intent(DeliveryDetails.this, DeliveryScanner.class);
                intent_order.putExtra("orderId", ordine.getId());
                startActivity(intent_order);
            }
        });

    }
    //funzione per consegnare gli ingredienti
    private void consegna(List<String> listaIngredienti){
        //prende l'id dell'ordine
        Intent intent = getIntent();
        String orderID = intent.getStringExtra("orderID");
        List<Boolean> consegnato = null;

        //salvare gli elementi sul db
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        Query orderQuery = dbRef.child("ordini").orderByChild(orderID);
        orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(IngredienteOrdine ingredients : ordine.getIngredienti()){
                        if(listaIngredienti.contains(ingredients.getId()) && ingredients.getStato_consegna().equals("in_corso")){
                            snapshot.getRef().child(ingredients.toString()).child(ingredients.getStato_consegna()).setValue("consegnato");
                        }
                    }
                    for(IngredienteOrdine ingredients : ordine.getIngredienti()){
                        if(!ingredients.getStato_consegna().equals("consegnato")){
                            consegnato.add(false);
                            break;
                        }
                    }
                    if(!consegnato.contains(false)) {
                        snapshot.getRef().child(orderID).setValue("concluso");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ...
            }
        });

        Task<Void> task = dbRef.child("ordini").child(ordine.getId()).setValue(ordine);
        //per aggiungere l'evento
        task.addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(DeliveryDetails.this, "Consegna avvenuta con successo", Toast.LENGTH_LONG).show();
                //faccio il back per ritornare nella schermata precedente, finita la parte dell'ordine
                DeliveryDetails.this.finish();
            }
        });
    }
}