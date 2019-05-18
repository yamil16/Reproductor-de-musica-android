package com.foxsing;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

    public class SeleccionMusicaPlayList extends AppCompatActivity {
    private int posicionPlayListMusica;
    List<Cancion> canciones;
    PlayList playlist;
    public static final String PLAYLISTCONMUSICA = "playlistconmusica";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playlist= new PlayList();
        canciones= new ArrayList<>();
        posicionPlayListMusica=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        Button volver = (Button) findViewById(R.id.volverActivityMain);
        assert volver != null;
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i2 = new Intent(SeleccionMusicaPlayList.this, MainActivity.class);
                i2.putExtra(PLAYLISTCONMUSICA, playlist);
                startActivity(i2);
                finish();
            }
        });
        Intent i2 = getIntent();
        String nombrePlayList;
        nombrePlayList=(String) i2.getSerializableExtra(MainActivity.PLAYLIST);
        TextView t=(TextView)findViewById(R.id.NombrePlaylist);
        assert t != null;
        t.setText(nombrePlayList);
        PlayList playlist=ObtenerIdPlayList(nombrePlayList,this);
        final Long idPlaylist= Long.valueOf(playlist.getId());
        final Context context=this;
        final ListView l = (ListView) findViewById(R.id.listViewPlaylist);
        AdaptarListViewMusica adaptadorMusica= new AdaptarListViewMusica(this, ObtenerTodosLosAudios(this));
        adaptadorMusica.setQuieroAlbum(false);
        assert l != null;
        l.setAdapter(adaptadorMusica);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cancion item = (Cancion) l.getAdapter().getItem(position);
                addTrackToPlaylist(context, item, idPlaylist,  posicionPlayListMusica);
                posicionPlayListMusica++;
                canciones.add(item);
                Toast toast1 = Toast.makeText(getApplicationContext(), "Se agrego la cancion: "+item.getNombre()+" a la Playlist. Por favor, presione proxima cancion que desee agregar o regrese atras.", Toast.LENGTH_LONG);
                toast1.show();

            }
        });
    }
    protected void onStart() {
        super.onStart();
        TextView t=(TextView)findViewById(R.id.NombrePlaylist);
        assert t != null;
        final ListView l = (ListView) findViewById(R.id.listViewPlaylist);
        AdaptarListViewMusica adaptadorMusica= new AdaptarListViewMusica(this, ObtenerTodosLosAudios(this));
        adaptadorMusica.setQuieroAlbum(false);
        assert l != null;
        l.setAdapter(adaptadorMusica);
    }
    private ArrayList<?> ObtenerTodosLosAudios(final Context context) {
        final ArrayList<Cancion> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id",MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.AudioColumns.DISPLAY_NAME,MediaStore.Audio.AudioColumns._ID};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                Cancion tipoMusica = new Cancion();
                String path = c.getString(0); // give path
                String album = c.getString(1); // give album name
                String artist = c.getString(2); // give artist name
                String name =c.getString(3); // name
                String id =c.getString(4); // id
                tipoMusica.setNombre(name);
                tipoMusica.setRuta(path);
                tipoMusica.setAlbun(album);
                tipoMusica.setArtista(artist);
                tipoMusica.setId(id);
                tempAudioList.add(tipoMusica);
            }
            c.close();
        }
        return tempAudioList;
    }

    public PlayList ObtenerIdPlayList(String nombrePlayList, Context context){
        PlayList nuevaPlayList;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {"*"};
        Cursor c= contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI ,projection,null, null, null);
        nuevaPlayList= new PlayList();
        if (c != null) {
            while (c.moveToNext()) {
                String nomPlaylist = c.getString(c.getColumnIndex("name"));
                if(nomPlaylist.equals(nombrePlayList)){
                    String idPlaylist = c.getString(c.getColumnIndex("_ID"));
                    nuevaPlayList.setNombre(nombrePlayList);
                    nuevaPlayList.setId(idPlaylist);
                    c.close();
                    return nuevaPlayList;

                }
            }
            c.close();
        }
        return nuevaPlayList;

    }

    public void addTrackToPlaylist(Context context, Cancion audio, Long playlist_id, int pos) {
        Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Uri newuri = ContentUris.withAppendedId(playlists, playlist_id);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, pos);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio.getId());
        Uri uriS = resolver.insert(newuri, values);
        resolver.notifyChange(Uri.parse("content://media"), null);
    }


}
