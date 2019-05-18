package com.foxsing;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final String ALBUM = "album";
    public static final String PLAYLIST = "playlist";
    Button crearPlayList;
    final Context context = this;
    boolean ViendoAlbum;
    Button VerAlbum;
    Button Salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ViendoAlbum = false;
        final boolean[] SeReproducioCanciones = {false};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Salir=(Button) findViewById(R.id.Salir);
        Salir.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 finish();
                                                 Intent intent = new Intent(Intent.ACTION_MAIN);
                                                 intent.addCategory(Intent.CATEGORY_HOME);
                                                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                 startActivity(intent);
                                             }
        });

        crearPlayList = (Button) findViewById(R.id.crearnuevalistarep);
        crearPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
                View mView = layoutInflaterAndroid.inflate(R.layout.dialogo_escribir_nueva_playlist, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                userInputDialogEditText.setText("");
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String nombrePlaylist=userInputDialogEditText.getText().toString();
                                addnewPlaylist(context,nombrePlaylist);
                                Intent i = new Intent(MainActivity.this, SeleccionMusicaPlayList.class);
                                i.putExtra(PLAYLIST, nombrePlaylist);
                                startActivity(i);
                            }
                        })

                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }

        });
        final ListView l = (ListView) findViewById(R.id.listViewAlbun);
        if (ViendoAlbum) {
            assert l != null;
            l.setAdapter(new AdaptarListViewMusica(this, getAllAudioFromDevice(this)) {

            });
            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(ViendoAlbum)
                        EjecutarReproducirAlbum(position,l,SeReproducioCanciones);

                }
            });
        } else { //cargo playlist
            assert l != null;
            l.setAdapter(new AdaptarListViewPlaylist(this, getAllUniquePlaylists(this)) {


            });
              l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ViendoAlbum)
                    EjecutarReproducirAlbum(position, l, SeReproducioCanciones);
                else {
                    Intent aux = null;
                    PlayList play = (PlayList) l.getAdapter().getItem(position);
                    Long idPlaylist = Long.valueOf(play.getId());
                    ArrayList<String> audio_ids = new ArrayList<>();
                    ArrayList<Cancion> tempAudioList = new ArrayList<>();
                    String nombrePlayListBusco = play.getNombre();
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                    Cursor c = contentResolver.query(playlists, new String[]{"*"}, null, null, null);
                    long playlistId = 0;
                    assert c != null;
                    c.moveToFirst();
                    do {
                        String plname = c.getString(c.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                        if (plname.equalsIgnoreCase(nombrePlayListBusco)) {
                            playlistId = c.getLong(c.getColumnIndex(MediaStore.Audio.Playlists._ID));
                            break;
                        }
                    } while (c.moveToNext());
                    c.close();
                    if (playlistId != 0) {

                        String[] MEDIA_COLUMNS = new String[]{
                                MediaStore.Audio.Playlists.Members.ARTIST,
                                MediaStore.Audio.Playlists.Members.TITLE,
                                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                                MediaStore.Audio.Playlists.Members.DISPLAY_NAME,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Playlists.Members._ID
                        };
                        ContentResolver cr = getContentResolver();
                        //Hacemos la consulta
                        Cursor cursor = cr.query(MediaStore.Audio.Playlists.Members.getContentUri("external",idPlaylist ),
                                MEDIA_COLUMNS, //Columnas a devolver
                                null,       //Condición de la query
                                null,       //Argumentos variables de la query
                                null);      //Orden de los resultados
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                String audio_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                                Cancion tipoMusica = new Cancion();
                                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)); // give album name
                                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)); // give artist name
                                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DISPLAY_NAME));
                                String path = PathCancion(album); //Busco un path para que service no se rompa al crear mp en play
                                tipoMusica.setNombre(name);
                                tipoMusica.setAlbun(album);
                                tipoMusica.setArtista(artist);
                                tipoMusica.setRuta(path);
                                tempAudioList.add(tipoMusica);
                                audio_ids.add(audio_id);
                            }

                        }
                        assert cursor != null;
                        cursor.close();
                    }
                    if (!SeReproducioCanciones[0]) {
                        Intent i = new Intent(MainActivity.this, ReproducirCanciones.class);
                        i.putExtra(ALBUM, tempAudioList);
                        SeReproducioCanciones[0] = true;
                        startActivity(i);
                    } else {
                        Intent i = new Intent(MainActivity.this, ReproducirCanciones.class);
                        i.putExtra(ALBUM, tempAudioList);
                        aux.replaceExtras(i);
                        startActivity(aux);
                    }
                }
            }
        });
        }

        Button verListView = (Button) findViewById(R.id.Ver_PlayList);
        assert verListView != null;
        verListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViendoAlbum=false;
                l.setAdapter(ActualizarListViewPlaylist());
            }
        });

        VerAlbum = (Button) findViewById(R.id.VerAlbum);
        VerAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViendoAlbum=true;
                l.setAdapter(ActualizarListViewAlbum());
            }
        });
    }
    public void EjecutarReproducirAlbum(int position, ListView l, boolean[] SeReproducioCanciones){
        Intent aux = null;
        ArrayList<Cancion> tempAudioList = new ArrayList<>();
        Cancion item = (Cancion) l.getAdapter().getItem(position);
        tempAudioList.add(item);
        if (!SeReproducioCanciones[0]) {
            Intent i = new Intent(MainActivity.this, ReproducirCanciones.class);
            i.putExtra(ALBUM, tempAudioList);
            SeReproducioCanciones[0] = true;
            startActivity(i);
        } else {
            Intent i = new Intent(MainActivity.this, ReproducirCanciones.class);
            i.putExtra(ALBUM, tempAudioList);
            aux.replaceExtras(i);
            startActivity(aux);
        }
    }


    private ListAdapter ActualizarListViewAlbum(){
        return new AdaptarListViewMusica(this, getAllAudioFromDevice(this));
    }

    private ListAdapter ActualizarListViewPlaylist() {
        return new AdaptarListViewPlaylist(this, getAllUniquePlaylists(this));
    }




    public static  ArrayList<PlayList> getAllUniquePlaylists(Context context) {
        final ArrayList<PlayList> tempAudioList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {"*"};
        Cursor c= contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,  projection,null, null,MediaStore.Audio.Playlists.NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                PlayList nuevaPlayList = new PlayList();
                String idPlaylist = c.getString(0);
                String nombrePlaylist = c.getString(c.getColumnIndex("name"));
                nuevaPlayList.setNombre(nombrePlaylist);
                nuevaPlayList.setId(idPlaylist);
                tempAudioList.add(nuevaPlayList);
            }
            c.close();
        }
        return tempAudioList;
    }


    public ArrayList<Cancion> getAllAudioFromDevice(final Context context) {
        final ArrayList<Cancion> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST, };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);//MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%utm%"}, null);
        if (c != null) {
            while (c.moveToNext()) {
                Cancion tipoMusica = new Cancion();
                String path = c.getString(0); // give path
                String album = c.getString(1); // give album name
                String artist = c.getString(2); // give artist name
                String name = path.substring(path.lastIndexOf("/") + 1); // name
                tipoMusica.setNombre(name);
                tipoMusica.setAlbun(album);
                tipoMusica.setArtista(artist);
                tipoMusica.setRuta(path);
                tempAudioList.add(tipoMusica);
            }
            c.close();
        }
        return tempAudioList;
    }


    public void addnewPlaylist(Context context, String newplaylist) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newplaylist);
        Uri newuri = resolver.insert(uri, values);

    }
    public String PathCancion(String album) {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c;
        String path="";
        c = getContentResolver().query(uri, projection, MediaStore.Audio.Albums.ALBUM + " = ?", new String[]{album}, null);
        if (c != null) while (c.moveToNext()) {
            path = c.getString(0); // give path
            return path;
        }
        return path;
    }

}
