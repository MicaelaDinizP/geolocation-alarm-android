package br.com.micaeladinizp.chegometro.data.remote;

import android.os.Handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import br.com.micaeladinizp.chegometro.data.RouteCallback;
import br.com.micaeladinizp.chegometro.data.model.Location;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("OsrmRouteClient Tests")
public class OsrmRouteClientTest {

    private static final Location VALID_ORIGIN =
            new Location(-22.28, -42.53, "Origin");

    private static final Location VALID_DESTINATION =
            new Location(-22.29, -42.54, "Destination");

    private final OsrmResponseParser parser =
            new OsrmResponseParser();

    private final ExecutorService executor =
            mock(ExecutorService.class);

    private final Handler handler =
            mock(Handler.class);

    private final RouteCallback callback =
            mock(RouteCallback.class);

    @Nested
    @DisplayName("Dependency validation")
    class DependencyValidation {

        @Test
        @DisplayName("Should create client with valid dependencies")
        void shouldCreateClientWithValidDependencies() {

            OsrmRouteClient client = assertDoesNotThrow(
                    () -> new OsrmRouteClient(
                            parser,
                            executor,
                            handler
                    )
            );

            assertNotNull(client);
        }

        @Test
        @DisplayName("Should reject null parser")
        void shouldRejectNullParser() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> new OsrmRouteClient(
                                    null,
                                    executor,
                                    handler
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("parser")
            );
        }

        @Test
        @DisplayName("Should reject null executor")
        void shouldRejectNullExecutor() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> new OsrmRouteClient(
                                    parser,
                                    null,
                                    handler
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("executor")
            );
        }

        @Test
        @DisplayName("Should reject null handler")
        void shouldRejectNullHandler() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> new OsrmRouteClient(
                                    parser,
                                    executor,
                                    null
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("mainHandler")
            );
        }
    }

    @Nested
    @DisplayName("FetchRoute parameter validation")
    class FetchRouteParameterValidation {

        private final OsrmRouteClient client =
                new OsrmRouteClient(
                        parser,
                        executor,
                        handler
                );

        @Test
        @DisplayName("Should reject null origin")
        void shouldRejectNullOrigin() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> client.fetchRoute(
                                    null,
                                    VALID_DESTINATION,
                                    callback
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("origin")
            );
        }

        @Test
        @DisplayName("Should reject null destination")
        void shouldRejectNullDestination() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> client.fetchRoute(
                                    VALID_ORIGIN,
                                    null,
                                    callback
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("destination")
            );
        }

        @Test
        @DisplayName("Should reject null callback")
        void shouldRejectNullCallback() {

            NullPointerException exception =
                    assertThrows(
                            NullPointerException.class,
                            () -> client.fetchRoute(
                                    VALID_ORIGIN,
                                    VALID_DESTINATION,
                                    null
                            )
                    );

            assertTrue(
                    exception.getMessage().contains("callback")
            );
        }
    }
}