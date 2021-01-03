package it.uninsubria.qrecipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import it.uninsubria.qrecipe.modelli.Ricetta;

//adapter prende la lista dei modelli e crea le view
public class RecipeAdapter extends ArrayAdapter<Ricetta> {
    public RecipeAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getItem accede alla lista interna dell'array e restituisce la posizione
        Ricetta ricetta = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_recipe, parent,false);
        }
        TextView name = (TextView)convertView.findViewById(R.id.recipe_name);
        name.setText(ricetta.getNome());
        ImageView image = (ImageView)convertView.findViewById(R.id.recipe_image);
       // try {
            //per scaricare l'immagine
           // URL url = new URL (ricetta.getImmagine());
           // Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //settare l'immagine
          //  image.setImageBitmap(bmp);
      //  } catch (MalformedURLException e) {
        //    e.printStackTrace();
       // } catch (IOException e) {
        //    e.printStackTrace();
      //  }
        return convertView;
    }
}
