package de.uni_koblenz.soma;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private static final int LOCATION_INTERVAL = 5000;
    private static final float LOCATION_DISTANCE = 10f;
    private DatabaseHelper database = null;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service starting");
        database = new DatabaseHelper(this);

        startForeground(101, updateNotificationBar());
        Log.i(TAG, "Service started");
    }

    private void initialize() {
        initializeLocationManager();
        initializeLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);
        } catch( SecurityException e ){
            Log.w(TAG, "Service could not be started: No Permission for location");
            stopSelf();
        }

        //updateNotificationBar();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service stopped");
        super.onDestroy();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        sendBroadcast(new Intent("de.uni_koblenz.soma.RESTART"));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        sendBroadcast(new Intent("de.uni_koblenz.soma.RESTART"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, intent.toString()+ " ; " + flags + " ; " + startId);
        initialize();
        return START_REDELIVER_INTENT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initializeLocationManager() {
        if(locationManager == null) locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private void initializeLocationListener() {
        if(locationListener == null) locationListener = new LocationListener(LocationManager.GPS_PROVIDER, database);
    }

    private Notification updateNotificationBar() {
        long dataCount = database.getLocationsCount();
        String contentText = "Ihre persönlichen Daten werden gesammelt"; // TODO
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(this, SoMAActivity.class);
        notificationIntent.setAction("de.uni_koblenz.soma.RESUME");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("SoMA")
                        .setContentText(contentText)
                        .setSubText("Data: " + dataCount)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(101, notification.build());

        Log.d(TAG, "updateNotification: dataCount: " + dataCount + ", notificationIntent: " + contentText);
        return notification.build();
    }
}
