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
        //checkbox non modificabile dall'utente
        ingredienteOrdine.setClickable(false);
        //se è consegnato verrà fuori la spunta
        ingredienteOrdine.setChecked(ingrediente.getStato_consegna().equals("consegnato"));

        TextView nome_ingrediente = (TextView) convertView.findViewById(R.id.nome);
        nome_ingrediente.setText(ingrediente.getIngrediente().getNome());

        TextView prezzoTot = (TextView) convertView.findViewById(R.id.prezzoPerQuantità);
        Double costo = ingrediente.getIngrediente().getCosto() * ingrediente.getQuantita();
        //stampa ultimi due decimali, percentuale tutti i valori interi
        String costo_stringa = String.format("%.2f", costo)+ " €";
        prezzoTot.setText(costo_stringa);

        TextView quantita = (TextView) convertView.findViewById(R.id.quantità);
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