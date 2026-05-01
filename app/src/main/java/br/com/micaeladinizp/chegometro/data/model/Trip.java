package br.com.micaeladinizp.chegometro.data.model;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.Objects;

public class Trip {

    private final Location origin;
    private final Location alertPoint;
    private final Location destination;
    private final double alarmDistanceMeters;
    private final Duration alarmDuration;
    private final AlarmType alarmType;

    public enum AlarmType {
        DISTANCE,
        TIME,
        DISTANCE_AND_TIME
    }

    private Trip(Builder builder) {
        this.origin = builder.origin;
        this.alertPoint = builder.alertPoint;
        this.destination = builder.destination;
        this.alarmDistanceMeters = builder.alarmDistanceMeters;
        this.alarmDuration = builder.alarmDuration;
        this.alarmType = builder.alarmType;
    }

    public static class Builder {
        private final Location origin;
        private final Location alertPoint;
        private final Location destination;

        private double alarmDistanceMeters;
        private Duration alarmDuration;
        private AlarmType alarmType = AlarmType.DISTANCE_AND_TIME;

        public Builder(Location origin, Location alertPoint, Location destination) {
            this.origin = Objects.requireNonNull(origin, "origin is required");
            this.alertPoint = Objects.requireNonNull(alertPoint, "alertPoint is required");
            this.destination = Objects.requireNonNull(destination, "destination is required");
        }

        public Builder alarmDistanceMeters(double meters) {
            this.alarmDistanceMeters = meters;
            return this;
        }

        public Builder alarmDuration(Duration duration) {
            this.alarmDuration = duration;
            return this;
        }

        public Builder alarmType(AlarmType type) {
            this.alarmType = Objects.requireNonNull(type, "alarmType is required");
            return this;
        }

        public Trip build() {

            if (alarmDistanceMeters != 0) {
                if (!Double.isFinite(alarmDistanceMeters) || alarmDistanceMeters <= 0) {
                    throw new IllegalStateException("Distance must be a finite value greater than 0");
                }
            }

            if (alarmDuration != null) {
                if (alarmDuration.isZero() || alarmDuration.isNegative()) {
                    throw new IllegalStateException("Duration must be greater than 0");
                }
            }

            switch (alarmType) {

                case DISTANCE:
                    if (alarmDistanceMeters <= 0) {
                        throw new IllegalStateException("Distance must be set for DISTANCE alarm");
                    }
                    if (alarmDuration != null) {
                        throw new IllegalStateException("Duration should not be set for DISTANCE alarm");
                    }
                    break;

                case TIME:
                    if (alarmDuration == null) {
                        throw new IllegalStateException("Duration must be set for TIME alarm");
                    }
                    if (alarmDistanceMeters > 0) {
                        throw new IllegalStateException("Distance should not be set for TIME alarm");
                    }
                    break;

                case DISTANCE_AND_TIME:
                    if (alarmDistanceMeters <= 0) {
                        throw new IllegalStateException("Distance must be set for DISTANCE_AND_TIME alarm");
                    }
                    if (alarmDuration == null) {
                        throw new IllegalStateException("Duration must be set for DISTANCE_AND_TIME alarm");
                    }
                    break;
            }

            return new Trip(this);
        }
    }

    @Override
    public String toString() {
        return "Trip{" +
                "origin=" + origin +
                ", alertPoint=" + alertPoint +
                ", destination=" + destination +
                ", alarmType=" + alarmType +
                '}';
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getAlertPoint() {
        return alertPoint;
    }

    public Location getDestination() {
        return destination;
    }

    public double getAlarmDistanceMeters() {
        return alarmDistanceMeters;
    }

    public Duration getAlarmDuration() {
        return alarmDuration;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }
}