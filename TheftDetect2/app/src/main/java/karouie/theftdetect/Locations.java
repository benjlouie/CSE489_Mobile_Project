package karouie.theftdetect;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Locations extends AppCompatActivity {

    private long locationEntries;
    private long locationOutsideEntries;
    final Handler h = new Handler();
    final int delay = 5000; //milliseconds
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateLocations();
            h.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationEntries = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        updateLocations();

        h.postDelayed(runnable, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(runnable);
    }

    public void updateLocations() {
        ProfileDb db = new ProfileDb(this);

        //update gps location lists
        ArrayList<GpsData> points = db.getAllGps();
        ArrayList<GpsData> pointsOutside = db.getAllGpsOutside();
        if(points.size() == locationEntries && pointsOutside.size() == locationOutsideEntries) {
            //no need to update
            Log.d("Locations.updateLocs", "results: " + points.size() + ", " + pointsOutside.size() + " items");
            return;
        }
        locationEntries = points.size();
        locationOutsideEntries = points.size();

        TextView locations = (TextView) findViewById(R.id.txtv_locationList);
        TextView outsideLocations = (TextView) findViewById(R.id.txtv_locationOutsideList);
        StringBuilder sb = new StringBuilder(400);
        for(GpsData data : points) {
            sb.append("" + data.latitude + " " + data.longitude + " " + data.radius + "\n");
        }
        if(points.size() > 0) {
            locations.setText(sb.toString());
        } else {
            locations.setText("GPS Points");
        }

        sb = new StringBuilder(400);
        for(GpsData data : pointsOutside) {
            sb.append("" + data.latitude + " " + data.longitude + "\n");
        }
        if(pointsOutside.size() > 0) {
            outsideLocations.setText(sb.toString());
        } else {
            outsideLocations.setText("GPS Outside Points");
        }

        //update thisWasMe button and text
        TextView textWasMe = (TextView) findViewById(R.id.txtv_pointWasMe);
        if(pointsOutside.size() > 0) {
            GpsData point = pointsOutside.get(0);
            textWasMe.setText("" + point.latitude + " " + point.longitude);
        } else {
            textWasMe.setText("selected point");
        }
    }

    public void thisWasMe(View view) {
        ProfileDb db = new ProfileDb(this);
        Context context = this.getApplicationContext();
        ArrayList<GpsData> pointsOutside = db.getAllGpsOutside();

        if(db.getTrialRun()) {
            //during trial, don't do it
            Toast.makeText(context, "Cannot add during trial period", Toast.LENGTH_SHORT).show();
            return;
        }

        //add top point to main gpsList
        if(pointsOutside.size() > 0) {
            GpsData point = pointsOutside.get(0);
            point.radius = db.getDefaultRadius();
            db.insertGps(point);
            db.deleteGpsOutside(point.latitude, point.longitude);
        } else {
            Toast.makeText(context, "No point selected", Toast.LENGTH_SHORT).show();
        }
    }
}
