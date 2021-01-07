package it.uninsubria.qrecipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

import it.uninsubria.qrecipe.modelli.IngredienteRicetta;
import it.uninsubria.qrecipe.modelli.Ricetta;

//adapter prende la lista dei modelli e crea le view
public class IngredientsAdapter extends ArrayAdapter<Ricetta> {
    public IngredientsAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getItem accede alla lista interna dell'array e restituisce la posizione
        Ricetta ricetta = getItem(position);
        List<IngredienteRicetta> ingredienti = ricetta.getIngredienti();
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_ingredienti, parent,false);
        }
        TextView name = (TextView)convertView.findViewById(R.id.ingredienti);
        name.setText(ricetta.getNome());

        for(IngredienteRicetta ingrediente: ingredienti){
            TextView prezzo = (TextView)convertView.findViewById(R.id.prezzo_text);
            prezzo.setText(ingrediente.getId());
        }


        return convertView;
    }
    /*per il collegamento dalla ricette alla lista degli ingredienti
    recipe_name.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //passaggio per entrare nella registrazione
            Intent intent_ingredients = new Intent(RecipeAdapter.this, IngredientsAdapter.class);
            startActivity(intent_ingredients);
        }
    });*/
}


