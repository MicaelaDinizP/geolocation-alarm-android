package br.com.micaeladinizp.chegometro.data.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RouteResult Tests")
class RouteResultTest {

    private static final double VALID_DISTANCE = 1000.0;
    private static final double VALID_DURATION = 600.0;

    @Nested
    @DisplayName("Successful creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Should create valid RouteResult using success()")
        void shouldCreateValidRouteResult() {
            RouteResult result = RouteResult.success(VALID_DISTANCE, VALID_DURATION);

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(VALID_DISTANCE, result.getDistanceMeters()),
                    () -> assertEquals(VALID_DURATION, result.getDurationSeconds())
            );
        }

        @Test
        @DisplayName("Should convert distance to kilometers correctly")
        void shouldConvertDistanceToKm() {
            RouteResult result = RouteResult.success(1500.0, VALID_DURATION);

            assertEquals(1.5, result.getDistanceKilometers());
        }

        @Test
        @DisplayName("Should convert duration to minutes correctly")
        void shouldConvertDurationToMinutes() {
            RouteResult result = RouteResult.success(VALID_DISTANCE, 120.0);

            assertEquals(2.0, result.getDurationMinutes());
        }

        @Test
        @DisplayName("Should convert meters to kilometers accurately")
        void shouldConvertDistanceWithPrecision() {
            double meters = 1234.56;
            RouteResult result = RouteResult.success(meters, VALID_DURATION);

            double expected = meters / 1000.0;

            assertEquals(expected, result.getDistanceKilometers(), 1e-5);
        }

        @Test
        @DisplayName("Should convert seconds to minutes accurately")
        void shouldConvertDurationWithPrecision() {
            double seconds = 123.45;
            RouteResult result = RouteResult.success(VALID_DISTANCE, seconds);

            double expected = seconds / 60.0;

            assertEquals(expected, result.getDurationMinutes(), 1e-4);
        }
    }

    @Nested
    @DisplayName("Invalid creation")
    class InvalidCreation {

        @Test
        @DisplayName("Should create invalid RouteResult using invalid()")
        void shouldCreateInvalidRouteResult() {
            RouteResult result = RouteResult.invalid();

            assertAll(
                    () -> assertFalse(result.isValid()),
                    () -> assertEquals(0.0, result.getDistanceMeters()),
                    () -> assertEquals(0.0, result.getDurationSeconds())
            );
        }
    }

    @Nested
    @DisplayName("Distance validation")
    class DistanceValidation {

        @ParameterizedTest(name = "Invalid distance: {0}")
        @ValueSource(doubles = {-1.0, -100.0})
        void shouldRejectNegativeDistance(double distance) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> RouteResult.success(distance, VALID_DURATION)
            );

            assertTrue(ex.getMessage().contains("distanceMeters"));
        }

        @Test
        @DisplayName("Should accept zero distance")
        void shouldAcceptZeroDistance() {
            assertDoesNotThrow(() -> RouteResult.success(0.0, VALID_DURATION));
        }
    }

    @Nested
    @DisplayName("Duration validation")
    class DurationValidation {

        @ParameterizedTest(name = "Invalid duration: {0}")
        @ValueSource(doubles = {-1.0, -500.0})
        void shouldRejectNegativeDuration(double duration) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> RouteResult.success(VALID_DISTANCE, duration)
            );

            assertTrue(ex.getMessage().contains("durationSeconds"));
        }

        @Test
        @DisplayName("Should accept zero duration")
        void shouldAcceptZeroDuration() {
            assertDoesNotThrow(() -> RouteResult.success(VALID_DISTANCE, 0.0));
        }
    }

    @Nested
    @DisplayName("Numeric edge cases (NaN / Infinity)")
    class NumericEdgeCases {

        @ParameterizedTest(name = "Invalid distance: {0}")
        @ValueSource(doubles = {
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        })
        void shouldRejectInvalidDistanceNumbers(double distance) {
            assertThrows(IllegalArgumentException.class,
                    () -> RouteResult.success(distance, VALID_DURATION));
        }

        @ParameterizedTest(name = "Invalid duration: {0}")
        @ValueSource(doubles = {
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        })
        void shouldRejectInvalidDurationNumbers(double duration) {
            assertThrows(IllegalArgumentException.class,
                    () -> RouteResult.success(VALID_DISTANCE, duration));
        }
    }

    @Nested
    @DisplayName("Combined invalid inputs")
    class CombinedInvalidCases {

        @Test
        @DisplayName("Should fail when both distance and duration are invalid")
        void shouldFailWhenBothInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> RouteResult.success(-1.0, -1.0));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle very large values")
        void shouldHandleLargeValues() {
            RouteResult result = RouteResult.success(Double.MAX_VALUE, Double.MAX_VALUE);

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(Double.MAX_VALUE, result.getDistanceMeters()),
                    () -> assertEquals(Double.MAX_VALUE, result.getDurationSeconds())
            );
        }
    }

    @Nested
    @DisplayName("Immutability")
    class Immutability {
        @Test
        @DisplayName("Should not have setters")
        void shouldNotHaveSetters() {
            var methods = RouteResult.class.getDeclaredMethods();

            for (var method : methods) {
                assertFalse(
                        method.getName().startsWith("set"),
                        "Setter found: " + method.getName()
                );
            }
        }

        @Test
        @DisplayName("All fields should be final")
        void shouldHaveAllFieldsFinal() {
            var fields = RouteResult.class.getDeclaredFields();

            for (var field : fields) {
                assertTrue(
                        java.lang.reflect.Modifier.isFinal(field.getModifiers()),
                        "Field " + field.getName() + " should be final"
                );
            }
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Should return formatted string for valid result")
        void shouldReturnFormattedStringWhenValid() {
            RouteResult result = RouteResult.success(VALID_DISTANCE, VALID_DURATION);

            String output = result.toString();

            assertAll(
                    () -> assertTrue(output.contains("distance=" + VALID_DISTANCE)),
                    () -> assertTrue(output.contains("duration=" + VALID_DURATION))
            );
        }

        @Test
        @DisplayName("Should return invalid string when result is invalid")
        void shouldReturnInvalidString() {
            RouteResult result = RouteResult.invalid();

            assertEquals("RouteResult{invalid}", result.toString());
        }
    }
}
