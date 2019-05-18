package com.foxsing;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;



public class widgetReproductorMusica extends AppWidgetProvider {
    public static final String EXTRA_NAME = "NAME";
    public static final String ClaveAudio = "audio";
    public static final String ClaveRemoteViews = "remoteViews";
        public static RemoteViews remoteViews;
    boolean play;
        public widgetReproductorMusica(){
        play=false;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.hasExtra(EXTRA_NAME)) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_appwidget_info);
            remoteViews.setTextViewText(R.id.MostrarNombreCancion, intent.getStringExtra(EXTRA_NAME));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_appwidget_info);
            Intent playintent = new Intent(context, ServicioMusica.class);
            PendingIntent pendienteplayIntent;
            Cancion albumReproducir = ReproducirCanciones.albumReproducir;
            if (!play) {
                play = true;
                playintent.setAction(ServicioMusica.Constants.ACTION.PLAYPAUSE_ACTION);
            } else {
                play = false;
                playintent.setAction(ServicioMusica.Constants.ACTION.PLAYPAUSE_ACTION);
            }
            playintent.putExtra(ClaveAudio, albumReproducir);
            pendienteplayIntent = PendingIntent.getService(context, 0, playintent, 0);
            remoteViews.setOnClickPendingIntent(R.id.Play, pendienteplayIntent);
            Intent nextintent = new Intent(context, ServicioMusica.class);
            nextintent.setAction(ServicioMusica.Constants.ACTION.NEXT_ACTION);
            nextintent.putExtra(ClaveAudio, albumReproducir);
            PendingIntent pendientenextIntent = PendingIntent.getService(context, 0, nextintent, 0);
            remoteViews.setOnClickPendingIntent(R.id.Next, pendientenextIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}