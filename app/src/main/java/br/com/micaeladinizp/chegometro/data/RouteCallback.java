package br.com.micaeladinizp.chegometro.data;

import br.com.micaeladinizp.chegometro.data.model.RouteResult;

public interface RouteCallback {
    void onSuccess(RouteResult result);
    void onError(Exception exception, String message);
}
