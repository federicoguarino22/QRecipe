package it.uninsubria.qrecipe;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uninsubria.qrecipe.modelli.Utente;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //prendono gli oggetti del layout
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passaggio per entrare nella registrazione
                Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_register);
            }
        });

    }

    /** Called when the user taps the Send button */

    private void login(String email, String pwd) {
        //sharepreferences si identifica per il nome, sono condivise da tutte le applicazioni,ho messo il nome completo dell'app per evitare conflitti
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("it.uninsubria.qrecipe.preferences", Context.MODE_PRIVATE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference();

        Query userQuery = usersRef.child("utenti").orderByChild("email").equalTo(email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean loggedIn = false;
                String tipoUtente = null;
                if(dataSnapshot.exists()) {
                    Utente u = dataSnapshot.getChildren().iterator().next().getValue(Utente.class);
                    tipoUtente = u.getTipo();
                    if(u.getPassword().equals(pwd)) {
                        //edit da oggetto editor
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userId", u.getId());
                        editor.apply();
                        loggedIn = true;
                    }
                }

                loginResult(loggedIn, tipoUtente);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void loginResult(boolean loggedIn, String tipo) {
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.GONE);
        //se il login viene effettuato con successo entro nel MainActivity
        if(loggedIn && tipo.equals("utente")){
            Intent intent = new Intent(this, MainActivity.class);
            //flag activity new task-->crea un nuovo task per la nuova activity
            //flag activity clear task-->cancella quello che c'è nell'attuale task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            /**
             *
             */
        }else if(loggedIn && tipo.equals("corriere")){
            Intent intent = new Intent(this, DeliveryInfo.class);
            //flag activity new task-->crea un nuovo task per la nuova activity
            //flag activity clear task-->cancella quello che c'è nell'attuale task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            Toast.makeText(LoginActivity.this, "Login errato", Toast.LENGTH_LONG).show();
        }
    }
}
