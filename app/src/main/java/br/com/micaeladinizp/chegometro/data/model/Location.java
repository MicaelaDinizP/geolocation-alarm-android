package br.com.micaeladinizp.chegometro.data.model;
public class Location {

    private final double latitude;
    private final double longitude;
    private final String name;

    public Location(double latitude, double longitude, String name) {
        validateName(name);
        validateCoordinates(latitude, longitude);

        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Location(double latitude, double longitude) {
        this(latitude, longitude, "");
    }

    private void validateCoordinates(double latitude, double longitude) {

        if (Double.isNaN(latitude) || Double.isInfinite(latitude)) {
            throw new IllegalArgumentException(
                    "Latitude must be a valid number, but was: " + latitude
            );
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90, but was: " + latitude);
        }

        if (Double.isNaN(longitude) || Double.isInfinite(longitude)) {
            throw new IllegalArgumentException(
                    "Longitude must be a valid number, but was: " + longitude);
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180, but was: " + longitude);
        }
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Location{lat=" + latitude +
                ", lng=" + longitude +
                ", name='" + name + "'}";
    }
}