package it.uninsubria.qrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ConcurrentSkipListSet;

import it.uninsubria.qrecipe.modelli.IngredienteOrdine;
import it.uninsubria.qrecipe.modelli.IngredienteRicetta;

public class OrderDetailsAdapter extends ArrayAdapter<IngredienteOrdine> {
    public OrderDetailsAdapter(@NonNull Context context) {
        super(context, 0);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getItem accede alla lista interna dell'array e restituisce la posizione
        IngredienteOrdine ingrediente = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_order_details, parent,false);
        }
        CheckBox ingredienteOrdine = (CheckBox) convertView.findViewById(R.id.checkBoxIngrediente);
        ingredienteOrdine.setText(ingrediente.getIngrediente().getNome());

        TextView quantità = (TextView) convertView.findViewById(R.id.quantità);
        quantità.setText(ingrediente.getQuantita() + " " +ingrediente.getIngrediente().getUnita_misura());

        TextView prezzoTot = (TextView) convertView.findViewById(R.id.prezzoPerQuantità);
        prezzoTot.setText( (ingrediente.getQuantita()*ingrediente.getIngrediente().getCosto()) + " euro");

        return convertView;
    }

}