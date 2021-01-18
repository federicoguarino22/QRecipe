package it.uninsubria.qrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        String ordineId = intent.getStringExtra("orderId");

        consegna = findViewById(R.id.ConsegnaQrCode);
        consegna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredienteOrdine ingrediente = null;
                if(result!=null){
                    //trovare ingrediente cercato
                    for(IngredienteOrdine ingredienteOrdine: ordine.getIngredienti()){
                        if(ingredienteOrdine.getId().equals(result)){
                            ingrediente = ingredienteOrdine;
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(DeliveryScanner.this, "QRCode non identificato", Toast.LENGTH_LONG).show();
                    return;
                }
                //verifico che ingrediente non sia nulla e che lo stato non sia consegnato
                if(ingrediente!=null){
                    if(ingrediente.getStato_consegna().equals("consegnato")){
                        Toast.makeText(DeliveryScanner.this, "Ingrediente già consegnato", Toast.LENGTH_LONG).show();
                    }
                    else{
                        final IngredienteOrdine ingredienteConsegna = ingrediente;
                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryScanner.this);
                        builder.setTitle("consegna ingrediente");
                        builder.setMessage("sei sicuro di voler consegnare l'ingrediente "+ingrediente.getIngrediente().getNome());
                        builder.setPositiveButton("consegna", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ingredienteConsegna.setStato_consegna("consegnato");

                                //salvare gli elementi sul db
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ordineRef = database.getReference();
                                Task<Void> task = ordineRef.child("ordini").child(ordine.getId()).setValue(ordine);
                                //per aggiungere l'evento
                                task.addOnCompleteListener(DeliveryScanner.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(DeliveryScanner.this, "Consegna avvenuta con successo", Toast.LENGTH_LONG).show();
                                        //faccio il back per ritornare nella schermata precedente, finita la parte dell'ordine
                                        DeliveryScanner.this.finish();
                                    }
                                });

                            }
                        });

                        builder.setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        //crea istanza del dialog e fa vedere dialog a schermo con .show
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
                else{
                    Toast.makeText(DeliveryScanner.this, "Ingrediente non riconosciuto", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        Query orderQuery = dbRef.child("ordini").orderByChild("id").equalTo(ordineId);
        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
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
                                    ingredienteOrdine.setIngrediente(ingrediente);
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