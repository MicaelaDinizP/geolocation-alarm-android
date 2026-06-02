package br.com.micaeladinizp.chegometro.data.remote;

import android.media.MediaRouter2;

import br.com.micaeladinizp.chegometro.data.RouteCallback;
import br.com.micaeladinizp.chegometro.data.model.Location;

public interface RouteClient {
    void fetchRoute(Location origin, Location destination, RouteCallback callback);
}
