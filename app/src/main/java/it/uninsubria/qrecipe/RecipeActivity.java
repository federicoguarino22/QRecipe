package it.uninsubria.qrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uninsubria.qrecipe.modelli.Ingrediente;
import it.uninsubria.qrecipe.modelli.IngredienteRicetta;
import it.uninsubria.qrecipe.modelli.Ricetta;
import it.uninsubria.qrecipe.modelli.Utente;

public class RecipeActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricetta);

        //dichiaro le istanze del layout
        final TextView name_ricetta= findViewById(R.id.name_ricetta);
        final ListView listaingredienti = findViewById(R.id.list_ingredients);
        final IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeActivity.this);
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
                if(dataSnapshot.exists()) {
                    final Ricetta ricetta = dataSnapshot.getChildren().iterator().next().getValue(Ricetta.class);
                    name_ricetta.setText(ricetta.getNome());
                    DatabaseReference ingredientsRef = database.getReference();
                    Query userQuery = ingredientsRef.child("ingredienti");
                    userQuery.addValueEventListener(new ValueEventListener(){
                        @Override
                        //prende la lista degli ingredienti in base alla ricetta
                        public void onDataChange(DataSnapshot dataSnapshot){
                            if(dataSnapshot.exists()){
                                //ciclo gli ingredienti della ricetta
                                for(IngredienteRicetta ingredienteRicetta: ricetta.getIngredienti()){
                                    //restituisce l'ingrediente
                                    Ingrediente ingrediente= dataSnapshot.child(ingredienteRicetta.getId()).getValue(Ingrediente.class);
                                    //associa l'ingrediente all'ingrediente ricetta dell'adapter
                                    ingredienteRicetta.setIngrediente(ingrediente);
                                    ingredientsAdapter.add(ingredienteRicetta);
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

}
