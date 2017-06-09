package de.uni_koblenz.soma;

/**
 * Created by Chris on 07.06.2017.
 */

public class DatabaseEntryObject {
    private final int id;
    private final float altitude;
    private final float accuracy;
    private final float latitude;
    private final float longitude;
    private final float bearing;
    private final long timestamp;
    private final float speed;

    public DatabaseEntryObject(int id, float accuracy, float altitude, float bearing, float latitude, float longitude, long timestamp, float speed) {
        this.id = id;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.timestamp = timestamp;
        this.speed = speed;
    }

    public int getId() {
        return this.id;
    }
}