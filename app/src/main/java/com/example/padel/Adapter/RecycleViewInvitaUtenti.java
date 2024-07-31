package com.example.padel.Adapter;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Obj.User;
import com.example.padel.R;

import java.util.ArrayList;

public class RecycleViewInvitaUtenti extends RecyclerView.Adapter<RecycleViewInvitaUtenti.ViewHolder> {


    private ArrayList<User> listaUtenti;
    private Context context;
    private Activity activity;

    public RecycleViewInvitaUtenti(ArrayList<User> listaUser, Context context,Activity activity) {
        this.listaUtenti = listaUser;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecycleViewInvitaUtenti.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.utente_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewInvitaUtenti.ViewHolder holder, int position) {
        String   nome=listaUtenti.get(position).getNome() +" "+ listaUtenti.get(position).getCognome();
        String    idInv = listaUtenti.get(position).getUserID();
        holder.nomecognome.setText(listaUtenti.get(position).getNome() +" "+ listaUtenti.get(position).getCognome());
        holder.ranking.setText("Ranking: "+listaUtenti.get(position).rankToString());

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("nome", nome);
                int retRes= activity.getIntent().getIntExtra("button",0);
                resultIntent.putExtra("idBtn", retRes);
             //   Toast.makeText(v.getContext(), "id " + idInv,Toast.LENGTH_SHORT).show();
                resultIntent.putExtra("invitato",idInv);
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUtenti.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nomecognome,ranking;

        private Button btn;

        public ViewHolder(View itemView) {
            super(itemView);
            nomecognome = (TextView) itemView.findViewById(R.id.nc_txt);
            ranking = (TextView) itemView.findViewById(R.id.r_txt);
            btn = (Button) itemView.findViewById(R.id.btn_segnala);
        }
    }
}
