package br.com.micaeladinizp.chegometro.data.remote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.micaeladinizp.chegometro.data.model.RouteResult;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OsrmResponseParser Tests")
class OsrmResponseParserTest {

    private static final double VALID_DISTANCE = 1000.0;
    private static final double VALID_DURATION = 120.0;

    private static final double ZERO_DISTANCE = 0.0;
    private static final double ZERO_DURATION = 0.0;

    private static final double NEGATIVE_VALUE = -1.0;

    private static final double LARGE_DISTANCE = 1_000_000_000.0;
    private static final double LARGE_DURATION = 999_999.0;

    private final OsrmResponseParser parser = new OsrmResponseParser();

    private String createResponse(String code, String routesContent) {
        return "{"
                + "\"code\":" + code + ","
                + "\"routes\":" + routesContent
                + "}";
    }

    private String createRoute(double distance, double duration) {
        return "[{"
                + "\"distance\":" + distance + ","
                + "\"duration\":" + duration
                + "}]";
    }

    private String validResponse() {
        return createResponse(
                "\"Ok\"",
                createRoute(VALID_DISTANCE, VALID_DURATION)
        );
    }

    @Nested
    @DisplayName("Successful parsing")
    class SuccessfulParsing {

        @Test
        @DisplayName("Should parse valid OSRM response")
        void shouldParseValidResponse() {
            RouteResult result = parser.parseRoute(validResponse());

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(VALID_DISTANCE, result.getDistanceMeters()),
                    () -> assertEquals(VALID_DURATION, result.getDurationSeconds())
            );
        }
    }

    @Nested
    @DisplayName("Code validation")
    class CodeValidation {

        @ParameterizedTest(name = "Invalid code: {0}")
        @ValueSource(strings = {
                "",
                "Error",
                "NoRoute",
                "InvalidQuery"
        })
        @DisplayName("Should reject non OK codes")
        void shouldRejectNonOkCodes(String code) {
            RouteResult result = parser.parseRoute(
                    createResponse("\"" + code + "\"", "[]")
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should accept lowercase ok code")
        void shouldAcceptLowercaseOkCode() {
            RouteResult result = parser.parseRoute(
                    createResponse(
                            "\"ok\"",
                            createRoute(VALID_DISTANCE, VALID_DURATION)
                    )
            );

            assertTrue(result.isValid());
        }

        @Test
        @DisplayName("Should reject null code")
        void shouldRejectNullCode() {
            RouteResult result = parser.parseRoute(
                    createResponse("null", "[]")
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject missing code")
        void shouldRejectMissingCode() {
            RouteResult result = parser.parseRoute(
                    "{ \"routes\": [] }"
            );

            assertFalse(result.isValid());
        }
    }

    @Nested
    @DisplayName("Routes validation")
    class RoutesValidation {

        @Test
        @DisplayName("Should reject empty routes array")
        void shouldRejectEmptyRoutesArray() {
            RouteResult result = parser.parseRoute(
                    createResponse("\"Ok\"", "[]")
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject missing routes field")
        void shouldRejectMissingRoutesField() {
            RouteResult result = parser.parseRoute(
                    "{ \"code\":\"Ok\" }"
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject null routes")
        void shouldRejectNullRoutes() {
            RouteResult result = parser.parseRoute(
                    createResponse("\"Ok\"", "null")
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject routes that is not an array")
        void shouldRejectRoutesThatIsNotArray() {
            RouteResult result = parser.parseRoute(
                    createResponse("\"Ok\"", "{}")
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject first route that is not an object")
        void shouldRejectRouteThatIsNotObject() {
            RouteResult result = parser.parseRoute(
                    createResponse("\"Ok\"", "[\"invalid\"]")
            );

            assertFalse(result.isValid());
        }
    }

    @Nested
    @DisplayName("Route fields validation")
    class RouteFieldsValidation {

        @Test
        @DisplayName("Should reject route without distance")
        void shouldRejectRouteWithoutDistance() {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{ \"duration\":100.0 }]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject route without duration")
        void shouldRejectRouteWithoutDuration() {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{ \"distance\":1000.0 }]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject negative distance")
        void shouldRejectNegativeDistance() {
            RouteResult result = parser.parseRoute(
                    createResponse(
                            "\"Ok\"",
                            createRoute(NEGATIVE_VALUE, VALID_DURATION)
                    )
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject negative duration")
        void shouldRejectNegativeDuration() {
            RouteResult result = parser.parseRoute(
                    createResponse(
                            "\"Ok\"",
                            createRoute(VALID_DISTANCE, NEGATIVE_VALUE)
                    )
            );

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject null distance")
        void shouldRejectNullDistance() {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{" +
                                    "\"distance\":null," +
                                    "\"duration\":100.0" +
                                    "}]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject null duration")
        void shouldRejectNullDuration() {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{" +
                                    "\"distance\":1000.0," +
                                    "\"duration\":null" +
                                    "}]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }

        @ParameterizedTest(name = "Invalid distance value: {0}")
        @ValueSource(strings = {
                "\"distance\":\"abc\"",
                "\"distance\":{}"
        })
        @DisplayName("Should reject invalid distance types")
        void shouldRejectInvalidDistanceTypes(String distanceField) {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{" +
                                    distanceField + "," +
                                    "\"duration\":100.0" +
                                    "}]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }

        @ParameterizedTest(name = "Invalid duration value: {0}")
        @ValueSource(strings = {
                "\"duration\":\"abc\"",
                "\"duration\":{}"
        })
        @DisplayName("Should reject invalid duration types")
        void shouldRejectInvalidDurationTypes(String durationField) {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[{" +
                                    "\"distance\":1000.0," +
                                    durationField +
                                    "}]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertFalse(result.isValid());
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should parse zero values")
        void shouldParseZeroValues() {
            RouteResult result = parser.parseRoute(
                    createResponse(
                            "\"Ok\"",
                            createRoute(ZERO_DISTANCE, ZERO_DURATION)
                    )
            );

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(ZERO_DISTANCE, result.getDistanceMeters()),
                    () -> assertEquals(ZERO_DURATION, result.getDurationSeconds())
            );
        }

        @Test
        @DisplayName("Should use first route when multiple routes exist")
        void shouldUseFirstRouteWhenMultipleRoutesExist() {
            String json =
                    createResponse(
                            "\"Ok\"",
                            "[" +
                                    "{" +
                                    "\"distance\":1000.0," +
                                    "\"duration\":100.0" +
                                    "}," +
                                    "{" +
                                    "\"distance\":9999.0," +
                                    "\"duration\":9999.0" +
                                    "}" +
                                    "]"
                    );

            RouteResult result = parser.parseRoute(json);

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(1000.0, result.getDistanceMeters()),
                    () -> assertEquals(100.0, result.getDurationSeconds())
            );
        }
    }

    @Nested
    @DisplayName("Malformed responses")
    class MalformedResponses {

        @Test
        @DisplayName("Should reject invalid JSON")
        void shouldRejectInvalidJson() {
            RouteResult result = parser.parseRoute("invalid-json");

            assertFalse(result.isValid());
        }

        @Test
        @DisplayName("Should reject empty route object")
        void shouldRejectEmptyRouteObject() {
            RouteResult result = parser.parseRoute(
                    createResponse("\"Ok\"", "[{}]")
            );

            assertFalse(result.isValid());
        }
    }

    @Nested
    @DisplayName("Numeric edge cases")
    class NumericEdgeCases {

        @Test
        @DisplayName("Should parse very large values")
        void shouldParseVeryLargeValues() {
            RouteResult result = parser.parseRoute(
                    createResponse(
                            "\"Ok\"",
                            createRoute(LARGE_DISTANCE, LARGE_DURATION)
                    )
            );

            assertAll(
                    () -> assertTrue(result.isValid()),
                    () -> assertEquals(LARGE_DISTANCE, result.getDistanceMeters()),
                    () -> assertEquals(LARGE_DURATION, result.getDurationSeconds())
            );
        }
    }
}