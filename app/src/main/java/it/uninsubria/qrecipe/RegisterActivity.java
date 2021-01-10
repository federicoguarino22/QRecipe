package it.uninsubria.qrecipe;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
                startRegister(nameEditText.getText().toString(),
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
    //funzione per iniziare la registrazione, fa tutte le validazioni
    private void startRegister(String name, String surname, String username, String phone, String email, String pwd, String confermapwd, int idTipo){
        boolean isValid = validate(email, pwd, name, surname, username, phone, confermapwd);
        if(isValid){
            control_register(name, surname, username, phone,  email, pwd, idTipo);
        }else{
            Toast.makeText(RegisterActivity.this, "Registrazione non valida", Toast.LENGTH_LONG).show();
        }
    }
    //funzione che effettua effettivamente la registrazione
    private void register(String name, String surname, String username, String phone, String email, String pwd, int idTipo) {


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


    }
    //validare l'input inserito dall'utente
    private boolean validate(String email, String pwd, String name, String surname, String username, String phone, String confermapwd) {
        //se la pwd è diversa dalla sua conferma allora restituirà false
        if(!pwd.equals(confermapwd)){
            return false;
        }
        if(email== null || email.isEmpty()){
            return false;
        }
        if(username== null || username.isEmpty()){
            return false;
        }
        if(name== null || name.isEmpty()){
            return false;
        }
        if(surname== null || surname.isEmpty()){
            return false;
        }
        if(phone== null || phone.isEmpty()){
            return false;
        }
        
        return true;


    }

    private void control_register(String name, String surname, String username, String phone, String email, String pwd, int idTipo) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference();

        //utenti con tutti i suoi input
        Query userQuery = usersRef.child("utenti");
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean registerIn = true;
                if(dataSnapshot.exists()) {
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        Utente utente =  data.getValue(Utente.class);
                        if(utente.getEmail()!=null && utente.getEmail().equals(email)){
                            registerIn = false;
                            break;
                        }
                        else if(utente.getPhone()!=null && utente.getPhone().equals(phone)){
                            registerIn = false;
                            break;
                        }
                        else if(utente.getUsername()!=null && utente.getUsername().equals(username)){
                            registerIn = false;
                            break;
                        }
                    }
                }
                if(registerIn){
                    register(name, surname, username, phone, email, pwd, idTipo);
                }else{
                    Toast.makeText(RegisterActivity.this, "Utente già esistente", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
