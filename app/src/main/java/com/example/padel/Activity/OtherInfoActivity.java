package com.example.padel.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class OtherInfoActivity extends AppCompatActivity {
    private Spinner spinner;
    private ImageView pic;

    private EditText bio;

    private Button btreg;

    private RadioGroup rg;

    private ProgressBar pb;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;

    DatabaseReference ref ;


    private Uri imgUri;
    ActivityResultLauncher<Intent> resultLauncher;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_info);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        //spinner = findViewById(R.id.spinnerRank);
        pic = findViewById(R.id.proPic);
        bio =findViewById(R.id.bioTxt);
        btreg = findViewById(R.id.btReg);
        pb = findViewById(R.id.pb);
        rg =findViewById(R.id.radioGrLevel);
    /*
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("LOW");
        arrayList.add("MEDIUM");
        arrayList.add("HIGH");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);*/

        registerResult();

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
                i.setType("image/*");
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

                            pic.setImageURI(imgUri);
                        }catch (Exception e)
                        {
                            Toast.makeText(OtherInfoActivity.this,"Immagine non selzionata",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void avanti(View view)
    {
        int level = rg.getCheckedRadioButtonId() == R.id.LOW ? 0 : rg.getCheckedRadioButtonId() == R.id.MEDIUM ? 201 : 501;
        String bio_string = bio.getText().toString();
        Bitmap img_bitmap =((BitmapDrawable)pic.getDrawable()).getBitmap();



       ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String pathImg = "profile_pic/pic_"+mAuth.getCurrentUser().getUid()+".jpg";

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
                        ref = FirebaseDatabase.getInstance().getReference("Utenti").child(mAuth.getCurrentUser().getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("bio",bio_string);
                        map.put("pic",uri.toString());
                        map.put("ranking",level);
                        ref.updateChildren(map);
                        Intent i = new Intent(OtherInfoActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });



            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                btreg.setEnabled(false);
                pb.setVisibility(View.VISIBLE);

            }
        });


    }

}