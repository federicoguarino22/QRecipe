package it.uninsubria.qrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import it.uninsubria.qrecipe.modelli.Utente;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //prendono gli oggetti del layout
        final EditText nameEditText = findViewById(R.id.name);
        final EditText cognomeEditText = findViewById(R.id.cognome);
        final EditText usernameEditText = findViewById(R.id.username_reg);
        final EditText phoneEditText = findViewById(R.id.cellulare);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText pwdEditText = findViewById(R.id.pwd);
        final EditText confermapwdEditText = findViewById(R.id.confermapwd);
        final Button registrazioneButton = findViewById(R.id.registrazione);
        final RadioGroup tipoRadioGroup = findViewById(R.id.tipo_radiogroup);

        registrazioneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(nameEditText.getText().toString(),
                         cognomeEditText.getText().toString(),
                         usernameEditText.getText().toString(),
                         phoneEditText.getText().toString(),
                         emailEditText.getText().toString(),
                         pwdEditText.getText().toString(),
                         confermapwdEditText.getText().toString(),
                         tipoRadioGroup.getCheckedRadioButtonId());
            }
        });
    }
    private void register(String name, String surname, String username, String phone, String email, String pwd, String confermapwd, int idTipo) {
        boolean isValid = validate(email, pwd, name, surname, username, phone, confermapwd);
        if(isValid){
            //identifica utente o corriere
            String tipo = "";
            if(idTipo==R.id.utente){
                tipo = "utente";
            }
            else if(idTipo==R.id.corriere){
                tipo = "corriere";
            }
            //registrazione utente
            //creo istanza oggetto utente
            Utente utente = new Utente(email, pwd, name, surname, username, phone, tipo);
            control_register(name,  surname,  username,  phone,  email,  pwd,  confermapwd, idTipo);

            //salvare gli elementi sul db
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference();
            //push del nuovo oggetto
            DatabaseReference pushedRef = usersRef.child("utenti").push();
            //oggetto task
            String userId = pushedRef.getKey();
            Task <Void> task = usersRef.child("utenti").child(userId).setValue(utente);
            //per aggiungere l'evento
            task.addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(RegisterActivity.this, "Registrazione avvenuta con successo", Toast.LENGTH_LONG).show();
                    //faccio il back per ritornare nella schermata precedente, finita la parte della registrazione
                    RegisterActivity.this.finish();
                }
            });

        }else{
            Toast.makeText(RegisterActivity.this, "Registrazione non valida", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(String email, String pwd, String name, String surname, String username, String phone, String confermapwd) {
        //se la pwd è diversa dalla sua conferma allora restituirà false
        if(!pwd.equals(confermapwd)){
            return false;
        }return true;

    }

    private boolean updateUser(String email, String password, String name, String surname, String username, String phone, String tipo) {
        //getting the specified user reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("utenti").child(email);

        //aggiorna utente
        Utente user = new Utente(email, password, name, surname, username, phone, tipo);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteUser(String email) {
        //getting the specified user reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("utenti").child(email);

        //rimuovi l'utente
        dR.removeValue();
        return true;
    }

    private void control_register(String name, String surname, String username, String phone, String email, String pwd, String confermapwd, int idTipo) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference();


        Query userQuery = usersRef.child("utenti").orderByChild("email").equalTo(email);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean registerIn = false;
                if(dataSnapshot.exists()) {
                    Utente u = dataSnapshot.getChildren().iterator().next().getValue(Utente.class);
                    if(!u.getEmail().equals(email) || !u.getPhone().equals((phone)) || !u.getUsername().equals(username)){
                        registerIn = true;
                    }
                }
                registerResult(registerIn);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void registerResult(boolean registerIn) {
        //se il login viene effettuato con successo entro nel MainActivity
        if (registerIn) {
            Intent intent = new Intent(this, MainActivity.class);
            //flag activity new task-->crea un nuovo task per la nuova activity
            //flag activity clear task-->cancella quello che c'è nell'attuale task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
