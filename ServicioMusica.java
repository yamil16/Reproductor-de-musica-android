package com.foxsing;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import java.util.ArrayList;


public class ServicioMusica extends Service {
    private MediaPlayer mp;
    private int posicionCancion;
    private int posicionAlbum;
    private ArrayList<Cancion> tempAudioList;
    private ArrayList<Cancion> tempAudioListDefecto;
    private  boolean seCargaronCanciones;
    private boolean seEstaReproduciendo = false;
    private boolean  InicioDefecto=true;
    private RemoteViews remoteViews;
    private String textoNotificacion;
    public static final String CMDTOGGLEPAUSE = "com.foxsing.action.togglepause";
    public static final String CMDSTOP = "com.foxsing.action.stop";
    public static final String CMDPAUSE = "com.foxsing.action.pause";
    public static final String CMDPLAY = "com.foxsing.action.play";
    public static final String CMDPREVIOUS = "com.foxsing.action.previous";
    public static final String CMDNEXT = "com.foxsing.action.next";
    public static final String TOGGLEPAUSE_ACTION ="com.foxsing.action.togglepause";


    public class ReproductorBinder extends Binder {
        private ServicioMusica serv;
        public  ReproductorBinder(ServicioMusica s){
            this.serv=s;
        }
        public ServicioMusica getService(){
            return serv;
        }
    }

    private ReproductorBinder repBinder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(repBinder==null){
            repBinder=new ReproductorBinder(this);
        }
        return repBinder;

    }

    public void onCreate() {
        if(!isReproduciendo()) {
            super.onCreate();
            repBinder = null;
            mp = new MediaPlayer();
            posicionCancion = 0;
            posicionAlbum = 0;
            tempAudioList = new ArrayList<>();
            tempAudioListDefecto=new ArrayList<>();
            seCargaronCanciones = false;
            remoteViews=null;
            InicioDefecto=true;
        }
    }
    public void onDestroy() {
        if (mp != null && mp.isPlaying()) {
            posicionCancion = mp.getCurrentPosition();
            mp.release();
            stopForeground(true);
            seCargaronCanciones = false;
            repBinder=null;
        }
    }
    public void cargarAlbumDefecto(){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c=getContentResolver().query(uri, projection, null, null, null);
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
                tempAudioListDefecto.add(tipoMusica);
            }
            c.close();
        }
    }

    public void cargarAlbum(Cancion am){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c;
        if((am==null)) {
            if (((tempAudioList == null) || ((tempAudioList.size()==0)))){
                c = getContentResolver().query(uri, projection, null, null, null);
            }
            else{
                c = null;
            }

        }
        else {
            tempAudioList = new ArrayList<>();
            String a=am.getAlbun();
            c = getContentResolver().query(uri, projection, MediaStore.Audio.Albums.ALBUM + " = ?", new String[]{a}, null);
        }
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
        seCargaronCanciones=true;
    }

    public static class Constants {
        public interface ACTION {
            String PLAYPAUSE_ACTION ="com.foxsing.action.togglepause";
            String MOSTRAR_NOMBRE="com.foxsing.action.mostrar";
            String PAUSE_ACTION = "com.foxsing.action.pause";
            String PREVIOUS_ACTION = "com.foxsing.action.prev";
            String PLAY_ACTION = "com.foxsing.action.play";
            String NEXT_ACTION = "com.foxsing.action.next";
            String STARTFOREGROUND_ACTION = "com.foxsing.action.startforeground";
            String STOP_ACTION = "com.foxsing.action.Stop";
            String SALIR_ACTION = "com.foxsing.action.Stop";
        }
        public interface NOTIFICATION_ID {
            int FOREGROUND_SERVICE = 101;
        }
    }


    private void addNotification() {

        Intent previusintent = new Intent(this, ServicioMusica.class);
        previusintent.setAction(ServicioMusica.Constants.ACTION.NEXT_ACTION);
        PendingIntent pendientepreviusIntent = PendingIntent.getService(this, 0,  previusintent, 0);
        Intent playintent = new Intent(this, ServicioMusica.class);
        PendingIntent pendienteplayIntent;
        playintent.setAction(ServicioMusica.Constants.ACTION.PLAYPAUSE_ACTION);
        pendienteplayIntent = PendingIntent.getService(this, 0,  playintent, 0);
        Intent nextintent = new Intent(this, ServicioMusica.class);
        nextintent.setAction(ServicioMusica.Constants.ACTION.NEXT_ACTION);
        PendingIntent pendientenextIntent = PendingIntent.getService(this, 0,  nextintent, 0);
        Intent stopintent = new Intent(this, ServicioMusica.class);
        stopintent.setAction(ServicioMusica.Constants.ACTION.STOP_ACTION);
        PendingIntent pendientestopIntent = PendingIntent.getService(this, 0,  stopintent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icono)
                        .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.drawable.icono2))
                        .setContentTitle("Fox Sing")
                        .addAction(R.drawable.previus,null, pendientepreviusIntent)
                        .addAction(R.drawable.playpause,null,pendienteplayIntent)
                        .addAction(R.drawable.next,null,pendientenextIntent)
                        .addAction(R.drawable.stop,null,pendientestopIntent)
                        .setContentText(textoNotificacion);
        Intent notificationIntent = new Intent(this, ReproducirCanciones.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,builder.build());
    }
    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);

        stopForeground(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(InicioDefecto) {
            cargarAlbumDefecto();
              InicioDefecto=false;
        }

        if (intent != null) {
            if(!seCargaronCanciones) {
                ArrayList<Cancion> canciones = (ArrayList<Cancion>) intent.getSerializableExtra(ReproducirCanciones.ClaveAudio);
                if ((canciones!=null)&&(canciones.size() == 1)) {
                    Cancion am = canciones.get(0);
                    cargarAlbum(am);
                } else {
                        tempAudioList = new ArrayList<>();
                        tempAudioList = canciones;
                    seCargaronCanciones = true;
                }
            }
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            if (CMDNEXT.equals(cmd) || Constants.ACTION.NEXT_ACTION.equals(action)) {
                nextMusic();
                actualizarNombreCancion();
            } else if (CMDPREVIOUS.equals(cmd) || Constants.ACTION.PREVIOUS_ACTION.equals(action)) {
                previusMusic();
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isReproduciendo()) {
                    pause();
                    actualizarNombreCancion();
                } else {
                    play();
                    actualizarNombreCancion();
                }
            } else if (CMDPAUSE.equals(cmd) || Constants.ACTION.PAUSE_ACTION.equals(action)) {
                pause();
            } else if (CMDPLAY.equals(cmd) || (Constants.ACTION.PLAY_ACTION.equals(action))) {
                play();
            } else if (CMDSTOP.equals(cmd)|| (Constants.ACTION.STOP_ACTION.equals(action))) {
                Stop();
            } else if (Constants.ACTION.SALIR_ACTION.equals(action)) {
                Salir();
            }
            else if (Constants.ACTION.MOSTRAR_NOMBRE.equals(action)) {
                actualizarNombreCancion();
                remoteViews = (RemoteViews) intent.getSerializableExtra(widgetReproductorMusica.ClaveRemoteViews);
                return START_STICKY;
            }

        }
        addNotification();
        return START_STICKY;
    }

    private void Salir() {
        System.exit(0);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void actualizarNombreCancion() {
        String nombre=getNombreCancion();
        Intent intent = new Intent(this,widgetReproductorMusica.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.xml.example_appwidget_info};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        intent.putExtra(widgetReproductorMusica.EXTRA_NAME, nombre);
        sendBroadcast(intent);
        if(remoteViews==null) {
            remoteViews = new RemoteViews(this.getPackageName(), R.layout.example_appwidget_info);
            remoteViews.setTextViewText(R.id.MostrarNombreCancion, nombre);
        }
        else {
            remoteViews.setTextViewText(R.id.MostrarNombreCancion, nombre);
        }
    }
    public void pause(){
        if (mp != null && mp.isPlaying()) {
            posicionCancion = mp.getCurrentPosition();
            mp.pause();
            seEstaReproduciendo = false;
        }
    }

    public void play(){
        if((tempAudioList==null) ||tempAudioList.size()==0) {
            tempAudioList = tempAudioListDefecto;
        }
        Cancion audio = tempAudioList.get(posicionAlbum);
        mp = MediaPlayer.create(this, Uri.parse(audio.getRuta()));
        mp.seekTo(posicionCancion);
        mp.setLooping(true);
        mp.start();
        seEstaReproduciendo = true;
        textoNotificacion=tempAudioList.get(posicionAlbum).getNombre();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                posicionCancion = 0;
                posicionAlbum++;

            }
        });
    }

    public void nextMusic(){
        if(posicionAlbum<tempAudioList.size()-1)
            posicionAlbum++;
        else
            posicionAlbum=0;

        posicionCancion=0;
        mp.release();
        play();
    }
    public void previusMusic(){
        if(posicionAlbum==0)
            posicionAlbum=tempAudioList.size()-1;
        else
            posicionAlbum--;
        posicionCancion=0;
        mp.release();
        play();
    }

    public boolean isReproduciendo(){
        return seEstaReproduciendo;
    }
    public String getNombreCancion(){
        return tempAudioList.get(posicionAlbum).getNombre();
    }
    public String getArtista(){
        return tempAudioList.get(posicionAlbum).getArtista();
    }
    public void Stop(){
        mp.release();
        stopForeground(true);
        parar();
    }
    public void parar() {
        removeNotification();
    }
}