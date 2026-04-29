package br.com.micaeladinizp.chegometro.data.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Trip Tests")
class TripTest {

    private static final Location ORIGIN = new Location(10, 10, "Origin");
    private static final Location ALERT = new Location(20, 20, "Alert");
    private static final Location DESTINATION = new Location(30, 30, "Destination");

    private TripBuilder tripBuilder() {
        return new TripBuilder();
    }

    private static class TripBuilder {
        private final Trip.Builder builder =
                new Trip.Builder(ORIGIN, ALERT, DESTINATION);

        TripBuilder asDistance() {
            builder.alarmType(Trip.AlarmType.DISTANCE)
                    .alarmDistanceMeters(100);
            return this;
        }

        TripBuilder asTime() {
            builder.alarmType(Trip.AlarmType.TIME)
                    .alarmDuration(Duration.ofMinutes(5));
            return this;
        }

        TripBuilder asDistanceAndTime() {
            builder.alarmType(Trip.AlarmType.DISTANCE_AND_TIME)
                    .alarmDistanceMeters(100)
                    .alarmDuration(Duration.ofMinutes(5));
            return this;
        }

        TripBuilder distance(double meters) {
            builder.alarmDistanceMeters(meters);
            return this;
        }

        TripBuilder duration(Duration duration) {
            builder.alarmDuration(duration);
            return this;
        }

        TripBuilder type(Trip.AlarmType type) {
            builder.alarmType(type);
            return this;
        }

        Trip build() {
            return builder.build();
        }
    }

    @Nested
    @DisplayName("Successful creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Should create DISTANCE alarm trip")
        void shouldCreateDistanceTrip() {
            Trip trip = tripBuilder().asDistance().build();

            assertAll(
                    () -> assertEquals(Trip.AlarmType.DISTANCE, trip.getAlarmType()),
                    () -> assertEquals(100, trip.getAlarmDistanceMeters()),
                    () -> assertNull(trip.getAlarmDuration())
            );
        }

        @Test
        @DisplayName("Should create TIME alarm trip")
        void shouldCreateTimeTrip() {
            Trip trip = tripBuilder().asTime().build();

            assertAll(
                    () -> assertEquals(Trip.AlarmType.TIME, trip.getAlarmType()),
                    () -> assertEquals(Duration.ofMinutes(5), trip.getAlarmDuration()),
                    () -> assertEquals(0, trip.getAlarmDistanceMeters())
            );
        }

        @Test
        @DisplayName("Should create DISTANCE_AND_TIME alarm trip")
        void shouldCreateDistanceAndTimeTrip() {
            Trip trip = tripBuilder().asDistanceAndTime().build();

            assertAll(
                    () -> assertEquals(Trip.AlarmType.DISTANCE_AND_TIME, trip.getAlarmType()),
                    () -> assertEquals(100, trip.getAlarmDistanceMeters()),
                    () -> assertEquals(Duration.ofMinutes(5), trip.getAlarmDuration())
            );
        }
    }

    @Nested
    @DisplayName("Distance validation")
    class DistanceValidation {

        @ParameterizedTest(name = "Invalid distance: {0}")
        @ValueSource(doubles = {
                -1,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        })
        void shouldRejectInvalidDistances(double distance) {
            assertThrows(IllegalStateException.class,
                    () -> tripBuilder()
                            .type(Trip.AlarmType.DISTANCE)
                            .distance(distance)
                            .build());
        }
    }

    @Nested
    @DisplayName("Duration validation")
    class DurationValidation {

        @Test
        @DisplayName("Should reject zero duration")
        void shouldRejectZeroDuration() {
            assertThrows(IllegalStateException.class,
                    () -> tripBuilder()
                            .type(Trip.AlarmType.TIME)
                            .duration(Duration.ZERO)
                            .build());
        }

        @Test
        @DisplayName("Should reject negative duration")
        void shouldRejectNegativeDuration() {
            assertThrows(IllegalStateException.class,
                    () -> tripBuilder()
                            .type(Trip.AlarmType.TIME)
                            .duration(Duration.ofSeconds(-1))
                            .build());
        }
    }

    @Nested
    @DisplayName("Alarm type rules")
    class AlarmTypeRules {

        @Test
        @DisplayName("DISTANCE should not allow duration")
        void distanceShouldNotAllowDuration() {
            assertThrows(IllegalStateException.class,
                    () -> tripBuilder()
                            .asDistance()
                            .duration(Duration.ofMinutes(1))
                            .build());
        }

        @Test
        @DisplayName("TIME should not allow distance")
        void timeShouldNotAllowDistance() {
            assertThrows(IllegalStateException.class,
                    () -> tripBuilder()
                            .asTime()
                            .distance(100)
                            .build());
        }

        @Test
        @DisplayName("DISTANCE_AND_TIME should require both values")
        void distanceAndTimeShouldRequireBothValues() {
            assertAll(
                    () -> assertThrows(IllegalStateException.class,
                            () -> tripBuilder()
                                    .type(Trip.AlarmType.DISTANCE_AND_TIME)
                                    .distance(100)
                                    .build()),

                    () -> assertThrows(IllegalStateException.class,
                            () -> tripBuilder()
                                    .type(Trip.AlarmType.DISTANCE_AND_TIME)
                                    .duration(Duration.ofMinutes(1))
                                    .build())
            );
        }
    }

    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTest {

        @Test
        @DisplayName("Should not expose setters")
        void shouldNotExposeSetters() {
            Method[] methods = Trip.class.getDeclaredMethods();

            boolean hasSetter = java.util.Arrays.stream(methods)
                    .anyMatch(method -> method.getName().startsWith("set"));

            assertFalse(hasSetter);
        }

        @Test
        @DisplayName("Should keep all fields final")
        void shouldKeepAllFieldsFinal() {
            for (Field field : Trip.class.getDeclaredFields()) {
                if (field.isSynthetic()) {
                    continue;
                }

                assertTrue(
                        Modifier.isFinal(field.getModifiers()),
                        "Field should be final: " + field.getName()
                );
            }
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Should contain key fields")
        void shouldContainKeyFields() {
            Trip trip = tripBuilder().asDistance().build();

            String result = trip.toString();

            assertAll(
                    () -> assertTrue(result.contains("origin=")),
                    () -> assertTrue(result.contains("alertPoint=")),
                    () -> assertTrue(result.contains("destination=")),
                    () -> assertTrue(result.contains("alarmType="))
            );
        }
    }
}