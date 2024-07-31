package com.example.padel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Obj.User;
import com.example.padel.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BannatiAdapter extends RecyclerView.Adapter<BannatiAdapter.ViewHolder> {



    private ArrayList<User> utenti;
    private Context context;


    private DatabaseReference ref;


    public BannatiAdapter(ArrayList<User> utenti,Context c)
    {
        this.utenti=utenti;
        this.context = c;
    }

    public BannatiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bannati_row,parent,false);
        return new BannatiAdapter.ViewHolder(view);
    }



    public void onBindViewHolder(BannatiAdapter.ViewHolder holder,int position)
    {
        holder.nome.setText(""+utenti.get(position).getNome() + " " + utenti.get(position).getCognome());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("Utenti").child(utenti.get(position).getUserID());
                Map<String,Object> m = new HashMap<>();
                m.put("stato","attivo");
                ref.updateChildren(m);
            }
        });
    }

    public int getItemCount() {
        return utenti.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nome;
        private ImageButton btn;

        public ViewHolder(View itemView)
        {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeCognomeR);
            btn = itemView.findViewById(R.id.btn_riammetti);
        }
    }
}
