package karouie.theftdetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Locations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        updateLocations();
    }

    public void addLocation(View view) {
        EditText latitude = (EditText) findViewById(R.id.txt_latitude);
        EditText longitude = (EditText) findViewById(R.id.txt_longitude);
        double lat = Double.parseDouble(latitude.getText().toString());
        double lon = Double.parseDouble(longitude.getText().toString());
        ProfileDb db = new ProfileDb(this);

        if(db.insertGps(new GpsData(lat, lon, "2016-11-26 17:32:15", 0))) {
            latitude.setText("");
            longitude.setText("");
            Log.d("Locations.addLocation", "inserted location correctly");
            updateLocations();
        } else {
            Log.d("Locations.addLocation", "failed to insert location");
        }
    }

    public void removeLocation(View view) {
        EditText latitude = (EditText) findViewById(R.id.txt_latitude);
        EditText longitude = (EditText) findViewById(R.id.txt_longitude);
        double lat = Double.parseDouble(latitude.getText().toString());
        double lon = Double.parseDouble(longitude.getText().toString());
        ProfileDb db = new ProfileDb(this);

        if(db.deleteGps(lat, lon) > 0) {
            latitude.setText("");
            longitude.setText("");
            Log.d("Locations.addLocation", "deleted location correctly");
            updateLocations();
        } else {
            Log.d("Locations.addLocation", "failed to delete location");
        }

    }

    public void getPoints(View view) {
        EditText latitude1 = (EditText) findViewById(R.id.txt_lat1);
        EditText latitude2 = (EditText) findViewById(R.id.txt_lat2);
        EditText longitude1 = (EditText) findViewById(R.id.txt_lon1);
        EditText longitude2 = (EditText) findViewById(R.id.txt_lon2);
        double lat1 = Double.parseDouble(latitude1.getText().toString());
        double lat2 = Double.parseDouble(latitude2.getText().toString());
        double lon1 = Double.parseDouble(longitude1.getText().toString());
        double lon2 = Double.parseDouble(longitude2.getText().toString());
        ProfileDb db = new ProfileDb(this);

        ArrayList<GpsData> points = db.getGPSWithin(lat1, lat2, lon1, lon2);
        Log.d("Locations.getPoints", "results: " + points.size() + " items");

        TextView results = (TextView) findViewById(R.id.txtv_resultList);
        StringBuilder sb = new StringBuilder(400);
        for(GpsData data : points) {
            sb.append("" + data.latitude + "\t" + data.longitude + "\n");
        }
        results.setText(sb.toString());
    }

    public void updateLocations() {
        ProfileDb db = new ProfileDb(this);
        ArrayList<GpsData> points = db.getAllGps();
        Log.d("Locations.updateLocs", "results: " + points.size() + " items");

        TextView locations = (TextView) findViewById(R.id.txtv_locationList);
        StringBuilder sb = new StringBuilder(400);
        for(GpsData data : points) {
            sb.append("" + data.latitude + "\t" + data.longitude + "\n");
        }
        locations.setText(sb.toString());
    }
}
