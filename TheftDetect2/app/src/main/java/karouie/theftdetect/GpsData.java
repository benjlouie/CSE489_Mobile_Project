package karouie.theftdetect;

/**
 * Created by Ben on 10/26/2016.
 */

public class GpsData {
    public double latitude;
    public double longitude;
    public String dateTime;
    public double radius;

    public GpsData(double latitude, double longitude, String dateTime) {
        this(latitude, longitude, dateTime, 0);
    }

    public GpsData(double latitude, double longitude, String dateTime, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
        this.radius = radius;
    }
}
