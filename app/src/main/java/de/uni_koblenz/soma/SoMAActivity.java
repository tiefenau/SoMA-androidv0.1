package de.uni_koblenz.soma;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class SoMAActivity extends AppCompatActivity implements View.OnClickListener {
    private final String SURVEYKEY = "survey_finished";

    private boolean serviceRunning = false;
    private DatabaseHelper database = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_ma);

        database = new DatabaseHelper(this);

        ((TextView)findViewById(R.id.dataCount)).setText(""+database.getLocationsCount());

        serviceRunning = isLocationServiceRunning();
        boolean surveyFinished = isSurveyFinished();

        Button surveyButton = (Button)findViewById(R.id.surveyButton);
        surveyButton.setVisibility(surveyFinished ? View.INVISIBLE : View.VISIBLE);
        surveyButton.setOnClickListener(this);

        Switch onoff = (Switch)findViewById(R.id.erhebungSwitch);
        onoff.setOnClickListener(this);
        onoff.setChecked(serviceRunning);
        onoff.setVisibility(surveyFinished ? View.VISIBLE : View.INVISIBLE);

        Button uploadData = (Button)findViewById(R.id.uploadDataButton);
        uploadData.setOnClickListener(this);
        uploadData.setVisibility(surveyFinished ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isSurveyFinished() {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(SURVEYKEY, false);
    }

    private boolean requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }
        return true;
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.erhebungSwitch: {
                if(requestLocationPermissions()){
                    if(serviceRunning){
                        stopLocationService();
                    } else {
                        startLocationService();
                    }
                } else {
                    ((Switch)findViewById(R.id.erhebungSwitch)).setChecked(false);
                }
                break;
            }
            case R.id.uploadDataButton : {
                uploadData();
                break;
            }
            case R.id.surveyButton: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, getToken());
                startActivity(browserIntent);

                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putBoolean(SURVEYKEY, true);
                editor.commit();
                break;
            }
            default:
                return;
        }
    }

    private Uri getToken() {
        return Uri.parse("https://www.google.com");
    }

    private void startLocationService() {
        startService(new Intent(this, LocationService.class));
        serviceRunning = true;
    }

    private void stopLocationService() {
        stopService(new Intent(this, LocationService.class));
        serviceRunning = false;
    }

    private void uploadData() {

    }
}
