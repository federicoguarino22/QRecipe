package it.uninsubria.qrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import it.uninsubria.qrecipe.modelli.Ingrediente;
import it.uninsubria.qrecipe.modelli.IngredienteOrdine;
import it.uninsubria.qrecipe.modelli.IngredienteRicetta;
import it.uninsubria.qrecipe.modelli.Ordine;
import it.uninsubria.qrecipe.modelli.Ricetta;

public class DeliveryScanner extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;
    Button consegna;
    String result = null;
    Ordine ordine = null;
    ConcurrentSkipListSet<String> ingredients = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_scanner);
        scannView = findViewById(R.id.devScannerView);
        codeScanner = new CodeScanner(this, scannView);
        resultData = findViewById(R.id.QRresult);

        codeScanner.setDecodeCallback( new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                //salvo il risultato
                DeliveryScanner.this.result = result.getText();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                    }
                });
            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        //restituisce l'intent con cui è stata chiamata l'activity
        Intent intent = getIntent();
        String ordineId = intent.getStringExtra("ordineId");

        consegna = findViewById(R.id.ConsegnaQrCode);
        consegna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result!=null){
                    //apre il dettaglio degli ingredienti
                    Intent intent_ingredient = new Intent(DeliveryScanner.this, DeliveryDetails.class);
                    intent_ingredient.putExtra("orderID", result);
                    startActivity(intent_ingredient);
                    finish();
                }
                else{
                    Toast.makeText(DeliveryScanner.this, "QRCode non identificato", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = database.getReference();
        Query userQuery = orderRef.child("ordini").orderByChild("id").equalTo(ordineId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ordine = dataSnapshot.getChildren().iterator().next().getValue(Ordine.class);
                    DatabaseReference ingredientsRef = database.getReference();
                    Query userQuery = ingredientsRef.child("ingredienti");
                    userQuery.addValueEventListener(new ValueEventListener(){
                        @Override
                        //
                        public void onDataChange(DataSnapshot dataSnapshot){
                            if(dataSnapshot.exists()){
                                for (IngredienteOrdine x : ordine.getIngredienti()){

                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        requestForCamera();
    }
    // gestione dei permessi della fotocamera
    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(DeliveryScanner.this, "Richiesti permessi per la camera", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }}