package com.example.padel.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.padel.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class GestioneCircoloActivity extends AppCompatActivity {

    private EditText nome;
    private ImageView img;

    private Uri imgUri;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;

    DatabaseReference ref ;

    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestione_circolo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nome = findViewById(R.id.changeName);
        img = findViewById(R.id.imgLogoGestione);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        registerResult();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
                i.setType("image/png");
                resultLauncher.launch(i);
            }
        });
    }

    private void registerResult()
    {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try{
                            imgUri = o.getData().getData();
                            Log.d("imgUri",imgUri.toString());

                            img.setImageURI(imgUri);
                        }catch (Exception e)
                        {
                            Toast.makeText(GestioneCircoloActivity.this,"Immagine non selzionata",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void avanti(View v)
    {

        Bitmap img_bitmap =((BitmapDrawable)img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img_bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        String pathImg = "FotoCircolo/"+System.currentTimeMillis()+".png";


        StorageReference StoRef = storageRef.child(pathImg);

        UploadTask uploadTask = StoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Gestisci il fallimento
                Log.e("FirebaseStorage", "Upload failed", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload completato con successo
                Log.d("FirebaseStorage", "Upload successful");

                StoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        ref = FirebaseDatabase.getInstance().getReference("Informazioni");
                        HashMap<String,Object> map = new HashMap<>();
                        if(!nome.getText().toString().equals(""))
                             map.put("nomeCircolo",nome.getText().toString());
                        map.put("logo",uri.toString());
                        ref.updateChildren(map);
                       // Intent i = new Intent(GestioneCircoloActivity.this,MainActivity.class);
                        //startActivity(i);
                        finish();
                    }
                });

                //

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Toast.makeText(getApplicationContext(),"Caricamento in corso...",Toast.LENGTH_SHORT).show();
            }
        });
    }
}