package task_05.osm;


public class Location {
    public final Double latitude;
    public final Double longitude;

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    // get the distance to another location, in meters
    public float distanceTo(Location otherLocation) {
        double lat1 = this.latitude;
        double lon1 = this.longitude;
        double lat2 = otherLocation.latitude;
        double lon2 = otherLocation.longitude;

        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (float) (earthRadius * c);
    }


    public boolean equals(Location otherLocation) {
        return this == otherLocation || (this.longitude.equals(otherLocation.longitude) && this.latitude.equals(otherLocation.latitude));
    }


    @Override
    public String toString() {
        return String.format("<osm.Location lat=%s lon=%s>", latitude, longitude);
    }
}
