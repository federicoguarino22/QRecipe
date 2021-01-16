package it.uninsubria.qrecipe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uninsubria.qrecipe.modelli.Ingrediente;
import it.uninsubria.qrecipe.modelli.IngredienteOrdine;
import it.uninsubria.qrecipe.modelli.IngredienteRicetta;
import it.uninsubria.qrecipe.modelli.Ordine;
import it.uninsubria.qrecipe.modelli.Ricetta;

public class RecipeActivity extends AppCompatActivity {

    private Ricetta ricetta = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView ClientAddress;
    Button Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricetta);

        //inizializzo fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //dichiaro le istanze del layout
        final TextView name_ricetta = findViewById(R.id.name_ricetta);
        final ListView listaingredienti = findViewById(R.id.list_ingredients);
        final IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeActivity.this);
        ClientAddress = findViewById(R.id.IndirizzoCliente);
        Location = findViewById(R.id.getLocation);
        //assoccia l'adpter con la listview
        listaingredienti.setAdapter(ingredientsAdapter);

        //restituisce l'intent con cui è stata chiamata l'activity
        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference();
        Query userQuery = recipeRef.child("ricette").orderByChild("id").equalTo(recipeId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ricetta = dataSnapshot.getChildren().iterator().next().getValue(Ricetta.class);
                    name_ricetta.setText(ricetta.getNome());
                    DatabaseReference ingredientsRef = database.getReference();
                    Query userQuery = ingredientsRef.child("ingredienti");
                    userQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        //prende la lista degli ingredienti in base alla ricetta
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //ciclo gli ingredienti della ricetta
                                for (IngredienteRicetta ingredienteRicetta : ricetta.getIngredienti()) {
                                    //restituisce l'ingrediente
                                    Ingrediente ingrediente = dataSnapshot.child(ingredienteRicetta.getId()).getValue(Ingrediente.class);
                                    if (ingrediente != null) {
                                        //associa l'ingrediente all'ingrediente ricetta dell'adapter
                                        ingredienteRicetta.setIngrediente(ingrediente);
                                        ingredientsAdapter.add(ingredienteRicetta);
                                    } else {
                                        Log.w("RecipeActivity", "Ingrediente nullo " + ingredienteRicetta.getId());
                                    }

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

        Button compra = findViewById(R.id.pagamento);
        compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check indirizzo
                String messaggio = "sei sicuro di voler acquistare gli ingredienti ad un costo di: ";
                double costo = 0;
                //ciclo gli ingredienti della ricetta e ricavo il costo totale
                for (IngredienteRicetta ingredienteRicetta : ricetta.getIngredienti()) {
                    costo += ingredienteRicetta.getQuantita() * ingredienteRicetta.getIngrediente().getCosto();
                }
                messaggio = messaggio + String.format("%.2f", costo) + "€";
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                builder.setMessage(messaggio)
                        .setPositiveButton("acquista", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                acquista();
                            }
                        })
                        .setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                //il messaggio sarà in base a cosa si clicca
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // controllo i permessi
                if (ActivityCompat.checkSelfPermission(RecipeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(RecipeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(RecipeActivity.this, Locale.getDefault());
                        List<Address> adresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        ClientAddress.setText(adresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    //funzione per acquistare gli ingredienti
    private void acquista(){
        Ordine ordine = new Ordine();
        //setto gli elementi di ordine -necessaria modifica alla classe ordine ora ricetta è di tipo Ricetta (fatto)
        ordine.setRicetta(ricetta.getId());
        ordine.setCliente("1");
        ordine.setData(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        //creazione lista ingredienti e ciclo gli ingredienti
        List<IngredienteOrdine> ingredientiOrdine = new ArrayList<IngredienteOrdine>();
        for(IngredienteRicetta ingredienteRicetta: ricetta.getIngredienti()){
            IngredienteOrdine ingredienteOrdine = new IngredienteOrdine();
            //identifico id ingrediente della ricetta
            ingredienteOrdine.setId(ingredienteRicetta.getId());
            ingredienteOrdine.setStato_consegna("spedito");
            ingredienteOrdine.setQuantita(ingredienteRicetta.getQuantita());
            ingredientiOrdine.add(ingredienteOrdine);

        }
        //settare gli ingredienti dell'ordine effettuato
        //campo indirizzo verificare che non sia vuoto
        ordine.setIndirizzo("");
        ordine.setIngredienti(ingredientiOrdine);

        //salvare gli elementi sul db
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ordineRef = database.getReference();
        //push del nuovo oggetto
        DatabaseReference pushedRef = ordineRef.child("ordini").push();
        //setto la chiave dell'utente
        ordine.setId(pushedRef.getKey());
        Task<Void> task = ordineRef.child("ordini").child(ordine.getId()).setValue(ordine);
        //per aggiungere l'evento
        task.addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RecipeActivity.this, "Acquisto avvenuto con successo", Toast.LENGTH_LONG).show();
                //faccio il back per ritornare nella schermata precedente, finita la parte dell'ordine
                RecipeActivity.this.finish();
            }
        });
    }

}
