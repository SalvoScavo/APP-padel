package com.example.padel.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.padel.R;
import com.example.padel.Obj.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {


    private EditText nome, cognome, pass, email;

    private TextView nomeCircolo;
    private ImageView logo;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        nomeCircolo = findViewById(R.id.nomeC_txt);
        logo = findViewById(R.id.logoCir);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailTxt);
        pass = findViewById(R.id.passTxt);
        nome = findViewById(R.id.nomeTxt);
        cognome = findViewById(R.id.cognomeTxt);
        ref = FirebaseDatabase.getInstance().getReference();

        ref = FirebaseDatabase.getInstance().getReference("Informazioni");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Glide.with(getApplicationContext()).load(snapshot.child("logo").getValue(String.class));
                    nomeCircolo.setText(""+snapshot.child("nomeCircolo").getValue(String.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void reg(View view) {
        String emailTxt = email.getText().toString();
        String passTxt = pass.getText().toString();
        String nomeTxt = nome.getText().toString();
        String cognomeTxt = cognome.getText().toString();

        if (emailTxt.isEmpty() || passTxt.isEmpty() || nomeTxt.isEmpty() || cognomeTxt.isEmpty()) {
            Toast.makeText(this, "Per favore, riempi tutti i campi.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Registrazione effettuata con successo.", Toast.LENGTH_SHORT).show();
                    addInfo( task.getResult().getUser());
                } else {
                    Toast.makeText(RegistrationActivity.this, "Errore durante la registrazione.", Toast.LENGTH_SHORT).show();
                }

            }
            });

    }

    public void addInfo(FirebaseUser user)
    {
        Log.d("TAG", "addInfo: user  "+ user.getUid());
        String nomeTxt = nome.getText().toString();
        String cognomeTxt = cognome.getText().toString();
        User us = new User(user.getUid(),nomeTxt,cognomeTxt);

        ref = FirebaseDatabase.getInstance().getReference("Utenti").child(user.getUid());
                ref.setValue(us);
        email.setText("");
        pass.setText("");
        nome.setText("");
        cognome.setText("");

        startActivity(new Intent(RegistrationActivity.this,OtherInfoActivity.class));
    }
    public void back(View view) {
            finish();
    }

}