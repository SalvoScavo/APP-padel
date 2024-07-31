package com.example.padel.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.padel.Obj.User;
import com.example.padel.R;
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

import java.util.Map;

public class LogActivity extends AppCompatActivity {

    EditText pass,email;

    TextView nome;
    ImageView logo;
    private FirebaseAuth mAuth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null)
            updateUI(mAuth.getCurrentUser());

        email = findViewById(R.id.emailTxt);
        pass = findViewById(R.id.passTxt);

        nome = findViewById(R.id.nomeCircolo);
        logo = findViewById(R.id.imgLogo);

        ref = FirebaseDatabase.getInstance().getReference("Informazioni");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                  //  Map<String,Object> mappa  =
                    Glide.with(getApplicationContext()).load(snapshot.child("logo").getValue(String.class)).into(logo);
                    nome.setText(""+ snapshot.child("nomeCircolo").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void log(View v)
    {

        String string_email = email.getText().toString().trim();
        String paswd = pass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(string_email, paswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "SignEmail:success");
                            Toast.makeText(LogActivity.this, "Authentication Success.",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "SignEmail:failure", task.getException());
                            Toast.makeText(LogActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });

    }

    public void updateUI(FirebaseUser user)
    {
        if(user == null) return;

        startActivity(new Intent(LogActivity.this,MainActivity.class));
        finish();
    }

    public void reg(View v){
        startActivity(new Intent(LogActivity.this, RegistrationActivity.class));
    }
}