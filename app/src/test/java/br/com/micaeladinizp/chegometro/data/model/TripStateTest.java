package br.com.micaeladinizp.chegometro.data.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TripState Tests")
class TripStateTest {

    private final Trip trip = mock(Trip.class);
    private final Location currentLocation = mock(Location.class);
    private final RouteResult routeResult = mock(RouteResult.class);

    @Nested
    @DisplayName("Successful creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Should create TripState with all fields")
        void shouldCreateTripStateWithAllFields() {


            TripState state = new TripState(
                    trip,
                    currentLocation,
                    routeResult,
                    TripState.Status.RUNNING
            );


            assertAll(
                    () -> assertEquals(trip, state.getTrip()),
                    () -> assertEquals(currentLocation, state.getCurrentLocation()),
                    () -> assertEquals(routeResult, state.getLatestRouteResult()),
                    () -> assertEquals(TripState.Status.RUNNING, state.getStatus())
            );
        }

        @Test
        @DisplayName("Should create TripState for every status")
        void shouldCreateTripStateForEveryStatus() {

            for (TripState.Status status : TripState.Status.values()) {

                assertDoesNotThrow(() ->
                        new TripState(
                                trip,
                                currentLocation,
                                routeResult,
                                status
                        )
                );
            }
        }
    }

    @Nested
    @DisplayName("Trip validation")
    class TripValidation {

        @Test
        @DisplayName("Should reject null trip")
        void shouldRejectNullTrip() {

            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TripState(
                            null,
                            currentLocation,
                            routeResult,
                            TripState.Status.RUNNING
                    )
            );

            assertEquals("trip is required", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Current location validation")
    class CurrentLocationValidation {

        @Test
        @DisplayName("Should reject null current location")
        void shouldRejectNullCurrentLocation() {

            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TripState(
                            trip,
                            null,
                            routeResult,
                            TripState.Status.RUNNING
                    )
            );

            assertEquals("currentLocation is required", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Route result validation")
    class RouteResultValidation {

        @Test
        @DisplayName("Should reject null route result")
        void shouldRejectNullRouteResult() {

            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TripState(
                            trip,
                            currentLocation,
                            null,
                            TripState.Status.RUNNING
                    )
            );

            assertEquals("latestRouteResult is required", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Status validation")
    class StatusValidation {

        @Test
        @DisplayName("Should reject null status")
        void shouldRejectNullStatus() {

            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new TripState(
                            trip,
                            currentLocation,
                            routeResult,
                            null
                    )
            );

            assertEquals("status is required", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("isRunning")
    class IsRunningTest {

        @ParameterizedTest
        @EnumSource(value = TripState.Status.class, names = {"RUNNING"})
        @DisplayName("Should return true when status is RUNNING")
        void shouldReturnTrueWhenRunning(TripState.Status status) {

            TripState state = new TripState(
                    trip,
                    currentLocation,
                    routeResult,
                    status
            );

            assertTrue(state.isRunning());
        }

        @ParameterizedTest
        @EnumSource(value = TripState.Status.class,
                names = {"RUNNING"},
                mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("Should return false when status is not RUNNING")
        void shouldReturnFalseWhenNotRunning(TripState.Status status) {

            TripState state = new TripState(
                    trip,
                    currentLocation,
                    routeResult,
                    status
            );

            assertFalse(state.isRunning());
        }
    }

    @Nested
    @DisplayName("hasValidRoute")
    class HasValidRouteTest {

        @Test
        @DisplayName("Should return true when route is valid")
        void shouldReturnTrueWhenRouteIsValid() {

            when(routeResult.isValid()).thenReturn(true);

            TripState state = new TripState(
                    trip,
                    currentLocation,
                    routeResult,
                    TripState.Status.RUNNING
            );

            assertTrue(state.hasValidRoute());

            verify(routeResult).isValid();
        }

        @Test
        @DisplayName("Should return false when route is invalid")
        void shouldReturnFalseWhenRouteIsInvalid() {

            when(routeResult.isValid()).thenReturn(false);

            TripState state = new TripState(
                    trip,
                    currentLocation,
                    routeResult,
                    TripState.Status.NO_NETWORK
            );

            assertFalse(state.hasValidRoute());

            verify(routeResult).isValid();
        }
    }

    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTest {

        @Test
        @DisplayName("Should have all fields as final")
        void shouldHaveAllFieldsAsFinal() {

            Field[] fields = TripState.class.getDeclaredFields();

            for (Field field : fields) {

                if (field.isSynthetic()) {
                    continue;
                }

                assertTrue(
                        Modifier.isFinal(field.getModifiers()),
                        () -> field.getName() + " should be final"
                );
            }
        }
    }

    @Nested
    @DisplayName("Status enum")
    class StatusEnumTest {

        @Test
        @DisplayName("Should contain all expected statuses")
        void shouldContainAllExpectedStatuses() {

            assertAll(
                    () -> assertNotNull(TripState.Status.valueOf("IDLE")),
                    () -> assertNotNull(TripState.Status.valueOf("RUNNING")),
                    () -> assertNotNull(TripState.Status.valueOf("NO_NETWORK")),
                    () -> assertNotNull(TripState.Status.valueOf("ALARMED")),
                    () -> assertNotNull(TripState.Status.valueOf("FINISHED"))
            );
        }

        @Test
        @DisplayName("Should contain exactly five statuses")
        void shouldContainExactlyFiveStatuses() {

            assertEquals(5, TripState.Status.values().length);
        }
    }
}