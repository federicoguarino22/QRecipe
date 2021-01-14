package it.uninsubria.qrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.uninsubria.qrecipe.modelli.Ordine;

public class OrdineAdapter extends ArrayAdapter<Ordine> {

    public OrdineAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getItem accede alla lista interna dell'array e restituisce la posizione
        Ordine ordine = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_orders, parent,false);
        }
        // configurazione dell'id dell'ordine
        TextView idOrdine = (TextView)convertView.findViewById(R.id.idOrdine);
        idOrdine.setText(ordine.getId());

        TextView indirizzo = (TextView)convertView.findViewById(R.id.Indirizzo);
        indirizzo.setText(ordine.getIndirizzo());

        return convertView;
    }

}
