package br.com.micaeladinizp.chegometro.data.model;

import java.util.Objects;

public class TripState {

    public enum Status {
        IDLE,
        RUNNING,
        NO_NETWORK,
        ALARMED,
        FINISHED
    }

    private final Trip trip;
    private final Location currentLocation;
    private final RouteResult latestRouteResult;
    private final Status status;

    public TripState(Trip trip, Location currentLocation,
                     RouteResult latestRouteResult, Status status) {

        this.trip = Objects.requireNonNull(trip, "trip is required");
        this.currentLocation = Objects.requireNonNull(currentLocation, "currentLocation is required");
        this.latestRouteResult = Objects.requireNonNull(latestRouteResult, "latestRouteResult is required");
        this.status = Objects.requireNonNull(status, "status is required");
    }

    public Trip getTrip() { return trip; }
    public Location getCurrentLocation() { return currentLocation; }
    public RouteResult getLatestRouteResult() { return latestRouteResult; }
    public Status getStatus() { return status; }
    public boolean isRunning() {
        return status == Status.RUNNING;
    }
    public boolean hasValidRoute() {
        return latestRouteResult.isValid();
    }
}