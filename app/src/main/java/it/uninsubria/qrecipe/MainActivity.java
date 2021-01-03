package it.uninsubria.qrecipe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uninsubria.qrecipe.modelli.Ricetta;
import it.uninsubria.qrecipe.modelli.Utente;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecipeAdapter recipeAdapter= new RecipeAdapter(this);
        ListView listView = findViewById(R.id.recipe_listview);
        listView.setAdapter(recipeAdapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference();
        Query recipeQuery =  recipeRef.child("ricette");
        recipeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //ciclo le ricette e passo il valore delle ricette
               for(DataSnapshot data:dataSnapshot.getChildren()){
                   Ricetta ricetta =  data.getValue(Ricetta.class);
                   recipeAdapter.add(ricetta);
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }


}