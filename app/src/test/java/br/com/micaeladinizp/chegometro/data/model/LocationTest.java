package br.com.micaeladinizp.chegometro.data.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Location Tests")
class LocationTest {
    private static final double VALID_LAT = 10.0;
    private static final double VALID_LNG = 20.0;
    private static final String VALID_NAME = "Test";

    @Nested
    @DisplayName("Successful creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Should create Location with all fields")
        void shouldCreateLocationWithAllFields() {
            Location location = new Location(VALID_LAT, VALID_LNG, VALID_NAME);

            assertAll(
                    () -> assertEquals(VALID_LAT, location.getLatitude()),
                    () -> assertEquals(VALID_LNG, location.getLongitude()),
                    () -> assertEquals(VALID_NAME, location.getName())
            );
        }

        @Test
        @DisplayName("Should create Location with coordinates only")
        void shouldCreateLocationWithCoordinatesOnly() {
            Location location = new Location(VALID_LAT, VALID_LNG);

            assertAll(
                    () -> assertEquals(VALID_LAT, location.getLatitude()),
                    () -> assertEquals(VALID_LNG, location.getLongitude()),
                    () -> assertEquals("", location.getName())
            );
        }
    }

    @Nested
    @DisplayName("Latitude validation")
    class LatitudeValidation {

        @ParameterizedTest(name = "Invalid latitude: {0}")
        @ValueSource(doubles = {-91.0, -100.0, 91.0, 999.0})
        void shouldRejectInvalidLatitudes(double latitude) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Location(latitude, VALID_LNG, VALID_NAME)
            );

            assertTrue(ex.getMessage().contains("Latitude"));
        }

        @Test
        @DisplayName("Should accept boundary latitude values")
        void shouldAcceptBoundaryLatitudeValues() {
            assertDoesNotThrow(() -> new Location(-90.0, VALID_LNG, VALID_NAME));
            assertDoesNotThrow(() -> new Location(90.0, VALID_LNG, VALID_NAME));
        }
    }

    @Nested
    @DisplayName("Longitude validation")
    class LongitudeValidation {

        @ParameterizedTest(name = "Invalid longitude: {0}")
        @ValueSource(doubles = {-181.0, -999.0, 181.0, 999.0})
        void shouldRejectInvalidLongitudes(double longitude) {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Location(VALID_LAT, longitude, VALID_NAME)
            );

            assertTrue(ex.getMessage().contains("Longitude"));
        }

        @Test
        @DisplayName("Should accept boundary longitude values")
        void shouldAcceptBoundaryLongitudeValues() {
            assertDoesNotThrow(() -> new Location(VALID_LAT, -180.0, VALID_NAME));
            assertDoesNotThrow(() -> new Location(VALID_LAT, 180.0, VALID_NAME));
        }
    }

    @Nested
    @DisplayName("Name validation")
    class NameValidation {

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldRejectNullName() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Location(VALID_LAT, VALID_LNG, null)
            );

            assertTrue(ex.getMessage().contains("name"));
        }

        @Test
        @DisplayName("Should allow empty name")
        void shouldAllowEmptyName() {
            assertDoesNotThrow(() -> new Location(VALID_LAT, VALID_LNG, ""));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should create location at (0,0)")
        void shouldCreateAtZeroCoordinates() {
            Location location = new Location(0.0, 0.0, "");

            assertAll(
                    () -> assertEquals(0.0, location.getLatitude()),
                    () -> assertEquals(0.0, location.getLongitude())
            );
        }
    }

    @Nested
    @DisplayName("Numeric edge cases (NaN / Infinity)")
    class NumericEdgeCases {

        @ParameterizedTest
        @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void shouldRejectInvalidLatitudeNumbers(double latitude) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Location(latitude, VALID_LNG, VALID_NAME));
        }

        @ParameterizedTest
        @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void shouldRejectInvalidLongitudeNumbers(double longitude) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Location(VALID_LAT, longitude, VALID_NAME));
        }
    }

    @Nested
    @DisplayName("Combined invalid inputs")
    class CombinedInvalidCases {

        @Test
        @DisplayName("Should fail when both latitude and longitude are invalid")
        void shouldFailWhenBothCoordinatesInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Location(999.0, 999.0, VALID_NAME));
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Should return formatted string with all fields")
        void shouldReturnFormattedString() {
            Location location = new Location(VALID_LAT, VALID_LNG, VALID_NAME);

            String result = location.toString();

            assertAll(
                    () -> assertTrue(result.contains("lat=" + VALID_LAT)),
                    () -> assertTrue(result.contains("lng=" + VALID_LNG)),
                    () -> assertTrue(result.contains("name='" + VALID_NAME + "'"))
            );
        }
    }
}