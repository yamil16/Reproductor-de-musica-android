package com.foxsing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class AdaptarListViewMusica  extends BaseAdapter {
    private ArrayList<?> entradas;
    private Context contexto;
    private boolean QuieroAlbum;

    public AdaptarListViewMusica(Context contexto, ArrayList<?> entradas) {
        super();
        this.contexto = contexto;
        this.entradas = entradas;
        QuieroAlbum=true;
    }
    public void setQuieroAlbum(boolean valor){
        QuieroAlbum=valor;
    }
    public  boolean getQuieroAlbum(){
        return QuieroAlbum;
    }
    @Override
    public int getCount() {
        return  entradas.size();
    }

    @Override
    public Object getItem(int position) {
        return entradas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contexto
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_musica, parent, false);
        }
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.fotoAlbunListItem);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.TextoAlbumListItem);
        Cancion audio= (Cancion) this.entradas.get(position);
        if(getQuieroAlbum())
            tvTitle.setText(audio.getAlbun());
        else
            tvTitle.setText(audio.getNombre());
        ivItem.setImageResource(audio.getImagen());
        return rowView;
    }

}
