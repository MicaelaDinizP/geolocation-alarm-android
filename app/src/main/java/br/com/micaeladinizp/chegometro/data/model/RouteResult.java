package br.com.micaeladinizp.chegometro.data.model;

public class RouteResult {

    private final double distanceMeters;
    private final double durationSeconds;
    private final boolean isValid;
    private RouteResult(double distanceMeters, double durationSeconds) {
        validate(distanceMeters, "distanceMeters");
        validate(durationSeconds, "durationSeconds");

        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.isValid = true;
    }
    private RouteResult() {
        this.distanceMeters = 0;
        this.durationSeconds = 0;
        this.isValid = false;
    }
    public static RouteResult success(double distanceMeters, double durationSeconds) {
        return new RouteResult(distanceMeters, durationSeconds);
    }
    public static RouteResult invalid() {
        return new RouteResult();
    }

    private void validate(double value, String fieldName) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be >= 0");
        }
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public boolean isValid() {
        return isValid;
    }
    public double getDistanceKilometers() {
        return distanceMeters / 1000.0;
    }

    public double getDurationMinutes() {
        return durationSeconds / 60.0;
    }

    @Override
    public String toString() {
        if (!isValid) return "RouteResult{invalid}";
        return "RouteResult{distance=" + distanceMeters + "m, duration=" + durationSeconds + "s}";
    }
}