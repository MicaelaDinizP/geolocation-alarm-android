package br.com.micaeladinizp.chegometro.data.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.micaeladinizp.chegometro.data.model.RouteResult;

public class OsrmResponseParser {

    public RouteResult parseRoute(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            String code = jsonObject.optString("code", null);
            if (code == null || !"Ok".equalsIgnoreCase(code)) {
                return RouteResult.invalid();
            }

            JSONArray routes = jsonObject.optJSONArray("routes");
            if (routes == null || routes.length() == 0) {
                return RouteResult.invalid();
            }

            JSONObject bestRoute = routes.optJSONObject(0);
            if (bestRoute == null) {
                return RouteResult.invalid();
            }

            double distanceMeters = bestRoute.optDouble("distance", -1);
            double durationSeconds = bestRoute.optDouble("duration", -1);

            if (distanceMeters < 0 || durationSeconds < 0) {
                return RouteResult.invalid();
            }

            return RouteResult.success(distanceMeters, durationSeconds);

        } catch (JSONException e) {
            return RouteResult.invalid();
        }
    }
}