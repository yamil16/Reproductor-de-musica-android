package com.foxsing;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import java.util.ArrayList;


public class AdaptarListViewPlaylist extends BaseAdapter {
    private ArrayList<?> entradas;
    private Context contexto;
    public AdaptarListViewPlaylist(Context contexto, ArrayList<?> entradas) {
        super();
        this.contexto = contexto;
        this.entradas = entradas;
    }
    public int getCount() {
        return  entradas.size();
    }
    public Object getItem(int position) {
        return entradas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_playlist, parent, false);
        }
        TextView tvTitle = (TextView) rowView.findViewById(R.id.nombrePlaylist);
        PlayList playlistnombre= (PlayList) this.entradas.get(position);
        tvTitle.setText(playlistnombre.getNombre());
        return rowView;
    }

}
