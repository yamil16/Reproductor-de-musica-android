package com.foxsing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

    public class ReproducirCanciones  extends Activity {
    public static final String ClaveAudio = "audio";
    private Button play;
    private Button pause;
    private WebView webview;
    private TextView nombreCancion;
    private TextView artistaCancion;
    private ServicioMusica servicioBinderEnEjecucion;

    public static Cancion albumReproducir;
    private Intent servicioEjecucion;

    public void setCallBackBinder(ServicioMusica.ReproductorBinder callBackBinder) {

        this.servicioBinderEnEjecucion = callBackBinder.getService();
    }

    public class LocalServiceConnector implements ServiceConnection {
        private ReproducirCanciones localActivity;

        public LocalServiceConnector(ReproducirCanciones localServiceActivity) {
            this.localActivity = localServiceActivity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.localActivity.setCallBackBinder((ServicioMusica.ReproductorBinder) service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalServiceConnector conexionConServicio = new LocalServiceConnector(this);
        Intent i2 = getIntent();
        ArrayList<Cancion> canciones;
        canciones = (ArrayList<Cancion>) i2.getSerializableExtra(MainActivity.ALBUM);
        Intent i = new Intent(ReproducirCanciones.this, ServicioMusica.class);
        i.putExtra(ClaveAudio, canciones);// (ALBUM,item);
        i.setAction(ServicioMusica.Constants.ACTION.STARTFOREGROUND_ACTION);
        servicioEjecucion = i;
        bindService(i, conexionConServicio, Context.BIND_AUTO_CREATE);
        startService(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reproduciendo_musica);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        Button siguiente = (Button) findViewById(R.id.next);
        Button anterior = (Button) findViewById(R.id.previus);
        Button volver = (Button) findViewById(R.id.volverActivityMain);
        nombreCancion = (TextView) findViewById(R.id.decirnombrecancion);
        artistaCancion = (TextView) findViewById(R.id.decirnombreArtista);
        webview= (WebView) findViewById(R.id.verpaginaWeb);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar(v);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausar(v);
            }
        });
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siguiente(v);
            }
        });
        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anterior(v);
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras(v);
            }
        });
        if (servicioBinderEnEjecucion != null && servicioBinderEnEjecucion.isReproduciendo()) {
            pause.setEnabled(true);
            play.setEnabled(false);
        } else {
            pause.setEnabled(false);
            play.setEnabled(true);
        }
    }


    public void iniciar(final View v) {

        play.setEnabled(false);
        pause.setEnabled(true);
        servicioBinderEnEjecucion.play();
        String nombreCan=servicioBinderEnEjecucion.getNombreCancion();
        int pos=nombreCan.indexOf(".mp3");
        nombreCan=nombreCan.substring(0,pos);

        String nombArt=servicioBinderEnEjecucion.getArtista();

        if ((nombArt.contains("unknown"))&& (nombreCan.contains("-"))){
            int posArtista=nombreCan.indexOf("-");
            nombArt=nombreCan.substring(0,posArtista);
            nombreCan=nombreCan.substring(posArtista+1,nombreCan.length());
            nombreCan=nombreCan.replace(' ','_');

        }
        nombreCancion.setText(nombreCan);
        artistaCancion.setText(nombArt);
        String direccion="https://lyrics.wikia.com/wiki/"+nombArt+":"+nombreCan;
        webview.loadUrl(direccion);

    }

    public void pausar(View v) {
        play.setEnabled(true);
        pause.setEnabled(false);
        servicioBinderEnEjecucion.pause();
    }


    public void siguiente(View v) {
        servicioBinderEnEjecucion.nextMusic();
        nombreCancion.setText(servicioBinderEnEjecucion.getNombreCancion());
        artistaCancion.setText(servicioBinderEnEjecucion.getArtista());

        String nombreCan=servicioBinderEnEjecucion.getNombreCancion();
        int pos=nombreCan.indexOf(".mp3");
        if(pos>0)
            nombreCan=nombreCan.substring(0,pos);
        String nombArt=servicioBinderEnEjecucion.getArtista();
        if ((nombArt.contains("unknown"))&& (nombreCan.contains("-"))){
            int posArtista=nombreCan.indexOf("-");
            nombArt=nombreCan.substring(0,posArtista);
            nombreCan=nombreCan.substring(posArtista+1,nombreCan.length());
            nombreCan=nombreCan.replace(' ','_');
        }
        nombreCancion.setText(nombreCan);
        artistaCancion.setText(nombArt);
        String direccion="https://lyrics.wikia.com/wiki/"+nombArt+":"+nombreCan;
        webview.loadUrl(direccion);
    }

    public void anterior(View v) {
        servicioBinderEnEjecucion.previusMusic();
        nombreCancion.setText(servicioBinderEnEjecucion.getNombreCancion());
        artistaCancion.setText(servicioBinderEnEjecucion.getArtista());
        String nombreCan=servicioBinderEnEjecucion.getNombreCancion();
        int pos=nombreCan.indexOf(".mp3");
        nombreCan=nombreCan.substring(0,pos);
        String nombArt=servicioBinderEnEjecucion.getArtista();
        if ((nombArt.contains("unknown"))&& (nombreCan.contains("-"))){
            int posArtista=nombreCan.indexOf("-");
            nombArt=nombreCan.substring(0,posArtista);
            nombreCan=nombreCan.substring(posArtista+1,nombreCan.length());
            nombreCan=nombreCan.replace(' ','_');
        }
        nombreCancion.setText(nombreCan);
        artistaCancion.setText(nombArt);
        String direccion="https://lyrics.wikia.com/wiki/"+nombArt+":"+nombreCan;
        webview.loadUrl(direccion);
    }

    public void volverAtras(View v) {
        servicioBinderEnEjecucion.parar();
        stopService(servicioEjecucion);
        Intent i2 = new Intent(ReproducirCanciones.this, MainActivity.class);
        startActivity(i2);
        this.finish();
    }
    protected void onDestroy(){
        super.onDestroy();
        servicioBinderEnEjecucion=null;
    }

}
