package de.uni_koblenz.soma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Chris on 07.06.2017.
 */

public class LocationListener implements android.location.LocationListener {
    private static final String TAG = "LocationListener";
    private DatabaseHelper database;
    public LocationListener(String provider, DatabaseHelper db)
    {
        Log.v(TAG, "Provider: " + provider);
        database = db;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "NewLocation: " + location);
        database.addLocation(location);
        updateNotificationBar();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v(TAG, "ProviderStatusChagned: " + provider + ";"+status+";"+extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(TAG, "Providerenabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v(TAG, "Providerdisabled: " + provider);
    }

    private void updateNotificationBar() {
        /*long dataCount = database.getLocationsCount();
        String contentText = "Ihre persönlichen Daten werden gesammelt"; // TODO
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(context, SoMAActivity.class);
        notificationIntent.setAction("de.uni_koblenz.soma.RESUME");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context)
                        .setContentTitle("SoMA")
                        .setContentText(contentText)
                        .setSubText("Data: " + dataCount)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(101, notification.build());

        Log.d(TAG, "updateNotification: dataCount: " + dataCount + ", notificationIntent: " + contentText);*/
    }
}
