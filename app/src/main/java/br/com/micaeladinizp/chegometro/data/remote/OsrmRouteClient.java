package br.com.micaeladinizp.chegometro.data.remote;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import br.com.micaeladinizp.chegometro.data.RouteCallback;
import br.com.micaeladinizp.chegometro.data.model.Location;
import br.com.micaeladinizp.chegometro.data.model.RouteResult;

public class OsrmRouteClient implements RouteClient {

    private static final String TAG = "OsrmRouteClient";
    private static final String BASE_URL = "router.project-osrm.org";
    private static final int TIMEOUT_MS = 10000;

    private final OsrmResponseParser responseParser;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public OsrmRouteClient(
            OsrmResponseParser parser,
            ExecutorService executor,
            Handler mainHandler) {

        this.responseParser =
                Objects.requireNonNull(parser, "parser is required");

        this.executorService =
                Objects.requireNonNull(executor, "executor is required");

        this.mainHandler =
                Objects.requireNonNull(mainHandler, "mainHandler is required");
    }

    @Override
    public void fetchRoute(
            Location origin,
            Location destination,
            RouteCallback callback) {

        Objects.requireNonNull(origin, "origin is required");
        Objects.requireNonNull(destination, "destination is required");
        Objects.requireNonNull(callback, "callback is required");

        String url = buildUrl(origin, destination);

        Log.d(TAG, "Request URL: " + url);

        executorService.execute(() -> {

            HttpURLConnection connection = null;

            try {

                connection = openConnection(url);

                String json = readResponse(connection);

                if (json == null || json.isBlank()) {

                    Log.w(TAG,
                            "Empty response received from API.");

                    postError(
                            callback,
                            new IllegalStateException(
                                    "Empty response"
                            ),
                            "Empty route response."
                    );

                    return;
                }

                RouteResult result =
                        responseParser.parseRoute(json);

                if (!result.isValid()) {

                    Log.w(TAG,
                            "Invalid route response received from API.");

                    postError(
                            callback,
                            new IllegalStateException(
                                    "Invalid route response"
                            ),
                            "Invalid route response."
                    );

                    return;
                }

                postSuccess(callback, result);

            } catch (IOException e) {

                Log.e(TAG,
                        "Connection error.",
                        e);

                postError(
                        callback,
                        e,
                        "Failed to connect to routing service."
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void postSuccess(
            RouteCallback callback,
            RouteResult result) {

        mainHandler.post(() -> {
            try {
                callback.onSuccess(result);
            } catch (Exception e) {
                Log.e(TAG,
                        "Callback execution failed.",
                        e);
            }
        });
    }

    private void postError(
            RouteCallback callback,
            Exception exception,
            String message) {

        mainHandler.post(() -> {
            try {
                callback.onError(
                        exception,
                        message
                );
            } catch (Exception e) {
                Log.e(TAG,
                        "Callback execution failed.",
                        e);
            }
        });
    }

    private HttpURLConnection openConnection(String url)
            throws IOException {

        URL routeUrl = new URL(url);

        HttpURLConnection connection =
                (HttpURLConnection) routeUrl.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        connection.setDoInput(true);

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                    "Unexpected response code: " + responseCode
            );
        }

        return connection;
    }

    private String readResponse(HttpURLConnection connection)
            throws IOException {

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     connection.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private String buildUrl(
            Location origin,
            Location destination) {

        String coordinates =
                origin.getLongitude() + "," + origin.getLatitude()
                        + ";"
                        + destination.getLongitude() + "," + destination.getLatitude();

        return new Uri.Builder()
                .scheme("https")
                .authority(BASE_URL)
                .appendPath("route")
                .appendPath("v1")
                .appendPath("driving")
                .appendEncodedPath(coordinates)
                .appendQueryParameter("overview", "false")
                .build()
                .toString();
    }
}