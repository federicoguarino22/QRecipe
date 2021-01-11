package it.uninsubria.qrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.io.IOException;


import java.util.ArrayList;
import java.util.List;

import it.uninsubria.qrecipe.modelli.IngredienteRicetta;
import it.uninsubria.qrecipe.modelli.Ricetta;

//adapter prende la lista dei modelli e crea le view
public class IngredientsAdapter extends ArrayAdapter<IngredienteRicetta> {
    public IngredientsAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getItem accede alla lista interna dell'array e restituisce la posizione
        IngredienteRicetta ingrediente = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_ingredients, parent,false);
        }
        TextView name = (TextView)convertView.findViewById(R.id.ingredienti);
        name.setText(ingrediente.getIngrediente().getNome());

        TextView prezzo = (TextView)convertView.findViewById(R.id.prezzo_text);
        Double costo = ingrediente.getIngrediente().getCosto() * ingrediente.getQuantita();
        //stampa ultimi due decimali, percentuale tutti i valori interi
        String costo_stringa = String.format("%.2f", costo)+ " €";
        prezzo.setText(costo_stringa);

        TextView quantita = (TextView)convertView.findViewById(R.id.dosi);
        Double dose = ingrediente.getQuantita();
        String dose_stringa = "";
        if(dose==0){
            dose_stringa = "q.b";
        }
        else{
           dose_stringa = String.format("%.2f", dose)+ ingrediente.getIngrediente().getUnita_misura();
        }
        quantita.setText(dose_stringa);

        return convertView;
    }

}


