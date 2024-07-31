package com.example.padel.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.padel.Obj.Notifica;
import com.example.padel.Obj.User;
import com.example.padel.R;

import java.util.ArrayList;

public class NotificheAdapter extends RecyclerView.Adapter<NotificheAdapter.ViewHolder> {

    private ArrayList<Notifica> listaNotifiche;
    private Context context;


    public NotificheAdapter(ArrayList<Notifica> lista, Context context) {
        this.listaNotifiche = lista;
        this.context = context;

    }


    public void onBindViewHolder(@NonNull NotificheAdapter.ViewHolder holder, int position) {

        holder.messaggio.setText(listaNotifiche.get(position).getMessaggio());
        if(listaNotifiche.get(position).getMessaggio().contains("invitato"))
        {
            holder.titolo.setText("Partita");
        }
        else
        {
            holder.titolo.setText("Segnalazione");
        }
    }



    public NotificheAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notifica_row,parent,false);
        return new NotificheAdapter.ViewHolder(view);
    }

    public int getItemCount() {
        return listaNotifiche.size();
    }








    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView messaggio,titolo;


        public ViewHolder(View itemView) {
            super(itemView);
            messaggio = (TextView) itemView.findViewById(R.id.not_txt);
            titolo = (TextView) itemView.findViewById(R.id.titolo_notifica_txt);
        }
    }
}
