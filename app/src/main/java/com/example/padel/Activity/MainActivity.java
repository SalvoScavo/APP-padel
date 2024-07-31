package com.example.padel.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.padel.Fragment.AccountFragment;
import com.example.padel.Fragment.HistoryFragment;
import com.example.padel.Fragment.HomeFragment;
import com.example.padel.Fragment.NotificationFragment;
import com.example.padel.Fragment.SearchFragment;
import com.example.padel.Obj.User;
import com.example.padel.R;
import com.example.padel.Services.BackgroundService;
import com.example.padel.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    private DatabaseReference ref;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("Utenti").child(mAuth.getCurrentUser().getUid());


        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    if(user.getStato().equalsIgnoreCase("BANNATO"))
                    {
                        Toast.makeText(MainActivity.this, "Utente Bannato", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(new HomeFragment());

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {

                        if(isOpen)
                            binding.bottomNavigationView.setVisibility(View.GONE);
                        else
                            binding.bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });


        binding.bottomNavigationView.setOnItemSelectedListener(menuItem -> {

            Log.d("BottomNav",menuItem.getItemId()+"");
            if(menuItem.getItemId() == R.id.home)
            {
                Log.d("BottomNav","Home");
                replaceFragment(new HomeFragment());

            }else if(menuItem.getItemId() == R.id.account)
            {
                Log.d("BottomNav","Account");
                replaceFragment(new AccountFragment());
            }else if(menuItem.getItemId() == R.id.notifiche)
            {
                Log.d("BottomNav","Notifiche");
                replaceFragment(new NotificationFragment());
            }else if(menuItem.getItemId() == R.id.partite)
            {
                Log.d("BottomNav","Partite");
                replaceFragment(new HistoryFragment());
            }else if(menuItem.getItemId() == R.id.search)
            {
                Log.d("BottomNav","Search");
                replaceFragment(new SearchFragment());
            }


            return true;
        });
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null)
        {
            Toast.makeText(MainActivity.this,"User is not logged in",Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void replaceFragment(Fragment f)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame,f);
        ft.commit();
    }
    public void logout(View view)
    {
        mAuth.signOut();
        Toast.makeText(MainActivity.this,"Arrivederci",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this,LogActivity.class));
        finish();
    }
}