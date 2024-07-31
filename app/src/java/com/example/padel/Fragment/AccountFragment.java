package com.example.padel.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.padel.Activity.GestioneBannati;
import com.example.padel.Activity.GestioneCampiActivity;
import com.example.padel.Activity.GestioneCircoloActivity;
import com.example.padel.Activity.LogActivity;
import com.example.padel.Activity.MaestriActivity;
import com.example.padel.Activity.MainActivity;
import com.example.padel.Activity.OtherInfoActivity;
import com.example.padel.Activity.RichiesteBanActivity;
import com.example.padel.R;
import com.example.padel.Obj.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference ref ;

    private ImageView profilePic,edit,confirm;
    private Button logout;
    private TextView nomeCognome,modPass,modPic,modBio,amministratore,ranking,rep;

    private ImageButton save;
    private Uri imgUri;

    ProgressBar pb;
    ActivityResultLauncher<Intent> resultLauncher;

    String dwUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth= FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        ref = FirebaseDatabase.getInstance().getReference("Utenti").child(mAuth.getCurrentUser().getUid());



    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        logout = (Button) view.findViewById(R.id.btnLogut);
        nomeCognome = (TextView) view.findViewById(R.id.nome_cognomeTXT);

        modPic = (TextView) view.findViewById(R.id.modPic_txt);
        modBio = (TextView) view.findViewById(R.id.modBio_txt);
        amministratore = (TextView) view.findViewById(R.id.admin_txt);
        profilePic = (ImageView) view.findViewById(R.id.fotocampoG);
        edit = (ImageView) view.findViewById(R.id.modfybtn);
        confirm = (ImageView) view.findViewById(R.id.ok);
        ranking = (TextView) view.findViewById(R.id.ranking_txt);
        rep = (TextView) view.findViewById(R.id.rep_txt);
        save =view.findViewById(R.id.btnSavePic);
        pb = view.findViewById(R.id.pb_img);

/*
        String url = "profile_pic/pic_"+mAuth.getCurrentUser().getUid()+".jpg";
        storageRef.child(url).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                dwUrl = "https://"+ task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token" +
                        task.getResult().getQueryParameters("token").get(0);
                Log.d("URL",dwUrl);
                if (getActivity() != null) {
                    Glide.with(getActivity()).load(dwUrl).into(profilePic);
                }

            }
        });*/

        writeData();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LogActivity.class));
                getActivity().finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setVisibility(View.VISIBLE);
                modBio.setEnabled(true);

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    confirm.setVisibility(View.INVISIBLE);
                    modBio.setEnabled(false);
                    ref.child("bio").setValue(modBio.getText().toString());
                    Toast.makeText(getActivity(),"Bio modificata con successo",Toast.LENGTH_SHORT).show();
            }
        });

        registerResult();
        modPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
                i.setType("image/*");
                resultLauncher.launch(i);

            }
        });

        return view;
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

                            profilePic.setImageURI(imgUri);
                            save.setVisibility(View.VISIBLE);
                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(),"Immagine non selzionata",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avanti();
            }
        });
    }


    public void avanti()
    {

        Bitmap img_bitmap =((BitmapDrawable)profilePic.getDrawable()).getBitmap();



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String pathImg = "profile_pic/pic_"+mAuth.getCurrentUser().getUid()+".jpg";

        storageRef.child(pathImg).delete();
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

                        map.put("pic",uri.toString());

                        ref.updateChildren(map);
                        pb.setVisibility(View.INVISIBLE);
                       Toast.makeText(getContext(),"Immagine modificata con successo",Toast.LENGTH_SHORT).show();
                    }
                });



            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                pb.setVisibility(View.VISIBLE);

            }
        });


    }
    private void writeData()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user = snapshot.getValue(User.class);
                    if(user!=null)
                    {
                        nomeCognome.setText(new StringBuilder().append(user.getNome()).append(" ").append(user.getCognome()).toString());
                        modBio.setText(user.getBio());

                        rep.append(""+user.getReputazione());
                        ranking.append(""+user.getRanking() + "-" +user.rankToString());

                        if (getActivity() != null) {
                            Glide.with(getActivity()).load(user.getPic()).into(profilePic);
                        }
                        if(user.isAdmin())
                        {
                            amministratore.setVisibility(View.VISIBLE);
                            amministratore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    View v1 = LayoutInflater.from(getContext()).inflate(R.layout.admin_layout,null);
                                    v1.findViewById(R.id.gestCampi).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(getContext(),"CAMPI",Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getContext(), GestioneCampiActivity.class);
                                            getActivity().startActivity(i);
                                        }
                                    });

                                    v1.findViewById(R.id.btnGestMaestri).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(getContext(), MaestriActivity.class);
                                            i.putExtra("admin",true);
                                            getActivity().startActivity(i);
                                        }
                                    });

                                    v1.findViewById(R.id.gestBan).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                            Intent i = new Intent(getContext(), GestioneBannati.class);
                                           getActivity().startActivity(i);
                                            //  Toast.makeText(getContext(),"BAN",Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                    v1.findViewById(R.id.richBAN).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                            Intent i = new Intent(getContext(), RichiesteBanActivity.class);
                                            getActivity().startActivity(i);
                                           // Toast.makeText(getContext(), "RICHIESTA BAN", Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                    v1.findViewById(R.id.btnGestione).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(getContext(), GestioneCircoloActivity.class);
                                            getActivity().startActivity(i);
                                        }
                                    });

                                    AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
                                            .setTitle("SELEZIONA ")
                                            .setView(v1)
                                            .setNegativeButton("INDIETRO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();


                                                }
                                            }).create();
                                    alertDialog.show();

                                }
                            });
                        }

                       /// DocumentReference docRef = storageRef.
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Errore");
            }
        });

    }
}